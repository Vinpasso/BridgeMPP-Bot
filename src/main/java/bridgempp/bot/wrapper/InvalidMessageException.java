package bridgempp.bot.wrapper;


public class InvalidMessageException extends RuntimeException {

	public InvalidMessageException(String reason) {
		super(reason);
	}

	public InvalidMessageException(Exception e)
	{
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7238644370488112678L;

}
