package cn.zenliu.vax.common.units;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Case converter
 * @author Zen.Liu
 */
sealed public interface CaseX permits
        CaseX.Camel,
        CaseX.Kebab,
        CaseX.LowerQualified,
        CaseX.Pascal,
        CaseX.Qualified,
        CaseX.Raw,
        CaseX.UpperKebab,
        CaseX.Snake,
        CaseX.UpperQualified,
        CaseX.UpperSnake {
    CaseX RAW = new Raw();
    CaseX CAMEL = new Camel();
    CaseX LOWER_CAMEL = CAMEL;
    CaseX PASCAL = new Pascal();
    CaseX UPPER_CAMEL = PASCAL;

    CaseX SCREAMING_KEBAB = new UpperKebab();
    CaseX UPPER_KEBAB = SCREAMING_KEBAB;
    CaseX KEBAB = new Kebab();
    CaseX LOWER_KEBAB = KEBAB;
    CaseX UPPER_SNAKE = new UpperSnake();
    CaseX SNAKE = new Snake();
    CaseX LOWER_SNAKE = SNAKE;
    CaseX QUALIFIED = new Qualified();
    CaseX LOWER_QUALIFIED = new LowerQualified();
    CaseX UPPER_QUALIFIED = new UpperQualified();
    Map<String, CaseX> CASES = Map.ofEntries(
            Map.entry("RAW_CASE", RAW),
            Map.entry("UPPER_QUALIFIED_CASE", UPPER_QUALIFIED),
            Map.entry("CAMEL_CASE", CAMEL),
            Map.entry("LOWER_CAMEL_CASE", LOWER_CAMEL),
            Map.entry("PASCAL_CASE", PASCAL),
            Map.entry("UPPER_CAMEL_CASE", UPPER_CAMEL),
            Map.entry("SCREAMING_KEBAB_CASE", SCREAMING_KEBAB),
            Map.entry("UPPER_KEBAB_CASE", UPPER_KEBAB),
            Map.entry("KEBAB_CASE", KEBAB),
            Map.entry("LOWER_KEBAB_CASE", LOWER_KEBAB),
            Map.entry("UPPER_SNAKE_CASE", UPPER_SNAKE),
            Map.entry("SNAKE_CASE", SNAKE),
            Map.entry("LOWER_SNAKE_CASE", LOWER_SNAKE),
            Map.entry("QUALIFIED_CASE", QUALIFIED),
            Map.entry("LOWER_QUALIFIED_CASE", LOWER_QUALIFIED),
            Map.entry("UPPER_QUALIFIED", UPPER_QUALIFIED)
    );
    Map<Class<? extends CaseX>, CaseX> CLASS_CASES = Map.ofEntries(
            //Map.entry(Raw.class, RAW),
            Map.entry(Qualified.class, UPPER_QUALIFIED),
            Map.entry(Camel.class, CAMEL),
            Map.entry(Pascal.class, PASCAL),
            Map.entry(UpperKebab.class, SCREAMING_KEBAB),
            Map.entry(Kebab.class, KEBAB),
            Map.entry(Snake.class, SNAKE),
            Map.entry(UpperSnake.class, LOWER_SNAKE),
            Map.entry(LowerQualified.class, LOWER_QUALIFIED),
            Map.entry(UpperQualified.class, UPPER_QUALIFIED)
    );

    static List<String> split(String s, String regex) {
        String[] words = s.split(regex);
        if (words.length == 1 && words[0].isEmpty()) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(words);
        }
    }

    String format(Iterable<String> atoms);

    List<String> parse(String name);

    default String to(CaseX dest, String name) {
        if (this == dest) return name;
        return dest.format(parse(name));
    }

    final class Raw implements CaseX {


        @Override
        public String format(Iterable<String> words) {
            var sb = new StringBuilder();
            words.forEach(sb::append);
            return sb.toString();
        }

        @Override
        public List<String> parse(String name) {
            return List.of(name);
        }
    }

    /**
     * lowerCaseWords
     */
    final class Camel implements CaseX {


        @Override
        public String format(Iterable<String> words) {
            var sb = new StringBuilder();

            for (var word : words) {
                if (!word.isEmpty()) {
                    var c = word.charAt(0);
                    if (sb.isEmpty()) {
                        sb.append(Character.toLowerCase(c));
                        sb.append(word.toLowerCase(), 1, word.length());
                        continue;
                    }
                    sb.append(Character.toUpperCase(c));
                    sb.append(word.toLowerCase(), 1, word.length());
                }
            }
            return sb.toString();
        }

        @Override
        public List<String> parse(String name) {
            return PASCAL.parse(name);
        }
    }

    /**
     * UpperCaseWords
     */
    final class Pascal implements CaseX {


        @Override
        public String format(Iterable<String> words) {
            var sb = new StringBuilder();
            for (var word : words) {
                if (!word.isEmpty()) {
                    var c = word.charAt(0);
                    if (Character.isLowerCase(c)) {
                        sb.append(Character.toUpperCase(c));
                        sb.append(word.toLowerCase(), 1, word.length());
                    } else {
                        sb.append(word.charAt(0));
                        sb.append(word.toLowerCase(), 1, word.length());
                    }
                }
            }
            return sb.toString();
        }

        @Override
        public List<String> parse(String name) {
            return CaseX.split(name, "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        }
    }

    /**
     * UPPER-CASE-WORDS
     */
    final class UpperKebab implements CaseX {

        static final Pattern pattern = Pattern.compile("(?:\\p{Alnum}|(?:(?<=\\p{Alnum})-(?=\\p{Alnum})))*");

        @Override
        public String format(Iterable<String> words) {
            var sb = new StringBuilder();
            for (var word : words) {
                if (!word.isEmpty()) {
                    if (!sb.isEmpty()) {
                        sb.append('-');
                    }
                    sb.append(word.toUpperCase());
                }
            }
            return sb.toString();
        }

        @Override
        public List<String> parse(String name) {
            if (!pattern.matcher(name).matches()) {
                throw new IllegalArgumentException("Invalid kebab case:" + name);
            }
            return CaseX.split(name, "\\-");
        }
    }

    /**
     * lower-case-words
     */
    final class Kebab implements CaseX {
        @Override
        public String format(Iterable<String> words) {
            var sb = new StringBuilder();
            for (var word : words) {
                if (!word.isEmpty()) {
                    if (!sb.isEmpty()) {
                        sb.append('-');
                    }
                    sb.append(word.toLowerCase());
                }
            }
            return sb.toString();
        }

        @Override
        public List<String> parse(String name) {
            return SCREAMING_KEBAB.parse(name);
        }
    }

    /**
     * UPPER_CASE_WORDS
     */
    final class UpperSnake implements CaseX {
        static final Pattern pattern = Pattern.compile("(?:\\p{Alnum}|(?:(?<=\\p{Alnum})_(?=\\p{Alnum})))*");

        @Override
        public String format(Iterable<String> words) {
            var sb = new StringBuilder();
            for (var word : words) {
                if (!word.isEmpty()) {
                    if (!sb.isEmpty()) {
                        sb.append('_');
                    }
                    sb.append(word.toUpperCase());
                }
            }
            return sb.toString();
        }

        @Override
        public List<String> parse(String name) {
            if (!pattern.matcher(name).matches()) {
                throw new IllegalArgumentException("Invalid snake case:" + name);
            }
            return CaseX.split(name, "\\_");
        }
    }

    /**
     * lower_case_words
     */
    final class Snake implements CaseX {
        @Override
        public String format(Iterable<String> words) {
            var sb = new StringBuilder();
            for (var word : words) {
                if (!word.isEmpty()) {
                    if (!sb.isEmpty()) {
                        sb.append('_');
                    }
                    sb.append(word.toLowerCase());
                }
            }
            return sb.toString();
        }

        @Override
        public List<String> parse(String name) {
            return UPPER_SNAKE.parse(name);
        }
    }

    /**
     * any.Case.words
     */
    final class Qualified implements CaseX {
        static final Pattern pattern = Pattern.compile("(?:\\p{Alnum}|(?:(?<=\\p{Alnum})\\.(?=\\p{Alnum})))*");

        @Override
        public String format(Iterable<String> words) {
            var sb = new StringBuilder();
            for (var word : words) {
                if (!word.isEmpty()) {
                    if (!sb.isEmpty()) {
                        sb.append('.');
                    }
                    sb.append(word);
                }
            }
            return sb.toString();
        }

        @Override
        public List<String> parse(String name) {
            if (!pattern.matcher(name).matches()) {
                throw new IllegalArgumentException("Invalid qualified case:" + name);
            }
            return split(name, "\\.");
        }

    }

    /**
     * lower.case.words
     */
    final class LowerQualified implements CaseX {


        @Override
        public String format(Iterable<String> words) {
            var sb = new StringBuilder();
            for (var word : words) {
                if (!word.isEmpty()) {
                    if (!sb.isEmpty()) {
                        sb.append('.');
                    }
                    sb.append(word.toLowerCase());
                }
            }
            return sb.toString();
        }

        @Override
        public List<String> parse(String name) {
            return QUALIFIED.parse(name);
        }

    }

    /**
     * UPPER.CASE.WORDS
     */
    final class UpperQualified implements CaseX {
        @Override
        public String format(Iterable<String> words) {
            var sb = new StringBuilder();
            for (var word : words) {
                if (!word.isEmpty()) {
                    if (!sb.isEmpty()) {
                        sb.append('.');
                    }
                    sb.append(word.toUpperCase());
                }
            }
            return sb.toString();
        }

        @Override
        public List<String> parse(String name) {
            return QUALIFIED.parse(name);
        }

    }

    UnaryOperator<String> cam2pas = s -> CaseX.CAMEL.to(CaseX.PASCAL, s);
    UnaryOperator<String> pas2cam = s -> CaseX.PASCAL.to(CaseX.CAMEL, s);
    UnaryOperator<String> cam2snk = s -> CaseX.CAMEL.to(CaseX.SNAKE, s);
    UnaryOperator<String> cam2scn = s -> CaseX.CAMEL.to(CaseX.UPPER_SNAKE, s);
    UnaryOperator<String> usn2cam = s -> CaseX.UPPER_SNAKE.to(CaseX.CAMEL, s);
    UnaryOperator<String> usn2pas = s -> CaseX.UPPER_SNAKE.to(CaseX.PASCAL, s);

}
