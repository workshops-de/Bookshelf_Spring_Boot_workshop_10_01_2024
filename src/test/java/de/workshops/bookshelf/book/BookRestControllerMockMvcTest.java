package de.workshops.bookshelf.book;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.workshops.bookshelf.ObjectMapperTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ObjectMapperTestConfiguration.class)
@ActiveProfiles("test")
@WithMockUser
class BookRestControllerMockMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void shouldGetAllBooks() throws Exception {
        final var mvcResult = mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andReturn();

        final var jsonPayloadAsString = mvcResult.getResponse().getContentAsString();

        List<Book> books = mapper.readValue(jsonPayloadAsString, new TypeReference<>() {
        });

        assertThat(books).size().isGreaterThanOrEqualTo(3);
    }
    @Test
    void shouldGetBookByAuthor_ok() throws Exception {
        mockMvc.perform(get("/book?author=Rob"))
                .andExpect(status().isOk());
    }
    @Test
    void shouldGetBookByAuthor_noContent() throws Exception {
        mockMvc.perform(get("/book?author=ABC"))
                .andExpect(status().isNoContent());
    }
    @Test
    void shouldGetBookByAuthor_badRequest() throws Exception {
        mockMvc.perform(get("/book?author=Ro"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateNewBook() throws Exception {
        var isbn = "123456789";
        var title = "My first book";
        var author = "Birgit Kratz";
        var description = "Inside the head of Birgit";

        mockMvc.perform(post("/book")
                        .content("""
                                {
                                    "isbn": "%s",
                                    "title": "%s",
                                    "author": "%s",
                                    "description": "%s"
                                }""".formatted(isbn, title, author, description))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
}