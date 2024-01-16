package de.workshops.bookshelf.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookJpaRepository extends JpaRepository<Book, Long> {
    Book findByIsbn(String isbn);

    Book findByAuthorContaining(String author);

    @Query("select b from Book b where b.author like ?1 or b.title like ?2")
    List<Book> search(String author, String title);
}
