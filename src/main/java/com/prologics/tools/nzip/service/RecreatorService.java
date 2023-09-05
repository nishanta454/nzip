package com.prologics.tools.nzip.service;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecreatorService {

    public void recreateProjectFromJson(File jsonFile, File outputDirectory) {
        try {
            String jsonContent = Files.readString(jsonFile.toPath());
            JSONObject projectJson = new JSONObject(jsonContent);
            
            String projectName = projectJson.getString("pname");
            JSONArray filesArray = projectJson.getJSONArray("pcontent");

            File rootDirectory = new File(outputDirectory, projectName);

            recreateFiles(rootDirectory, filesArray);
        } catch (IOException e) {
            log.error("exception while converting json to project", e);
        }
    }
    
    private void recreateFiles(File rootDirectory, JSONArray filesArray) {
    	for (int i = 0; i < filesArray.length(); i++) {
            JSONObject fileObject = filesArray.getJSONObject(i);
            String filePath = fileObject.getString("path");
            String fileContent = fileObject.getString("content");
            createFile(rootDirectory, filePath, fileContent);
        }
    }

    private void createFile(File outputDirectory, String filePath, String content) {
        try {
        	File recreatedFile = new File(outputDirectory, filePath);
            recreatedFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(recreatedFile)) {
                writer.write(new String(Base64.getDecoder().decode(content)));
            }
        } catch (IOException e) {
        	log.error("exception while creating file", e);
        }
    }
}
