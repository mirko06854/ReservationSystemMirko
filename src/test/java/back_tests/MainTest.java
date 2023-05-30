package back_tests;

import back.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class MainTest {
    private ObjectMapper objectMapper;
    private File tempFile;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        tempFile = new File("src/test/resources/temp.json");

        File resourcesFolder = new File("src/test/resources");
        if (!resourcesFolder.exists()) {
            resourcesFolder.mkdirs();
        }
    }

    @After
    public void tearDown() {
        tempFile.delete();
    }

    @Test
    public void testSerializationDeserialization() throws IOException {
        Table originalTable = new Table(1, 4);
        originalTable.setAvailable(true);

        objectMapper.writeValue(tempFile, originalTable);

        Table deserializedTable = objectMapper.readValue(tempFile, Table.class);

        assertEquals(originalTable.getTableNumber(), deserializedTable.getTableNumber());
        assertEquals(originalTable.getCapacity(), deserializedTable.getCapacity());
        assertEquals(originalTable.isAvailable(), deserializedTable.isAvailable());
    }

    @Test
    public void testNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new  Table(1, -4));
    }

    @Test
    public void testNegativeTableNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Table(-1, 4));
    }

    @Test
    public void testBothNegativeCapacityAndNegativeTableNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Table(-1, -4));
    }
}
