package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JsonDTO {
    List<PropertyDTO> data;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{data:[");
        for(int i=0; i<data.size();i++){
            sb.append("{_id:\"" + data.get(i).get_id() + "\",");
            sb.append("index:\"" + data.get(i).getIndex() + "\",");
            sb.append("balance:\"" + data.get(i).getBalance() + "\",");
            sb.append("age:\"" + data.get(i).getAge() + "\",");
            sb.append("favoriteFruit:\"" + data.get(i).getFavoriteFruit() + "\"},");
        }
        System.out.println("len: " + sb.length());
        sb.deleteCharAt(sb.length()-1);
        sb.append("]}");
        return sb.toString();
    }
}
