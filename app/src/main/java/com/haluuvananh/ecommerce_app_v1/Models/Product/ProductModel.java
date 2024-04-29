package com.haluuvananh.ecommerce_app_v1.Models.Product;

import java.io.Serializable;

public class ProductModel implements Serializable {

    private String img_url;
    private String brand;
    private String category;
    private String color;
    private String description;
    private String name;
    private int price;
    private String rating;

    public ProductModel() {
    }

    public ProductModel(String img_url, String brand, String category, String color, String description, String name, int price, String rating) {
        this.img_url = img_url;
        this.brand = brand;
        this.category = category;
        this.color = color;
        this.description = description;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
