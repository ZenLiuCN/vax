package vax.query;

/**
 * @author Zen.Liu
 * @since 2025-01-18
 */
public interface Stage {

    interface Joinable extends Stage{

    }
    interface Queryable extends Stage{

    }
    interface Mutable<T> extends Stage{
        Statement.Create merge();
    }
    interface Remove<T> extends Stage{
        Statement.Delete remove();
    }
    interface Spring extends Stage{

    }
}
