package pl.epsi.gtsacore.util.functionalinterface;

@FunctionalInterface
public interface Thenable<T> {

    T andThen(Runnable runnable);

}
