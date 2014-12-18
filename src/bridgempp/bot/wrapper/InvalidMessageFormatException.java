package bridgempp.bot.wrapper;


public class InvalidMessageFormatException extends RuntimeException {

	public InvalidMessageFormatException(Exception e) {
		super(e.toString());
		setStackTrace(e.getStackTrace());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7238644370488112678L;

}
