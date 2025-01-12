package vax.common.modifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Codegen modifiers. All modifiers only effect when '@Vax' present.
 *
 * @author Zen.Liu
 * @since 2024-12-08
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Vax {
    /**
     * generate a Binary backend type. not allow for XDomain.
     */
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.TYPE)
    @interface Binary {
    }

    /**
     * generate a POJO backend type. not allow for XDomain.
     */
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.TYPE)
    @interface Pojo {
    }

    /**
     * generate event-bus proxy, only allow for XDomain.
     */
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.TYPE)
    @interface Proxy {
    }

    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.TYPE)
    @interface Event {
        /// The event address
        String value();
    }
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    @interface Default {
    }
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    @interface Compute {
    }
}
