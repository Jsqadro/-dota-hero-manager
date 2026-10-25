package heroes.ui;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.IntConsumer;

import heroes.model.Hero;
import heroes.service.HeroImportService;
import heroes.service.HeroService;
import heroes.service.ImportReport;
import heroes.sort.InsertionSort;
import heroes.sort.MergeSort;
import heroes.sort.SortAlgorithm;
import heroes.sort.SortDirection;
import heroes.sort.StreamSort;
import heroes.validation.HeroValidator;

/** Уровень 1: «Реализуйте консольное меню», работа в цикле до пункта «Выход». */
public final class ConsoleApplication {
    private final HeroService heroService;
    private final HeroImportService importService;
    private final ConsoleInput input;
    private final PrintStream output;

    public ConsoleApplication(HeroService heroService, HeroImportService importService,
                              ConsoleInput input, PrintStream output) {
        this.heroService = heroService;
        this.importService = importService;
        this.input = input;
        this.output = output;
    }

    public void run() {
        output.println("Учёт героев Dota 2. Уровни 1, 2 и 3.");
        try {
            while (true) {
                printMenu();
                int command = input.readInt("Выберите пункт меню: ");
                switch (command) {
                    case 1 -> addHero();
                    case 2 -> loadHeroes();
                    case 3 -> processHeroes();
                    case 4 -> printAll();
                    case 5 -> getHero();
                    case 6 -> removeHero();
                    case 0 -> {
                        output.println("Работа завершена.");
                        return;
                    }
                    default -> output.println("Неизвестная команда. Выберите пункт от 0 до 6.");
                }
            }
        } catch (UncheckedIOException exception) {
            output.println("\nОшибка чтения ввода: " + exception.getCause().getMessage());
        } catch (NoSuchElementException exception) {
            output.println("\nВвод завершён. Работа завершена.");
        }
    }

    private void printMenu() {
        output.println("""

                ===== МЕНЮ =====
                1. Добавить героя вручную
                2. Загрузить героев из CSV/TXT
                3. Отфильтровать и отсортировать по проценту побед
                4. Показать всех героев
                5. Получить героя по номеру в списке
                6. Удалить героя по номеру в списке
                0. Выход
                """);
        output.println("Героев в коллекции: " + heroService.size());
    }

    private void addHero() {
        String name = input.readValidated("Имя героя: ", HeroValidator::normalizeAndValidateName);
        double winRate = input.readWinRate("Процент побед (0–100): ");
        String attribute = input.readValidated("Атрибут (strength/agility/intelligence/universal): ",
                HeroValidator::normalizeAndValidateAttribute);
        try {
            heroService.add(new Hero(name, winRate, attribute));
            output.println("Герой добавлен.");
        } catch (IllegalArgumentException exception) {
            output.println("Ошибка: " + exception.getMessage());
        }
    }

    private void loadHeroes() {
        String filename = input.readLine("Путь к файлу (например, data/heroes.csv): ");
        if (filename.isBlank()) {
            output.println("Ошибка: путь к файлу не должен быть пустым.");
            return;
        }
        try {
            printImportReport(importService.load(Path.of(filename)));
        } catch (IOException exception) {
            output.println("Не удалось прочитать файл: " + exception.getMessage());
        } catch (IllegalArgumentException | SecurityException exception) {
            output.println("Ошибка доступа или пути к файлу: " + exception.getMessage());
        }
    }

    private void printImportReport(ImportReport report) {
        output.println("Добавлено героев: " + report.added());
        if (!report.errors().isEmpty()) {
            output.println("Пропущенные строки:");
            report.errors().stream()
                    .map(error -> "Строка " + error.lineNumber() + ": " + error.message())
                    .forEach(output::println);
        }
    }

    private void processHeroes() {
        double minimumWinRate = input.readWinRate("Минимальный процент побед (0–100): ");
        SortAlgorithm algorithm = readSortAlgorithm();
        SortDirection direction = readSortDirection();
        List<Hero> results = heroService.findByMinimumWinRate(minimumWinRate, algorithm, direction);
        printProcessingResults(minimumWinRate, direction, results);
    }

    private void printProcessingResults(double minimumWinRate, SortDirection direction,
                                        List<Hero> results) {
        String order = direction == SortDirection.ASCENDING ? "по возрастанию" : "по убыванию";
        output.println("Процент побед не ниже " + minimumWinRate + ", сортировка " + order + ":");
        if (results.isEmpty()) {
            output.println("Подходящих героев нет.");
            return;
        }
        // Уровень 1: «выполните хотя бы одно преобразование данных через map()».
        results.stream().map(HeroFormatter::format).forEach(output::println);
    }

    private SortAlgorithm readSortAlgorithm() {
        // Уровень 3: «выбирать необходимую реализацию во время выполнения программы».
        output.println("1. Stream API  2. Сортировка вставками  3. Сортировка слиянием");
        return switch (input.readChoice("Алгоритм: ", 1, 3)) {
            case 1 -> new StreamSort();
            case 2 -> new InsertionSort();
            default -> new MergeSort();
        };
    }

    private SortDirection readSortDirection() {
        output.println("1. По возрастанию  2. По убыванию");
        return input.readChoice("Направление: ", 1, 2) == 1
                ? SortDirection.ASCENDING : SortDirection.DESCENDING;
    }

    private void printAll() {
        if (heroService.isEmpty()) {
            output.println("Список пуст.");
            return;
        }
        List<Hero> heroes = heroService.getAll();
        for (int index = 0; index < heroes.size(); index++) {
            output.println((index + 1) + ". " + HeroFormatter.format(heroes.get(index)));
        }
    }

    private void getHero() {
        withHeroIndex("Номер в списке", index ->
                output.println(HeroFormatter.format(heroService.get(index))));
    }

    private void removeHero() {
        withHeroIndex("Номер удаляемого героя", index ->
                output.println("Удалён: " + HeroFormatter.format(heroService.remove(index))));
    }

    private void withHeroIndex(String prompt, IntConsumer action) {
        if (heroService.isEmpty()) {
            output.println("Список пуст.");
            return;
        }
        int number = input.readInt(prompt + " (от 1 до " + heroService.size() + "): ");
        if (number < 1 || number > heroService.size()) {
            output.println("Ошибка: нет героя с таким номером в списке.");
            return;
        }
        action.accept(number - 1);
    }
}
