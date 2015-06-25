package bots.CalendarBot;

public class Decoder {
	String txt;
	
	public Decoder(String txt) {
		this.txt = decode(decode(txt));		
	}
	
	public byte[] decode (String txt) {
		String[] msgSplitted = txt.split("\\.");
		byte[] info = new byte[msgSplitted.length];
		
		for (int i = 0; i < info.length; i++) {
			info[i] = Byte.parseByte(msgSplitted[i]);
		}
		return info;
	}
	
	public String decode (byte[] txt) {
		String info = "";
		for (int i = 0; i < txt.length; i++) {
			info = info + ((char) (txt[i]));
		}
		
		return info;
	}
	
	public String getTxt() {
		return txt;
	}
	
	public void setTxt(String txt) {
		this.txt = decode(decode(txt));
	}
	
	@Override
	public String toString () {
		return txt;
	}
}
