import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import java.util.Map;
public class test_spectral_loading {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(new File("stars_1k.json"), Map.class);
        List<Map<String, Object>> stars = (List<Map<String, Object>>) data.get("objects");
        System.out.println("First 10 stars with spectral types:");
        int count = 0;
        for (Map<String, Object> star : stars) {
            if (star.containsKey("spectralType") && count < 10) {
                System.out.println(String.format("  %s: %s (spectralType: %s)", 
                    star.get("name"), 
                    star.get("mag"),
                    star.get("spectralType")));
                count++;
            }
        }
        long withSpectral = stars.stream().filter(s -> s.containsKey("spectralType")).count();
        System.out.println("\nTotal stars with spectralType field: " + withSpectral + " / " + stars.size());
    }
}
