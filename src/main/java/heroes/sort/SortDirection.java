package heroes.sort;

import java.util.Comparator;

public enum SortDirection {
    ASCENDING,
    DESCENDING;

    public <T> Comparator<T> applyTo(Comparator<T> comparator) {
        return this == ASCENDING ? comparator : comparator.reversed();
    }
}
