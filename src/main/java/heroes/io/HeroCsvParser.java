package heroes.io;

import heroes.model.Hero;
import heroes.validation.HeroValidator;

/** Простой CSV: три поля с разделителем «;», без экранирования кавычками. */
public final class HeroCsvParser {
    private static final String HEADER = "name;winRate;attribute";
    private static final int FIELD_COUNT = 3;

    public boolean isHeader(String line) {
        return HEADER.equalsIgnoreCase(line.strip());
    }

    public Hero parse(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException(
                    "Ожидается 3 поля: имя;процент побед;атрибут.");
        }
        return new Hero(fields[0], HeroValidator.parseWinRate(fields[1]), fields[2]);
    }
}
