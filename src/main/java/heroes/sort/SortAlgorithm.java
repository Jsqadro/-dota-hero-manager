package heroes.sort;

import java.util.Comparator;
import java.util.List;

/** Уровень 3: «общий интерфейс SortAlgorithm» с Generics; результат — новый список. */
public interface SortAlgorithm {
    <T> List<T> sort(List<T> source, Comparator<? super T> comparator);
}
