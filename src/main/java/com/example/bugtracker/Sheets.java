package com.example.bugtracker;

public class Sheets {
    private int id;
    private String title;
    private String composer;
    private int year;
    private int pages;

    //constructor
    public Sheets(int id, String title, String composer, int year, int pages) {
        this.id = id;
        this.title = title;
        this.composer = composer;
        this.year = year;
        this.pages = pages;
    }

    //getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getComposer() {
        return composer;
    }

    public int getYear() {
        return year;
    }

    public int getPages() {
        return pages;
    }


}
