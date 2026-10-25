package heroes.sort;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import heroes.collection.SimpleArrayList;

/** Устойчивая сортировка слиянием: O(n log n), дополнительная память O(n). */
public final class MergeSort implements SortAlgorithm {
    @Override
    public <T> List<T> sort(List<T> source, Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        List<T> result = new SimpleArrayList<>(source);
        List<T> buffer = new SimpleArrayList<>(source);
        sortRange(result, buffer, 0, result.size(), comparator);
        return result;
    }

    private <T> void sortRange(List<T> values, List<T> buffer, int from, int to,
                               Comparator<? super T> comparator) {
        if (to - from < 2) {
            return;
        }
        int middle = from + (to - from) / 2;
        sortRange(values, buffer, from, middle, comparator);
        sortRange(values, buffer, middle, to, comparator);
        merge(values, buffer, from, middle, to, comparator);
    }

    private <T> void merge(List<T> values, List<T> buffer, int from, int middle, int to,
                           Comparator<? super T> comparator) {
        int left = from;
        int right = middle;
        for (int target = from; target < to; target++) {
            if (left < middle && (right >= to
                    || comparator.compare(values.get(left), values.get(right)) <= 0)) {
                buffer.set(target, values.get(left++));
            } else {
                buffer.set(target, values.get(right++));
            }
        }
        for (int index = from; index < to; index++) {
            values.set(index, buffer.get(index));
        }
    }
}
