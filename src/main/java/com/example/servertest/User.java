package com.example.servertest;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    String id;
    String password;
    String firstName;
    String lastName;
    ArrayList<Item> itemsPosted;
    ArrayList<Item> itemsBought;

    public User(String id, String password, String firstName, String lastName){
        this.id = id;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.itemsPosted = new ArrayList<>();
        this.itemsBought = new ArrayList<>();
    }

    public User(){

    }
}
