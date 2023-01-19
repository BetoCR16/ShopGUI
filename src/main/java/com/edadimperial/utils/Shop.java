package com.edadimperial.utils;

import org.bukkit.Material;

import java.util.List;

public class Shop {
    private static int shopId;
    private Material item;
    private double value;
    public Shop(int id, Material material, double price){
        shopId = id;
        item = material;
        value = price;
    }

    public int getShopId(){
        return shopId;
    }

    public Material getItem() {
        return item;
    }

    public double getPrice() {
        return value;
    }


}
