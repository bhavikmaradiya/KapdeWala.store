package com.collabcreation.kapda.model;

public class CartItem {
    private String itemId, image;
    private String productId;
    private int color, quantity;
    private String size;
    private int totalQuantity;

    public CartItem() {
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public CartItem(String itemId, String product, int color, int quantity, String size) {
        this.productId = product;
        this.color = color;
        this.quantity = quantity;
        this.size = size;
        this.itemId = itemId;
    }




    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getItemId() {
        return itemId;
    }

    public String getProduct() {
        return productId;
    }

    public void setProduct(String productId) {
        this.productId = productId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
