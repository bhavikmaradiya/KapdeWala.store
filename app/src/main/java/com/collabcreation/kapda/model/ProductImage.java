package com.collabcreation.kapda.model;

import java.io.Serializable;

public class ProductImage implements Serializable {
    private int productColor;
    private String productImage;

    public ProductImage(int productColor, String productImage) {
        this.productColor = productColor;
        this.productImage = productImage;
    }

    public ProductImage() {
    }

    public int getProductColor() {
        return productColor;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductColor(int productColor) {
        this.productColor = productColor;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
}
