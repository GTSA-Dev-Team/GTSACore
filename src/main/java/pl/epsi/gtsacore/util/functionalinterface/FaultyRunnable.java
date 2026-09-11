package pl.epsi.gtsacore.util.functionalinterface;

@FunctionalInterface
public interface FaultyRunnable {
    void run() throws Exception;
}
