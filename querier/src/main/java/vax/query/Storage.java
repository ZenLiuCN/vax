package vax.query;

import java.util.List;
import java.util.Map;

/**
 * @author Zen.Liu
 * @since 2025-03-07
 */
public abstract class Storage<T, S extends Storage<T, ?>> implements Model<T>, Model.Alias<S> {
    @Override
    public Map<String, Model.Field<?>> $fields() {
        return fields();
    }

    @Override
    public List<String> $order() {
        return fieldNames();
    }

    protected abstract S _me();

    public String name;
    public String alias;

    @Override
    public S as(String alias) {
        assert this.alias == null : "already have an alias";
        this.alias = alias;
        return _me();
    }

    public static abstract class Field<T, F extends Field<T, ?>> implements Model.Field<T>, Model.Alias<F> {
        public String name;
        public String alias;
        public Render<T> render;

        abstract F _me();

        @Override
        public F as(String alias) {
            assert this.alias == null : "already have an alias";
            this.alias = alias;
            return _me();
        }
    }

    protected abstract Field<T, ?> field(String name);

    protected abstract List<String> fieldNames();

    protected abstract Map<String, Model.Field<?>> fields();
}
