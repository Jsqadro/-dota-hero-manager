package heroes.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import heroes.io.HeroFileReader;
import heroes.io.ImportError;

/** Координирует чтение файла и добавление прошедших проверку записей. */
public final class HeroImportService {
    private final HeroService heroService;
    private final HeroFileReader fileReader;

    public HeroImportService(HeroService heroService, HeroFileReader fileReader) {
        this.heroService = Objects.requireNonNull(heroService);
        this.fileReader = Objects.requireNonNull(fileReader);
    }

    public ImportReport load(Path path) throws IOException {
        // Добавление начинается после полного чтения: ошибка чтения не изменит коллекцию.
        HeroFileReader.ReadResult result = fileReader.read(path);
        List<ImportError> errors = new ArrayList<>(result.errors());
        int added = 0;
        for (HeroFileReader.Row row : result.rows()) {
            try {
                heroService.add(row.hero());
                added++;
            } catch (IllegalArgumentException exception) {
                errors.add(new ImportError(row.lineNumber(), exception.getMessage()));
            }
        }
        errors.sort(Comparator.comparingInt(ImportError::lineNumber));
        return new ImportReport(added, errors);
    }
}
