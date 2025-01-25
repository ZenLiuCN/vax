package vax.common.units;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Zen.Liu
 * @since 2025-01-12
 */
public interface Inference {
    record PkgEntry(
            int index,
            String remains
    ) {}

    record FieldEntry(
            String name,
            Type type
    ) {
        public static final IntObjectMap<String> PACKAGES = new IntObjectHashMap<>();

        static {
            PACKAGES.put(0, "java.lang");
            PACKAGES.put(1, "java.time");
            PACKAGES.put(2, "java.util.collection");
            PACKAGES.put(3, "io.netty.util.collection");
            PACKAGES.put(4, "org.jooq.lambda.tuple");
        }

        public static Optional<String> registeredPackage(int v) {
            return Optional.ofNullable(PACKAGES.get(v));
        }

        public static Optional<Integer> register(String v) {
            if (PACKAGES.containsValue(v)) return Optional.empty();
            var n = PACKAGES.size();
            PACKAGES.put(n, v);
            return Optional.of(n);
        }

        public static Optional<PkgEntry> simplify(String pkg) {
            var i = pkg.lastIndexOf('.');
            var p = pkg;
            var values = PACKAGES.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            while (i > 0) {
                p = p.substring(0, i);
                if (values.containsKey(p))
                    return Optional.of(new PkgEntry(values.get(p), pkg.replace(p, "")));
                i = p.lastIndexOf('.');
            }
            return Optional.empty();
        }

        public static Optional<FieldEntry> extract(String fieldName) {
            var i = fieldName.lastIndexOf(TYPE_DIVIDER);
            if (i < 0) return Optional.empty();
            var t = fieldName.substring(i + 2);
            if (t.contains(TYPE_SPLITTER)) t = t.substring(0, t.indexOf(TYPE_SPLITTER));
            var n = Case.usn2cam.apply(fieldName.substring(0, i));
            return Optional.of(new FieldEntry(n, resolve(t)));
        }

        public static Class<?> resolve(String type) {
            return switch (type) {
                case SUFFIX_BOOL -> boolean.class;
                case SUFFIX_I8 -> byte.class;
                case SUFFIX_I16 -> short.class;
                case SUFFIX_I32 -> int.class;
                case SUFFIX_I64 -> long.class;
                case SUFFIX_F32 -> float.class;
                case SUFFIX_F64 -> double.class;
                default -> switch (type.charAt(0)) {
                    case TYPE_ARRAY -> Reflect.array(resolve(type.substring(1)));
                    case TYPE_PKG -> {
                        var x = 0;
                        var i = 1;
                        for (i = 1; i < type.length(); i++) {
                            var c = type.charAt(i);
                            if (c >= '0' && c <= '9') x = x * 10 + (c - '0');
                            else break;
                        }
                        var pkg = PACKAGES.get(x);
                        if (pkg == null) throw new IllegalStateException("missing package in registry of " + x);
                        yield Reflect.forName(pkg + '.' + type.substring(i).replaceAll("_", "."));
                    }
                    default -> Reflect.forName(type.substring(1).replaceAll("_", "."));
                };
            };
        }

        public static String combine(Type type) {
            if (type instanceof Class<?> t) {
                if (t.isPrimitive())
                    return Objects.requireNonNull(
                            type == byte.class ? SUFFIX_I8
                                    : type == short.class ? SUFFIX_I16
                                    : type == int.class ? SUFFIX_I32
                                    : type == long.class ? SUFFIX_I64
                                    : type == float.class ? SUFFIX_F32
                                    : type == double.class ? SUFFIX_F64
                                    : type == boolean.class ? SUFFIX_BOOL
                                    : null
                            , "not supported primitive type");
                if (t.isArray()) return TYPE_ARRAY + combine(t.componentType());
                var p=t.getName();
                var i=p.lastIndexOf('.');
                var n=p.substring(i+1);
                var pkg=p.substring(0,i);
                return simplify(pkg).map(x->TYPE_PKG+""+x.index).map(x->x+n)
                        .orElseGet(()-> pkg.replaceAll("\\.","_")+"_"+n);
            }
            if (type instanceof ParameterizedType t) {
                return combine(t.getOwnerType()) + TYPE_SPLITTER + Arrays.stream(t.getActualTypeArguments())
                        .map(FieldEntry::combine).collect(Collectors.joining(TYPE_DELIMITER));
            }
            return "";//TODO
        }

        public String build() {
            return Case.cam2scn.apply(name) + TYPE_DIVIDER + combine(type);
        }

    }

    String TYPE_SPLITTER = "$$";
    String TYPE_DELIMITER = "$$$";
    char TYPE_ARRAY = '$';
    char TYPE_PKG = '_';

    String PREFIX_FIELD = "FIELD_";
    String TYPE_DIVIDER = "_$";
    String SUFFIX_I8 = "I8";
    String SUFFIX_I16 = "I16";
    String SUFFIX_I32 = "I32";
    String SUFFIX_I64 = "I64";
    String SUFFIX_F32 = "F32";
    String SUFFIX_F64 = "F64";
    String SUFFIX_BOOL = "BOOL";


    /**
     * contract static function <br/>
     * purify= JsonObject->JsonObject
     */
    String CONTRACT_PURIFY = "purify";

}
