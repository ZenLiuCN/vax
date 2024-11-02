package cn.zenliu.vax.common.trait;

/**
 * Something with a version
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Auditable {
    long creator();
    /**
     * timestamp value
     */
    long created();

    long modifier();

    /**
     * timestamp value
     */
    long modified();
}
