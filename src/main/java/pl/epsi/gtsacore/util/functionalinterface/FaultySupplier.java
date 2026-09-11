package pl.epsi.gtsacore.util.functionalinterface;

public interface FaultySupplier<T, E extends Throwable> {
    T get() throws E;
}
