package pl.epsi.gtsacore.util;

import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.util.functionalinterface.FaultyFunction;
import pl.epsi.gtsacore.util.functionalinterface.FaultySupplier;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Option<T> {
    private T value;
    private boolean some;

    private Option(T value, boolean some) {
        this.value = value;
        this.some = some;
    }

    public static <T> Option<T> some(T some) {
        return new Option<>(some, true);
    }

    public static <T> Option<T> none() {
        return new Option<>(null, false);
    }

    public static <T> Option<T> useIf(boolean condition, Supplier<T> value) {
        if (condition) return Option.some(value.get());
        return Option.none();
    }

    public static <T> Option<T> useIfNotNull(@Nullable T nullable) {
        if (nullable == null) return Option.none();
        return Option.some(nullable);
    }

    public static <T> Option<T> useIfNotFaulty(FaultySupplier<T, ? extends Throwable> value) {
        try {
            return Option.some(value.get());
        } catch (Throwable e) {
            return Option.none();
        }
    }

    public boolean isSome() {
        return some;
    }

    public boolean isNone() {
        return !some;
    }

    public T unwrap() {
        if (isNone()) throw new IllegalStateException("Unwrapped a None Option!");
        return value;
    }

    public T expect(String message) {
        if (isNone()) throw new IllegalStateException(message);
        return value;
    }

    public T unwrapOr(T defaultValue) {
        if (isNone()) return defaultValue;
        return value;
    }

    public T unwrapOrElse(Supplier<T> defaultValue) {
        if (isNone()) return defaultValue.get();
        return value;
    }

    public T unwrapOrThrow(Throwable t) throws Throwable {
        if (isNone()) throw t;
        return value;
    }

    public T unwrapOrPanic(Throwable t) {
        if (isNone()) throw new RuntimeException(t);
        return value;
    }

    public void inspect(Consumer<T> inspector) {
        if (isSome()) inspector.accept(value);
    }

    public <R> Option<R> map(Function<T, R> mapper) {
        if (isNone()) return Option.none();
        return Option.some(mapper.apply(value));
    }

    public <R, E extends Throwable> Option<R> faultyMap(FaultyFunction<T, R, E> mapper) throws E {
        if (isNone()) return Option.none();
        return Option.some(mapper.apply(value));
    }

    public <R> R mapOr(R defaultValue, Function<T, R> mapper) {
        if (isNone()) return defaultValue;
        return mapper.apply(value);
    }

    public <R> R mapOrElse(Supplier<R> defaultValue, Function<T, R> mapper) {
        if (isNone()) return defaultValue.get();
        return mapper.apply(value);
    }

    @SuppressWarnings("unchecked")
    public <O, R> Option<R> flatMap(Function<O, R> mapper) {
        if (isNone()) return Option.none();
        T t = unwrap();
        if (t instanceof Option<?> unknown) {
            Option<O> opt = (Option<O>) unknown;
            if (opt.isNone()) return Option.none();
            O o = opt.unwrap();
            return Option.some(mapper.apply(o));
        }
        return Option.none();
    }

    @SuppressWarnings("unchecked")
    public <R> Option<R> flatten() {
        if (isNone()) return Option.none();
        T t = unwrap();
        if (t instanceof Option<?> unknown) {
            Option<R> opt = (Option<R>) unknown;
            return opt.flatten();
        }
        return Option.some((R) t);
    }

    public Option<T> take() {
        if (isNone()) return Option.none();
        T t = unwrap();
        value = null;
        some = false;
        return Option.some(t);
    }

    public Optional<T> asOptional() {
        if (isNone()) return Optional.empty();
        return Optional.ofNullable(value);
    }

    @Override
    public String toString() {
        return isSome() ? "Some(" + value + ")" : "None";
    }

    @Override
    public int hashCode() {
        return isSome() ? Objects.hashCode(value) : 0;
    }

    @SuppressWarnings("unchecked")
    public T unwrapOrDefault(T... ignore) {
        if (isSome()) return value;

        Class<T> clazz = (Class<T>) ignore.getClass().getComponentType();
        if (clazz == byte.class || clazz == Byte.class) return (T) (Byte) (byte) 0;
        if (clazz == short.class || clazz == Short.class) return (T) (Short) (short) 0;
        if (clazz == int.class || clazz == Integer.class) return (T) (Integer) 0;
        if (clazz == long.class || clazz == Long.class) return (T) (Long) 0L;
        if (clazz == float.class || clazz == Float.class) return (T) (Float) 0f;
        if (clazz == double.class || clazz == Double.class) return (T) (Double) 0d;
        if (clazz == char.class || clazz == Character.class) return (T) (Character) '\0';
        if (clazz == boolean.class || clazz == Boolean.class) return (T) (Boolean) false;
        if (clazz == List.class) return (T) Collections.EMPTY_LIST;
        if (clazz == String.class) return (T) "";
        return null;
    }

    @SuppressWarnings("unchecked")
    public T unwrapOrIdentity(T... ignore) {
        if (isSome()) return value;

        Class<T> clazz = (Class<T>) ignore.getClass().getComponentType();
        if (clazz == byte.class || clazz == Byte.class) return (T) (Byte) (byte) 1;
        if (clazz == short.class || clazz == Short.class) return (T) (Short) (short) 1;
        if (clazz == int.class || clazz == Integer.class) return (T) (Integer) 1;
        if (clazz == long.class || clazz == Long.class) return (T) (Long) 1L;
        if (clazz == float.class || clazz == Float.class) return (T) (Float) 1f;
        if (clazz == double.class || clazz == Double.class) return (T) (Double) 1d;
        if (clazz == boolean.class || clazz == Boolean.class) return (T) (Boolean) true;
        return null;
    }

    public T unwrapOrNull() {
        if (isSome()) return value;
        return null;
    }
}
