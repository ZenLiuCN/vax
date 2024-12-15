package vax.common.modifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Codegen modifiers
 *
 * @author Zen.Liu
 * @since 2024-12-08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Vax {
    /**
     * generate a Binary backend type. not allow for XDomain.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Binary {
    }

    /**
     * generate a JsonObject backend type. not allow for XDomain.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Json {

    }

    /**
     * generate a Mutable protocol. not allow for XDomain.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Mutable {
    }

    /**
     * generate a POJO backend type. not allow for XDomain.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Pojo {
    }

    /**
     * generate an event-bus service, only allow for XDomain.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Service {
    }

}
