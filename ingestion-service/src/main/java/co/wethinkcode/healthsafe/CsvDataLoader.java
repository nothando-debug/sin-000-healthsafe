package co.wethinkcode.healthsafe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CsvDataLoader {

    public Map<String, Ward> loadWards(String filePath) {
        Map<String, Ward> cleanWards = new HashMap<>();
        String line;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); 
            
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length < 4) continue; 
                
                String wardId = columns[0].trim().toUpperCase();
                String wing = columns[1].trim().toUpperCase();
                String department = columns[2].trim().toUpperCase();
                
                int beds = 0; 
                try {
                    int parsedBeds = Integer.parseInt(columns[3].trim());
                    beds = Math.max(parsedBeds, 0); 
                } catch (NumberFormatException e) {
                    System.out.println("Invalid bed count for " + wardId + ". Defaulting to 0.");
                }
                
                Ward cleanWard = new Ward(wardId, wing, department, beds);
                cleanWards.put(wardId, cleanWard);
            }
        } catch (IOException e) {
            System.err.println("Critical error reading the file: " + e.getMessage());
        }
        
        return cleanWards;
    }
}