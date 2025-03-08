package vax.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zen.Liu
 * @since 2025-03-07
 */
public abstract class Dialect {
    /**
     * @param mode       current build mode
     * @param primary    the primary sequence will join with space.
     * @param secondary  the temporary secondary sequence will join with comma.
     *                   This will reset and push to primary by invoke {@link #fold()} after each sub-render method called.
     *                   Or called {@link #fold }by the sub-render method.
     * @param buffer     the temporary buffer use to render complex string sequence.
     * @param parameters the parameters hold all parameter (with name as key).
     * @param holders    the holders hold all parameter placeholder (with name as key).
     * @param renders    the renders hold all final output value renders with proper order.
     */
    public record Builder(State.MODE mode,
                          List<CharSequence> primary,
                          List<CharSequence> secondary,
                          StringBuilder buffer,
                          Map<String, Object> parameters,
                          Map<String, Value.hold<?>> holders,
                          List<List<Object>> arguments,
                          List<Render<?>> renders) {
        Builder(State.MODE mode) {
            this(mode,
                 new ArrayList<>(),
                 new ArrayList<>(),
                 new StringBuilder(),
                 new HashMap<>(),
                 new HashMap<>(),
                 new ArrayList<>(),
                 new ArrayList<>()
                );
        }

        public Builder primary(CharSequence v) {
            primary.add(v);
            return this;
        }

        public Builder fold() {
            if (secondary.isEmpty()) return this;
            primary.add(String.join(",", secondary));
            secondary.clear();
            return this;
        }

        public String sql() {
            return String.join(" ", primary);
        }
    }

    public abstract State.Executor executor();

    /**
     * main build entry
     *
     * @param s status
     * @return builder
     */
    public Builder render(State.Status s) {
        var b = new Builder(s.mode);
        switch (s.mode) {
            case ONE -> renderOne(b, s);
            case ANY -> renderAny(b, s);
            case COUNT -> renderCount(b, s);
            case PUT -> renderPut(b, s);
            case DELETE -> renderDelete(b, s);
            case INTO -> renderInto(b, s);
            case SET -> renderSet(b, s);
        }
        return b;
    }

    protected void renderSet(Builder b, State.Status s) {

    }

    protected void renderInto(Builder b, State.Status s) {}

    protected void renderDelete(Builder b, State.Status s) {}


    protected void renderPut(Builder b, State.Status s) {
        b.primary("INSERT");
        b.primary("INTO");
        primary(b, s.primary);
        b.primary("(");
        computePrimaryFields(b,s.primary);
        b.fold();
        b.primary("(");
        //! todo
        if (s.join != null) {
            joiner(b, s.primary, s.join);
        }

        if (s.filter != null) {
            b.primary("WHERE");
            condition(b, s.filter);
        }
        if (s.sort != null) {
            b.primary("GROUP").primary("BY");
            group(b, s.sort);
        }
        if (s.skip != null || s.limit != null) {
            limitation(b, s.skip, s.limit);
        }
    }

    /**
     *
     * @param b the builder
     * @param primary the primary table
     */
    protected abstract void computePrimaryFields(Builder b, Model<?> primary);

    protected void renderCount(Builder b, State.Status s) {
        b.primary("SELECT COUNT(*)");
        b.primary("FROM");
        primary(b, s.primary);
        if (s.join != null) {
            joiner(b, s.primary, s.join);
        }
        if (s.filter != null) {
            b.primary("WHERE");
            condition(b, s.filter);
        }
        if (s.sort != null) {
            b.primary("GROUP").primary("BY");
            group(b, s.sort);
        }
        if (s.skip != null || s.limit != null) {
            limitation(b, s.skip, s.limit);
        }
    }

    protected void renderAny(Builder b, State.Status s) {
        b.primary("SELECT");
        selection(b, s.primary, s.pick);
        b.primary("FROM");
        primary(b, s.primary);
        if (s.join != null) {
            joiner(b, s.primary, s.join);
        }
        if (s.filter != null) {
            b.primary("WHERE");
            condition(b, s.filter);
        }
        if (s.sort != null) {
            b.primary("GROUP").primary("BY");
            group(b, s.sort);
        }
        if (s.skip != null || s.limit != null) {
            limitation(b, s.skip, s.limit);
        }
    }

    protected void renderOne(Builder b, State.Status s) {
        b.primary("SELECT");
        selection(b, s.primary, s.pick);
        b.primary("FROM");
        primary(b, s.primary);
        if (s.join != null) {
            joiner(b, s.primary, s.join);
        }
        if (s.filter != null) {
            b.primary("WHERE");
            condition(b, s.filter);
        }
        if (s.sort != null) {
            b.primary("GROUP").primary("BY");
            group(b, s.sort);
        }
        if (s.skip != null || s.limit != null) {
            limitation(b, s.skip, s.limit);
        } else {
            b.primary("LIMIT 1");
        }
    }

    protected abstract void group(Builder b, List<Expr.Sorter> sort);

    protected abstract void limitation(Builder b, Integer skip, Integer limit);

    protected abstract void joiner(Builder b, Model<?> primary, List<Expr.Join> join);

    protected abstract void condition(Builder b, Value.Bool primary);

    protected abstract void primary(Builder b, Model<?> primary);

    protected abstract void selection(Builder b, Model<?> primary, List<Model.Field<?>> pick);

    protected abstract void parameter(Builder b, Value.param<?> v);

    protected abstract void field(Builder b, Model.Field<?> v);

}
