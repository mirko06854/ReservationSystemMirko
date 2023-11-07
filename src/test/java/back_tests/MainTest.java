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
        assertThrows(IllegalArgumentException.class, () -> new Table(1, -4));
    }

    @Test
    public void testNegativeTableNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Table(-1, 4));
    }

    @Test
    public void testBothNegativeCapacityAndNegativeTableNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Table(-1, -4));
    }


    /**
     * Test method for validating table booking time.
     * <p>
     * This method tests the validation of table booking time using the validateTableBookingTime() method.
     * It asserts that an IllegalArgumentException is thrown for invalid time formats and values,
     * and no exception is thrown for valid time formats.
     */
    @Test
    public void testTableBookingTime() {
        assertThrows(IllegalArgumentException.class, () -> validateTableBookingTime("25:00"));
        assertThrows(IllegalArgumentException.class, () -> validateTableBookingTime("01:60"));
        assertThrows(IllegalArgumentException.class, () -> validateTableBookingTime("ab:12"));
        assertThrows(IllegalArgumentException.class, () -> validateTableBookingTime("12:3b"));
        assertThrows(IllegalArgumentException.class, () -> validateTableBookingTime("12"));
        assertThrows(IllegalArgumentException.class, () -> validateTableBookingTime("123:45"));
        assertThrows(IllegalArgumentException.class, () -> validateTableBookingTime("12:345"));

        validateTableBookingTime("01:00");
        validateTableBookingTime("12:34");
        validateTableBookingTime("24:00");
    }

    /**
     * Validates the table booking time.
     *
     * @param time the time string to be validated
     * @throws IllegalArgumentException if the time string does not match the expected format
     *                                  <p>
     *                                  We have a regex, where:
     *                                  <ul>
     *                                   <li>^ denotes the start of the string.</li>
     *                                   <li> [01]?[0-9] matches the hours part of the time. It allows for either a single digit (0-9) or a leading zero followed by a digit (00-09 or 10-19). </li>
     *                                   <li> | is the alternation operator, allowing either the previous pattern or the next pattern to match. </li>
     *                                   <li> 2[0-4] matches the hours from 20 to 24. </li>
     *                                   <li> : matches the colon separating the hours and minutes. </li>
     *                                   <li> [0-5][0-9] matches the minutes part of the time, allowing for any digit (0-9) in the tens place and any digit (0-9) in the units place (00-59). </li>
     *                                   <li> $ denotes the end of the string. </li>
     *                                  </ul>
     *                                  <p>
     *                                  In summary, this regular expression pattern ensures that the time string matches the format of a valid time between 1:00 and 24:00, where the hours can be expressed in one or two digits, and the minutes must be two digits.
     *                                  <p>
     *                                  For example, this pattern would match strings like "01:00", "12:34", or "24:00" but not "25:00", "ab:12", or "12:3b".
     */
    private void validateTableBookingTime(String time) {
        if (!time.matches("^([01]?[0-9]|2[0-4]):[0-5][0-9]$")) {
            throw new IllegalArgumentException("Invalid table booking time: " + time);
        }
    }
}
