package cn.zenliu.vax.query;

import io.vertx.core.Future;
import org.jooq.lambda.tuple.Tuple2;

import java.util.List;
import java.util.Optional;

/**
 * @author Zen.Liu
 * @since 2024-10-07
 */
public interface F<U> {
    interface Source<U> extends joins<U> {

    }

    interface Filter<U> {
        Filter<U> filter(E<Boolean> cond);
    }

    interface Slicer<U> {
        Slicer<U> skip(E<Integer> qnt);
        Slicer<U> limit(E<Integer> qnt);
    }

    interface Query<U> {
        Future<U> one();

        Future<List<U>> any();

        Future<Optional<U>> maybe();

        Future<Integer> count();
    }

    interface Mutable<U> {
        Future<Integer> gather(List<E.Op.Assign<?>> cols);

        Future<Integer> modify(List<E.Op.Assign<?>> cols);

        Future<Integer> drop();
    }

    interface joins<U0> {
        <U1> Source<Tuple2<U0, U1>> join(Source<U1> o, E<Boolean> on);

        <U1> Source<Tuple2<U0, U1>> left(Source<U1> o, E<Boolean> on);

        <U1> Source<Tuple2<U0, U1>> right(Source<U1> o, E<Boolean> on);

        <U1> Source<Tuple2<U0, U1>> cross(Source<U1> o, E<Boolean> on);
    }

}
