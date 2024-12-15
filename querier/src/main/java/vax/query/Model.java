package vax.query;

import java.util.List;

/**
 * storage model
 *
 * @author Zen.Liu
 * @since 2024-12-08
 */
public interface Model<T> {
    String $name();

    Reader.ModelReader<T> $reader();

    List<Field<?>> $fields();

    interface Field<T> {
        Model<?> $model();

        Reader.FieldReader<T> $reader();
    }
}
