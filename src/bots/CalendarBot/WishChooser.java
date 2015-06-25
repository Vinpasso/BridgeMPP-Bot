package bots.CalendarBot;

public class WishChooser {
	String wish;
	
	public WishChooser () {
		setWish();
	}
	
	public WishChooser (int age) {
		setWish();
	}
	
	public void setWish () {
		BirthdayWishes bw = new BirthdayWishes();
		int number = (int) (Math.random() * bw.length());
		wish = new Decoder(bw.getWishDefault(number)).getTxt();
	}
	
	public String getWish() {
		return wish;
	}
	
	@Override
	public String toString () {
		return wish;
	}
}
