package com.collabcreation.kapda.model;

import java.util.List;

public class Category {
    String categoryId, categoryName, categoryPhoto;
    List<SubCategory> subCategoryList;

    public Category(String categoryId, String categoryName, String categoryPhoto) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryPhoto = categoryPhoto;
    }

    public Category() {
    }

    public void setSubCategoryList(List<SubCategory> subCategoryList) {
        this.subCategoryList = subCategoryList;
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

    public List<SubCategory> getSubCategoryList() {
        return subCategoryList;
    }
}
