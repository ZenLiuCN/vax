package cn.zenliu.vax.common.meta;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static cn.zenliu.vax.common.meta.Meta.Native.*;

/**
 * <ul>
 *     <li>identity(id): long type of an universal identifier of combined a int domain id and a int identity code</li>
 * </ul>
 *
 * @author Zen.Liu
 * @since 2024-10-13
 */
public interface Meta {
    interface Builtin {
        long BUILTIN_IDENTIFIER_ANY = 4294967295L;
        int BUILTIN_DOMAIN_NATIVES = -1;
        int BUILTIN_NATIVE_VOID = -1;
        int BUILTIN_NATIVE_BOOLEAN = 0;

        int BUILTIN_NATIVE_I1 = 10;
        int BUILTIN_NATIVE_I2 = 11;
        int BUILTIN_NATIVE_I4 = 12;
        int BUILTIN_NATIVE_I8 = 13;

        int BUILTIN_NATIVE_F4 = 20;
        int BUILTIN_NATIVE_F8 = 21;
        int BUILTIN_NATIVE_DEC = 22;

        int BUILTIN_NATIVE_BIN = 30;
        int BUILTIN_NATIVE_BLOB = 31;
        int BUILTIN_NATIVE_STR = 32;

        int BUILTIN_NATIVE_TIMESTAMP = 40;
        int BUILTIN_NATIVE_TIME = 41;
        int BUILTIN_NATIVE_DATE = 42;
        int BUILTIN_NATIVE_DATETIME = 43;

        int BUILTIN_NATIVE_JSON_OBJECT = 50;
        int BUILTIN_NATIVE_JSON_ARRAY = 51;

        int BUILTIN_NATIVE_TUPLE = 60;
        int BUILTIN_NATIVE_ARRAY = 61;
        String INFO_KEY = "k";
        String INFO_VALUE = "v";
        Native VOID = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_VOID).combine(),
                NATIVE_MODE_VALUE,
                "void",
                "nothing",
                JsonObject.of());
        Native BOOL = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_BOOLEAN).combine(),
                NATIVE_MODE_VALUE,
                "bool",
                "one byte of boolean value",
                JsonObject.of());
        Native I1 = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_I1).combine(),
                NATIVE_MODE_VALUE,
                "int8",
                "one byte signed integer.",
                JsonObject.of());
        Native I2 = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_I2).combine(),
                NATIVE_MODE_VALUE,
                "int16",
                "two byte signed integer.",
                JsonObject.of());
        Native I4 = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_I4).combine(),
                NATIVE_MODE_VALUE,
                "int32",
                "four byte signed integer.",
                JsonObject.of());
        Native I8 = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_I8).combine(),
                NATIVE_MODE_VALUE,
                "int64",
                "eight byte signed integer.",
                JsonObject.of());

        Native F4 = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_F4).combine(),
                NATIVE_MODE_VALUE,
                "float32",
                "four byte signed float point number.",
                JsonObject.of());
        Native F8 = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_F8).combine(),
                NATIVE_MODE_VALUE,
                "float64",
                "eight byte signed float point number.",
                JsonObject.of());
        Native DEC = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_DEC).combine(),
                NATIVE_MODE_VALUE,
                "decimal",
                "a number for precision value.",
                JsonObject.of());

        Native BIN = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_BIN).combine(),
                NATIVE_MODE_VALUE,
                "binary",
                "short binary data",
                JsonObject.of());
        Native BLOB = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_BLOB).combine(),
                NATIVE_MODE_VALUE,
                "blob",
                "large binary data",
                JsonObject.of());
        Native STRING = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_STR).combine(),
                NATIVE_MODE_VALUE,
                "string",
                "UTF8 encoded text",
                JsonObject.of());

        Native TIMESTAMP = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_TIMESTAMP).combine(),
                NATIVE_MODE_VALUE,
                "timestamp",
                "UTC timestamp",
                JsonObject.of());
        Native TIME = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_TIME).combine(),
                NATIVE_MODE_VALUE,
                "time",
                "time",
                JsonObject.of());
        Native DATE = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_DATE).combine(),
                NATIVE_MODE_VALUE,
                "date",
                "date",
                JsonObject.of());
        Native DATETIME = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_DATETIME).combine(),
                NATIVE_MODE_VALUE,
                "datetime",
                "time and date without timezone",
                JsonObject.of());
        Native JSON_OBJECT = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_JSON_OBJECT).combine(),
                NATIVE_MODE_LINK | NATIVE_MODE_REPEAT,
                "jsonObject",
                "string to any projections",
                JsonObject.of(
                        INFO_KEY, STRING.id,
                        INFO_VALUE, BUILTIN_IDENTIFIER_ANY
                ));
        Native JSON_ARRAY = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_JSON_ARRAY).combine(),
                NATIVE_MODE_REPEAT,
                "jsonArray",
                "any repeats",
                JsonObject.of(
                        "value", BUILTIN_IDENTIFIER_ANY
                ));
        Native ARRAY = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_ARRAY).combine(),
                NATIVE_MODE_REPEAT,
                "array",
                "any of same type repeats",
                JsonObject.of(
                        INFO_VALUE, BUILTIN_IDENTIFIER_ANY
                ));
        Native TUPLE = new Native(new Identifier(BUILTIN_DOMAIN_NATIVES, BUILTIN_NATIVE_TUPLE).combine(),
                NATIVE_MODE_REPEAT,
                "tuple",
                "any repeats",
                JsonObject.of(
                        INFO_VALUE, BUILTIN_IDENTIFIER_ANY
                ));

    }


    //region def

    /**
     * @return the meta identity
     */
    long id();

    /**
     * @return identifier that parsed
     */
    default Identifier identifier() {
        return Identifier.parse(id());
    }

    int KIND_NATIVE = 0;
    int KIND_SOME = 1;
    int KIND_ACTION = 2;
    int KIND_ENTITY = 3;
    int KIND_DOMAIN = 4;

    /**
     * @return kind of the meta
     */
    @MagicConstant(valuesFromClass = Meta.class)
    int kind();

    /**
     * @return mode for each kind
     */
    int mode();

    /**
     * @return the name for human.
     */
    String name();

    /**
     * @return the descriptions of the element
     */
    String comment();

    /**
     * Only {@link Entity} and {@link Domain} have natives.
     *
     * @return the native definition (class name)
     */
    default @Nullable String natives() {
        return null;
    }

    JsonObject info();


    default JsonObject toJson() {
        return JsonObject.of(
                "id", id(),
                "identifier", identifier().toJson()
                , "mode", mode()
                , "name", name()
                , "comment", comment()
                , "natives", natives()
                , "info", info()
        );
    }
    //endregion

    /**
     * individual data types
     *
     * @param id   the id.
     * @param mode the mode.
     * @param name the name.
     * @param info the extra data that relative to mode.
     */
    record Native(
            long id,
            @MagicConstant(flagsFromClass = Native.class) int mode,
            String name,
            String comment,
            JsonObject info
    ) implements Meta {
        /**
         * an individual data
         */
        public static int NATIVE_MODE_VALUE = 1;
        /**
         * an enumeration
         */
        public static int NATIVE_MODE_ENUM = 1 << 2;
        /**
         * a container type that can repeat one kind of value
         */
        public static int NATIVE_MODE_REPEAT = 1 << 3;
        /**
         * a container type that connect one to another.(Pair or Entry)
         */
        public static int NATIVE_MODE_LINK = 1 << 4;
        /**
         * a container type with many types of values
         */
        public static int NATIVE_MODE_UNION = 1 << 5;


        @Override
        public int kind() {
            return KIND_NATIVE;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Native n && n.id == id;
        }

    }

    /**
     * one Some always references a Native or an Entity or an Action.
     * <ul>
     *     <li>a Property of an Entity</li>
     *     <li>a Consumable of a Action</li>
     *     <li>a Product of a Action</li>
     * </ul>
     *
     * @param id   the id of the referenced element
     * @param name the name of property
     * @param mode the modifier
     */
    record Some(
            long id,
            String name,
            String comment,
            @MagicConstant(flagsFromClass = Some.class) int mode,
            JsonObject info
    ) implements Meta {
        /**
         * always exists or optional
         */
        public static final int SOME_MODE_MAYBE = 1;
        /**
         * the property should be indexed
         */
        public static final int SOME_MODE_INDEX = 1 << 1;


        @Override
        public int kind() {
            return KIND_SOME;
        }
    }

    /**
     * An action of a domain or method of an Entity.
     *
     * @param id       (domain,id) or form of (entity,id)
     * @param name     the name
     * @param mode     the modifier
     * @param consumes the consumes of Some
     * @param product  the product
     * @param info     the extra information
     */
    record Action(
            long id,
            @MagicConstant(valuesFromClass = Action.class) int mode,
            String name,
            String comment,
            JsonObject info,
            List<Some> consumes,
            Some product
    ) implements Meta {
        /**
         * a normal action that invokes by others.
         */
        public static final int ACTION_MODE_NORMAL = 0;
        /**
         * this action listen to an event.
         */
        public static final int ACTION_MODE_LISTENER = 1;
        /**
         * An action that spawn or destroy an Actor Entity which have actions to directly use.<br/>
         * The signature must be like (long domain,long actor, int spawnFlag,JsonObject payload):string.
         */
        public static final int ACTION_MODE_SPAWN = 2;

        @Override
        public int kind() {
            return KIND_ACTION;
        }

        @Override
        public JsonObject toJson() {
            var j = Meta.super.toJson();
            if (consumes != null && !consumes.isEmpty()) {
                var p = new JsonArray();
                consumes.forEach(v -> p.add(v.toJson()));
                j.put("consumes", p);
            }
            if (product != null) {
                j.put("product", product.toJson());
            }
            return j;
        }
    }

    /**
     * An entity present an Existence or an Event.
     *
     * @param id         (domain,id)
     * @param mode       the modifier
     * @param name       the name
     * @param properties properties of the entity
     * @param actions    actions of  the entity
     */
    record Entity(
            long id,
            @MagicConstant(valuesFromClass = Entity.class) int mode,
            String name,
            String comment,
            String natives,
            JsonObject info,
            List<Some> properties,
            List<Action> actions
    ) implements Meta {
        /**
         * a normal entity
         */
        public static final int ENTITY_MODE_EXISTENCE = 0;
        /**
         * an actor entity
         */
        public static final int ENTITY_MODE_ACTOR = 1;
        /**
         * an event
         */
        public static final int ENTITY_MODE_EVENT = 2;

        @Override
        public int kind() {
            return KIND_ENTITY;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Native n && n.id == id;
        }

        @Override
        public JsonObject toJson() {
            var j = Meta.super.toJson();
            if (properties != null && !properties.isEmpty()) {
                var p = new JsonArray();
                properties.forEach(v -> p.add(v.toJson()));
                j.put("properties", p);
            }
            if (actions != null && !actions.isEmpty()) {
                var p = new JsonArray();
                actions.forEach(v -> p.add(v.toJson()));
                j.put("actions", p);
            }
            return j;
        }
    }

    /**
     * Domain:
     * <ul>
     *     <li> identifier always a classifier id of zero.</li>
     *     <li> maybe have group of actions, witch makes it have a Domain System instance.</li>
     * </ul>
     *
     * @param id      (0, id)
     * @param mode
     * @param name
     * @param actions
     */
    record Domain(
            long id,
            int mode,
            String name,
            String comment,
            String natives,
            JsonObject info,
            List<Action> actions
    ) implements Meta {
        @Override
        public int kind() {
            return KIND_DOMAIN;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Native n && n.id == id;
        }

        @Override
        public JsonObject toJson() {
            var j = Meta.super.toJson();
            if (actions == null || actions.isEmpty()) return j.put("actions", JsonArray.of());
            var r = JsonArray.of();
            actions.forEach(x -> r.add(x.toJson()));
            j.put("actions", r);
            return j;
        }
    }

    record Identifier(int classifier, int identity) {
        public static final Identifier ANY = new Identifier(0, -1);

        public static void main(String[] args) {
            System.out.println(ANY);
            System.out.println(Long.toString(ANY.combine(), 16));
        }

        @Override
        public String toString() {
            return (classifier == 0 ? "" : Integer.toHexString(classifier)) + Integer.toHexString(identity);
        }

        public static Identifier parse(long ui) {
            return new Identifier((int) (ui >> 32), (int) ui);
        }

        public long combine() {
            return ((long) classifier << 32) | (identity & 0xFFFFFFFFL);
        }

        public Identifier fromJson(JsonObject j) {
            var c = j.getInteger("classifier");
            var i = j.getInteger("identity");
            if (c == null || i == null) return null;
            return new Identifier(c, i);
        }

        public JsonObject toJson() {
            return JsonObject.of("classifier", classifier, "identity", identity);
        }
    }

}
