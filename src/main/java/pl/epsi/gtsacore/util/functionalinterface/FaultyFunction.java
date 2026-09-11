package pl.epsi.gtsacore.util.functionalinterface;

@FunctionalInterface
public interface FaultyFunction<T, U, E extends Throwable> {
    U apply(T t) throws E;
}
