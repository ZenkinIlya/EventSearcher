package com.startup.eventsearcher.main.ui.events.model;

public class Category {

    private String categoryName;
    private Integer categoryImage;

    public Category(String categoryName, Integer categoryImage){
        this.categoryImage = categoryImage;
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Integer getCategoryImage() {
        return categoryImage;
    }
}
