package heroes.model;

import java.util.Objects;

import heroes.validation.HeroValidator;

/** Уровень 1: «создайте класс минимум с тремя полями». */
public final class Hero {
    private final String name;
    private final double winRate;
    private final String attribute;

    public Hero(String name, double winRate, String attribute) {
        this.name = HeroValidator.normalizeAndValidateName(name);
        this.winRate = HeroValidator.validateWinRate(winRate);
        this.attribute = HeroValidator.normalizeAndValidateAttribute(attribute);
    }

    public String getName() {
        return name;
    }

    public double getWinRate() {
        return winRate;
    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public String toString() {
        return "Hero[name=" + name + ", winRate=" + winRate + ", attribute=" + attribute + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Hero hero)) {
            return false;
        }
        return Double.compare(winRate, hero.winRate) == 0
                && name.equals(hero.name) && attribute.equals(hero.attribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, winRate, attribute);
    }
}
