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
sealed public interface Cases permits
        Cases.Camel,
        Cases.Kebab,
        Cases.LowerQualified,
        Cases.Pascal,
        Cases.Qualified,
        Cases.Raw,
        Cases.UpperKebab,
        Cases.Snake,
        Cases.UpperQualified,
        Cases.UpperSnake {
    Cases RAW = new Raw();
    Cases CAMEL = new Camel();
    Cases LOWER_CAMEL = CAMEL;
    Cases PASCAL = new Pascal();
    Cases UPPER_CAMEL = PASCAL;

    Cases SCREAMING_KEBAB = new UpperKebab();
    Cases UPPER_KEBAB = SCREAMING_KEBAB;
    Cases KEBAB = new Kebab();
    Cases LOWER_KEBAB = KEBAB;
    Cases UPPER_SNAKE = new UpperSnake();
    Cases SNAKE = new Snake();
    Cases LOWER_SNAKE = SNAKE;
    Cases QUALIFIED = new Qualified();
    Cases LOWER_QUALIFIED = new LowerQualified();
    Cases UPPER_QUALIFIED = new UpperQualified();
    Map<String, Cases> CASES = Map.ofEntries(
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
    Map<Class<? extends Cases>, Cases> CLASS_CASES = Map.ofEntries(
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

    default String to(Cases dest, String name) {
        if (this == dest) return name;
        return dest.format(parse(name));
    }

    final class Raw implements Cases {


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
    final class Camel implements Cases {


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
    final class Pascal implements Cases {


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
            return Cases.split(name, "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        }
    }

    /**
     * UPPER-CASE-WORDS
     */
    final class UpperKebab implements Cases {

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
            return Cases.split(name, "\\-");
        }
    }

    /**
     * lower-case-words
     */
    final class Kebab implements Cases {
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
    final class UpperSnake implements Cases {
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
            return Cases.split(name, "\\_");
        }
    }

    /**
     * lower_case_words
     */
    final class Snake implements Cases {
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
    final class Qualified implements Cases {
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
    final class LowerQualified implements Cases {


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
    final class UpperQualified implements Cases {
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

    UnaryOperator<String> cam2pas = s -> Cases.CAMEL.to(Cases.PASCAL, s);
    UnaryOperator<String> pas2cam = s -> Cases.PASCAL.to(Cases.CAMEL, s);
    UnaryOperator<String> cam2snk = s -> Cases.CAMEL.to(Cases.SNAKE, s);
    UnaryOperator<String> cam2scn = s -> Cases.CAMEL.to(Cases.UPPER_SNAKE, s);
    UnaryOperator<String> usn2cam = s -> Cases.UPPER_SNAKE.to(Cases.CAMEL, s);
    UnaryOperator<String> usn2pas = s -> Cases.UPPER_SNAKE.to(Cases.PASCAL, s);

}
