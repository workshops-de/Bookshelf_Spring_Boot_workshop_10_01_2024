package de.workshops.bookshelf.book;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@WebMvcTest(controllers = BookRestController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
class BookRestControllerMockMvcWithMockitoTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookService service;

    @Captor
    ArgumentCaptor<String> isbnCaptor;

    @Test
    void shouldGetAllBooks() throws Exception {
        when(service.getAllBooks()).thenReturn(List.of(new Book(), new Book()));

        final var mvcResult = mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andReturn();

        final var jsonPayloadAsString = mvcResult.getResponse().getContentAsString();

        List<Book> books = mapper.readValue(jsonPayloadAsString, new TypeReference<>() {
        });

        assertThat(books).hasSize(2);
    }

    @Test
    void shouldGetBookByIsbn() throws Exception {
        when(service.getBookByIsbn(isbnCaptor.capture())).thenReturn(new Book());

        final var mvcResult = mockMvc.perform(get("/book/123456789"))
                .andExpect(status().isOk())
                .andReturn();

        final var isbnCaptorValue = isbnCaptor.getValue();

        assertThat(isbnCaptorValue).isEqualTo("123456789");
    }
}