package cn.zenliu.vax.common;

/**
 * Recording is a group of data, without any actions
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Records extends Entry{
    @Override
    Class<? extends Records> $type();
}
