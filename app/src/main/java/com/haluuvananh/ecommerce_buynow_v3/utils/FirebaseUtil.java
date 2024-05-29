package com.haluuvananh.ecommerce_buynow_v3.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class FirebaseUtil {

    public static CollectionReference getCategories(){
        return FirebaseFirestore.getInstance().collection("categories");
    }

    public static CollectionReference getProducts() {
        return FirebaseFirestore.getInstance().collection("products");
    }

    public static CollectionReference getBanner(){
        return FirebaseFirestore.getInstance().collection("banners");
    }

    public static CollectionReference getCartItems(){
        return FirebaseFirestore.getInstance().collection("cart").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection("items");
    }

    public static CollectionReference getWishlistItems(){
        return FirebaseFirestore.getInstance().collection("wishlists").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection("items");
    }

    public static CollectionReference getOrderItems(){
        return FirebaseFirestore.getInstance().collection("orders").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).collection("items");
    }

    public static CollectionReference getReviews(int pid){
        return FirebaseFirestore.getInstance().collection("reviews").document(pid+"").collection("review");
    }

    public static DocumentReference getDetails(){
        return FirebaseFirestore.getInstance().collection("dashboard").document("details");
    }

    public static StorageReference getProductImageReference(String id){
        return FirebaseStorage.getInstance().getReference().child("product_images").child(id);
    }

    public static StorageReference getCategoryImageReference(String id){
        return FirebaseStorage.getInstance().getReference().child("category_images").child(id);
    }

    public static StorageReference getBannerImageReference(String id){
        return FirebaseStorage.getInstance().getReference().child("banner_images").child(id);
    }
}
