package bots.QuizBot;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity(name = "QuizHeader")
public class QuizHeader
{
	@Id()
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "Identifier", nullable = false)
	private int identifier;
	
	@Column(name = "HeaderName", nullable = false, length=255)
	private String name = "Unnamed Header";
	
	@ManyToOne(optional = false, cascade=CascadeType.ALL)
	private Quiz quiz;
	
	@Column(name="isQuestion", nullable=false)
	private boolean isQuestion = false;
	
	@Column(name="isAnswer", nullable=false)
	private boolean isAnswer = false;
	
	@Column(name="precision", nullable=false)
	private float precision = -1.0f;

	public QuizHeader(String name, boolean isQuestion, boolean isAnswer, float precision, Quiz quiz)
	{
		this.name = name;
		this.isQuestion = isQuestion;
		this.isAnswer = isAnswer;
		this.precision = precision;
		this.quiz = quiz;
	}
	
	@SuppressWarnings("unused")
	private QuizHeader()
	{
		
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the quiz
	 */
	public Quiz getQuiz()
	{
		return quiz;
	}

	/**
	 * @return the isQuestion
	 */
	public boolean isQuestion()
	{
		return isQuestion;
	}

	/**
	 * @return the Precision
	 */
	public float getPrecision()
	{
		return precision;
	}

	/**
	 * @return
	 */
	public boolean isAnswer()
	{
		return isAnswer;
	}
	
}
