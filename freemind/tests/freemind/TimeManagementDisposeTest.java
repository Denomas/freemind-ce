package tests.freemind;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that TimeManagement.disposeDialog() uses defensive
 * try-catch-finally pattern so the dialog always closes (AC-5b).
 *
 * Uses source code verification (headless-compatible) because
 * disposeDialog() is private and requires full Swing + plugin context.
 */
@DisplayName("TimeManagement disposeDialog (AC-5b)")
class TimeManagementDisposeTest {

    private static final String SOURCE_PATH =
        "freemind/accessories/plugins/time/TimeManagement.java";

    @Test
    @DisplayName("disposeDialog() contains try-finally pattern")
    void disposeDialogHasTryFinally() throws IOException {
        String source = readSourceFile();
        String disposeMethod = extractMethod(source, "disposeDialog");
        assertNotNull(disposeMethod, "disposeDialog() method must exist in TimeManagement.java");
        assertTrue(disposeMethod.contains("finally"),
            "disposeDialog() must use try-finally to guarantee dialog closure");
    }

    @Test
    @DisplayName("disposeDialog() calls setVisible(false) in finally block")
    void disposeDialogCallsSetVisibleInFinally() throws IOException {
        String source = readSourceFile();
        String disposeMethod = extractMethod(source, "disposeDialog");
        assertNotNull(disposeMethod);

        // Extract the finally block content
        int finallyIdx = disposeMethod.indexOf("finally");
        assertTrue(finallyIdx > 0, "finally block must exist");
        String afterFinally = disposeMethod.substring(finallyIdx);
        assertTrue(afterFinally.contains("setVisible(false)"),
            "finally block must call setVisible(false) to guarantee dialog closes");
    }

    @Test
    @DisplayName("disposeDialog() calls dispose() in finally block")
    void disposeDialogCallsDisposeInFinally() throws IOException {
        String source = readSourceFile();
        String disposeMethod = extractMethod(source, "disposeDialog");
        assertNotNull(disposeMethod);

        int finallyIdx = disposeMethod.indexOf("finally");
        assertTrue(finallyIdx > 0);
        String afterFinally = disposeMethod.substring(finallyIdx);
        assertTrue(afterFinally.contains(".dispose()"),
            "finally block must call dispose() to release dialog resources");
    }

    @Test
    @DisplayName("disposeDialog() nulls sCurrentlyOpenTimeManagement in finally")
    void disposeDialogNullsStaticFieldInFinally() throws IOException {
        String source = readSourceFile();
        String disposeMethod = extractMethod(source, "disposeDialog");
        assertNotNull(disposeMethod);

        int finallyIdx = disposeMethod.indexOf("finally");
        assertTrue(finallyIdx > 0);
        String afterFinally = disposeMethod.substring(finallyIdx);
        assertTrue(afterFinally.contains("sCurrentlyOpenTimeManagement = null"),
            "finally block must null sCurrentlyOpenTimeManagement to prevent stale references");
    }

    @Test
    @DisplayName("disposeDialog() catches exceptions from storeDialogPositions")
    void disposeDialogCatchesStoreException() throws IOException {
        String source = readSourceFile();
        String disposeMethod = extractMethod(source, "disposeDialog");
        assertNotNull(disposeMethod);

        assertTrue(disposeMethod.contains("storeDialogPositions"),
            "disposeDialog must call storeDialogPositions");
        assertTrue(disposeMethod.contains("catch (Exception"),
            "disposeDialog must catch exceptions to prevent propagation");
    }

    @Test
    @DisplayName("disposeDialog() saves calendar state before dispose()")
    void disposeDialogSavesCalendarStateBeforeDispose() throws IOException {
        String source = readSourceFile();
        String disposeMethod = extractMethod(source, "disposeDialog");
        assertNotNull(disposeMethod);

        int calendarSaveIdx = disposeMethod.indexOf("lastDate = getCalendar()");
        int disposeIdx = disposeMethod.indexOf(".dispose()");
        assertTrue(calendarSaveIdx > 0,
            "disposeDialog must save lastDate before dispose");
        assertTrue(calendarSaveIdx < disposeIdx,
            "Calendar state must be saved BEFORE dispose() (calendar becomes invalid after dispose)");
    }

    private String readSourceFile() throws IOException {
        Path path = findProjectRoot().resolve(SOURCE_PATH);
        assertTrue(Files.exists(path), "TimeManagement.java must exist at: " + path);
        return Files.readString(path);
    }

    /**
     * Extracts a method body from source by matching balanced braces.
     */
    private String extractMethod(String source, String methodName) {
        // Find the method signature
        int idx = source.indexOf("void " + methodName + "()");
        if (idx < 0) return null;

        // Find the opening brace
        int braceStart = source.indexOf('{', idx);
        if (braceStart < 0) return null;

        // Match balanced braces
        int depth = 1;
        int pos = braceStart + 1;
        while (pos < source.length() && depth > 0) {
            char c = source.charAt(pos);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            pos++;
        }
        return source.substring(idx, pos);
    }

    private Path findProjectRoot() {
        Path current = Paths.get(System.getProperty("user.dir"));
        while (current != null) {
            if (Files.exists(current.resolve("settings.gradle.kts"))) {
                return current;
            }
            current = current.getParent();
        }
        return Paths.get(System.getProperty("user.dir"));
    }
}
