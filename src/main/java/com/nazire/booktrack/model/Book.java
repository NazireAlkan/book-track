package com.nazire.booktrack.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    @ManyToMany(mappedBy = "favoritesBooks")
    private Set<User> users = new HashSet<>();

    @ManyToMany(mappedBy = "addBooks")
    private Set<User> userHaveBooks = new HashSet<>();

    public Book(){

    }

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<User> getUserHaveBooks() {
        return userHaveBooks;
    }

    public void setUserHaveBooks(Set<User> userHaveBooks) {
        this.userHaveBooks = userHaveBooks;
    }
}
