package com.prologics.tools.nzip.util;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpClientUtil {
	
	private HttpClientUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static String get(String url, Map<String, String> headers) {
		return makeRequest("GET", url, headers, null);
	}

	public static String post(String url, Map<String, String> headers, String payload) {
		return makeRequest("POST", url, headers, payload);
	}

	public static String put(String url, Map<String, String> headers, String payload) {
		return makeRequest("PUT", url, headers, payload);
	}

	public static String delete(String url, Map<String, String> headers) {
		return makeRequest("DELETE", url, headers, null);
	}

	private static String makeRequest(String method, String url, Map<String, String> headers, String payload) {
		var httpClient = HttpClients.createDefault();

		try {
			HttpResponse response;
			if ("GET".equalsIgnoreCase(method)) {
				var httpGet = new HttpGet(url);
				addHeaders(httpGet, headers);
				response = httpClient.execute(httpGet);
			} else if ("POST".equalsIgnoreCase(method)) {
				var httpPost = new HttpPost(url);
				addHeaders(httpPost, headers);
				addPayload(httpPost, payload);
				response = httpClient.execute(httpPost);
			} else if ("PUT".equalsIgnoreCase(method)) {
				var httpPut = new HttpPut(url);
				addHeaders(httpPut, headers);
				addPayload(httpPut, payload);
				response = httpClient.execute(httpPut);
			} else if ("DELETE".equalsIgnoreCase(method)) {
				var httpDelete = new HttpDelete(url);
				addHeaders(httpDelete, headers);
				response = httpClient.execute(httpDelete);
			} else {
				throw new IllegalArgumentException("Unsupported HTTP method");
			}

			var statusCode = response.getStatusLine().getStatusCode();
			log.info("status code: {}", statusCode);

			var responseEntity = response.getEntity();
			if (responseEntity != null) {
				return EntityUtils.toString(responseEntity);
			}
		} catch (Exception e) {
			log.error("exception while calling api", e);
		}
		return null;
	}

	private static void addHeaders(Object httpRequest, Map<String, String> headers) {
		if (headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				((org.apache.http.message.AbstractHttpMessage) httpRequest).addHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	private static void addPayload(HttpEntityEnclosingRequestBase httpEntityRequest, String payload) {
		if (payload != null) {
			httpEntityRequest.setEntity(new StringEntity(payload, "UTF-8"));
		}
	}
}
