package book_Assignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BookRepository {
    private final List<Book> books;
    private final Map<String, List<Book>> authorToBooks;
    private final Map<Integer, List<Book>> ratingToBooks; 
    private final Set<String> authorSet;

    public BookRepository(List<Book> books) {
        this.books = new ArrayList<>(books);
        this.authorToBooks = new HashMap<>();
        this.ratingToBooks = new HashMap<>();
        this.authorSet = new HashSet<>();
        index();
    }

    private void index() {
        for (Book b : books) {
            String authorKey = normalizeKey(b.getAuthor());
            authorToBooks.computeIfAbsent(authorKey, k -> new ArrayList<>()).add(b);
            int ratingKey = toOneDecimalKey(b.getUserRating());
            ratingToBooks.computeIfAbsent(ratingKey, k -> new ArrayList<>()).add(b);
            authorSet.add(b.getAuthor());
        }

        Comparator<Book> byYearThenTitle = Comparator
                .comparingInt(Book::getYear)
                .thenComparing(Book::getTitle);
        for (List<Book> list : authorToBooks.values()) {
            list.sort(byYearThenTitle);
        }
        for (List<Book> list : ratingToBooks.values()) {
            list.sort(byYearThenTitle);
        }
    }

    private String normalizeKey(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private int toOneDecimalKey(double rating) {
        return (int) Math.round(rating * 10.0);
    }

    
    public int countBooksByAuthor(String author) {
        List<Book> list = authorToBooks.get(normalizeKey(author));
        return list == null ? 0 : list.size();
    }

    public Set<String> getAllAuthors() {
        return Collections.unmodifiableSet(authorSet);
    }

    public List<String> getBookTitlesByAuthor(String author) {
        List<Book> list = authorToBooks.get(normalizeKey(author));
        if (list == null) return Collections.emptyList();
        return list.stream().map(Book::getTitle).collect(Collectors.toList());
    }

    public List<Book> getBooksByExactRating(double rating) {
        List<Book> list = ratingToBooks.get(toOneDecimalKey(rating));
        if (list == null) return Collections.emptyList();
        return list;
    }

    public List<String> getTitlesAndPricesByAuthor(String author) {
        List<Book> list = authorToBooks.get(normalizeKey(author));
        if (list == null) return Collections.emptyList();
        return list.stream()
                .map(b -> b.getTitle() + " - $" + b.getPrice())
                .collect(Collectors.toList());
    }

    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(books);
    }
}


