package de.bots.command.news;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Cultures {
	private final static String DEFAULT_CULTURE = "de";

	private static HashMap<String, String> cultures;

	public static String getDefaultCulture() {
		return DEFAULT_CULTURE;
	}

	public static String getCultures() {
		return getCultures(-1);
	}

	public static String getCultures(int count) {
		StringBuilder sb = new StringBuilder();
		Object[] keys = cultures.keySet().toArray();
		int to = count < 0 ? keys.length : count > keys.length ? keys.length : count;
		for (int i = 0; i < to; i++) {
			sb.append("(")
					.append(cultures.get(keys[i]))
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

	public static void init() {
		String culs = NewsDownloader.downloadCultures();
		JSONArray jsonArray = new JSONArray(culs);
		cultures = new HashMap<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject o = (JSONObject)jsonArray.get(i);
			String englishName = o.getString("english_culture_name");
			String code = o.getString("country_code");
			cultures.put(englishName, code);
		}
	}

	public static String verifyCulture(String culture) {
		String code;
		if (cultures.values().contains(culture)) {
			code = culture;
		} else {
			code = cultures.get(culture);
		}

		return code;
	}
}
