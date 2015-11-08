package bots.QuizBot;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity(name = "QuizValue")
public class QuizValue
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="Identifier", nullable = false)
	private int identifier;
	
	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	private QuizEntry quizEntry;
	
	@ManyToOne(optional = false)
	private QuizHeader quizHeader;

	@Column(name = "Value", length = 1024, nullable = true)
	private String value;

	public QuizValue(QuizEntry quizEntry, QuizHeader quizHeader, String value)
	{
		this.quizHeader = quizHeader;
		this.quizEntry = quizEntry;
		this.value = value.trim();
	}
	
	@SuppressWarnings("unused")
	private QuizValue()
	{
		
	}

	public boolean hasValue()
	{
		return value != null && !value.isEmpty();
	}
	
	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}
	
	public String getValueType()
	{
		return quizHeader.getName();
	}

	/**
	 * @return the quizHeader
	 */
	public QuizHeader getQuizHeader()
	{
		return quizHeader;
	}
	
}
