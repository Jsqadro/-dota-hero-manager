package heroes.io;

/** Ошибка отдельной строки; текст для консоли формируется в слое UI. */
public record ImportError(int lineNumber, String message) {
}
