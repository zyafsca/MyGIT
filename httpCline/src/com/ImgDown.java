package com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class ImgDown implements Runnable {
	
	private static Logger log = Logger.getLogger(ImgDown.class);
	private String url = "";
	private String savePath;
	private String oldUrl = null;
	private String content;
	private String[] line;
	private String imgLine;
	private byte[] bytes;
	private int len;

	private HttpClient client;

	public ImgDown(String url) {
		this.url = url;
		this.savePath = Running.props.getProperty("savePath");
		
		
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public void findImg(String url, boolean mkdir) throws Exception {
		client = HttpClient.getHttpClient();
		content = client.getContentByString(url);
		// 创建文件夹
		if (mkdir) {
			String title = getTitle(content);
			if (title != null){
				String path = getSavePath() + File.separator + title;
				
				File file = new File(path);
				if (!file.exists()) {
					file.mkdir();
				}
				
				setSavePath(path);
			}


			File flag = new File(getSavePath() + File.separator
					+ "readme.txt");
			try {
				flag.createNewFile();
				FileWriter fw = new FileWriter(flag);
				fw.write(url);
				fw.flush();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		line = content.split("\n");
		imgLine = "";
		for (String s : line) {
			if (matcher("<img ", s) != null) {
				imgLine = s.trim();
				break;
			}
		}
		String nUrl = getNextPageUrl(imgLine);
		String sUrl = getImgUrl(imgLine);

		client.close();

		if (!nUrl.equals(oldUrl)) {

			oldUrl = nUrl;
			saveImg(sUrl);

			this.findImg(nUrl, false);
		} else {
			return;
		}

	}

	public String getTitle(String html) {
		try {
			String s = matcher("<title>(.*?)</title>", html);
			s = s.substring(7, s.length() - 8);
			s = s.replace("\\", "").replace("|", "").replace("/", "")
					.replace("?", "").replace(":", "");
			
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getNextPageUrl(String html) {
		String s = matcher("href=\"(.*?)\"", html);
		s = s.replace("\"", "");
		s = s.replace("href=", "");
		return s;
	}

	public String getImgUrl(String html) {
		String s = matcher("src=\"(.*?)\"", html);
		s = s.replace("\"", "");
		s = s.replace("src=", "");
		return s;
	}

	public void saveImg(String url) throws Exception {
			client = HttpClient.getHttpClient();
			InputStream in = client.getContentByStream(url);
			String fileName = url.substring(url.lastIndexOf("/") + 1);
			File file = new File(this.getSavePath() + File.separator + fileName);

			if (!file.exists()) {
				FileOutputStream out = new FileOutputStream(file);
				len = 0;
				bytes = new byte[1024];
				while ((len = in.read(bytes)) != -1) {
					out.write(bytes, 0, len);
				}

				out.flush();
				out.close();
				in.close();
			}

			log.info(fileName);

			client.close();

	}

	private String matcher(String regex, String content) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		if (m.find()) {
			return m.group();
		} else {
			return null;
		}
	}

	@Override
	public void run() {
		try {
			this.findImg(url, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}