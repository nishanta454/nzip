package com.prologics.tools.nzip.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.prologics.tools.nzip.util.EncDecUtil;
import com.prologics.tools.nzip.util.GitIgnoreUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParserService {

	private static final List<String> DIRECTORIES_TO_EXCLUDE = List.of("target", ".mvn", ".classpath", ".settings",
			"mvnw", "mvnw.cmd", ".git", ".devcontainer", ".vscode", ".idea", "__pycache__");

	public void parseProjectToJson(File projectDirectory, File outputFileDirectory) {
		try {
			JSONObject projectJson = new JSONObject();
			JSONArray filesArray = new JSONArray();

			String projectName = projectDirectory.getName();
			projectJson.put("pname", EncDecUtil.encrypt(projectName));

			parseDirectory(projectDirectory, projectDirectory, filesArray);

			projectJson.put("pcontent", filesArray);
			writeProjectJsonToFile(new File(outputFileDirectory, projectDirectory.getName() + ".json"), projectJson);
		} catch (Exception e) {
			log.error("exception while reading project files", e);
		}
	}

	private static void parseDirectory(File rootDirectory, File currentDirectory, JSONArray filesArray) throws JSONException, Exception {
		if (DIRECTORIES_TO_EXCLUDE.contains(currentDirectory.getName())) {
			return; // Skip parsing .git folder
		}

		File[] files = currentDirectory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					parseDirectory(rootDirectory, file, filesArray);
				} else {
					String relativePath = rootDirectory.toPath().relativize(file.toPath()).toString();

					// Check .gitignore rules
					if (!shouldIgnore(relativePath, rootDirectory)) {
						JSONObject fileObject = new JSONObject();
						fileObject.put("path", EncDecUtil.encrypt(relativePath));
						fileObject.put("content", readFileContent(file));
						filesArray.put(fileObject);
					}
				}
			}
		}
	}

	private static String readFileContent(File file) throws Exception {
		String fileContent = null;
		try {
			return EncDecUtil.encrypt(new String(Files.readAllBytes(file.toPath())));
		} catch (IOException e) {
			log.error("exception while reading file");
		}
		return fileContent;
	}

	private static boolean shouldIgnore(String filePath, File rootDirectory) {
		File gitIgnoreFile = new File(rootDirectory, ".gitignore");
		if (gitIgnoreFile.exists() && gitIgnoreFile.isFile()) {
			try {
				List<String> gitIgnoreRules = Files.readAllLines(gitIgnoreFile.toPath());
				for (String rule : gitIgnoreRules) {
					if (rule.startsWith("#") || rule.trim().isEmpty()) {
						continue; // Skip comments and empty lines
					}
					if (GitIgnoreUtil.isIgnored(rule, filePath)) {
						return true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private static void writeProjectJsonToFile(File outputFile, JSONObject projectJson) {
		try (FileWriter writer = new FileWriter(outputFile)) {
			writer.write(projectJson.toString(4)); // Use 4 spaces for indentation
		} catch (IOException e) {
			log.error("exception while writing output json", e);
		}
	}
}