package de.workshops.bookshelf.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record BookSearchRequest(@Size(min = 3) String author, @NotBlank String title) {
}
