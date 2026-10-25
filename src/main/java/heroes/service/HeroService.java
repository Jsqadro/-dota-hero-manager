package heroes.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import heroes.collection.SimpleArrayList;
import heroes.model.Hero;
import heroes.sort.SortAlgorithm;
import heroes.sort.SortDirection;
import heroes.validation.HeroValidator;

/** Управляет коллекцией героев. Не зависит от консоли и файлов. */
public final class HeroService {
    private static final Comparator<Hero> BY_WIN_RATE = Comparator
            .comparingDouble(Hero::getWinRate)
            .thenComparing(Hero::getName)
            .thenComparing(Hero::getAttribute);

    private final List<Hero> heroes = new SimpleArrayList<>();
    private final Set<String> names = new HashSet<>();

    public HeroService() {
    }

    public HeroService(Collection<? extends Hero> initialHeroes) {
        Objects.requireNonNull(initialHeroes).forEach(this::add);
    }

    public void add(Hero hero) {
        Objects.requireNonNull(hero, "Герой не должен быть null.");
        String key = hero.getName().toLowerCase(Locale.ROOT);
        if (names.contains(key)) {
            throw new IllegalArgumentException("Герой " + hero.getName() + " уже существует.");
        }
        heroes.add(hero);
        names.add(key);
    }

    public List<Hero> findByMinimumWinRate(double minimumWinRate, SortAlgorithm algorithm,
                                           SortDirection direction) {
        HeroValidator.validateWinRate(minimumWinRate);
        Objects.requireNonNull(algorithm);
        Objects.requireNonNull(direction);
        // Уровень 1: «отфильтруйте объекты по одному условию».
        List<Hero> filtered = heroes.stream()
                .filter(hero -> hero.getWinRate() >= minimumWinRate)
                .toList();
        return List.copyOf(algorithm.sort(filtered, direction.applyTo(BY_WIN_RATE)));
    }

    public List<Hero> getAll() {
        return List.copyOf(heroes);
    }

    public Hero get(int index) {
        return heroes.get(index);
    }

    public Hero remove(int index) {
        Hero removed = heroes.remove(index);
        names.remove(removed.getName().toLowerCase(Locale.ROOT));
        return removed;
    }

    public int size() {
        return heroes.size();
    }

    public boolean isEmpty() {
        return heroes.isEmpty();
    }
}
