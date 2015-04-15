package com;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClient {

	private static HttpClient httpClient;

	private CloseableHttpClient closeablehttpclient;
	private CloseableHttpResponse response;
	private HttpGet get;
	private HttpEntity entity;

	private HttpClient() {
		closeablehttpclient = HttpClients.createDefault();
	}

	public static HttpClient getHttpClient() {
		if (httpClient == null)
			httpClient = new HttpClient();
		return httpClient;
	}

	private HttpEntity getEntity(String url) {
		try {
			get = new HttpGet(url);
			get.setHeader(
					"Cookie",
					"xres=3; ipb_member_id=2275540; ipb_pass_hash=c7a38a59e0090431484d63368a52a27d; event=1; __utma=185428086.1248103407.1424028986.1424028986.1424028986.1; __utmz=185428086.1424028986.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
			response = closeablehttpclient.execute(get);
			get = null;
			
			return response.getEntity();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getContentByString(String url) {
		try {
			// 打印响应状态
			// System.out.println(response.getStatusLine());
			entity = getEntity(url);
			if (entity != null) {
				return EntityUtils.toString(entity);
				// 打印响应内容长度
				// System.out.println("Response content length: "
				// + entity.getContentLength());
				// 打印响应内容
				// System.out.println("Response content: " + content);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public InputStream getContentByStream(String url) {
		entity = getEntity(url);
		try {
			if (entity != null) {
				return entity.getContent();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void close() {
		try {
			response.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String url = "http://lofi.e-hentai.org/s/690ff5ef49/789553-15";
		HttpClient client = new HttpClient();

		String content = client.getContentByString(url);

		System.out.println(content);
	}

}