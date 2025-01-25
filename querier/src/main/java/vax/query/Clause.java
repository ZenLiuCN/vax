package vax.query;

import org.jetbrains.annotations.Nullable;

/**
 * @author Zen.Liu
 * @since 2025-01-18
 */
public interface Clause {
    interface Where extends Clause {}

    interface From extends Clause {}

    interface Select extends Clause {
        From from();

        Where where();

        @Nullable Limited limited();

        @Nullable Ordered ordered();

        @Nullable Grouped grouped();
    }

    interface UnionSelect extends Select {
        Select[] selections();
    }

    interface Modify extends Clause {}

    interface Assign extends Clause {}

    interface Grouped extends Clause {}

    interface Ordered extends Clause {}

    interface Limited extends Clause {}

    interface Join extends Clause {}
}
