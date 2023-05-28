package back_tests;

import back.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MainTest {
    private ObjectMapper objectMapper;
    private File tempFile;

    /**We use @see <a href="https://junit.org/junit4/javadoc/4.12/org/junit/Before.html">@Before</a> and  @see <a href="https://junit.org/junit4/javadoc/4.12/org/junit/After.html"> @After </a>
     * to respectively create a json and test it firstly (done with Before) before each test,
     * and, secondly , such json
     * is deleted after each test (done with @After)
     * **/
    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        tempFile = new File("src/test/resources/temp.json");

        // should add the resources folder if it doesn't exist
        File resourcesFolder = new File("src/test/resources");
        if (!resourcesFolder.exists()) {
            resourcesFolder.mkdirs();  // Create the "resources" folder and any necessary parent directories
        }
    }

    @After
    public void tearDown() {
        tempFile.delete();
    }

    /** The IOException can be thrown because the writeValue() and readValue()
     * throws such exception if there are any issues with reading from
     * or writing to the file.**/
    @Test
    public void testSerializationDeserialization() throws IOException {
        // Create a new back.Table instance
        Table originalTable = new Table(1, 4);
        originalTable.setAvailable(true);

        // Serialize the back.Table object to JSON and write it to a temporary file
        objectMapper.writeValue(tempFile, originalTable);

        // Deserialize the JSON from the temporary file back into a back.Table object
        Table deserializedTable = objectMapper.readValue(tempFile, Table.class);

        // Assert the values of the deserialized back.Table object
        assertEquals(originalTable.getTableNumber(), deserializedTable.getTableNumber());
        assertEquals(originalTable.getCapacity(), deserializedTable.getCapacity());
        assertEquals(originalTable.isAvailable(), deserializedTable.isAvailable());
    }
}
