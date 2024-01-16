package de.workshops.bookshelf.book;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class BookService {

    private final BookJpaRepository repository;

    BookService(BookJpaRepository repository) {
        this.repository = repository;
    }

    List<Book> getAllBooks() {
        return repository.findAll();
    }

    Book getBookByIsbn(String isbnValue) throws BookException {
        return repository.findByIsbn(isbnValue);
//        return repository.findAllBooks().stream()
//                .filter(book -> book.getIsbn().equals(isbnValue))
//                .findFirst().orElseThrow(() ->new BookException("Could not find book for ISBN"));
    }

    Book getBookByAuthor(String author) {
        return repository.findAll().stream()
                .filter(book -> book.getAuthor().contains(author))
                .findFirst().orElse(null);
    }

    List<Book> searchBooks(BookSearchRequest bookSearchRequest) {
        return repository.search(bookSearchRequest.author(), bookSearchRequest.title());
//        return repository.findAllBooks().stream()
//                .filter(book -> book.getAuthor().contains(bookSearchRequest.author())
//                                || book.getTitle().startsWith(bookSearchRequest.title()))
//                .toList();
    }

    public Book createBook(Book newBook) {
        return repository.save(newBook);
//        repository.createBook(newBook);
//        return newBook;
    }
}
