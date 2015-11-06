package bots.QuizBot;

public class QuizQuestion
{
	private String givenHeader;
	private String givenValue;
	private String wantedHeader;
	private String wantedValue;
	
	public QuizQuestion(String givenHeader, String givenValue, String wantedHeader, String wantedValue)
	{
		this.givenHeader = givenHeader;
		this.givenValue = givenValue;
		this.wantedHeader = wantedHeader;
		this.wantedValue = wantedValue;
	}
	
	public String askQuestion()
	{
		return "The " + givenHeader + " is " + givenValue + ". What is the " + wantedHeader + "?";
	}
	
	public boolean testAnswer(String answer)
	{
		return answer.trim().equalsIgnoreCase(wantedValue);
	}
	
	public String provideResolution()
	{
		return "The " + wantedHeader + " of the " + givenHeader + " of " + givenValue + " is " + wantedValue;
	}
	
	public String provideNegative(String attempt)
	{
		return "The " + wantedHeader + " of the " + givenHeader + " of " + givenValue + " is not " + attempt;
	}

	
	/**
	 * @return the givenHeader
	 */
	public String getGivenHeader()
	{
		return givenHeader;
	}
	/**
	 * @return the givenValue
	 */
	public String getGivenValue()
	{
		return givenValue;
	}
	/**
	 * @return the wantedHeader
	 */
	public String getWantedHeader()
	{
		return wantedHeader;
	}
	/**
	 * @return the wantedValue
	 */
	public String getWantedValue()
	{
		return wantedValue;
	}

	
	
}
