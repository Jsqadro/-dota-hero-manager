package heroes.ui;

import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;

import heroes.validation.HeroValidator;

public final class ConsoleInput {
    private final Scanner scanner;
    private final PrintStream output;

    public ConsoleInput(Scanner scanner, PrintStream output) {
        this.scanner = Objects.requireNonNull(scanner);
        this.output = Objects.requireNonNull(output);
    }

    public String readLine(String prompt) {
        output.print(prompt);
        output.flush();
        try {
            String line = scanner.nextLine();
            checkReadFailure();
            return line.strip();
        } catch (NoSuchElementException exception) {
            checkReadFailure();
            throw exception;
        }
    }

    public int readInt(String prompt) {
        return readValidated(prompt, value -> {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Введите целое число.");
            }
        });
    }

    public double readWinRate(String prompt) {
        return readValidated(prompt, HeroValidator::parseWinRate);
    }

    public int readChoice(String prompt, int minimum, int maximum) {
        while (true) {
            int choice = readInt(prompt);
            if (choice >= minimum && choice <= maximum) {
                return choice;
            }
            output.println("Ошибка: выберите число от " + minimum + " до " + maximum + ".");
        }
    }

    public <T> T readValidated(String prompt, Function<String, T> parser) {
        while (true) {
            String value = readLine(prompt);
            try {
                return parser.apply(value);
            } catch (IllegalArgumentException exception) {
                output.println("Ошибка: " + exception.getMessage());
            }
        }
    }

    private void checkReadFailure() {
        if (scanner.ioException() != null) {
            throw new UncheckedIOException("Не удалось прочитать ввод.", scanner.ioException());
        }
    }
}
