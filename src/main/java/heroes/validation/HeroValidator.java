package heroes.validation;

import java.util.Locale;
import java.util.Map;

/** Уровень 1: «Выполните валидацию вводимых данных». */
public final class HeroValidator {
    private static final int MAX_NAME_LENGTH = 100;
    private static final double MIN_WIN_RATE = 0;
    private static final double MAX_WIN_RATE = 100;
    private static final String ATTRIBUTE_MESSAGE =
            "Атрибут героя: strength, agility, intelligence или universal.";
    private static final Map<String, String> ATTRIBUTES = Map.of(
            "strength", "strength", "сила", "strength",
            "agility", "agility", "ловкость", "agility",
            "intelligence", "intelligence", "интеллект", "intelligence",
            "universal", "universal", "универсальный", "universal");

    private HeroValidator() {
    }

    public static String normalizeAndValidateName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Имя героя не должно быть пустым.");
        }
        String name = value.strip();
        if (name.length() > MAX_NAME_LENGTH || name.contains(";")
                || name.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException(
                    "Имя героя: до 100 символов, без точки с запятой и управляющих символов.");
        }
        return name;
    }

    public static double validateWinRate(double value) {
        if (!Double.isFinite(value) || value < MIN_WIN_RATE || value > MAX_WIN_RATE) {
            throw new IllegalArgumentException("Процент побед должен быть числом от 0 до 100.");
        }
        return value;
    }

    public static double parseWinRate(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Процент побед должен быть числом.");
        }
        try {
            return validateWinRate(Double.parseDouble(value.strip().replace(',', '.')));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Процент побед должен быть числом.", exception);
        }
    }

    public static String normalizeAndValidateAttribute(String value) {
        if (value == null) {
            throw new IllegalArgumentException(ATTRIBUTE_MESSAGE);
        }
        String attribute = ATTRIBUTES.get(value.strip().toLowerCase(Locale.ROOT));
        if (attribute == null) {
            throw new IllegalArgumentException(ATTRIBUTE_MESSAGE);
        }
        return attribute;
    }
}
