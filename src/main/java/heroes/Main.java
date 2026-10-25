package heroes;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import heroes.io.HeroCsvParser;
import heroes.io.HeroFileReader;
import heroes.service.HeroImportService;
import heroes.service.HeroService;
import heroes.ui.ConsoleApplication;
import heroes.ui.ConsoleInput;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            HeroService heroService = new HeroService();
            HeroFileReader fileReader = new HeroFileReader(new HeroCsvParser());
            HeroImportService importService = new HeroImportService(heroService, fileReader);
            ConsoleInput input = new ConsoleInput(scanner, System.out);
            new ConsoleApplication(heroService, importService, input, System.out).run();
        }
    }
}
