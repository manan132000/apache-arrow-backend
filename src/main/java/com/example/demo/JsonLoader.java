package com.example.demo;

import com.example.demo.dto.PropertyDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.InputStream;

@Component
public class JsonLoader {

    public PropertyDTO loadJson(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Get the resource as an InputStream
//            Resource resource = new ClassPathResource("classpath:" + fileName);
//            System.out.println(file.getName());
            File file = ResourceUtils.getFile("properties.json");
            System.out.println(file.getName());
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream( fileName);
//            Check if the file was found
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found! " + fileName);
            } else {
                // Parse JSON file to Java object
                System.out.println("file found");
                return objectMapper.readValue(inputStream, PropertyDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
