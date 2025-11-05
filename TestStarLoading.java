import com.fasterxml.jackson.databind.ObjectMapper;
import com.dqrapps.planetarium.logic.model.Stars;
import java.io.File;

public class TestStarLoading {
    public static void main(String[] args) {
        try {
            ObjectMapper om = new ObjectMapper();
            System.out.println("Current working directory: " + System.getProperty("user.dir"));

            File testFile = new File("stars_sao_fk5.json");
            System.out.println("File exists: " + testFile.exists());
            System.out.println("File path: " + testFile.getAbsolutePath());
            System.out.println("File size: " + testFile.length() + " bytes");

            if (testFile.exists()) {
                Stars stars = om.readerFor(Stars.class).readValue(testFile);
                System.out.println("Successfully loaded: " + stars.getStarList().size() + " stars");
                System.out.println("First star: " + stars.getStarList().get(0).getName());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
