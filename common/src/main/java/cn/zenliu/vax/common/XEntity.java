package cn.zenliu.vax.common;

/**
 * Entity is a Record
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface XEntity extends XRecord {
    @Override
    Class<? extends XEntity> $type();
}
