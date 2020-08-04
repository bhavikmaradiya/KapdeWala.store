package com.collabcreation.kapda.model;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;

public class Common {
    public static final String KEY_USER = "user";
    public static final String KEY_PRODUCT = "keyProduct";
    public static final String SELECTED_COLOR = "selectedColor";
    public static final String ADDRESS = "address";
    public static final String ADDRESS_POS = "addressPos";
    public static final String FROM = "from";
    public static final String PROMO = "promo";
    private static final String USER_REF = "users";
    private static final String BANNER_REF = "banners";
    private static final String CART_REF = "cartList";
    private static final String PROMO_REF = "promoList";
    private static final String ADDRESS_REF = "addressList";
    private static final String POSTCODE = "postCode";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String LANDMARK = "landMark";
    private static final String PHONENO = "phoneNo";
    public static User CURRENT_USER = null;
    private static final String CATEGORY_REF = "categoryList";
    private static final String SUB_CATEGORY_REF = "subCategoryList";
    private static final String TOKEN_REF = "tokens";
    public static final String USER = "user";
    private static final String PRODUCT_REF = "products";
    private static final String WISHLIST_REF = "wishList";
    private static final String PINCODECHECK_URL = "https://apiv2.shiprocket.in/v1/external/open/postcode/details?postcode=";

    public static DatabaseReference getWishListRefOf(String uId) {
        return FirebaseDatabase.getInstance().getReference(WISHLIST_REF).child(uId);
    }

    private static String getUid(Context context){
        return context.getSharedPreferences(Common.USER, MODE_PRIVATE).getString(Common.USER,"");
    }

    public static void changeAddress(Context context, Address address) {
        context.getSharedPreferences(getUid(context), MODE_PRIVATE).edit().putString(USER, address.getReceiverName()).putString(PHONENO, address.getPhoneNo()).putString(LANDMARK, address.getLandMark()).putString(ADDRESS, address.getAddress()).putString(POSTCODE, address.getPostcode()).putString(CITY, address.getCity()).putString(STATE, address.getState()).apply();
    }

    public static String checkPincodeStatus(String pincode) {
        return PINCODECHECK_URL + pincode;
    }

    public static DatabaseReference getWishListRef() {
        return FirebaseDatabase.getInstance().getReference(WISHLIST_REF).child(CURRENT_USER.getUserId());
    }

    public static String getDefaultUser(Context context) {
        return context.getSharedPreferences(getUid(context), MODE_PRIVATE).getString(USER, "");
    }

    public static String getDefaultPhoneNo(Context context) {
        return context.getSharedPreferences(getUid(context), MODE_PRIVATE).getString(PHONENO, "");
    }

    public static String getDefaultPincode(Context context) {
        return context.getSharedPreferences(getUid(context), MODE_PRIVATE).getString(Common.POSTCODE, "");
    }

    public static String getDefaultAddress(Context context) {
        return context.getSharedPreferences(getUid(context), MODE_PRIVATE).getString(Common.ADDRESS, "");
    }

    public static String getDefaultCity(Context context) {
        return context.getSharedPreferences(getUid(context), MODE_PRIVATE).getString(CITY, "");
    }

    public static String getDefaultState(Context context) {
        return context.getSharedPreferences(getUid(context), MODE_PRIVATE).getString(STATE, "");
    }

    public static String getDefaultLandmark(Context context) {
        return context.getSharedPreferences(getUid(context), MODE_PRIVATE).getString(LANDMARK, "");
    }

    public static int getDefaultPos(Context context) {
        return context.getSharedPreferences(getUid(context), MODE_PRIVATE).getInt(ADDRESS_POS, -1);
    }

    public static DatabaseReference getProductRef() {
        return FirebaseDatabase.getInstance().getReference(PRODUCT_REF);
    }

    public static DatabaseReference getProductOf(String productId) {
        return FirebaseDatabase.getInstance().getReference(PRODUCT_REF).child(productId);
    }

    public static DatabaseReference getAddressRef(String uId) {
        return FirebaseDatabase.getInstance().getReference(ADDRESS_REF).child(uId);
    }

    public static DatabaseReference getUserRefOf(String userId) {
        return FirebaseDatabase.getInstance().getReference(USER_REF).child(userId);
    }

    public static DatabaseReference getBannerRef() {
        return FirebaseDatabase.getInstance().getReference(BANNER_REF);
    }


    public static DatabaseReference getTokenRefOf(String uId) {
        return FirebaseDatabase.getInstance().getReference(TOKEN_REF).child(uId);
    }

    public static DatabaseReference getCategoriesRef() {
        return FirebaseDatabase.getInstance().getReference(CATEGORY_REF);
    }

    public static DatabaseReference getSubCategoryOf(String categoryId) {
        return FirebaseDatabase.getInstance().getReference(CATEGORY_REF).child(categoryId).child(SUB_CATEGORY_REF);
    }

    public static DatabaseReference getCartRefOf(String itemId) {
        return FirebaseDatabase.getInstance().getReference(CART_REF).child(CURRENT_USER.getUserId()).child(itemId);
    }

    public static DatabaseReference getCartRef() {
        return FirebaseDatabase.getInstance().getReference(CART_REF).child(CURRENT_USER.getUserId());
    }

    public static DatabaseReference getPromoRef(String promoId) {
        return FirebaseDatabase.getInstance().getReference(PROMO_REF).child(promoId);
    }

    public static DatabaseReference getPromoRef() {
        return FirebaseDatabase.getInstance().getReference(PROMO_REF);
    }

    public interface RegisterCompleteListener {

        void onComplete(boolean isComplete);
    }

    // TODO: 03-02-2020 Sample BannersList
//    getBannerRef().child(getBannerRef().push().getKey())
//            .setValue(new Banner("https://storiesflistgv2.blob.core.windows.net/stories/2018/10/BBDTipSheet_mainbanner.jpg", "b1", "banner1"));
//    getBannerRef().child(getBannerRef().push().getKey())
//            .setValue(new Banner("https://storiesflistgv2.blob.core.windows.net/stories/2019/09/Banner-01-800x360.jpg", "b2", "banner2"));
//    getBannerRef().child(getBannerRef().push().getKey())
//            .setValue(new Banner("https://sac.flipkart.net/wp-content/uploads/2018/04/blog-banner.jpg", "b3", "banner3"));
//    getBannerRef().child(getBannerRef().push().getKey())
//            .setValue(new Banner("https://sac.flipkart.net/wp-content/uploads/2018/06/PLA-BANNER-1.4.png", "b4", "banner4"));
//    getBannerRef().child(getBannerRef().push().getKey())
//            .setValue(new Banner("https://storiesflistgv2.blob.core.windows.net/stories/2016/09/daily_offers_banner_Final.jpg", "b5", "banner5"));

    // TODO: 03-02-2020 Sample Product
//    List<ProductColor> productColorList = new ArrayList<>();
//        productColorList.clear();
//        productColorList.add(new ProductColor(Color.RED, 1));
//        productColorList.add(new ProductColor(Color.WHITE, 1));
//        productColorList.add(new ProductColor(Color.BLACK, 1));
//    List<ProductImage> productImageList = new ArrayList<>();
//        productImageList.clear();
//        productImageList.add(new ProductImage(Color.RED, "https://cdn.shopify.com/s/files/1/2222/6375/products/weird_800x.png?v=1506086327"));
//        productImageList.add(new ProductImage(Color.WHITE, "https://m.media-amazon.com/images/I/B1JMiB+-dyS._CLa%7C2140,2000%7C618ZudYXLyL.png%7C0,0,2140,2000+645.0,529.0,809.0,971.0.png"));
//        productImageList.add(new ProductImage(Color.BLACK, "https://www.kindpng.com/picc/m/63-635260_six-pack-of-peaks-t-shirt-hd-png.png"));
//    List<ProductSize> productSizeList = new ArrayList<>();
//        productSizeList.clear();
//        productSizeList.add(new ProductSize("S", 3));
//    String key = getProductRef().push().getKey();
//    Product product = new Product("Friends For You Half Sleeve T-Shirt", key, 250, "https://m.media-amazon.com/images/I/B1JMiB+-dyS._CLa%7C2140,2000%7C618ZudYXLyL.png%7C0,0,2140,2000+645.0,529.0,809.0,971.0.png");
//        product.setProductColorList(productColorList);
//        product.setProductImageList(productImageList);
//        product.setProductSizeList(productSizeList);
//        product.setInOffer(false);
//    getProductRef().child(key).setValue(product);

}
