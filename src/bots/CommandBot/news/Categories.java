package bots.CommandBot.news;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

public class Categories {
	private final static int DEFAULT_CATEGORY = 403;

	private static HashMap<String, Integer> defaultCategories;

	public static void init() {
		defaultCategories = parseCategories(Cultures.getDefaultCulture());
	}

	public static String getCategories() {
		return parseCategories();
	}

	public static String getCategories(int count) {
		return parseCategories(count);
	}

	public static String getCategories(String culture) {
		return getCategories(culture, -1);
	}

	public static String getCategories(String culture, int count) {
		String code = Cultures.verifyCulture(culture);
		Set<String> keys = parseCategories(code).keySet();
		String[] s = new String[keys.size()];
		s = keys.toArray(s);
		StringBuilder sb = new StringBuilder();
		int to = count < 0 ? keys.size() : count > keys.size() ? keys.size() : count;
		for (int i = 0; i < to; i++) {
			sb.append(s[i]).append("\n");
		}

		if (count > 0 && keys.size()-count > 0) {
			sb.append("and ").append(keys.size()-count).append(" more...");
		}
		return sb.toString();
	}

	public static String parseCategories() {
		Set<String> keys = defaultCategories.keySet();
		String[] categories = new String[keys.size()];
		categories = keys.toArray(categories);
		StringBuilder sb = new StringBuilder();
		for (String s : categories) {
			sb.append(s + "\n");
		}
		return sb.toString();
	}

	public static String parseCategories(int count) {
		Set<String> keys = defaultCategories.keySet();
		String[] categories = new String[keys.size()];
		categories = keys.toArray(categories);
		StringBuilder sb = new StringBuilder();
		int to = count < 0 ? categories.length : count > categories.length ? categories.length : count;
		for (int i = 0; i < to; i++) {
			sb.append(categories[i]).append("\n");
		}

		if (count > 0 && categories.length-count > 0) {
			sb.append("and ").append(categories.length-count).append(" more...");
		}
		return sb.toString();
	}

	public static HashMap<String, Integer> parseCategories(String code) {
		String cats = NewsDownloader.downloadCategories(code);
		JSONArray jsonArray = new JSONArray(cats);
		HashMap<String, Integer> categories = new HashMap<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject o = (JSONObject)jsonArray.get(i);
			String catString = o.getString("display_category_name");
			Integer catInt = o.getInt("category_id");
			categories.put(catString, catInt);
		}
		return categories;
	}

	public static int verifyCategory(String cat) {
		if (cat == null) {
			return DEFAULT_CATEGORY;
		}
		if (defaultCategories.keySet().contains(cat)) {
			return defaultCategories.get(cat);
		}
		return -1;
	}

	public static int verifyCategory(String culture, String cat) {
		if (cat == null) {
			return DEFAULT_CATEGORY;
		}
		if (Cultures.getDefaultCulture().equals(culture)) {
			return verifyCategory(cat);
		}
		HashMap<String, Integer> cats = parseCategories(Cultures.verifyCulture(culture));
		if (cats.keySet().contains(cat)) {
			return cats.get(cat);
		}
		return -1;
	}
}
