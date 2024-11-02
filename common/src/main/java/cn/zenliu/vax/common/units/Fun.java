package cn.zenliu.vax.common.units;

/**
 * @author Zen.Liu
 * @since 2024-10-22
 */
public interface Fun {

    interface A<T> extends Fun {
        T a();
    }

    interface S<T, R> extends Fun {
        void s(T o, R v);

        default C<T, R> c() {
            return (o, v) -> {
                s(o, v);
                return o;
            };
        }

    }

    interface C<T, R> extends Fun {
        T c(T o, R v);
    }

    interface M<T, R> extends Fun {
        R m(T o);

        default Fun.A<R> a(T o) {
            return () -> m(o);
        }
    }

    interface U {
        int dim();
    }

}
