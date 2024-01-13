package de.workshops.bookshelf.book;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
@Validated
public class BookViewController {
    private final BookService service;

    public BookViewController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public String getAllBooks(Model model) {
        final var allBooks = service.getAllBooks();

        model.addAttribute("books", allBooks);

        return "booksView";
    }
}
