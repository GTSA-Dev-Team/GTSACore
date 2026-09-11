package pl.epsi.gtsacore.util.functionalinterface;

public interface FaultyConsumer<T, E extends Throwable> {
    void accept(T t) throws E;
}
