package vax.common.trait;

import java.time.Instant;

/**
 * Something with a version
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface XAuditable {
    long creator();

    /**
     * timestamp value
     */
    Instant created();

    long editor();

    /**
     * timestamp value
     */
    Instant edited();
}
