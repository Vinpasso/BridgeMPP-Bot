package bots.QuizBot;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import bridgempp.bot.database.PersistenceManager;

@Entity(name = "QuizBot_Quiz")
public class Quiz
{
	/**
	 * 
	 */
	@Id
	@Column(name = "Name", nullable = false, length=255)
	private String name = "Unnamed Quiz";
	
	@Column(name = "NumberOfEntryColumns", nullable = false)
	private int numberOfColumns = 0;
	
	@Column(name="Headers", nullable = false)
	@OneToMany(mappedBy="quiz", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<QuizHeader> headers = new ArrayList<QuizHeader>();
	
	@Column(name="Entries", nullable = false)
	@OneToMany(mappedBy="quiz", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<QuizEntry> entries = new ArrayList<QuizEntry>();
	
	public Quiz(String name)
	{
		this.name = name;
	}
	
	@SuppressWarnings("unused")
	private Quiz()
	{
		
	}
	
	public void addHeader(String name, boolean isQuestion, boolean isAnswer, float precision)
	{
		QuizHeader quizHeader = new QuizHeader(name, isQuestion, isAnswer, precision, this);
		PersistenceManager.getForCurrentThread().updateState(quizHeader);
		headers.add(quizHeader);
		numberOfColumns = headers.size();
	}
	

	public void addData(String[] data)
	{
		if(data.length != numberOfColumns)
		{
			throw new InvalidParameterException("Data length did now Match number of Columns");
		}
		QuizEntry quizEntry = new QuizEntry(headers, data, this);
		PersistenceManager.getForCurrentThread().updateState(quizEntry);
		entries.add(quizEntry);
	}

	public String getName()
	{
		return name;
	}
	
	public QuizQuestion getQuestion()
	{
		Random random = new Random();

		QuizEntry entry = entries.get(random.nextInt(entries.size()));
		QuizValue value1;
		QuizValue value2;
		do
		{
			value1 = entry.getRandomQuestionValue(random);
			value2 = entry.getRandomAnswerValue(random);
		} while(value1.equals(value2));
		return new QuizQuestion(value1.getQuizHeader(), value1, value2.getQuizHeader(), value2, entry);
	}

	public int getTableWidth()
	{
		return numberOfColumns;
	}

}
