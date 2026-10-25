package heroes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import heroes.model.Hero;
import heroes.service.HeroService;
import heroes.sort.InsertionSort;
import heroes.sort.MergeSort;
import heroes.sort.SortAlgorithm;
import heroes.sort.SortDirection;
import heroes.sort.StreamSort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Уровень 3: две собственные сортировки, Generics, полиморфизм, направление. */
final class Level3Tests {
    @Test
    void insertionSortSortsInBothDirections() {
        sortsInBothDirections(new InsertionSort());
    }

    @Test
    void mergeSortSortsInBothDirections() {
        sortsInBothDirections(new MergeSort());
    }

    @Test
    void allAlgorithmsGiveSameResultThroughSortAlgorithmInterface() {
        List<Integer> source = new ArrayList<>(List.of(3, -1, 2));
        for (SortAlgorithm algorithm : List.of(new StreamSort(), new InsertionSort(), new MergeSort())) {
            assertEquals(List.of(-1, 2, 3), algorithm.sort(source, Comparator.naturalOrder()));
            assertEquals(List.of(3, 2, -1), algorithm.sort(source, Comparator.reverseOrder()));
            assertEquals(List.of(3, -1, 2), source);
        }
    }

    @Test
    void serviceFiltersAndSortsThroughChosenAlgorithm() {
        HeroService service = TestSupport.newHeroService();
        Hero sniper = new Hero("Sniper", 30, "agility");
        Hero axe = new Hero("Axe", 50, "strength");
        Hero zeus = new Hero("Zeus", 40, "intelligence");
        service.add(sniper);
        service.add(axe);
        service.add(zeus);
        // Один интерфейс — три реализации, выбор происходит во время работы программы.
        for (SortAlgorithm algorithm : List.of(new StreamSort(), new InsertionSort(), new MergeSort())) {
            assertEquals(List.of(zeus, axe),
                    service.findByMinimumWinRate(40, algorithm, SortDirection.ASCENDING));
            assertEquals(List.of(axe, zeus),
                    service.findByMinimumWinRate(40, algorithm, SortDirection.DESCENDING));
        }
        assertEquals(List.of(sniper, axe, zeus), service.getAll());
        assertThrows(IllegalArgumentException.class,
                () -> service.findByMinimumWinRate(-1, new StreamSort(), SortDirection.ASCENDING));
    }

    private static void sortsInBothDirections(SortAlgorithm algorithm) {
        List<Integer> source = new ArrayList<>(List.of(5, 1, 4, 1, 3));
        assertEquals(List.of(1, 1, 3, 4, 5), algorithm.sort(source, Comparator.naturalOrder()));
        assertEquals(List.of(5, 4, 3, 1, 1), algorithm.sort(source, Comparator.reverseOrder()));
        // Исходный список не меняется, Generics позволяют сортировать любые типы.
        assertEquals(List.of(5, 1, 4, 1, 3), source);
        List<String> words = List.of("bb", "a", "ccc");
        assertEquals(List.of("a", "bb", "ccc"),
                algorithm.sort(words, Comparator.comparingInt(String::length)));
    }
}
