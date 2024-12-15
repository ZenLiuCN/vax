package vax.query;

import java.nio.Buffer;
import java.time.temporal.Temporal;

/**
 * Expression model
 *
 * @author Zen.Liu
 * @since 2024-12-08
 */
public interface Expr {
    interface Value<T> extends Expr {

    }
    interface Num<T extends Number> extends Value<T> {
    }

    interface Binary extends Value<byte[]> {
    }

    interface Blob extends Value<Buffer> {
    }

    interface Text extends Value<CharSequence> {
    }

    interface Temporal<T extends java.time.temporal.Temporal> extends Value<T> {
    }

    interface Bool extends Value<Boolean> {
    }
}
