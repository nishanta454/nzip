package com.prologics.tools.nzip.util;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class GitIgnoreUtil {
	private GitIgnoreUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static boolean isIgnored(String gitIgnoreRule, String filePath) {
		String pattern = gitIgnoreRule.replace(".", "\\.").replace("*", ".*").replace("?", ".");

		try {
			Pattern regex = Pattern.compile(pattern);
			return regex.matcher(filePath).matches();
		} catch (PatternSyntaxException e) {
			return false;
		}
	}
}
