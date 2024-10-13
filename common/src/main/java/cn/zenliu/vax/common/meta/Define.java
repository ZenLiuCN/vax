package cn.zenliu.vax.common.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zen.Liu
 * @since 2024-10-13
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Define {

    int kind() default Meta.KIND_DOMAIN;

    /**
     * @return the referenced identifier
     */
    long reference() default -1;


    String name();

    /**
     * @return the comment of element
     */
    String comment();
}
