package bots.QuizBot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity(name = "QUIZENTRY")
public class QuizEntry
{
	@Id()
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "IDENTIFIER", nullable = false)
	private int identifier;
	
	@OneToMany(mappedBy="quizEntry", cascade=CascadeType.ALL, orphanRemoval = true)
	private List<QuizValue> quizValues = new ArrayList<QuizValue>();
	
	@ManyToOne(optional = false)
	private Quiz quiz;

	public QuizEntry(List<QuizHeader> headers, String[] data, Quiz quiz)
	{
		this.quiz = quiz;
		for(int i = 0; i < headers.size(); i++)
		{
			quizValues.add(new QuizValue(this, headers.get(i), data[i]));
		}
	}
	
	@SuppressWarnings("unused")
	private QuizEntry()
	{
		
	}
	
	public QuizValue getRandomQuestionValue(Random random)
	{
		QuizValue quizValue;
		do {
			quizValue = quizValues.get(random.nextInt(quizValues.size()));
		} while(!quizValue.getQuizHeader().isQuestion() || !quizValue.hasValue());
		return quizValue;
	}
	
	public QuizValue getRandomAnswerValue(Random random)
	{
		QuizValue quizValue;
		do {
			quizValue = quizValues.get(random.nextInt(quizValues.size()));
		} while(!quizValue.getQuizHeader().isAnswer() || !quizValue.hasValue());
		return quizValue;
	}	
	public String toString()
	{
		String fields = "";
		Iterator<QuizValue> iterator = quizValues.iterator();
		while(iterator.hasNext())
		{
			QuizValue value = iterator.next();
			fields += value.getValueType() + ": " + value.getValue() + "\n";
		}
		return fields;
	}
}
