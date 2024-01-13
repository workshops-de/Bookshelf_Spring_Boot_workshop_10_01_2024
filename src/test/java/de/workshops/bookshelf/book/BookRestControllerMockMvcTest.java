package de.workshops.bookshelf.book;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BookRestControllerMockMvcTest {

    @LocalServerPort
    int port;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void shouldGetAllBooks() throws Exception {
        final var mvcResult = mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(3)))
                .andReturn();

        final var jsonPayloadAsString = mvcResult.getResponse().getContentAsString();

        List<Book> books = mapper.readValue(jsonPayloadAsString, new TypeReference<>() {
        });

        assertThat(books).hasSize(3);
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
}