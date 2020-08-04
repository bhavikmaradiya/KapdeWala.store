package com.collabcreation.kapda.model;

public class SubCategory {
    String categoryId, categoryName, categoryPhoto;

    public SubCategory(String categoryId, String categoryName, String categoryPhoto) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryPhoto = categoryPhoto;
    }

    public SubCategory() {
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryPhoto() {
        return categoryPhoto;
    }
}
