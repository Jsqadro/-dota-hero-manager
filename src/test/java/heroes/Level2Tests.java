package heroes;

import java.util.List;

import org.junit.jupiter.api.Test;

import heroes.collection.SimpleArrayList;
import heroes.model.Hero;
import heroes.service.HeroService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Уровень 2: собственная коллекция add/get/remove/size и автоматическое расширение. */
final class Level2Tests {
    @Test
    void collectionOperations() {
        List<Integer> list = new SimpleArrayList<>();
        assertEquals(0, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        for (int value = 0; value < 50; value++) {
            list.add(value);
        }
        assertEquals(50, list.size());
        assertEquals(0, list.get(0));
        assertEquals(49, list.get(49));
        list.set(0, -1);
        assertEquals(-1, list.get(0));
        assertEquals(10, list.remove(10));
        assertEquals(11, list.get(10));
        assertEquals(49, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(49));
    }

    @Test
    void growthExpandsAutomatically() {
        List<Integer> list = new SimpleArrayList<>();
        for (int value = 0; value < 1000; value++) {
            list.add(value);
        }
        assertEquals(1000, list.size());
        for (int value = 0; value < 1000; value++) {
            assertEquals(value, list.get(value));
        }
    }

    @Test
    void heroStorageUsesOwnCollectionForGetAndRemove() {
        HeroService service = TestSupport.newHeroService();
        Hero axe = new Hero("Axe", 53, "strength");
        Hero zeus = new Hero("Zeus", 51, "intelligence");
        service.add(axe);
        service.add(zeus);
        assertEquals(axe, service.get(0));
        assertEquals(zeus, service.remove(1));
        assertEquals(1, service.size());
        assertEquals(List.of(axe), service.getAll());
        assertThrows(IndexOutOfBoundsException.class, () -> service.get(1));
        // После удаления имя освобождается и можно добавить снова.
        service.add(zeus);
        assertEquals(2, service.size());
    }
}
