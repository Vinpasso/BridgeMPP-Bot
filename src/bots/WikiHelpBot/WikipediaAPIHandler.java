package bots.WikiHelpBot;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
	public static final int maxSummaryCharacterCount = 200;
	public static final boolean doReturnHTMLText = true;
	
	public String wikiLangDomain;

	String url;
	String response;
	
	public WikipediaAPIHandler(String wikiLangDomain) {
		super();
		this.wikiLangDomain = wikiLangDomain;
	}
	
	private String buildWikiParseURL(String topic) {

		try {
			return "http://" + wikiLangDomain + ".wikipedia.org/w/api.php?action=parse&format=xml&page=" + URLEncoder.encode(topic, "UTF-8") + "&redirects=&noimages&section=0";
		} catch (UnsupportedEncodingException e) {
			return "http://de.wikipedia.org/w/api.php?action=parse&format=xml&page=" + topic + "&redirects=&noimages&section=0";
		}
	}
	
	public InputStream readURL(String topic) {
		InputStream wikiResponse = null;
		try {
			URL wikiurl = new URL(buildWikiParseURL(topic));
			wikiResponse = wikiurl.openStream();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return wikiResponse;
	}
	
	public String removeHTMLfromString(String text) {
		
		if(doReturnHTMLText){
			String extendedWikiURLText = extendWikiURLLinksPattern.matcher(text).replaceAll(wikiLangDomain + ".wikipedia.org/wiki/");
			return extendedWikiURLText;
		}
		
		String modWikiPageString = removeHTMLTagsPattern.matcher(text).replaceAll("");
		String rmaddWhiteSpacesWikiPageString = removeadditionalWhiteSpaces.matcher(modWikiPageString).replaceAll(" ");
		String rmaddNewLinesWikiPageString = removeadditionalnewLines.matcher(rmaddWhiteSpacesWikiPageString).replaceAll("\n");
		return rmaddNewLinesWikiPageString;
	}
	
	public String shortenWikiLink(String topic, String summary){
		StringBuilder result = new StringBuilder();
		int EndSummaryPosition = maxSummaryCharacterCount > summary.length() ? summary.length():maxSummaryCharacterCount;
		if(summary.indexOf('.', EndSummaryPosition) >= 0){
			EndSummaryPosition = summary.indexOf('.', EndSummaryPosition);
		}
		result.append(summary.substring(0, EndSummaryPosition));
		result.append( "\u2026" + System.lineSeparator() + "https://" + wikiLangDomain + ".wikipedia.org/wiki/" + topic);
		
		return result.toString();
	}
	
	private boolean resolveRedirection(String textNodeText) {

		boolean isRedirection = textNodeText.indexOf("<div class=\"redirectMsg\">") >= 0;

		if (isRedirection) {
			int redirectStartIndex = textNodeText.indexOf("?title=") + 7;
			int redirectEndIndex = textNodeText.indexOf("&", redirectStartIndex);
			String redirect = textNodeText.substring(redirectStartIndex, redirectEndIndex);
			textNodeText = extractHTMLPageText(readURL(redirect));
		}
		return isRedirection;
	}

	private String useTablePage(String pageText) {

		String wikiTableText = pageText;
		int indexOfSisterprojects = pageText.indexOf("<div class=\"sisterproject\"") - 20;

		if (indexOfSisterprojects >= 0) {
			String tableTextwithSeeAlso = pageText.substring(0, indexOfSisterprojects);
			int indexOfSeeAlso = tableTextwithSeeAlso.lastIndexOf("<b>");
			wikiTableText = tableTextwithSeeAlso.substring(0, indexOfSeeAlso);
		}

		return removeHTMLfromString(wikiTableText);
	}

	private String handleTextNode(String textNodeText) {

		if (resolveRedirection(textNodeText)) {
			return removeHTMLfromString(textNodeText);
		}

		int indexOfTableEnd = textNodeText.contains("</table>") ? textNodeText.lastIndexOf("</table>") + 9: 0;
		int indexOfReferences = textNodeText.indexOf("<ol class=\"references\"");
		indexOfReferences = indexOfReferences >= 0 ? indexOfReferences : textNodeText.length();
		String HTMLwikiPageText = textNodeText.substring(indexOfTableEnd, indexOfReferences);
		String wikiPageText = removeHTMLfromString(HTMLwikiPageText);

		if (wikiPageText.matches("[\n ]*")) {
			return useTablePage(textNodeText.substring(0, indexOfTableEnd));
		}
		return wikiPageText;

	}

	private String extractHTMLPageText(InputStream WikiXml) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc;
			doc = db.parse(WikiXml);

			doc.normalize();

			NodeList textNodes = doc.getElementsByTagName("text");
			if (doc.getElementsByTagName("error").item(0) != null) {
				return null;
			}
			Node textNode = textNodes.item(0);
			if (textNode != null) {
				return handleTextNode(textNode.getTextContent());
			}
			return null;
		} catch (Exception e){
			return null;
		}
	}	
	
	public String getWikiSummary(String topic){
		InputStream wikiResponseStream = readURL(topic);
		String wikiResponseString = extractHTMLPageText(wikiResponseStream);
		return wikiResponseString;
	}
	
}
