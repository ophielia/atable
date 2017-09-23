package com.meg.atable.api.model;

import java.util.ArrayList;
import java.util.List;

public class Dish {

    private Long dish_id;

    private String dishName;

    private String description;

    private List<Tag> tags = new ArrayList<>();

    private Long userId;

    public Dish(Long userId, String dishName) {
        this.userId = userId;
        this.dishName = dishName;
    }

    public Dish() {
        // empty constructor
    }

    public Dish(Long userId, String dishName, String description) {
        this.userId = userId;
        this.dishName = dishName;
        this.description = description;
    }

    public Dish(Long id) {
        this.dish_id = id;
    }

    public Long getId() {
        return dish_id;
    }


    public String getDishName() {
        return dishName;
    }

    public Dish dishName(String dishName) {
        this.dishName = dishName;
        return this;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public Dish tags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Dish userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Dish description(String description) {
        this.description = description;
        return this;
    }

}