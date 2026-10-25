package heroes.service;

import java.util.List;

import heroes.io.ImportError;

public record ImportReport(int added, List<ImportError> errors) {
    public ImportReport {
        errors = List.copyOf(errors);
    }
}
