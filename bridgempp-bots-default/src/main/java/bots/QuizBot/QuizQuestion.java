package bots.QuizBot;

import org.apache.commons.lang3.StringUtils;

public class QuizQuestion
{
	private QuizEntry entry;
	private QuizHeader givenHeader;
	private QuizValue givenValue;
	private QuizHeader wantedHeader;
	private QuizValue wantedValue;
	
	public QuizQuestion(QuizHeader givenHeader, QuizValue givenValue, QuizHeader wantedHeader, QuizValue wantedValue, QuizEntry entry)
	{
		this.givenHeader = givenHeader;
		this.givenValue = givenValue;
		this.wantedHeader = wantedHeader;
		this.wantedValue = wantedValue;
		this.entry = entry;
	}
	
	@SuppressWarnings("unused")
	private QuizQuestion()
	{
		
	}
	
	public String askQuestion()
	{
		return "The " + givenHeader.getName() + 
				" is " + givenValue.getValue() + 
				". What is the " + wantedHeader.getName() + "?";
	}
	
	public boolean testAnswer(String answer)
	{
		if(wantedHeader.getPrecision() >= 1.0f)
		{
			return answer.trim().equalsIgnoreCase(wantedValue.getValue());
		}
		else
		{
			return StringUtils.getJaroWinklerDistance(answer.trim(), wantedValue.getValue()) > wantedHeader.getPrecision();
		}
	}
	
	public String provideResolution()
	{
		return "The " + wantedHeader.getName() + 
				" of the " + givenHeader.getName() + 
				" of " + givenValue.getValue() + 
				" is " + wantedValue.getValue() + "\nThe Solution is:\n" + entry.toString();
	}
	
	public String provideNegative(String attempt)
	{
		return "The " + wantedHeader.getName() + " of the " + givenHeader.getName() + " of " + givenValue.getValue() + " is not " + attempt;
	}
	
}
