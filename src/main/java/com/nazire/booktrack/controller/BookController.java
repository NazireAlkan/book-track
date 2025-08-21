package com.nazire.booktrack.controller;

import com.nazire.booktrack.dto.BookDTO;
import com.nazire.booktrack.model.Book;
import com.nazire.booktrack.service.FavoriteBookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/books")
public class BookController {

    private final FavoriteBookService favoriteBookService;

    public BookController(FavoriteBookService favoriteBookService) {
        this.favoriteBookService = favoriteBookService;
    }

    @PostMapping
    public ResponseEntity<String> addBook(@RequestBody Book book){
        Book savedBook = favoriteBookService.addBook(book);
        return ResponseEntity.ok("Eklenen kitabın id'si: " + savedBook.getId());
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<String> addFavorite(@PathVariable Long bookId){
        Book favoriteBook = favoriteBookService.addFavoriteBook(bookId);
        return ResponseEntity.ok("Favorilere eklenen kitabın id'si: " + favoriteBook.getId() + " ismi: " + favoriteBook.getTitle());
    }

    @GetMapping("/search")
    public ResponseEntity<String> searchBook(@RequestParam String title){ //Sefiller
       BookDTO searchedBook =  favoriteBookService.searchBook(title);
        return ResponseEntity.ok(searchedBook.getAuthor());
    }


    @GetMapping
    public Set<BookDTO> getFavorites(){
        return favoriteBookService.getBooks();
    }
}
