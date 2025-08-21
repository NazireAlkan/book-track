package com.nazire.booktrack.repository;

import com.nazire.booktrack.dto.BookDTO;
import com.nazire.booktrack.model.Book;
import com.nazire.booktrack.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<BookDTO> findByTitle(String title);
}
