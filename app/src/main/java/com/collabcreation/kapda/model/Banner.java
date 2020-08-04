package com.collabcreation.kapda.model;

public class Banner {
    String bannerUrl, bannerName, bannerId;

    public Banner() {
    }

    public Banner(String bannerUrl, String bannerName, String bannerId) {
        this.bannerUrl = bannerUrl;
        this.bannerName = bannerName;
        this.bannerId = bannerId;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public String getBannerName() {
        return bannerName;
    }

    public String getBannerId() {
        return bannerId;
    }
}
