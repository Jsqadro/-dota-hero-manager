package heroes.sort;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import heroes.collection.SimpleArrayList;

/** Устойчивая сортировка вставками: O(n^2) в худшем случае. */
public final class InsertionSort implements SortAlgorithm {
    @Override
    public <T> List<T> sort(List<T> source, Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        List<T> result = new SimpleArrayList<>(source);
        for (int index = 1; index < result.size(); index++) {
            T current = result.get(index);
            int position = index - 1;
            while (position >= 0 && comparator.compare(result.get(position), current) > 0) {
                result.set(position + 1, result.get(position));
                position--;
            }
            result.set(position + 1, current);
        }
        return result;
    }
}
