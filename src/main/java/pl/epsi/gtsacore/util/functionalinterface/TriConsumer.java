package pl.epsi.gtsacore.util.functionalinterface;

@FunctionalInterface
public interface TriConsumer<T, U, V> {

    void accept(T t, U u, V v);

}
