package de.workshops.bookshelf.book;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class BookService {

    private final BookRepository repository;

    BookService(BookRepository repository) {
        this.repository = repository;
    }

    List<Book> getAllBooks() {
        return repository.findAllBooks();
    }

    Book getBookByIsbn(String isbnValue) throws BookException {
        return repository.findAllBooks().stream()
                .filter(book -> book.getIsbn().equals(isbnValue))
                .findFirst().orElseThrow(() ->new BookException("Could not find book for ISBN"));
    }

    Book getBookByAuthor(String author) {
        return repository.findAllBooks().stream()
                .filter(book -> book.getAuthor().contains(author))
                .findFirst().orElse(null);
    }

    List<Book> searchBooks(BookSearchRequest bookSearchRequest) {
        return repository.findAllBooks().stream()
                .filter(book -> book.getAuthor().contains(bookSearchRequest.author())
                                || book.getTitle().startsWith(bookSearchRequest.title()))
                .toList();
    }

}
