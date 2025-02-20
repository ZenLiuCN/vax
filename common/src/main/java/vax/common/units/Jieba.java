package vax.common.units;

import io.netty.util.collection.CharObjectHashMap;
import io.netty.util.collection.CharObjectMap;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import lombok.SneakyThrows;
import lombok.With;
import org.jetbrains.annotations.Nullable;
import org.jooq.lambda.tuple.Tuple;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.netty.util.collection.CharObjectHashMap.DEFAULT_CAPACITY;
import static io.netty.util.collection.CharObjectHashMap.DEFAULT_LOAD_FACTOR;
import static io.netty.util.internal.MathUtil.safeFindNextPositivePowerOfTwo;

/**
 * Base on  <a href="https://github.com/anderscui/jieba.NET">Jieba.Net 0.42.2</a>
 *
 * @author Zen.Liu
 * @since 2025-02-20
 */
public interface Jieba {
    /**
     * probability dictionary, based on {@link CharObjectHashMap}
     */
    final class Prob {
        private static final double NULL_VALUE = Double.NEGATIVE_INFINITY;
        private int maxSize;
        private final float loadFactor;
        private char[] keys;
        private double[] values;
        private int size;
        private int mask;

        public Prob() {
            this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
        }

        public Prob(int initialCapacity) {
            this(initialCapacity, DEFAULT_LOAD_FACTOR);
        }

        public Prob(int initialCapacity, float loadFactor) {
            if (loadFactor <= 0.0f || loadFactor > 1.0f) {
                throw new IllegalArgumentException("loadFactor must be > 0 and <= 1");
            }
            this.loadFactor = loadFactor;
            int capacity = safeFindNextPositivePowerOfTwo(initialCapacity);
            mask = capacity - 1;
            keys = new char[capacity];
            values = new double[capacity];
            maxSize = calcMaxSize(capacity);
        }

        public int size() {
            return size;
        }

        public double get(char key, double def) {
            var index = indexOf(key);
            return index == -1 ? def : values[index];
        }

        public double get(char key) {
            return get(key, MinProb);
        }

        public boolean containsKey(char key) {
            return indexOf(key) >= 0;
        }

        public double put(char key, double value) {
            int startIndex = hashIndex(key);
            int index = startIndex;

            for (; ; ) {
                if (values[index] == NULL_VALUE) {
                    keys[index] = key;
                    values[index] = value;
                    growSize();
                    return NULL_VALUE;
                }
                if (keys[index] == key) {
                    var previousValue = values[index];
                    values[index] = value;
                    return previousValue;
                }
                if ((index = probeNext(index)) == startIndex) {
                    throw new IllegalStateException("Unable to insert");
                }
            }
        }

        public double remove(char key) {
            int index = indexOf(key);
            if (index == -1) {
                return NULL_VALUE;
            }

            var prev = values[index];
            removeAt(index);
            return prev;
        }

        public void clear() {
            Arrays.fill(keys, (char) 0);
            Arrays.fill(values, NULL_VALUE);
            size = 0;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public boolean isEmptyValue(double v) {
            return v == NULL_VALUE;
        }

        private int calcMaxSize(int capacity) {
            var upperBound = capacity - 1;
            return Math.min(upperBound, (int) (capacity * loadFactor));
        }

        private void removeAt(final int index) {
            --size;
            keys[index] = 0;
            values[index] = Double.MIN_VALUE;
            int nextFree = index;
            int i = probeNext(index);
            for (var value = values[i]; value != NULL_VALUE; value = values[i = probeNext(i)]) {
                char key = keys[i];
                int bucket = hashIndex(key);
                if (i < bucket && (bucket <= nextFree || nextFree <= i) ||
                        bucket <= nextFree && nextFree <= i) {
                    keys[nextFree] = key;
                    values[nextFree] = value;
                    keys[i] = 0;
                    values[i] = NULL_VALUE;
                    nextFree = i;
                }
            }
        }

        private int hashIndex(char key) {
            return hashCode(key) & mask;
        }

        private static int hashCode(char key) {
            return key;
        }

        private int probeNext(int index) {
            return (index + 1) & mask;
        }

        private int indexOf(char key) {
            var startIndex = hashIndex(key);
            var index = startIndex;
            for (; ; ) {
                if (index >= values.length) {
                    return -1;
                }
                if (key == keys[index]) {
                    return index;
                }
                if ((index = probeNext(index)) == startIndex) {
                    return -1;
                }
            }
        }

        private void rehash(int newCapacity) {
            var oldKeys = keys;
            var oldVals = values;
            keys = new char[newCapacity];
            values = new double[newCapacity];
            maxSize = calcMaxSize(newCapacity);
            mask = newCapacity - 1;
            System.arraycopy(oldKeys, 0, keys, 0, oldKeys.length);
            System.arraycopy(oldVals, 0, values, 0, oldVals.length);
        }

        private void growSize() {
            size++;
            if (size > maxSize) {
                if (keys.length == Integer.MAX_VALUE) {
                    throw new IllegalStateException("Max capacity reached at size=" + size);
                }
                rehash(keys.length << 1);
            }
        }


        @Override
        public String toString() {
            if (isEmpty()) {
                return "{}";
            }
            var sb = new StringBuilder(4 * size);
            sb.append('{');
            var n = 0;
            for (var value : values) {
                if (n != 0) {
                    sb.append(", ");
                }
                sb.append(keys[n]).append('=').append(value);
                n++;
            }
            return sb.append('}').toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Prob other) {
                if (size != other.size) {
                    return false;
                }
                for (int i = 0; i < values.length; ++i) {
                    var value = values[i];
                    var key = keys[i];
                    var otherValue = other.get(key, NULL_VALUE);
                    if (value != otherValue) {
                        return false;
                    }
                }
            }
            return false;

        }

        @Override
        public int hashCode() {
            int hash = size;
            for (char key : keys) {
                hash ^= hashCode(key);
            }
            return hash;
        }
    }

    final class Trie {
        static class Node {
            char key;
            int frequency;
            CharObjectMap<Node> child;

            Node(char key) {
                this.key = key;
                this.frequency = 0;
            }

            Node(char key, int frequency, CharObjectMap<Node> child) {
                this.key = key;
                this.frequency = frequency;
                this.child = child;
            }

            int insert(char[] s, int p, int freq) {
                if (s == null || s.length == 0 || p >= s.length) {
                    return 0;
                }
                if (child == null) child = new CharObjectHashMap<>();
                var c = s[p];
                if (!child.containsKey(c)) {
                    child.put(c, new Node(c));
                }
                var cur = child.get(c);
                if (p == s.length - 1) {
                    cur.frequency += freq;
                    return cur.frequency;
                }
                return cur.insert(s, p + 1, freq);
            }

            @Nullable Node search(char[] s, int p) {
                if (s == null || s.length == 0) return null;
                if (p >= s.length || child == null) return null;
                var next = child.get(s[p]);
                if (p == s.length - 1) {
                    return next;
                }
                return next == null ? null : next.search(s, p + 1);
            }

        }

        static final char ROOT = '\0';
        Node root;
        int count;
        int totalFrequency;

        Trie() {
            root = new Node(ROOT);
            count = 0;
        }

        boolean contains(char[] word) {
            var f = root.search(word, 0);
            return f != null && f.frequency > 0;
        }

        boolean startWith(char[] word) {
            var f = root.search(word, 0);
            return f != null;
        }

        int frequency(char[] word) {
            var f = root.search(word, 0);
            return f != null ? f.frequency : 0;
        }

        int insert(char[] s, int frequency) {
            var i = root.insert(s, 0, frequency);
            if (i > 0) {
                totalFrequency += frequency;
                count++;
            }
            return i;
        }

    }

    record Node(char key, @With Node parent) {}


    double MinProb = -3.14e100;

    List<String> NounPos = List.of("n", "ng", "nr", "nrfg", "nrt", "ns", "nt", "nz");
    List<String> VerbPos = List.of("v", "vd", "vg", "vi", "vn", "vq");
    List<String> IdiomPos = List.of("i");





    final class TagSegment {
        final static class TagTokenizer {
            static final Lazy<TagTokenizer> INSTANCE = new Lazy<>(TagTokenizer::new);
            static final Map<String, Prob> emitProb = new HashMap<>();
            static final Map<String, Map<String, Double>> transProb = new HashMap<>();
            static final Map<String, Double> startProb = new HashMap<>();
            static final CharObjectMap<Set<String>> state = new CharObjectHashMap<>();

            private TagTokenizer() {
                load();
            }

            static void load() {
                //! TODO
            }

            public record Pair(String word, String flag) {}

            public List<Pair> cut(String sentence) {
                var probs = viterbiCut(sentence);
                var pos = probs.route;
                var tokens = new ArrayList<Pair>();
                var b = 0;
                var n = 0;
                for (int i = 0; i < sentence.length(); i++) {
                    var p = pos.get(i).split("-");
                    var cs = p[0].charAt(0);
                    var flag = p[1];
                    switch (cs) {
                        case 'B' -> b = i;
                        case 'E' -> {
                            tokens.add(new Pair(sentence.substring(b, i + 1), flag));
                            n = i + 1;
                        }
                        case 'S' -> {
                            tokens.add(new Pair(sentence.substring(i, i + 1), flag));
                            n = i + 1;
                        }
                    }
                }
                if (n < sentence.length()) {
                    tokens.add(new Pair(sentence.substring(n), pos.get(n).split("-")[1]));
                }
                return tokens;
            }

            record Info(double prob, List<String> route) {}

            private Info viterbiCut(String sentence) {
                var v = new ArrayList<HashMap<String, Double>>();
                var p = new ArrayList<HashMap<String, String>>();
                var states = new HashSet<>(transProb.keySet());
                v.add(new HashMap<>());
                p.add(new HashMap<>());
                //init
                for (var s : state.getOrDefault(sentence.charAt(0), states)) {
                    var emp = emitProb.get(s).get(sentence.charAt(0));
                    v.get(0).put(s, startProb.get(s) + emp);
                    p.get(0).put(s, "");
                }
                for (var i = 1; i < sentence.length(); i++) {
                    v.add(new HashMap<>());
                    p.add(new HashMap<>());
                    var pre = p.get(i - 1).keySet().stream().filter(x -> !transProb.get(x).isEmpty()).toList();
                    var curCandidates = pre.stream()
                                           .flatMap(x -> transProb.get(x).keySet().stream())
                                           .collect(Collectors.toSet());
                    var obs = state.get(sentence.charAt(i));
                    obs = curCandidates.stream()
                                       .filter(obs::contains)
                                       .collect(Collectors.toSet());
                    if (obs.isEmpty()) {
                        obs = curCandidates.isEmpty() ? states : curCandidates;
                    }
                    for (var s : obs) {
                        var emp = emitProb.get(s).get(sentence.charAt(i));
                        var prob = Double.MIN_VALUE;
                        var state = "";
                        for (var s1 : pre) {
                            var tp = transProb.get(s1).getOrDefault(s, Double.MIN_VALUE);
                            tp = v.get(i - 1).get(s1) + tp + emp;
                            if (prob < tp || (prob == tp && state.compareTo(s1) < 0)) {
                                prob = tp;
                                state = s1;
                            }
                        }
                        v.get(i).put(s, prob);
                        p.get(i).put(s, state);
                    }
                }
                var lastV = v.get(v.size() - 1);
                var last = p.get(p.size() - 1).keySet().stream().map(x -> Tuple.tuple(x, lastV.get(x))).toList();
                var endProb = Double.MIN_VALUE;
                var endState = "";
                for (var s : last) {
                    if (endProb < s.v2 || (endProb == s.v2 && endState.compareTo(s.v1) < 0)) {
                        endProb = s.v2;
                        endState = s.v1;
                    }
                }
                var route = new String[sentence.length()];
                var n = sentence.length() - 1;
                var cur = endState;
                while (n >= 0) {
                    route[n] = cur;
                    cur = p.get(n).get(cur);
                    n--;
                }
                return new Info(endProb, Arrays.asList(route));
            }
        }
        static final Pattern RegexChineseInternal = Pattern.compile("([一-鿕a-zA-Z0-9+#&._%·\\-]+)");
        static final Pattern RegexSkipInternal = Pattern.compile("(\r\n|\s)");
        static final Pattern RegexChineseDetail = Pattern.compile("([\u4E00-\u9FD5]+)");
        static final Pattern RegexSkipDetail = Pattern.compile("([\\.0-9]+|[a-zA-Z0-9]+)");
        static final Pattern RegexEnglishWords = Pattern.compile("[a-zA-Z0-9]+");
        static final Pattern RegexNumbers = Pattern.compile("[.0-9]+");
        static final Pattern RegexEnglishChar = Pattern.compile("^[a-zA-Z0-9]$");
        static final Map<String, String> WordTag = new HashMap<>();
        static final WordDictionary wd = WordDictionary.INSTANCE.get();
        static final TagTokenizer ft = TagTokenizer.INSTANCE.get();
        static {
            //TODO load WordTags
            /**
             * try
             *             {
             *                 _wordTagTab = new Dictionary<string, string>();
             *                 var lines = File.ReadAllLines(ConfigManager.MainDictFile, Encoding.UTF8);
             *                 foreach (var line in lines)
             *                 {
             *                     var tokens = line.Split(' ');
             *                     if (tokens.Length < 2)
             *                     {
             *                         Debug.Fail(string.Format("Invalid line: {0}", line));
             *                         continue;
             *                     }
             *
             *                     var word = tokens[0];
             *                     var tag = tokens[2];
             *
             *                     _wordTagTab[word] = tag;
             *                 }
             *             }
             *             catch (IOException e)
             *             {
             *                 Debug.Fail(string.Format("Word tag table load failure, reason: {0}", e.Message));
             *             }
             *             catch (FormatException fe)
             *             {
             *                 Debug.Fail(fe.Message);
             *             }
             */
        }
        private Segmenter seg;
        public TagSegment(){
            this(new Segmenter());
        }
        public TagSegment(Segmenter seg){
            this.seg=seg;
        }
        private void checkNewUserWordTags()
        {
            if (!seg.userTag.isEmpty())            {
                WordTag.putAll(seg.userTag);
                seg.userTag.clear();
            }
        }
        record Tag(String w,String t){}
        List<Tag> cut(String text, boolean hmm) {
            checkNewUserWordTags();
            var blocks = RegexChineseInternal.split(text);
            Function<String, List<Tag>> mth = hmm?this::dagHmm:this::dag;
            var tokens = new ArrayList<Tag>();
            var chs=RegexChineseInternal.asPredicate();
            var sk=RegexSkipInternal.asPredicate();
            for (var blk : blocks){
                if (chs.test(blk))
                    tokens.addAll(mth.apply(blk));
                else   {
                    var tmp = RegexSkipInternal.split(blk);
                    for (var x : tmp)   {
                        if (sk.test(x))
                            tokens.add(new Tag(x, "x"));
                        else x.chars().forEach(c->{
                            if(Character.isDigit(c)){
                                tokens.add(new Tag(Character.toString(c), "m"));
                            }else    if(Character.isAlphabetic(c)){
                                tokens.add(new Tag(Character.toString(c), "eng"));
                            }else{
                                tokens.add(new Tag(Character.toString(c), "x"));
                            }
                        });

                }
            }

        }
            return tokens;
        }
        List<Tag> dag(String s){
            return seg.dag(s,(i,x)->new Tag(x,WordTag.getOrDefault(x,"x")));
            }
            List<Tag> dagHmm(String s){

            }
    }
    final class WordDictionary {
        static final Lazy<WordDictionary> INSTANCE = new Lazy<>(WordDictionary::new);
        Map<String, Integer> trie;
        long total;

        private WordDictionary() {
            load();
        }

        static void load() {
            //!TODO load dictionary
        }

        boolean contains(String word) {
            return trie.containsKey(word) && trie.get(word) > 0;
        }

        int frequency(String key) {
            var n = trie.getOrDefault(key, 1);
            return n <= 0 ? 1 : n;
        }

        void add(String word, int freq) { //,@Nullable String tag
            if (contains(word)) {
                total -= trie.get(word);
            }
            trie.put(word, freq);
            total += freq;
            for (int i = 0; i < word.length(); i++) {
                var wf = word.substring(0, i + 1);
                if (!trie.containsKey(wf)) {
                    trie.put(wf, 0);
                }
            }
        }

        void delete(String word) {
            add(word, 0);
        }

        int suggest(String word, List<String> segments) {
            var freq = 1L;
            for (var s : segments) {
                freq *= frequency(s);
            }
            return (int) Math.max(freq * total + 1, frequency(word));
        }
    }

    enum Mode {
        INDEX, SEARCH
    }

    final class Segmenter {
        static final class FinalTokenizer {
            static final Lazy<FinalTokenizer> INSTANCE = new Lazy<>(FinalTokenizer::new);
            char[] STATES = {'B', 'M', 'E', 'S'};
            Pattern CHINESE = Pattern.compile("([\\u4E00-\\u9FD5]+)");
            Pattern SKIP = Pattern.compile("([a-zA-Z0-9]+(?:\\.\\d+)?%?)");

            record Pair(char key, @With double freq) {}

            static final CharObjectMap<Prob> emitProb = new CharObjectHashMap<>();
            static final CharObjectMap<Prob> transProb = new CharObjectHashMap<>();
            static final Prob startProb = new Prob();
            static final CharObjectMap<char[]> prevStatus = new CharObjectHashMap<>();

            private FinalTokenizer() {
                load();
            }

            private void load() {
                prevStatus.clear();
                prevStatus.put('B', new char[]{'E', 'S'});
                prevStatus.put('M', new char[]{'M', 'B'});
                prevStatus.put('S', new char[]{'S', 'E'});
                prevStatus.put('E', new char[]{'B', 'M'});
                startProb.clear();

                startProb.put('B', -0.26268660809250016);
                startProb.put('E', -3.14e+100);
                startProb.put('M', -3.14e+100);
                startProb.put('S', -1.4652633398537678);
                //!TODO load files may serialize with binary format

            }

            private List<String> viterbiCut(String sentence) {
                var v = new ArrayList<Prob>();
                var path = new CharObjectHashMap<Node>();
                v.add(new Prob());
                for (var state : STATES) {
                    var emp = emitProb.get(state).get(sentence.charAt(0));
                    v.get(0).put(state, startProb.get(state) + emp);
                    path.put(state, new Node(state, null));
                }
                for (int i = 1; i < sentence.length(); i++) {
                    var c = sentence.charAt(i);
                    var p = new Prob();
                    v.add(p);
                    var ps = new CharObjectHashMap<Node>();
                    for (var state : STATES) {
                        var emp = emitProb.get(state).get(c);
                        var candidate = new Pair('\0', Double.MIN_VALUE);
                        for (var entry : prevStatus.entries()) {
                            var key = entry.key();
                            var tp = transProb.get(key).get(state);
                            tp = v.get(i - 1).get(key) + tp + emp;
                            if (candidate.freq <= tp) {
                                candidate = new Pair(key, tp);
                            }
                        }
                        p.put(state, candidate.freq);
                        ps.put(state, new Node(state, path.get(candidate.key)));
                    }
                    path = ps;
                }

                var pos = new char[sentence.length()];
                {
                    var pE = v.get(v.size() - 1).get('E');
                    var pS = v.get(v.size() - 1).get('S');
                    var fp = pE < pS ? path.get('S') : path.get('E');
                    var x = 0;
                    while (fp != null) {
                        pos[x] = fp.key;
                        fp = fp.parent;
                        x++;
                    }
                }
                var tk = new ArrayList<String>();
                var b = 0; var n = 0;
                var len = sentence.length() - 1;
                for (int i = 0; i <= len; i++) {
                    switch (pos[len - i]) {
                        case 'B' -> b = i;
                        case 'E' -> {
                            tk.add(sentence.substring(b, i + 1));
                            n = i + 1;
                        }
                        case 'S' -> {
                            tk.add(sentence.substring(i, i + 1));
                            n = i + 1;
                        }
                    }

                }
                if (n <= len) tk.add(sentence.substring(n));
                return tk;
            }

            public List<String> cut(String sentence) {
                var tokens = new ArrayList<String>();
                var isChinese = CHINESE.asMatchPredicate();
                for (var blk : CHINESE.split(sentence)) {
                    if (isChinese.test(blk)) {
                        tokens.addAll(viterbiCut(blk));
                    } else {
                        var segments = Arrays.stream(SKIP.split(blk)).filter(seg -> seg == null || seg.isBlank()).toList();
                        tokens.addAll(segments);
                    }
                }
                return tokens;
            }
        }
        static final WordDictionary wd = WordDictionary.INSTANCE.get();
        static final FinalTokenizer ft = FinalTokenizer.INSTANCE.get();
        static final Set<String> loaded = new HashSet<>();
        static final Pattern RegexChineseDefault = Pattern.compile("([\u4E00-\u9FD5a-zA-Z0-9+#&._%·\\-]+)");
        static final Pattern RegexSkipDefault = Pattern.compile("(\r\n|\s)");
        static final Pattern RegexChineseCutAll = Pattern.compile("([\u4E00-\u9FD5]+)");
        static final Pattern RegexSkipCutAll = Pattern.compile("[^a-zA-Z0-9+#\n]");
        static final Pattern RegexEnglishChars = Pattern.compile("[a-zA-Z0-9]");
        static final Pattern RegexUserDict = Pattern.compile("^(?<word>.+?)(?<freq> [0-9]+)?(?<tag> [a-z]+)?$");
        Map<String, String> userTag = new HashMap<>();

        public List<String> cutHMM(String text, boolean hmm) {return cut(text, false, hmm);}

        public List<String> cutAll(String text, boolean all) {return cut(text, false, true);}

        public List<String> cut(String text) {return cut(text, false, true);}

        public List<String> cut(String text, boolean all, boolean hmm) {
            var chs = all ? RegexChineseCutAll : RegexChineseDefault;
            var skp = all ? RegexSkipCutAll : RegexSkipDefault;
            Function<String, List<String>> mth = all ? this::all : hmm ? this::dagHmm : this::dag;
            return exec(text, mth, chs, skp, all);
        }

        public List<String> cutForSearch(String text, boolean hmm) {
            var r = new ArrayList<String>();
            var ws = cut(text, false, hmm);
            for (var w : ws) {
                var wd = w.length();
                if (wd > 2) {
                    for (int i = 0; i < wd - 1; i++) {
                        var g = w.substring(i, i + 2);
                        if (Segmenter.wd.contains(g)) r.add(g);
                    }
                }
                if (wd > 3) {
                    for (int i = 0; i < wd - 2; i++) {
                        var g = w.substring(i, i + 3);
                        if (Segmenter.wd.contains(g)) r.add(g);
                    }
                }
                r.add(w);
            }
            return r;
        }

        List<String> exec(String text, Function<String, List<String>> mth, Pattern chs, Pattern skp, boolean all) {
            var out = new ArrayList<String>();
            var blk = chs.split(text);
            var ch = chs.asMatchPredicate();
            var sk = skp.asMatchPredicate();
            for (var s : blk)
                if (!s.isBlank())
                    if (ch.test(s))
                        out.addAll(mth.apply(s));
                    else
                        for (var x : skp.split(s))
                            if (sk.test(x))
                                out.add(x);
                            else if (all)
                                out.addAll(x.chars().mapToObj(Character::toString).toList());
                            else
                                out.add(x);

            return out;
        }

        List<String> all(String text) {
            var dag = dagOf(text);
            var words = new ArrayList<String>();
            var last = new int[]{-1};
            dag.forEach((k, next) -> {
                if (next.size() == 1 && k > last[0]) {
                    words.add(text.substring(k, next.get(0) + 1));
                    last[0] = next.get(0);
                } else {
                    for (int x : next) {
                        if (x > k) {
                            words.add(text.substring(k, x + 1));
                            last[0] = x;
                        }
                    }
                }
            });
            return words;
        }

        List<String> dag(String text) {
            return dag(text,(i,s)->s);
        }
       <T>List<T> dag(String text, BiFunction<Integer,String,T> fn) {
            var dag = dagOf(text);
            var route = calc(text, dag);
            var words = new ArrayList<T>();
            var x = 0;
            var n = text.length();
            var y = -1;
            var buf = new StringBuilder();
            var en = RegexEnglishChars.asMatchPredicate();
            while (x < n) {
                y = route.get(x).key + 1;
                var w = text.substring(x, y);
                if (en.test(w) && w.length() == 1) {
                    buf.append(w);
                } else {
                    if (!buf.isEmpty()) {
                        words.add(fn.apply(0,buf.toString()));
                        buf.setLength(0);
                    }
                    words.add(fn.apply(1,w));
                }
                x = y;
            }
            if (!buf.isEmpty()) {
                words.add(fn.apply(2,buf.toString()));
            }
            return null;
        }

        static <T> void hmm(StringBuilder buf, List<T> words,BiFunction<Integer,String,T> fn) {
            if (buf.length() == 1) words.add(fn.apply(1,buf.toString()));
            else {
                var t = buf.toString();
                if (!wd.contains(t)) {
                    var tk = ft.cut(t);
                    words.addAll(tk.stream().map(x->fn.apply(2,x)).toList());
                } else {
                    words.addAll(t.chars().mapToObj(Character::toString).map(x->fn.apply(3,x)).toList());
                }
            }
        }
        List<String> dagHmm(String text){
            return dagHmm(text,(i,s)->s);
        }
       <T> List<T> dagHmm(String text,BiFunction<Integer,String,T> fn) {
            var dag = dagOf(text);
            var route = calc(text, dag);
            var words = new ArrayList<T>();
            var x = 0;
            var n = text.length();
            var buf = new StringBuilder();
            while (x < n) {
                var y = route.get(x).key + 1;
                var w = text.substring(x, y);
                if (y - x == 1) buf.append(w);
                else {
                    if (!buf.isEmpty()) {
                        hmm(buf, words,fn);
                        buf.setLength(0);
                    }
                    words.add(fn.apply(0,w));
                }
                x = y;
            }
            if (!buf.isEmpty()) {
                hmm(buf, words,fn);
            }
            return null;
        }

        IntObjectMap<List<Integer>> dagOf(String s) {
            var dag = new IntObjectHashMap<List<Integer>>();
            var trie = wd.trie;
            var n = s.length();
            for (int i = 0; i < n; i++) {
                var t = new ArrayList<Integer>();
                var x = i;
                var frag = s.substring(i, 1);
                while (x < n && trie.containsKey(frag)) {
                    if (trie.getOrDefault(frag, 0) > 0) {
                        t.add(x);
                    }
                    x++;
                    if (x < n) frag = s.substring(i, x + 1 + i);
                }
                if (t.isEmpty()) t.add(i);
                dag.put(i, t);
            }
            return dag;
        }

        record Freq(int key, double freq) {}

        IntObjectMap<Freq> calc(String s, IntObjectMap<List<Integer>> dag) {
            var n = s.length();
            var r = new IntObjectHashMap<Freq>();
            r.put(n, new Freq(0, 0.0));
            var total = Math.log(wd.total);
            for (int i = n - 1; i > -1; i--) {
                var candidate = new Freq(-1, Double.MIN_VALUE);
                for (var x : dag.get(i)) {
                    var freq = Math.log(wd.frequency(s.substring(i, x + 1 + i))) - total + r.get(x + 1).freq;
                    if (candidate.freq < freq) {
                        candidate = new Freq(x, freq);
                    }
                }
                r.put(i, candidate);
            }
            return r;
        }

        @SneakyThrows
        public void withUserDict(Path file) {
            if (loaded.contains(file.toString())) return;
            Files.readAllLines(file)
                 .stream()
                 .filter(Predicate.not(String::isBlank))
                 .forEach(line -> {
                     var tokens = RegexUserDict.matcher(line.trim());
                     var word = tokens.group("word").trim();
                     var freq = tokens.group("freq").trim();
                     var tag = tokens.group("tag").trim();
                     addWord(word, freq.isBlank() ? 0 : Integer.parseInt(freq), tag);
                 });
            loaded.add(file.toString());
        }

        public void addWord(String word, int freq, @Nullable String tag) {
            if (freq <= 0) {
                freq = wd.suggest(word, cutHMM(word, false));
            }
            wd.add(word, freq);
            if (tag != null && !tag.isBlank()) {
                userTag.put(word, tag);
            }
        }

        public void deleteWord(String word) {
            wd.delete(word);
        }

        public List<Token> tokenize(String text, Mode mode, boolean hmm) {
            var r = new ArrayList<Token>();
            var s = 0;
            switch (mode) {
                case INDEX -> {
                    for (var w : cut(text, false, hmm)) {
                        var wd = w.length();
                        r.add(new Token(w, s, s + wd));
                        s += wd;
                    }
                }
                case SEARCH -> {
                    for (var w : cut(text, false, hmm)) {
                        var wd = w.length();
                        if (wd > 2) {
                            for (int i = 0; i < wd - 1; i++) {
                                var g = w.substring(i, i + 2);
                                if (Segmenter.wd.contains(g)) r.add(new Token(g, s + i, s + i + 2));
                            }
                        }
                        if (wd > 3) {
                            for (int i = 0; i < wd - 2; i++) {
                                var g = w.substring(i, i + 3);
                                if (Segmenter.wd.contains(g)) r.add(new Token(g, s + i, s + i + 3));
                            }
                        }
                        r.add(new Token(w, s, s + wd));
                        s += wd;
                    }
                }
            }
            return r;
        }

    }

    record Token(String w, int start, int end) {}

    class Lazy<T> {
        private Supplier<T> su;
        private volatile T v;

        Lazy(Supplier<T> su) {
            this.su = su;
        }

        public T get() {
            if (v == null) {
                synchronized (this) {
                    if (v == null) {
                        v = su.get();
                        su = null;
                    }
                }
            }
            return v;
        }


    }
}
