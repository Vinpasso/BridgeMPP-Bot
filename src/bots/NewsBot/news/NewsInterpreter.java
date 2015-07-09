package bots.NewsBot.news;

import bots.NewsBot.logger.ErrorLogger;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class NewsInterpreter {
	private final String HELP = "News - Help:\n" +
			"    Usage: !news [params...]\n" +
			"    no param - show top 3 titles\n" +
			"    <n> - show article n\n" +
			"    limit <num> - shows 'num' titles\n" +
			"    cultures [limit <num>] - lists first 'num' cultures\n" +
			"    categories list [culture] [limit <num>] - shows first 'num' categories from given culture\n" +
			"    category [culture <cul> cat] <category> [limit <num>] - displays first 'num' German or culture's category cultures\n" +
			"    show [culture <cul> cat] <category> <n> - shows article 'n' of given culture and category\n" +
			"    help/?  - shows this help\n"
	;

	public static boolean debug = false;

	public void init() {
		Cultures.init();
		Categories.init();
	}

	public String getAnswer(String parameters) {
		try {
			if (parameters == null || parameters.equals("")) {
				return getDefaultNews();
			}
			String[] params = parameters.split(" ");
			int paramLength = params.length;

			if (params[0].matches("[0-9]+")) {
				return Articles.getArticle(Integer.parseInt(params[0]));
			}

			switch (params[0]) {
				case "debug":
					if (params[1].equals("on")) {
						debug = true;
					} else if (params[1].equals("off")) {
						debug = false;
					}
					return null;
				case "help":
				case "?":
					return HELP;
				case "limit":
					if (paramLength == 2 && params[1].matches("[0-9]+")) {
						return Titles.getTitles(Integer.parseInt(params[1]));
					} else {
						return "Please specify a number of elements to limit to.";
					}
				case "cul":
				case "culs":
				case "culture":
				case "cultures":
					if (paramLength > 2 && params[1].equals("limit") && params[2].matches("[0-9]+")) {
						return Cultures.getCultures(Integer.parseInt(params[2]));
					}
					return Cultures.getCultures(3);
				case "cat":
				case "cats":
				case "category":
				case "categories":
					if (paramLength > 1 && params[1].equals("list")) {
						if (paramLength == 2) {
							return Categories.getCategories();
						} else if (paramLength == 4 && params[2].equals("limit") && params[3].matches("[0-9]+")) {
							return Categories.getCategories(Integer.parseInt(params[3]));
						} else if (paramLength >= 3 && params[2].matches("[a-zA-Z_\\(\\)]+")) {
							if (paramLength >= 5 && params[paramLength - 2].equals("limit") && params[paramLength - 1].matches("[0-9]+")) {
								return Categories.getCategories(getArrayAsString(params, 2, paramLength - 2), Integer.parseInt(params[paramLength - 1]));
							}
							return Categories.getCategories(params[2]);
						} else {
							return "No valid arguments found.";
						}
					} else {
						if (paramLength >= 2 && params[1].equals("culture")) {
							int index;
							if ((index = Arrays.asList(params).lastIndexOf("cat")) != -1) {
								String result;
								if (paramLength >= 2 && params[paramLength - 2].equals("limit") && params[paramLength - 1].matches("[0-9]+")) {
									String culture = getArrayAsString(params, 2, index);
									String category = getArrayAsString(params, 1, params.length - 2);
									result = Titles.getTitles(culture, category, Integer.parseInt(params[paramLength - 1]));
								} else {
									String culture = getArrayAsString(params, 2, index);
									String category = getArrayAsString(params, index + 1, paramLength);
									result = Titles.getTitles(culture, category);
								}
								if (result == null) {
									return "No valid (culture, category) tuple.";
								}
								return result;
							}
						}
						String result;
						if (paramLength >= 2 && params[paramLength - 2].equals("limit") && params[paramLength - 1].matches("[0-9]+")) {
							String category = getArrayAsString(params, 1, params.length - 2);
							result = Titles.getTitles(category, Integer.parseInt(params[paramLength - 1]));
						} else {
							String category = getArrayAsString(params, 1, params.length);
							result = Titles.getTitles(category);
						}
						if (result == null) {
							return "No valid category.";
						}
						return result;
					}
				case "show":
				case "display":
					List<String> args = Arrays.asList(params);
					String culture = null;
					String category;
					int n;
					int index;
					if ((index = args.indexOf("culture")) != -1) {
						culture = getArrayAsString(params, index+1, args.indexOf("cat"));
						index = args.indexOf("cat") + 1;
					}
					category = getArrayAsString(params, index, params.length - 1);
					n = Integer.parseInt(params[params.length - 1]);
					if (category != null) {
						return Articles.getArticle(culture, category, n);
					} else {
						return Articles.getArticle(category, n);
					}
			}
			return "Please insert a valid bots.";
		} catch (Exception e) {
			ErrorLogger.logger.log(Level.SEVERE, "An Error has occured:", e);
			if (debug) {
				e.printStackTrace();
			}
			return "Input couldn't be interpreted by the news-server.";
		}
	}

	public String getDefaultNews() {
		return Titles.getTitles(3);
	}

	public String getArrayAsString(Object[] args, int start, int end) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < end; i++) {
			if (i > start) {
				sb.append(" ");
			}
			sb.append(args[i]);
		}
		return sb.toString();
	}
}
