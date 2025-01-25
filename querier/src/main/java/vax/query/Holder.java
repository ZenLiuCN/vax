package vax.query;

/// placeholder
sealed public interface Holder<T> extends Value<T> {
    Class<T> type();

    /// unique int id
    int id();

    record holder<T>(Class<T> type, int id) implements vax.query.Holder<T> {}

    static <T> vax.query.Holder<T> of(Class<T> type, int id) {
        return new holder<>(type, id);
    }
}
