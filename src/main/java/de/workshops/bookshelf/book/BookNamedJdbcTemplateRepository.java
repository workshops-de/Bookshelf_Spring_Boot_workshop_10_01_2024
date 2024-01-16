package de.workshops.bookshelf.book;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class BookNamedJdbcTemplateRepository {
    private final NamedParameterJdbcTemplate template;

    public BookNamedJdbcTemplateRepository(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    List<Book> findAllBooks() {
        return template.query("select * from book;", new BeanPropertyRowMapper<Book>(Book.class));
    }

    void createBook(Book newBook) {
        String sql = "INSERT INTO book (title, description, author, isbn) VALUES (:title, :description, :author, :isbn);";
        template.update(sql, Map.of(
                "description", newBook.getDescription(),
                "author", newBook.getAuthor(),
                "title", newBook.getTitle(),
                "isbn", newBook.getIsbn()
        ));
    }
}
