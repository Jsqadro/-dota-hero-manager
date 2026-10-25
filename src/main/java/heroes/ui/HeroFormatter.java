package heroes.ui;

import java.util.Locale;

import heroes.model.Hero;

/** Представление для консоли; модель и сервис возвращают данные без форматирования. */
public final class HeroFormatter {
    private HeroFormatter() {
    }

    public static String format(Hero hero) {
        return String.format(Locale.ROOT, "%s | процент побед: %.2f%% | атрибут: %s",
                hero.getName(), hero.getWinRate(), hero.getAttribute());
    }
}
