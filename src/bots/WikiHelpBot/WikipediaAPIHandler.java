package bots.WikiHelpBot;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WikipediaAPIHandler {

	public static final Pattern removeHTMLTagsPattern = Pattern.compile("<[^>]*>", Pattern.DOTALL);
	public static final Pattern removeadditionalWhiteSpaces = Pattern.compile(" +", Pattern.DOTALL);
	public static final Pattern removeadditionalnewLines = Pattern.compile("\n+", Pattern.DOTALL);
	public static final Pattern extendWikiURLLinksPattern = Pattern.compile("/wiki/");
	public static final Pattern extendReferenceURLLinksPattern = Pattern.compile("href=\"#");
	public static final Pattern extendMiscURLPattern = Pattern.compile("href=//");
	public static final int maxSummaryCharacterCount = 200;

	public static final boolean doReturnHTMLText = true;
	public static final boolean doAppendWikiSourceURL = true;

	public String wikiLangDomain;

	private String topic;

	public WikipediaAPIHandler(String wikiLangDomain) {
		super();
		this.wikiLangDomain = wikiLangDomain;
	}

	private String encodeURL(String unencoded) {
		try {
			return URLEncoder.encode(unencoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return unencoded;
		}
	}

	private String buildWikiParseURL(String topic) {
		return "https://" + wikiLangDomain + ".wikipedia.org/w/api.php?action=query&prop=extracts&format=xml&exintro=&exsectionformat=plain&rawcontinue=&titles=" + topic + "&redirects=true";
	}

	private String buildWikiSourceURL() {
		return new StringBuilder().append("https://").append(wikiLangDomain).append(".wikipedia.org/wiki/" + topic).toString();
	}

	public InputStream readURL(String topic) {
		InputStream wikiResponse = null;
		try {
			URL wikiurl = new URL(buildWikiParseURL(topic));
			HttpURLConnection wikiconnection = (HttpURLConnection) wikiurl.openConnection();
			wikiResponse = wikiconnection.getInputStream();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wikiResponse;
	}

	public String removeHTMLfromString(String text) {

		if (doReturnHTMLText) {
			String extendedWikiURLText = extendWikiURLLinksPattern.matcher(text).replaceAll("http://" + wikiLangDomain + ".wikipedia.org/wiki/");
			String extendedWikiAndReferenceURLText = extendReferenceURLLinksPattern.matcher(extendedWikiURLText).replaceAll("href=\"http://" + wikiLangDomain + ".wikipedia.org/wiki/" + topic + "#");
			String extendedWikiReferenceAndMiscURLText = extendMiscURLPattern.matcher(extendedWikiAndReferenceURLText).replaceAll("href=http://");
			return extendedWikiReferenceAndMiscURLText;
		}

		String modWikiPageString = removeHTMLTagsPattern.matcher(text).replaceAll("");
		String rmaddWhiteSpacesWikiPageString = removeadditionalWhiteSpaces.matcher(modWikiPageString).replaceAll(" ");
		String rmaddNewLinesWikiPageString = removeadditionalnewLines.matcher(rmaddWhiteSpacesWikiPageString).replaceAll("\n");
		return rmaddNewLinesWikiPageString;
	}

	public String shortenWikiLink(String topic, String summary) {
		StringBuilder result = new StringBuilder();
		int EndSummaryPosition = maxSummaryCharacterCount > summary.length() ? summary.length() : maxSummaryCharacterCount;
		if (summary.indexOf('.', EndSummaryPosition) >= 0) {
			EndSummaryPosition = summary.indexOf('.', EndSummaryPosition);
		}
		result.append(summary.substring(0, EndSummaryPosition));
		result.append("\u2026" + System.lineSeparator() + "https://" + wikiLangDomain + ".wikipedia.org/wiki/" + topic);

		return result.toString();
	}

	private String handleTextNode(String textNodeText) {

		String wikiPageText = removeHTMLfromString(textNodeText);
		if (wikiPageText.matches("[\n ]*")) {
			return null;
		}
		return wikiPageText;

	}

	private String extractHTMLPageText(InputStream WikiXml) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc;
			//System.out.println(IOUtils.toString(WikiXml, "UTF-8"));
			doc = db.parse(WikiXml);

			doc.normalize();

			NodeList textNodes = doc.getElementsByTagName("extract");
			if (doc.getElementsByTagName("error").item(0) != null) {
				return null;
			}
			Node textNode = textNodes.item(0);
			if (textNode != null) {
				return handleTextNode(textNode.getTextContent());
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public String getWikiSummary(String topic) {
		this.topic = encodeURL(topic);
		InputStream wikiResponseStream = readURL(this.topic);
		String wikiResponseString = extractHTMLPageText(wikiResponseStream);
		if (!doAppendWikiSourceURL || wikiResponseString == null) {
			return wikiResponseString;
		} else {
			if (doReturnHTMLText) {
				StringBuilder wikiSourceString = new StringBuilder(wikiResponseString);
				wikiSourceString.append("<br>");
				wikiSourceString.append("(");
				wikiSourceString.append("<a href=\"").append(buildWikiSourceURL()).append("\"> ");
				wikiSourceString.append(buildWikiSourceURL());
				wikiSourceString.append("</a>)");
				return wikiSourceString.toString();
			}
			return wikiResponseString.concat("\u2026" + System.lineSeparator() + "(Source: http://" + wikiLangDomain + ".wikipedia.org/wiki/" + this.topic + ")");
		}
	}

}
