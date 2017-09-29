package bots.CalendarBot;

public class BirthdayWishes {
	private final String[] wishDefault = wishDefault().split("\\.0\\.");
	private final String[] wish20 = wish20().split("\\.0\\.");;
	private final String[] wish18 = wish18().split("\\.0\\.");;
	
	
	private String wishDefault () {
		return "90.117.109.32.71.101.98.117.114.116.115.116.97.103.32.114.101.99.104.116.32.118.105.101.108.32.71.108.117.101.99.107.44.10.117.110.100."
				+ "32.118.111.109.32.75.117.99.104.101.110.32.109.105.114.32.100.97.115.32.103.114.111.101.115.115.116.101.32.83.116.117.101.99.107.46.0."
				+ "69.115.32.103.105.98.116.32.101.105.110.105.103.101.32.71.114.117.101.110.100.101.32.100.105.101.32.100.97.102.117.101.114.32.115.112."
				+ "114.101.99.104.101.110.44.10.100.97.115.32.119.105.114.32.68.101.105.110.101.110.32.71.101.98.117.114.116.115.116.97.103.32.97.109.32."
				+ "98.101.115.116.101.110.32.118.101.114.103.101.115.115.101.110.46.10.68.111.99.104.32.119.105.101.32.101.115.32.100.105.101.32.69.104."
				+ "114.101.32.110.117.110.32.101.105.110.109.97.108.32.103.101.98.117.101.104.114.116.44.10.119.105.114.100.32.116.114.111.116.122.100.101."
				+ "109.32.110.111.99.104.32.102.114.111.101.104.108.105.99.104.32.103.114.97.116.117.108.105.101.114.116.46.0.72.101.114.122.108.105.99."
				+ "104.101.115.32.66.101.105.108.101.105.100.32.122.117.109.32.71.101.98.117.114.116.115.116.97.103.44.10.119.105.114.32.104.97.98.101.110."
				+ "32.68.105.114.32.76.105.108.105.101.110.32.109.105.116.103.101.98.114.97.99.104.116.44.10.100.101.110.110.32.84.114.97.117.101.114.32."
				+ "105.115.116.32.104.101.117.116.101.32.97.110.103.101.115.97.103.116.44.10.68.117.32.104.97.115.116.32.100.105.101.32.68.114.111.104.117."
				+ "110.103.32.119.97.104.114.32.103.101.109.97.99.104.116.46.0.72.101.114.122.108.105.99.104.101.115.32.66.101.105.108.101.105.100.32.122."
				+ "117.32.68.101.105.110.101.109.32.71.101.98.117.114.116.115.116.97.103.44.10.100.97.109.105.116.32.100.101.114.32.84.97.103.32.110.105."
				+ "99.104.116.32.115.111.32.116.114.97.117.114.105.103.32.105.115.116.44.10.104.97.98.101.110.32.119.105.114.32.65.108.107.111.104.111.108."
				+ "32.109.105.116.103.101.98.114.97.99.104.116.44.10.100.97.109.105.116.32.68.117.32.68.101.105.110.32.69.108.101.110.100.32.102.117.101."
				+ "114.32.83.116.117.110.100.101.110.32.118.101.114.103.105.115.115.116.46.0.72.101.114.122.108.105.99.104.101.110.32.71.108.117.101.99."
				+ "107.119.117.110.115.99.104.32.122.117.109.32.71.101.98.117.114.116.115.116.97.103.44.10.119.101.114.32.119.101.105.115.115.32.115.99."
				+ "104.111.110.44.32.100.101.114.32.119.105.101.118.105.101.108.116.101.32.100.97.115.32.105.115.116.44.10.104.111.102.102.101.110.116.108."
				+ "105.99.104.32.103.105.98.116.39.115.32.119.97.115.32.122.117.32.116.114.105.110.107.101.110.44.10.100.97.109.105.116.32.68.117.32.105."
				+ "104.110.32.110.105.101.32.109.101.104.114.32.118.101.114.103.105.115.115.116.46.0.72.109.109.32.38.32.73.32.100.111.32.110.111.116.32."
				+ "107.110.111.119.32.119.104.121.44.32.98.117.116.32.73.32.104.97.100.32.97.32.115.116.114.111.110.103.32.117.114.103.101.32.116.111.32."
				+ "115.101.110.100.32.121.111.117.32.97.32.116.101.120.116.32.109.101.115.115.97.103.101.33.32.66.117.116.32.119.104.121.63.32.10.73.32.107."
				+ "110.111.119.33.32.72.65.80.80.89.32.66.73.82.84.72.68.65.89.33.0.73.32.100.105.100.110.39.116.32.102.111.114.103.101.116.32.121.111.117."
				+ "114.32.98.105.114.116.104.100.97.121.46.32.10.73.32.106.117.115.116.32.102.111.114.103.111.116.32.116.111.100.97.121.39.115.32.100.97."
				+ "116.101.33.0.73.32.99.97.110.39.116.32.98.101.108.105.101.118.101.32.105.116.39.115.32.97.108.114.101.97.100.121.32.98.101.101.110.32."
				+ "97.32.121.101.97.114.32.115.105.110.99.101.32.116.104.101.32.108.97.115.116.32.116.105.109.101.32.73.32.100.105.100.110.39.116.32.98."
				+ "117.121.32.121.111.117.32.97.110.121.116.104.105.110.103.32.102.111.114.32.121.111.117.114.32.98.105.114.116.104.100.97.121.46.0.74.117."
				+ "115.116.32.119.97.110.116.101.100.32.116.111.32.98.101.32.116.104.101.32.102.105.114.115.116.32.111.110.101.32.116.111.32.119.105.115."
				+ "104.32.121.111.117.32.97.32.104.97.112.112.121.32.98.105.114.116.104.100.97.121.32.10.115.111.32.73.32.99.97.110.32.102.101.101.108.32."
				+ "115.117.112.101.114.105.111.114.32.116.111.32.121.111.117.114.32.111.116.104.101.114.32.119.101.108.108.45.119.105.115.104.101.114.115."
				+ "46.0.83.111.114.114.121.32.73.32.102.111.114.103.111.116.32.116.111.32.102.111.114.103.101.116.32.105.116.39.115.32.121.111.117.114.32."
				+ "98.105.114.116.104.100.97.121.32.116.111.100.97.121.46.0.67.111.110.103.114.97.116.117.108.97.116.105.111.110.115.32.111.110.32.98.101."
				+ "105.110.103.32.97.32.121.101.97.114.32.99.108.111.115.101.114.32.116.111.32.102.105.110.100.105.110.103.32.111.117.116.32.105.102.32."
				+ "97.116.104.101.105.115.109.32.119.97.115.32.116.104.101.32.114.105.103.104.116.32.99.104.111.105.99.101.46.0.72.97.112.112.121.32.98."
				+ "105.114.116.104.100.97.121.32.116.111.32.115.111.109.101.111.110.101.32.73.32.104.111.112.101.32.105.115.32.109.121.32.102.114.105.101."
				+ "110.100.10.101.118.101.110.32.119.104.101.110.32.119.101.39.114.101.32.116.111.111.32.115.101.110.105.108.101.32.116.111.32.114.101.109."
				+ "101.109.98.101.114.32.101.97.99.104.32.111.116.104.101.114.39.115.32.98.105.114.116.104.100.97.121.115.46.0.72.97.112.112.121.32.66.105."
				+ "114.116.104.100.97.121.32.116.111.32.115.111.109.101.111.110.101.32.119.104.111.32.119.97.115.110.39.116.32.119.101.108.99.111.109.101."
				+ "100.32.105.110.116.111.32.116.104.101.32.119.111.114.108.100.32.118.105.97.32.116.119.101.101.116.32.111.114.32.115.116.97.116.117.115."
				+ "32.117.112.100.97.116.101.46.0.";
	}
	
	private String wish18 () {
		return "73.110.116.101.108.108.105.103.101.110.116.44.32.119.105.116.122.105.103.44.32.99.104.97.114.109.97.110.116.44.32.115.101.120.121.32.117."
				+ "110.100.32.101.110.100.108.105.99.104.32.118.111.108.108.106.97.101.104.114.105.103.32.45.10.78.97.106.97.32.97.108.115.111.32.101.105."
				+ "110.115.32.100.97.118.111.110.32.116.114.105.102.102.116.32.97.117.102.32.106.101.100.101.110.32.70.97.108.108.32.122.117.46.0.72.117.114."
				+ "114.97.44.32.68.117.32.98.105.115.116.32.101.110.100.108.105.99.104.32.118.111.108.108.106.97.101.104.114.105.103.33.10.74.101.116.122."
				+ "116.32.109.117.115.115.32.100.105.99.104.32.100.101.105.110.101.32.77.117.116.116.105.32.110.105.99.104.116.32.109.101.104.114.32.107.114."
				+ "97.110.107.115.99.104.114.101.105.98.101.110.44.32.119.101.110.110.32.100.117.32.109.97.108.32.110.105.99.104.116.32.122.117.114.32.85."
				+ "110.105.32.107.111.109.109.115.116.46.0.";
	}
	
	private String wish20 () {
		return "68.117.32.98.105.115.116.32.122.119.97.114.32.110.111.99.104.32.110.105.99.104.116.32.97.108.116.44.32.97.98.101.114.32.97.108.115.32.72.117."
				+ "110.100.32.119.97.101.114.115.116.32.68.117.32.115.99.104.111.110.32.49.52.48.33.32.69.115.32.105.115.116.32.97.108.108.101.115.32.114.101."
				+ "108.97.116.105.118.33.0.72.101.114.122.108.105.99.104.101.110.32.71.108.117.101.99.107.119.117.110.115.99.104.32.122.117.109.32.50.48.46."
				+ "10.68.117.32.98.105.115.116.32.110.117.110.32.101.105.110.32.97.108.116.101.115.32.72.97.117.115.33.10.77.105.116.32.106.101.100.101.109.32."
				+ "74.97.104.114.32.107.111.109.109.115.116.32.68.117.32.115.99.104.110.101.108.108.101.114.32.97.110.32.100.105.101.32.82.101.110.116.101.46."
				+ "10.72.97.115.116.32.68.117.32.115.99.104.111.110.32.68.101.105.110.101.110.32.65.110.116.114.97.103.32.97.117.115.103.101.102.117.101.108."
				+ "108.116.63.0.65.98.32.104.101.117.116.101.32.103.101.104.111.101.114.115.116.32.68.117.32.110.105.99.104.116.32.109.101.104.114.32.122.117."
				+ "32.100.101.110.32.84.101.101.110.115.44.10.115.111.110.100.101.114.110.32.122.117.32.100.101.110.32.84.119.101.110.115.46.10.83.101.105.32."
				+ "110.105.99.104.116.32.116.114.97.117.114.105.103.44.32.100.97.115.32.76.101.98.101.110.32.103.101.104.116.32.119.101.105.116.101.114.46.0."
				+ "72.117.114.114.97.44.32.68.117.32.98.105.115.116.32.107.101.105.110.32.84.101.101.110.97.103.101.114.32.109.101.104.114.33.32.10.65.98.32."
				+ "104.101.117.116.101.32.98.105.115.116.32.68.117.32.97.108.115.111.32.118.101.114.110.117.101.110.102.116.105.103.44.32.98.101.115.111.110."
				+ "110.101.110.44.32.112.102.108.105.99.104.116.98.101.119.117.115.115.116.44.32.102.108.101.105.115.115.105.103.44.32.112.117.101.110.107.116."
				+ "108.105.99.104.44.32.103.101.104.115.116.32.102.114.117.101.104.32.105.110.115.32.66.101.116.116.32.117.110.100.32.102.97.101.104.114.115."
				+ "116.32.109.97.120.105.109.97.108.32.51.50.32.107.109.47.104.32.105.110.32.100.101.114.32.51.48.101.114.45.90.111.110.101.33.46.46.46.72.65."
				+ "72.65.32.75.76.65.82.33.0.72.101.114.122.108.105.99.104.101.110.32.71.108.117.101.99.107.119.117.110.115.99.104.44.32.68.117.32.98.105.115."
				+ "116.32.110.117.110.32.111.102.102.105.122.105.101.108.108.32.107.101.105.110.32.110.101.114.118.101.110.100.101.114.44.32.115.99.104.119.105."
				+ "101.114.105.103.101.114.32.117.110.100.32.100.114.97.109.97.116.105.115.99.104.101.114.32.84.101.101.110.97.103.101.114.32.109.101.104.114."
				+ "44.10.115.111.110.100.101.114.110.32.110.117.114.110.111.99.104.32.110.101.114.118.101.110.100.44.32.115.99.104.119.105.101.114.105.103.32."
				+ "117.110.100.32.101.105.110.32.100.114.97.109.97.116.105.115.99.104.101.114.32.84.119.101.110.46.0.";
	}
	
	/**
	 * 
	 * @param age
	 * @return random wish for age (18; 20; -1: default)
	 */
	public String getWish (int age) {
		String[] wish;
		switch (age) {
		case 18:
			wish = wish18;
			break;
		case 20:
			wish = wish20;
			break;
			default:
				wish = wishDefault;
		}
		int number = (int) (Math.random() * wish.length);
		return decode(wish[number]);
	}
	
	
	
	private String decode (String txt) {
		String[] msgSplitted = txt.split("\\.");
		String info = "";
		for (int i = 0; i < msgSplitted.length; i++) {
			info = info + ((char) (Byte.parseByte(msgSplitted[i])));
		}
		return info;
	}
}