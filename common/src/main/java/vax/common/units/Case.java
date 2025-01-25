package vax.common.units;


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
sealed public interface Case permits
        Case.Camel,
        Case.Kebab,
        Case.LowerQualified,
        Case.Pascal,
        Case.Qualified,
        Case.Raw,
        Case.UpperKebab,
        Case.Snake,
        Case.UpperQualified,
        Case.UpperSnake {
    Case RAW = new Raw();
    Case CAMEL = new Camel();
    Case LOWER_CAMEL = CAMEL;
    Case PASCAL = new Pascal();
    Case UPPER_CAMEL = PASCAL;

    Case SCREAMING_KEBAB = new UpperKebab();
    Case UPPER_KEBAB = SCREAMING_KEBAB;
    Case KEBAB = new Kebab();
    Case LOWER_KEBAB = KEBAB;
    Case UPPER_SNAKE = new UpperSnake();
    Case SNAKE = new Snake();
    Case LOWER_SNAKE = SNAKE;
    Case QUALIFIED = new Qualified();
    Case LOWER_QUALIFIED = new LowerQualified();
    Case UPPER_QUALIFIED = new UpperQualified();
    Map<String, Case> CASES = Map.ofEntries(
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
    Map<Class<? extends Case>, Case> CLASS_CASES = Map.ofEntries(
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

    default String to(Case dest, String name) {
        if (this == dest) return name;
        return dest.format(parse(name));
    }

    final class Raw implements Case {


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
    final class Camel implements Case {


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
    final class Pascal implements Case {


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
            return Case.split(name, "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        }
    }

    /**
     * UPPER-CASE-WORDS
     */
    final class UpperKebab implements Case {

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
            return Case.split(name, "\\-");
        }
    }

    /**
     * lower-case-words
     */
    final class Kebab implements Case {
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
    final class UpperSnake implements Case {
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
            return Case.split(name, "\\_");
        }
    }

    /**
     * lower_case_words
     */
    final class Snake implements Case {
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
    final class Qualified implements Case {
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
    final class LowerQualified implements Case {


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
    final class UpperQualified implements Case {
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

    UnaryOperator<String> cam2pas = s -> Case.CAMEL.to(Case.PASCAL, s);
    UnaryOperator<String> pas2cam = s -> Case.PASCAL.to(Case.CAMEL, s);
    UnaryOperator<String> cam2snk = s -> Case.CAMEL.to(Case.SNAKE, s);
    UnaryOperator<String> cam2scn = s -> Case.CAMEL.to(Case.UPPER_SNAKE, s);
    UnaryOperator<String> usn2cam = s -> Case.UPPER_SNAKE.to(Case.CAMEL, s);
    UnaryOperator<String> usn2pas = s -> Case.UPPER_SNAKE.to(Case.PASCAL, s);

}
