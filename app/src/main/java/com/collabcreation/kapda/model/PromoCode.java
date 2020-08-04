package com.collabcreation.kapda.model;

public class PromoCode {
    private double discountPrice;
    private String categoryId;
    boolean isValidForOneTime = false, isUsed = false;
    private String promoCode, promoId;

    public PromoCode() {
    }

    public PromoCode(double discountPrice, String categoryId, boolean isValidForOneTime, boolean isUsed, String promoCode, String promoId) {
        this.discountPrice = discountPrice;
        this.categoryId = categoryId;
        this.isValidForOneTime = isValidForOneTime;
        this.isUsed = isUsed;
        this.promoCode = promoCode;
        this.promoId = promoId;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isValidForOneTime() {
        return isValidForOneTime;
    }

    public void setValidForOneTime(boolean validForOneTime) {
        isValidForOneTime = validForOneTime;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getPromoId() {
        return promoId;
    }

    public void setPromoId(String promoId) {
        this.promoId = promoId;
    }
}
