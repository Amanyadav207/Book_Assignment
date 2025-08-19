package book_Assignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DatasetReader {

    public static List<Book> readBooks(Path csvPath) throws IOException {
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // header
            if (line == null) {
                return books;
            }
            while ((line = reader.readLine()) != null) {
                List<String> fields = parseCsvLine(line);
                if (fields.size() != 7) {
                    // Try to read additional lines if quotes are unbalanced
                    StringBuilder sb = new StringBuilder(line);
                    while (!hasBalancedQuotes(sb.toString())) {
                        String cont = reader.readLine();
                        if (cont == null) break;
                        sb.append("\n").append(cont);
                        fields = parseCsvLine(sb.toString());
                        if (fields.size() == 7 && hasBalancedQuotes(sb.toString())) break;
                    }
                    if (fields.size() != 7) {
                        continue; // skip malformed
                    }
                }

                try {
                    String title = fields.get(0);
                    String author = fields.get(1);
                    double userRating = Double.parseDouble(fields.get(2));
                    int reviews = Integer.parseInt(fields.get(3));
                    int price = Integer.parseInt(fields.get(4));
                    int year = Integer.parseInt(fields.get(5));
                    String genre = fields.get(6);
                    books.add(new Book(title, author, userRating, reviews, price, year, genre));
                } catch (Exception parseEx) {
                    // skip malformed record
                }
            }
        }
        return books;
    }

    private static boolean hasBalancedQuotes(String s) {
        boolean inQuotes = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"') {
                // toggle unless escaped by double quotes per RFC4180
                if (inQuotes && i + 1 < s.length() && s.charAt(i + 1) == '"') {
                    i++; // skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            }
        }
        return !inQuotes;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>(8);
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++; // skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        // trim whitespace around unquoted fields
        for (int i = 0; i < result.size(); i++) {
            result.set(i, result.get(i).trim());
        }
        return result;
    }
}


