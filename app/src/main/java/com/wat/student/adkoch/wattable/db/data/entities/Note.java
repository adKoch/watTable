package com.wat.student.adkoch.wattable.db.data.entities;

public final class Note {
    private String title;
    private String description;
    private String author;
    private boolean isPublic;

    public boolean getIsPublic(){
        return isPublic;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public Note(String title, String description, String author, boolean isPublic) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.isPublic = isPublic;
    }
}
