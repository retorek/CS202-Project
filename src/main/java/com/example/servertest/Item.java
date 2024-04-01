package com.example.servertest;


import java.io.Serializable;

public class Item implements Serializable {

    protected String name;
    protected float price;
    protected boolean isAvailable;

    public Item(String name, float price, boolean isAvailable){
        this.name = name;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    public Item(){

    }
}
