package com.collabcreation.kapda.model;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String productThumbnail;
    private String productName, productId, productDescription;
    private float productOriginalPrice, productDeliveryPrice;
    private List<ProductImage> productImageList;
    private List<ProductColor> productColorList;
    private List<ProductSize> productSizeList;
    private boolean isFreeDeliveryAvailable, inOffer;
    private String productSubtitle;
    private String categoryId, subCategoryId;
    private int offerPercentage;

    public String getCategoryId() {
        return categoryId;
    }


    public double getPayablePrice() {
        if (isInOffer()) {
            double dis, s;
            dis = getOfferPercentage();
            s = 100 - dis;
            return (s * getProductOriginalPrice()) / 100;
        } else {
            return getProductOriginalPrice();
        }
    }

    public String getProductSubtitle() {
        return productSubtitle;
    }

    public void setProductSubtitle(String productSubtitle) {
        this.productSubtitle = productSubtitle;
    }

    public String getProductThumbnail() {
        return productThumbnail;
    }

    public void setProductThumbnail(String productThumbnail) {
        this.productThumbnail = productThumbnail;
    }

    public void setInOffer(boolean inOffer, int offerPercentage) {
        this.inOffer = inOffer;
        this.offerPercentage = offerPercentage;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public Product(String productName, String productId, float productOriginalPrice, String productThumbnail) {
        this.productName = productName;
        this.productId = productId;
        this.productOriginalPrice = productOriginalPrice;
        this.productThumbnail = productThumbnail;
    }

    public Product() {
    }


    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setProductFinalPrice(float productOriginalPrice) {
        this.productOriginalPrice = productOriginalPrice;
    }

    public void setProductDeliveryPrice(int productDeliveryPrice) {
        this.productDeliveryPrice = productDeliveryPrice;
    }

    public void setProductImageList(List<ProductImage> productImageList) {
        this.productImageList = productImageList;
    }


    public void setProductSizeList(List<ProductSize> productSizeList) {
        this.productSizeList = productSizeList;
    }

    public void setFreeDeliveryAvailable(boolean freeDeliveryAvailable) {
        isFreeDeliveryAvailable = freeDeliveryAvailable;
    }

    public int getOfferPercentage() {
        return offerPercentage;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public float getProductOriginalPrice() {
        return productOriginalPrice;
    }

    public float getProductDeliveryPrice() {
        return productDeliveryPrice;
    }

    public List<ProductImage> getProductImageList() {
        return productImageList;
    }

    public List<ProductColor> getProductColorList() {
        return productColorList;
    }

    public void setProductColorList(List<ProductColor> productColorList) {
        this.productColorList = productColorList;
    }

    public List<ProductSize> getProductSizeList() {
        return productSizeList;
    }

    public boolean isFreeDeliveryAvailable() {
        return isFreeDeliveryAvailable;
    }

    public double getProductOfferPrice() {
        if (isInOffer()) {
            double dis, s;
            dis = getOfferPercentage();
            s = 100 - dis;
            return (s * getProductOriginalPrice()) / 100;
        } else {
            return 0;
        }
    }


    public boolean isInOffer() {
        return inOffer;
    }
}
