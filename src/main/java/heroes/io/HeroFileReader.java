package heroes.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import heroes.model.Hero;

/** Уровень 1: чтение файла — «используйте конструкцию try-with-resources». */
public final class HeroFileReader {
    private final HeroCsvParser parser;

    public HeroFileReader(HeroCsvParser parser) {
        this.parser = Objects.requireNonNull(parser);
    }

    public ReadResult read(Path path) throws IOException {
        List<Row> rows = new ArrayList<>();
        List<ImportError> errors = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            boolean firstDataLine = true;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = removeBom(line, lineNumber);
                if (isIgnored(line)) {
                    continue;
                }
                boolean header = firstDataLine && parser.isHeader(line);
                firstDataLine = false;
                if (!header) {
                    parseRow(line, lineNumber, rows, errors);
                }
            }
        }
        return new ReadResult(rows, errors);
    }

    private String removeBom(String line, int lineNumber) {
        return lineNumber == 1 && line.startsWith("\uFEFF") ? line.substring(1) : line;
    }

    private boolean isIgnored(String line) {
        return line.isBlank() || line.strip().startsWith("#");
    }

    private void parseRow(String line, int lineNumber, List<Row> rows, List<ImportError> errors) {
        try {
            rows.add(new Row(lineNumber, parser.parse(line)));
        } catch (IllegalArgumentException exception) {
            errors.add(new ImportError(lineNumber, exception.getMessage()));
        }
    }

    public record Row(int lineNumber, Hero hero) {
    }

    public record ReadResult(List<Row> rows, List<ImportError> errors) {
        public ReadResult {
            rows = List.copyOf(rows);
            errors = List.copyOf(errors);
        }
    }
}
