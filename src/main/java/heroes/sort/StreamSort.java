package heroes.sort;

import java.util.Comparator;
import java.util.List;

/** Уровень 1: «отсортируйте данные по одному полю» — через Stream API. */
public final class StreamSort implements SortAlgorithm {
    @Override
    public <T> List<T> sort(List<T> source, Comparator<? super T> comparator) {
        return source.stream().sorted(comparator).toList();
    }
}
