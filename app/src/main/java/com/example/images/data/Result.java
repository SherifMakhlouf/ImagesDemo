package com.example.images.data;

/**
 * Result which can either be successful or faulty.
 */
public final class Result<T> {

    /**
     * Value of a successful value. {@code null} if value is faulty.
     */
    public final T value;

    /**
     * Error of a faulty value. {@code null} if value is successful.
     */
    public final Throwable throwable;

    /**
     * @return new successful value.
     */
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    /**
     * @return new faulty value.
     */
    public static <T> Result<T> error(Throwable throwable) {
        return new Result<>(null, throwable);
    }

    private Result(T value,
                   Throwable throwable) {
        this.value = value;
        this.throwable = throwable;
    }

    /**
     * @return {@code true} if value is successful. {@code false} if it is unsuccessful.
     */
    public boolean isSuccess() {
        return throwable == null;
    }

    @Override
    public String toString() {
        return "Result{" +
                "value=" + value +
                ", throwable=" + throwable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result<?> result = (Result<?>) o;

        return (value != null ? value.equals(result.value) : result.value == null)
                && (throwable != null ? throwable.equals(result.throwable) : result.throwable == null);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (throwable != null ? throwable.hashCode() : 0);
        return result;
    }

}
