package heroes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import heroes.model.Hero;
import heroes.service.ImportReport;
import heroes.ui.ConsoleInput;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Уровень 1: меню, добавление вручную и из файла, валидация, исключения, Stream API. */
final class Level1Tests {
    @Test
    void heroHasThreeValidatedFields() {
        Hero hero = new Hero("  Axe  ", 53.2, " СИЛА ");
        assertEquals("Axe", hero.getName());
        assertEquals("strength", hero.getAttribute());
        for (String name : new String[]{null, "", "   ", "а;б", "а".repeat(101)}) {
            assertThrows(IllegalArgumentException.class, () -> new Hero(name, 45, "strength"));
        }
        for (double rate : new double[]{-0.1, 100.1, Double.NaN}) {
            assertThrows(IllegalArgumentException.class, () -> new Hero("Axe", rate, "strength"));
        }
        for (String attribute : new String[]{null, "", "unknown"}) {
            assertThrows(IllegalArgumentException.class, () -> new Hero("Axe", 45, attribute));
        }
    }

    @Test
    void addValidatesAndRejectsDuplicates() {
        var service = TestSupport.newHeroService();
        service.add(new Hero("Axe", 53, "strength"));
        assertThrows(IllegalArgumentException.class,
                () -> service.add(new Hero("Axe", 40, "agility")));
        assertThrows(IllegalArgumentException.class,
                () -> service.add(new Hero("axe", 40, "agility")));
        assertThrows(IllegalArgumentException.class,
                () -> service.add(new Hero("Zeus", 130, "intelligence")));
        assertEquals(1, service.size());
    }

    @Test
    void menuRunsUntilExitCommand() {
        String output = TestSupport.console("99\nabc\n0\n1\n");
        assertTrue(output.contains("Неизвестная команда"));
        assertTrue(output.contains("Введите целое число"));
        assertTrue(output.contains("Работа завершена."));
        // После «Выход» программа не продолжает работу.
        assertFalse(output.contains("Имя героя:"));
    }

    @Test
    void manualAddRetriesInvalidFields() {
        String output = TestSupport.console("1\n\nAxe\n130\n50,5\nstrength\n4\n0\n");
        assertTrue(output.contains("Имя героя не должно быть пустым"));
        assertTrue(output.contains("Процент побед должен быть числом от 0 до 100"));
        assertTrue(output.contains("Герой добавлен."));
        assertTrue(output.contains("Axe | процент побед: 50.50% | атрибут: strength"));
    }

    @Test
    void menuFiltersAndSortsLoadedFile() {
        String output = TestSupport.console("2\ndata/heroes.csv\n3\n52\n2\n2\n0\n");
        assertTrue(output.contains("Добавлено героев: 5"));
        assertTrue(output.contains("Процент побед не ниже 52.0, сортировка по убыванию:"));
        List<String> rates = Pattern.compile("побед: (\\S+)%").matcher(output).results()
                .map(match -> match.group(1)).toList();
        assertEquals(List.of("53.20", "52.80"), rates);
    }

    @Test
    void importsHeroesFromCsvFile() throws IOException {
        var service = TestSupport.newHeroService();
        ImportReport report = TestSupport.newImportService(service).load(Path.of("data/heroes.csv"));
        assertEquals(5, report.added());
        assertEquals(List.of(), report.errors());
        assertEquals(5, service.size());
        assertEquals("strength", service.get(0).getAttribute());
    }

    @Test
    void reportsErrorsForInvalidRows() throws IOException {
        var service = TestSupport.newHeroService();
        ImportReport report = TestSupport.newImportService(service).load(Path.of("data/heroes-invalid.csv"));
        assertEquals(1, report.added());
        assertEquals(7, report.errors().size());
        assertEquals(3, report.errors().get(0).lineNumber());
        assertEquals(9, report.errors().get(6).lineNumber());
        assertEquals(1, service.size());
    }

    @Test
    void consoleInputRepeatsOnErrorAndDetectsEndOfInput() {
        try (Scanner scanner = new Scanner("abc\n7\n");
             var output = new java.io.PrintStream(new java.io.ByteArrayOutputStream(), true,
                     java.nio.charset.StandardCharsets.UTF_8)) {
            ConsoleInput input = new ConsoleInput(scanner, output);
            assertEquals(7, input.readInt("Число: "));
        }
        try (Scanner scanner = new Scanner("");
             var output = new java.io.PrintStream(new java.io.ByteArrayOutputStream(), true,
                     java.nio.charset.StandardCharsets.UTF_8)) {
            ConsoleInput input = new ConsoleInput(scanner, output);
            assertThrows(NoSuchElementException.class, () -> input.readLine(""));
        }
    }
}
