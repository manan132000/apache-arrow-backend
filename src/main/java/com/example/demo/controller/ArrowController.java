package com.example.demo.controller;

import com.example.demo.config.JsonLoader;
import com.example.demo.dto.JsonDTO;
import org.apache.coyote.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowStreamWriter;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.VarCharVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin("*")
public class ArrowController {

    @Autowired
    private JsonLoader jsonLoader;

    @GetMapping("/getArrowData")
    public void getArrowData(HttpServletResponse response) {

        try (BufferAllocator allocator = new RootAllocator(Long.MAX_VALUE)) {
            JSONArray recordArray = getJsonData();

            List<String> ids = new ArrayList<>();
            List<String> indexes = new ArrayList<>();
            List<String> balances = new ArrayList<>();
            List<String> ages = new ArrayList<>();
            List<String> favFruits = new ArrayList<>();

            for (int i = 0; i < recordArray.length(); i++) {
                JSONObject record = recordArray.getJSONObject(i);
                ids.add(record.getString("_id"));
                indexes.add(record.getString("index"));
                balances.add(record.getString("balance"));
                ages.add(record.getString("age"));
                favFruits.add(record.getString("favoriteFruit"));
            }

//            List<String> names = Arrays.asList("John", "Jane");
//            List<Integer> ages = Arrays.asList(30, 25);

            // Create the schema
            Field idField = new Field("_id", FieldType.nullable(new ArrowType.Utf8()), null);
            Field indexField = new Field("index", FieldType.nullable(new ArrowType.Utf8()), null);
            Field balanceField = new Field("balance", FieldType.nullable(new ArrowType.Utf8()), null);
            Field ageField = new Field("age", FieldType.nullable(new ArrowType.Utf8()), null);
            Field favFruitField = new Field("favoriteFruit", FieldType.nullable(new ArrowType.Utf8()), null);
            Schema schema = new Schema(Arrays.asList(idField, indexField, balanceField, ageField, favFruitField));

            // Create a VectorSchemaRoot
            VectorSchemaRoot root = VectorSchemaRoot.create(schema, allocator);

            // Allocate memory for the vectors
            VarCharVector idVector = (VarCharVector) root.getVector("_id");
            VarCharVector indexVector = (VarCharVector) root.getVector("index");
            VarCharVector balanceVector = (VarCharVector) root.getVector("balance");
            VarCharVector ageVector = (VarCharVector) root.getVector("age");
            VarCharVector favFruitVector = (VarCharVector) root.getVector("favoriteFruit");

            root.allocateNew();

            // Populate vectors with data
            for (int i = 0; i < ids.size(); i++) {
                idVector.setSafe(i, ids.get(i).getBytes());
                indexVector.setSafe(i, indexes.get(i).getBytes());
                balanceVector.setSafe(i, balances.get(i).getBytes());
                ageVector.setSafe(i, ages.get(i).getBytes());
                favFruitVector.setSafe(i, favFruits.get(i).getBytes());

            }

            // Set the row count
            root.setRowCount(ids.size());

//            printNameVector(nameVector);

            // Write the Arrow data to the response
            response.setContentType("application/octet-stream");

            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                 ArrowStreamWriter writer = new ArrowStreamWriter(root, null, out)) {

                writer.start();
                writer.writeBatch();
                writer.end();

                byte[] arrowBytes = out.toByteArray();
                response.setContentLength(arrowBytes.length);

                try (OutputStream os = response.getOutputStream()) {
                    os.write(arrowBytes);
                    os.flush();
                }
            } finally {
                root.close();
                allocator.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @GetMapping("/getJson")
//    public ResponseEntity<JsonDTO> getJson(){
//        JsonDTO jsonDTO=jsonLoader.getJsonData("properties.json");
//        return new ResponseEntity<>(jsonDTO,HttpStatus.OK);
//    }

    private void printNameVector(VarCharVector nameVector) {
        for (int i = 0; i < nameVector.getValueCount(); i++) {
            if (!nameVector.isNull(i)) {
                System.out.println("Name at index " + i + ": " + new String(nameVector.get(i)));
            } else {
                System.out.println("Name at index " + i + ": null");
            }
        }
    }

    private JSONArray getJsonData() {
        JsonDTO jsonDTO=jsonLoader.getJsonData("properties.json");
        System.out.println("res" + jsonDTO.toString().substring(0,100));
        JSONObject jsonObj = new JSONObject(jsonDTO.toString());
        return jsonObj.getJSONArray("data");
    }
}
