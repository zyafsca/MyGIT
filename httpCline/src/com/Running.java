package com;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.pool.ThreadPool;

public class Running {

	public static Properties props;

	private static Logger log = Logger.getLogger(Running.class);

	public static void main(String[] args) {

		readProperties();
		down();
	}

	private static void readProperties() {
		try {
			props = new Properties();
			InputStream in = null;
			try {
				in = new FileInputStream("pro.properties");
				props.load(in);

			} catch (IOException e) {
				in = Running.class.getResourceAsStream("/pro.properties");
				props.load(in);
			}

			in.close();

			if (props.isEmpty())
				return;

			Iterator<Object> it = props.keySet().iterator();

			while (it.hasNext()) {
				String key = it.next().toString();
				String value = props.getProperty(key);

				log.info(key + " : " + value);
			}

			log.info("------------------------------------");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void down() {
		String filePath = props.get("urlFile").toString();

		List<String> urlList = new ArrayList<String>();
		try {
			FileReader fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0)
					urlList.add(line);
			}

			br.close();
			fr.close();

			int corePoolSize = Integer.parseInt(props
					.getProperty("corePoolSize"));
			int maximumPoolSize = Integer.parseInt(props
					.getProperty("maximumPoolSize"));
			long keepAliveTime = Long.parseLong(props
					.getProperty("keepAliveTime"));

			ThreadPool pool = new ThreadPool(corePoolSize, maximumPoolSize,
					keepAliveTime, TimeUnit.SECONDS,
					new ArrayBlockingQueue<Runnable>(urlList.size()));

			for (String url : urlList) {
				pool.execute(new ImgDown(url));
			}

			pool.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}