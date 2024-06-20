package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyDTO {
    private String _id;
    private int index;
    private String balance;
    private int age;
    private String favoriteFruit;

    @Override
    public String toString(){
        return "{" +
                "_id:" + _id + "," +
                "index:" + index + "," +
                "balance:" + balance + "," +
                "age:" + age + "," +
                "favoriteFruit:" + favoriteFruit +
                "}";
    }
}
