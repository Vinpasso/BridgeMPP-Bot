package bots.CalendarBot;

public class BirthdayWishes {
	private String[] wishDefault;
	private String[] wish20;
	
	public BirthdayWishes () {
		wishDefault = wishDefault().split("\\.0\\.");
	}
	
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
	
	private String wish20 () {
		return "";
	}
	
	
	public String[] getWishDefault () {
		return wishDefault;
	}
	
	public String getWishDefault (int index) {
		try {
			return wishDefault[index];
		} catch (Exception e) {
			return null;
		}
	}
	
	public int length () {
		return wishDefault.length;
	}
}