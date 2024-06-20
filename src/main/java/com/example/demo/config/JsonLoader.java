package com.example.demo.config;

import com.example.demo.dto.JsonDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

@Configuration
//@PropertySource("classpath:properties.json")
public class JsonLoader {

    public JsonDTO getJsonData(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ClassPathResource resource = new ClassPathResource("properties.json");
            InputStream inputStream = resource.getInputStream();
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found! " + fileName);
            } else {
                // Parse JSON file to Java object
                System.out.println("file found");
                JsonDTO json = objectMapper.readValue(inputStream, JsonDTO.class);
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

