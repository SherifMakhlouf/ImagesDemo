package com.example.images.data;

/**
 * Data of one page out of possibly many.
 */
public class Paginated<T> {

    public final int currentPage;
    public final int totalPages;
    public final T result;

    public Paginated(int currentPage, int totalPages, T result) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.result = result;
    }

    @Override
    public String toString() {
        return "Paginated{" +
                "currentPage=" + currentPage +
                ", totalPages=" + totalPages +
                ", result=" + result +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Paginated<?> that = (Paginated<?>) o;

        return currentPage == that.currentPage
                && totalPages == that.totalPages
                && (result != null ? result.equals(that.result) : that.result == null);
    }

    @Override
    public int hashCode() {
        int result1 = currentPage;
        result1 = 31 * result1 + totalPages;
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        return result1;
    }

}
