package de.workshops.bookshelf.book;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookJdbcTemplateRepository {
    private final JdbcTemplate template;
    private final JdbcClient client;

    public BookJdbcTemplateRepository(JdbcTemplate template, JdbcClient client) {
        this.template = template;
        this.client = client;
    }

    List<Book> findAllBooks() {
        return client.sql("select * from book;")
                .query(new BeanPropertyRowMapper<Book>(Book.class))
                .list();
//        return template.query("select * from book;", new BeanPropertyRowMapper<Book>(Book.class));
    }

    void createBook(Book newBook) {
        String sql = "INSERT INTO book (title, description, author, isbn) VALUES (?, ?, ?, ?);";
        template.update(sql, newBook.getTitle(), newBook.getDescription(), newBook.getAuthor(), newBook.getIsbn());
    }
}
