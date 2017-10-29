package com.meg.atable.api.model;

/**
 * Created by margaretmartin on 24/10/2017.
 */
public enum ListType {

    BaseList("BaseList"),
    PickUpList("PickUpList"),
    InProcess("InProcess"),
    ActiveList("ActiveList");

    private final String display;


    ListType(String displayName) {
        this.display = displayName;
    }

    public String getDisplayName() {
        return display;
    }


}
