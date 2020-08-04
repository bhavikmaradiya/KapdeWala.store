package com.collabcreation.kapda.model;


import java.io.Serializable;

public class ProductColor implements Serializable {
    private int productColor;
    private int productQuantity;

    public ProductColor(int productColor, int productQuantity) {
        this.productColor = productColor;
        this.productQuantity = productQuantity;
    }

    public ProductColor() {
    }

    public int getProductColor() {
        return productColor;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductColor(int productColor) {
        this.productColor = productColor;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
}
