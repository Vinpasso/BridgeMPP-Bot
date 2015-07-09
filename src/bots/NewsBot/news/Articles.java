package bots.NewsBot.news;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class Articles {
	public static String getArticle(int id) {
		return getArticle(null, id);
	}

	public static String getArticle(String category, int id) {
		int cat = Categories.verifyCategory(category);
		return getArticle(cat, id);
	}

	public static String getArticle(int category, int id) {
		return parseArticle(category, id);
	}

	public static String getArticles(String category, int num) {
		int cat = Categories.verifyCategory(category);
		return getArticles(cat, num);
	}

	public static String getArticles(int category, int num) {
		return parseArticles(category, num);
	}

	public static String getArticle(String culture, String category, int id) {
		HashMap<String, String> articles = parseArticles(culture, category);
		Object[] keys = articles.keySet().toArray();
		return "(" + id + ") " + keys[id] + ":\n" + articles.get(keys[id]);
	}

	public static String parseArticle(int category, int id) {
		HashMap<String, String> articles = parseArticles(category);
		Object[] keys = articles.keySet().toArray();
		return "(" + id + ") " + keys[id] + ":\n" + articles.get(keys[id]);
	}

	public static String parseArticles(int category, int count) {
		HashMap<String, String> articles = parseArticles(category);
		Object[] keys = articles.keySet().toArray();
		StringBuilder sb = new StringBuilder();

		int to = count < 0 ? keys.length : count > keys.length ? keys.length : count;
		for (int i = 0; i < to; i++) {
			sb.append("(").append(i).append(") ")
					.append(keys[i])
					.append(":\n")
					.append(articles.get(keys[0]))
			;
		}

		if (count > 0 && keys.length-count > 0) {
			sb.append("and ").append(keys.length-count).append(" more...");
		}
		return sb.toString();
	}

	public static HashMap<String, String> parseArticles(int category) {
		String json = NewsDownloader.downloadArticles(category);
		JSONObject root = new JSONObject(json);
		JSONArray jsonArray = (JSONArray)root.get("articles");
		HashMap<String, String> articles = new HashMap<>();
		for (int i  = 0; i < jsonArray.length(); i++) {
			JSONObject o = (JSONObject)jsonArray.get(i);
			articles.put(o.getString("title").replaceAll("\n", " "), o.getString("summary").replaceAll("\n", " "));
		}
		return articles;
	}

	public static HashMap<String, String> parseArticles(String culture, String category) {
		String code = Cultures.verifyCulture(culture);
		HashMap<String, Integer> cats = Categories.parseCategories(code);
		Integer cat = cats.get(category);
		if (cat == null) {
			return null;
		}

		String json = NewsDownloader.downloadArticles(cat);
		JSONObject root = new JSONObject(json);
		JSONArray jsonArray = (JSONArray)root.get("articles");
		HashMap<String, String> articles = new HashMap<>();
		for (int i  = 0; i < jsonArray.length(); i++) {
			JSONObject o = (JSONObject)jsonArray.get(i);
			articles.put(o.getString("title").replaceAll("\n", " "), o.getString("summary").replaceAll("\n", " "));
		}
		return articles;
	}

}
