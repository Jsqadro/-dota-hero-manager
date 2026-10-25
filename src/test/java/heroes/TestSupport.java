package heroes;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import heroes.io.HeroCsvParser;
import heroes.io.HeroFileReader;
import heroes.service.HeroImportService;
import heroes.service.HeroService;
import heroes.ui.ConsoleApplication;
import heroes.ui.ConsoleInput;

final class TestSupport {
    private TestSupport() {
    }

    static HeroService newHeroService() {
        return new HeroService();
    }

    static HeroImportService newImportService(HeroService service) {
        return new HeroImportService(service, new HeroFileReader(new HeroCsvParser()));
    }

    static String console(String script) {
        try (Scanner scanner = new Scanner(script)) {
            HeroService service = newHeroService();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
                ConsoleInput input = new ConsoleInput(scanner, capture);
                new ConsoleApplication(service, newImportService(service), input, capture).run();
            }
            return output.toString(StandardCharsets.UTF_8);
        }
    }
}
