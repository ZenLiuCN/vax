package vax.query;

import java.util.List;

/**
 * storage model
 *
 * @author Zen.Liu
 * @since 2024-12-08
 */
public interface Model<T> {


    interface Field<T> {
        Entry<?> $entry();

        Func.FieldReader<T> $reader();
    }
    interface Entry<T>{
        String $name(boolean schema);

        Func.ModelReader<T> $reader();

        List<Field<?>> $fields();
    }
}
