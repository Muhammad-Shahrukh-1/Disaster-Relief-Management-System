import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    Main Main = new Main();
    private static final String TEST_FILE = "testfile.txt";

    @Test
    public static void setup() {
        // Create the file for testing purposes before running the tests
        try {
            File testFile = new File(TEST_FILE);
            if (!testFile.exists()) {
                testFile.createNewFile();
            }
        } catch (IOException e) {
            fail("Setup failed: " + e.getMessage());
        }
    }

    @AfterAll
    public static void cleanup() {
        // Cleanup after all tests are done
        File testFile = new File(TEST_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

}
