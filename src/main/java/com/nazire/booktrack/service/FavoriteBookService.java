package com.nazire.booktrack.service;

import com.nazire.booktrack.dto.BookDTO;
import com.nazire.booktrack.model.Book;
import com.nazire.booktrack.model.User;
import com.nazire.booktrack.repository.BookRepository;
import com.nazire.booktrack.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteBookService {
    //User ve Book repository ekle +
    /*
    Favroi kitap ekleme:
    kullanıcı adını getir
    kullanıcıyı username göre bul
    kullanıcı bulunamazsa hata fırlat

    Kitabı ara
    kitap yoksa hata fırlat

    kullanıcı üzerinden favori kitabı ekle
    kullanıcıyı kaydet

     */
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public FavoriteBookService(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public Book addBook(Book book){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException(username + " adlı kullanıcı bulunamadı"));

        user.getAddBooks().add(book);
        bookRepository.save(book);
        userRepository.save(user);

        return book;
    }

    public Book addFavoriteBook(Long bookId){ //1
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); //nazirelkn@gmail.com
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException(username + " adlı kullanıcı bulunamadı"));

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Kitaplığınızda " + bookId + "'li kitap bulunamadı!"));

        user.getFavoritesBooks().add(book);
        bookRepository.save(book);

        return book;
        //userRepository.save(user);
    }

    public BookDTO searchBook(String title){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException(username + " adlı kullanıcı bulunamadı!"));

        return bookRepository.findByTitle(title).orElseThrow(() -> new RuntimeException("Kitap bulunamadı!"));
    }


    public Set<BookDTO> getBooks(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException(username + " adlı kullanıcı bulunamadı"));

        return user.getAddBooks().stream()
                .map(book -> new BookDTO(book.getTitle(), book.getAuthor()))
                .collect(Collectors.toSet());
    }
}
