package com.example.servertest;


import java.io.Serializable;

public class Item implements Serializable {

    protected String name;

    protected String id;
    protected float price;
    protected boolean isAvailable;

    public Item(String name, String id, float price, boolean isAvailable){
        this.id = id;
        this.name = name;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    public Item(){

    }
}
