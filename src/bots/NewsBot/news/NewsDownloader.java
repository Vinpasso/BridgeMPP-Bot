package bots.NewsBot.news;

import bots.logger.ErrorLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

public class NewsDownloader {

	public static String downloadCultures() {
		return download("http://api.feedzilla.com/v1/cultures.json");
	}
	public static String downloadCategories(String cultureCode) {
		return download("http://api.feedzilla.com/v1/categories.json?culture_code=" + cultureCode);
	}

	public static String downloadArticles(int category) {
		return download("http://api.feedzilla.com/v1/categories/" + category + "/articles.json");
	}

	private static String download(String website) {
		try {
			URL url = new URL(website);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			return sb.toString();
		} catch (IOException e) {
			ErrorLogger.logger.log(Level.SEVERE, "An Error has occured:", e);
			if (NewsInterpreter.debug) {
				e.printStackTrace();
			}
			return "No connection to news-server possible";
		}
	}
}
