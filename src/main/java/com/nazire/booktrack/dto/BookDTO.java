package com.nazire.booktrack.dto;

public class BookDTO {
    public String title;
    public String author;

    public BookDTO(String title,String author) {

        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
