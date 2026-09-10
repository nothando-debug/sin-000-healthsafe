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

                Integer beds = null;
                String note = null;
                String rawBeds = columns[3].trim();
                

                try {
                    int parsedBeds = Integer.parseInt(columns[3].trim());
                    if (parsedBeds >= 0 && parsedBeds <= 100) {
                        beds = parsedBeds;
                    } else {
                        note = "bedsAvailable was out of realistic bounds ('" + rawBeds + "') — flagged for follow-up";                    }
        
                } catch (NumberFormatException e) {
                    note = "bedsAvailable was non-numeric ('" + rawBeds + "') — flagged for follow-up";                }

                Ward cleanWard = new Ward(wardId, wing, department, beds, note);
                cleanWards.put(wardId, cleanWard);
            }
        } catch (IOException e) {
            System.err.println("Critical error reading the file: " + e.getMessage());
        }
        
        return cleanWards;
    }
    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return titleCase.toString().trim();
    }
}