package back;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**This class is the main class.
 * We have an <b>ObjectMapper</b>
 * that is used for serialize and deserialize Json to back.Table Object
 * and vice-versa
 * **/
public class Main {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Create a new back.Table instance
        Table table = new Table(3, 4);

        try {
            // Serialize the Table object to JSON and write it to a file
            objectMapper.writeValue(new File("src/main/resources/tables.json"), table);

            // Deserialize the JSON from the file back into a Table object
            Table deserializedTable = objectMapper.readValue(new File("src/main/resources/tables.json"), Table.class);

            // Print the deserialized Table object
            System.out.println("Deserialized back.Table: " + deserializedTable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



