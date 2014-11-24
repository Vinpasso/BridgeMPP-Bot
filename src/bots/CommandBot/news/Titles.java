package bots.CommandBot.news;

import java.util.HashMap;

public class Titles {
	public static String getTitles(int num) {
		return getTitles(null, num);
	}

	public static String getTitles(String category) {
		return getTitles(category, -1);
	}

	public static String getTitles(String category, int num) {
		int cat = Categories.verifyCategory(category);
		return getTitles(cat, num);
	}

	public static String getTitles(int category, int num) {
		return parseTitles(category, num);
	}

	public static String getTitles(String culture, String category) {
		return parseTitles(culture, category, -1);
	}

	public static String getTitles(String culture, String category, int count) {
		return parseTitles(culture, category, count);
	}

	public static String parseTitles(int category, int count) {
		if (category == -1) {
			return null;
		}
		HashMap<String, String> articles = Articles.parseArticles(category);
		Object[] keys = articles.keySet().toArray();
		StringBuilder sb = new StringBuilder();

		int to = count < 0 ? keys.length : count > keys.length ? keys.length : count;
		for (int i = 0; i < to; i++) {
			sb.append("(")
					.append(i)
					.append(") ")
					.append(keys[i])
					.append("\n")
			;
		}

		if (count > 0 && keys.length-count > 0) {
			sb.append("and ").append(keys.length-count).append(" more...");
		}
		return sb.toString();
	}

	public static String parseTitles(String culture, String category, int count) {
		HashMap<String, String> articles = Articles.parseArticles(culture, category);
		Object[] keys = articles.keySet().toArray();
		StringBuilder sb = new StringBuilder();

		int to = count < 0 ? keys.length : count > keys.length ? keys.length : count;
		for (int i = 0; i < to; i++) {
			sb.append("(")
					.append(i)
					.append(") ")
					.append(keys[i])
					.append("\n")
			;
		}

		if (count > 0 && keys.length-count > 0) {
			sb.append("and ").append(keys.length-count).append(" more...");
		}
		return sb.toString();
	}
}
