package com.example.images.data;

/**
 * Data of one page out of possibly many.
 */
public class Paginated<T> {

    public final int currentPage;
    public final int totalPages;
    public final T value;

    public Paginated(int currentPage, int totalPages, T value) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Paginated{" +
                "currentPage=" + currentPage +
                ", totalPages=" + totalPages +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Paginated<?> that = (Paginated<?>) o;

        return currentPage == that.currentPage
                && totalPages == that.totalPages
                && (value != null ? value.equals(that.value) : that.value == null);
    }

    @Override
    public int hashCode() {
        int result1 = currentPage;
        result1 = 31 * result1 + totalPages;
        result1 = 31 * result1 + (value != null ? value.hashCode() : 0);
        return result1;
    }

}
