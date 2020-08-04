package com.collabcreation.kapda.model;

import java.io.Serializable;
import java.util.List;

public class ProductSize implements Serializable {
    private String productSize;
    private List<ProductColor> productColorList;

    public ProductSize(String productSize, List<ProductColor> productColorList) {
        this.productSize = productSize;
        this.productColorList = productColorList;
    }

    public ProductSize() {
    }

    public String getProductSize() {
        return productSize;
    }


    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public List<ProductColor> getProductColorList() {
        return productColorList;
    }

    public void setProductColorList(List<ProductColor> productColorList) {
        this.productColorList = productColorList;
    }
}
