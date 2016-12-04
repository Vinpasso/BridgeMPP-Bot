package bots.QuizBot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import bridgempp.bot.database.PersistenceManager;
import bridgempp.bot.metawrapper.MetaClass;
import bridgempp.bot.metawrapper.MetaMethod;
import bridgempp.bot.wrapper.Message;

@MetaClass(triggerPrefix="?quiz ", helpTopic="Quiz Bot. Asks you Questions. Gives you Money.")
public class QuizBot
{
	private HashMap<String, Integer> points;
	private Quiz activeQuiz;
	private QuizQuestion activeQuestion;
	private int questionNumber;
	private int maxQuestionNumber;
	
	@MetaMethod(trigger="new ",helpTopic ="Create a new Quiz with name. Requires the Quiz Name")
	public String importQuiz(String name)
	{
		Quiz quiz = new Quiz(name);

		PersistenceManager.getForCurrentThread().updateState(quiz);
		return "Quiz successfully created";
	}
	
	@MetaMethod(trigger="remove ",helpTopic ="Remove a new Quiz with name. Requires the Quiz Name")
	public String removeQuiz(String name)
	{
		Quiz quiz = PersistenceManager.getForCurrentThread().getFromPrimaryKey(Quiz.class, name);
		if(quiz == null)
		{
			return "Quiz not found";
		}
		PersistenceManager.getForCurrentThread().removeState(quiz);
		return "Quiz successfully removed";
	}
	
	@MetaMethod(trigger="header ", helpTopic="Import a header of Data in CSV. Requires Quiz Name, Header Name, isQuestion, isAnswer, Precision")
	public String headerData(String name, String headerName, boolean isQuestion, boolean isAnswer, float precision)
	{
		Quiz quiz = PersistenceManager.getForCurrentThread().getFromPrimaryKey(Quiz.class, name);
		if(quiz == null)
		{
			return "Quiz not found";
		}
		quiz.addHeader(headerName, isQuestion, isAnswer, precision);
		return "Successfully imported Header";
	}
	
	@MetaMethod(trigger="data ", helpTopic="Import a row of Data in CSV. Requires Name, Values")
	public String rowData(String name, String data)
	{
		Quiz quiz = PersistenceManager.getForCurrentThread().getFromPrimaryKey(Quiz.class, name);
		if(quiz == null)
		{
			return "Quiz not found";
		}
		String[] columns = data.split(";");
		if(quiz.getTableWidth() != columns.length)
		{
			return "Invalid number of Columns";
		}
		quiz.addData(columns);
		return "Successfully imported Row";
	}
	
	@MetaMethod(trigger = "start ", helpTopic="Start a Quiz. Requires name and number of questions")
	public String startQuiz(String quizName, int numberOfQuestions)
	{
		activeQuiz = PersistenceManager.getForCurrentThread().getFromPrimaryKey(Quiz.class, quizName);
		points = new HashMap<String, Integer>();
		if(activeQuiz == null)
		{
			return "Quiz not found";
		}
		if(numberOfQuestions < 1)
		{
			return "Please enter a valid number of Questions";
		}
		this.questionNumber = 0;
		this.maxQuestionNumber = numberOfQuestions;
		return "Welcome to the Quiz Bot. The Quiz: " + activeQuiz.getName() + " has now started\n" + sendQuestion();
	}
	
	@MetaMethod(trigger ="answer", helpTopic="Provide an answer for a Quiz. Requires an answer")
	public String answerQuestion(Message message)
	{
		String attempt = message.getMessage().substring("?quiz answer ".length());
		if(activeQuestion == null)
		{
			return "There is no active Question";
		}
		if(activeQuestion.testAnswer(attempt))
		{
			int score = points.getOrDefault(message.getSender(), 0) + 1;
			points.put(message.getSender(), score);
			return "Congratulations: " + activeQuestion.provideResolution() + "\n" + sendQuestion();
		}
		return "Sorry, " + activeQuestion.provideNegative(attempt);
	}
	
	@MetaMethod(trigger="skip", helpTopic="Skip this stupid question")
	public String skipQuestion()
	{
		return "Question skipped:\n" + activeQuestion.provideResolution() + "\n" + sendQuestion();
	}

	private String sendQuestion()
	{
		questionNumber++;
		if(questionNumber > maxQuestionNumber)
		{
			return sendQuizCompleted();
		}
		activeQuestion = activeQuiz.getQuestion();
		return "Question #" + questionNumber + "/" + maxQuestionNumber + ": " + activeQuestion.askQuestion();
	}

	private String sendQuizCompleted()
	{
		if(activeQuiz == null || points == null)
		{
			return "No Quiz active or no points scored";
		}
		String result = "The Quiz has been completed.\nScores:\n";
		ArrayList<Entry<String, Integer>> scores = new ArrayList<>(points.entrySet());
		Collections.sort(scores, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)
			{
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		Iterator<Entry<String, Integer>> scoreIterator = scores.iterator();
		while(scoreIterator.hasNext())
		{
			Entry<String, Integer> entry = scoreIterator.next();
			result += entry.getKey() + ":\t" + entry.getValue() + "\n";
		}
		activeQuiz = null;
		activeQuestion = null;
		points = null;
		return result;
	}
	
}
