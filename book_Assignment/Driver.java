package book_Assignment;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Driver {
    public static void main(String[] args) {
        Path csv;
        if (args.length > 0) {
            csv = Paths.get(args[0]);
        } else {
            Path p1 = Paths.get("resources", "data.csv");
            Path p2 = Paths.get("book_Assignment", "resources", "data.csv");
            if (Files.exists(p1)) csv = p1; else if (Files.exists(p2)) csv = p2; else csv = p1;
        }
        try {
            List<Book> books = DatasetReader.readBooks(csv);
            BookRepository repo = new BookRepository(books);

            Scanner scanner = new Scanner(System.in);
            boolean run = true;
            while (run) {
                System.out.println();
                System.out.println("Choose an option:");
                System.out.println("1. Total number of books by an author");
                System.out.println("2. List all authors");
                System.out.println("3. List all books by an author");
                System.out.println("4. List books by exact user rating");
                System.out.println("5. List titles and prices by an author");
                System.out.println("6. Print first 5 books (sanity)");
                System.out.println("0. Exit");
                System.out.print("Enter choice: ");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        System.out.print("Enter author: ");
                        String a1 = scanner.nextLine();
                        System.out.println("Total books: " + repo.countBooksByAuthor(a1));
                        break;
                    case "2":
                        repo.getAllAuthors().stream().sorted().forEach(System.out::println);
                        break;
                    case "3":
                        System.out.print("Enter author: ");
                        String a2 = scanner.nextLine();
                        List<String> titles = repo.getBookTitlesByAuthor(a2);
                        if (titles.isEmpty()) System.out.println("No books found.");
                        else titles.forEach(System.out::println);
                        break;
                    case "4":
                        System.out.print("Enter exact rating (e.g., 4.7): ");
                        try {
                            double r = Double.parseDouble(scanner.nextLine().trim());
                            List<Book> byRating = repo.getBooksByExactRating(r);
                            if (byRating.isEmpty()) System.out.println("No books found.");
                            else byRating.forEach(b -> System.out.println(b.getTitle() + " (" + b.getUserRating() + ")"));
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid rating.");
                        }
                        break;
                    case "5":
                        System.out.print("Enter author: ");
                        String a3 = scanner.nextLine();
                        List<String> priceLines = repo.getTitlesAndPricesByAuthor(a3);
                        if (priceLines.isEmpty()) System.out.println("No books found.");
                        else priceLines.forEach(System.out::println);
                        break;
                    case "6":
                        repo.getAllBooks().stream().limit(5).forEach(Book::printDetails);
                        break;
                    case "0":
                        run = false;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read dataset from: " + csv.toAbsolutePath());
            System.err.println(e.getMessage());
        }
    }
}


