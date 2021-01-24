package com.startup.eventsearcher.main.ui.events.model;

public class Category {

    private final String categoryName;
    private final int categoryImage;

    public Category(String categoryName, int categoryImage){
        this.categoryImage = categoryImage;
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getCategoryImage() {
        return categoryImage;
    }
}
