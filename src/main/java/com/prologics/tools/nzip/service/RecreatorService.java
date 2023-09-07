package com.prologics.tools.nzip.service;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONObject;

import com.prologics.tools.nzip.util.EncDecUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecreatorService {

    public void recreateProjectFromJson(File jsonFile, File outputDirectory) {
        try {
            String jsonContent = Files.readString(jsonFile.toPath());
            JSONObject projectJson = new JSONObject(jsonContent);
            
            String projectName = EncDecUtil.decrypt(projectJson.getString("pname"));
            JSONArray filesArray = projectJson.getJSONArray("pcontent");

            File rootDirectory = new File(outputDirectory, projectName);

            recreateFiles(rootDirectory, filesArray);
        } catch (Exception e) {
            log.error("exception while converting json to project", e);
        }
    }
    
    private void recreateFiles(File rootDirectory, JSONArray filesArray) throws Exception {
    	for (int i = 0; i < filesArray.length(); i++) {
            JSONObject fileObject = filesArray.getJSONObject(i);
            String filePath = EncDecUtil.decrypt(fileObject.getString("path"));
            String fileContent = fileObject.getString("content");
            createFile(rootDirectory, filePath, fileContent);
        }
    }

    private void createFile(File outputDirectory, String filePath, String content) throws Exception {
        try {
            File recreatedFile = new File(outputDirectory, filePath);
            File parentDir = recreatedFile.getParentFile();

            if (!parentDir.exists() && !parentDir.mkdirs()) {
                log.error("Failed to create directories for: {}", parentDir.getAbsolutePath());
                return;
            }

            try (FileWriter writer = new FileWriter(recreatedFile)) {
                writer.write(EncDecUtil.decrypt(content));
            }
        } catch (IOException e) {
            log.error("exception while creating file", e);
        }
    }
}
