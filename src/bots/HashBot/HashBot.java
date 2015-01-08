package bots.HashBot;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;

public class HashBot {

	public final static Pattern isCapitalWord = Pattern.compile("[A-ZÄÖÜ][a-z]*");
	public final static Pattern removeSpecialLetters = Pattern.compile("[^a-zA-Z0-9äöüß ]");

	public final static Random r = new Random();
	
	private int hashChance = 89;

	public String generateHash(String algorithm, String message) {

		try {
			return Hex.encodeHexString(MessageDigest.getInstance(algorithm).digest(message.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException e) {
			return "No such algorithm. You probably misplled it or JAVA doesn't know it";
		} catch (UnsupportedEncodingException e) {
			return "Something went wrong internally";
		}

	}

	public String generateHashTags(String message) {
		if (r.nextInt(100) > hashChance) {

			StringBuilder hashtags = new StringBuilder();
			String[] splitmessage = removeSpecialLetters.matcher(message).replaceAll("").split(" ");

			for (String s : splitmessage) {
				if (r.nextInt(100) < 89 && !s.equals("") && isCapitalWord.matcher(s).matches()) {
					try {
						hashtags.append("<a href=\"twitter.com/hashtag/").append(URLEncoder.encode(s, "UTF-8")).append("?src=hash\"").append(">");
					} catch (UnsupportedEncodingException e) {
						hashtags.append("<a href=\"twitter.com/hashtag/").append(s).append("?src=hash\"").append(">");
					}
					hashtags.append("#").append(s);
					hashtags.append("</a> ");

				}
			}
			return hashtags.toString();
		}
		return null;
	}
	
	public void setHashChance(int hashChance) {
		this.hashChance = hashChance;
	}

	public static void main(String[] args) {
		boolean exit = false;
		HashBot hashBot = new HashBot();
		Scanner reader = new Scanner(System.in);

		while (!exit) {
			String line = reader.nextLine();
			exit = line.equals("exit");
			if (!exit) {
				System.out.println(hashBot.generateHashTags(line));
				System.out.println(hashBot.generateHash("SHA", line));
			}
		}
		reader.close();
	}

}
