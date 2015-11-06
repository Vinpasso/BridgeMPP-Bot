package bots.QuizBot;

import java.io.Serializable;
import java.util.Random;

public class Quiz implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -379496367759177405L;
	private String name;
	private String[] headers;
	private String[][] data;
	
	public Quiz(String name)
	{
		this.name = name;
	}

	public void setHeaders(String[] headers)
	{
		this.headers = headers;
	}

	public void addData(String[] data, int row)
	{
		this.data[row] = data;
	}

	public void setDataSize(int numberColumns, int numberRows)
	{
		headers = new String[numberColumns];
		data = new String[numberRows][numberColumns];
	}

	public String getName()
	{
		return name;
	}
	
	public QuizQuestion getQuestion()
	{
		Random random = new Random();
		int givenColumn;
		int wantedColumn;
		int valueRow;
		
		String givenData;
		String wantedData;
		do
		{
			givenColumn = random.nextInt(headers.length);
			wantedColumn = (givenColumn + 1 + random.nextInt(headers.length - 1)) % headers.length;
			valueRow = random.nextInt(data.length);
			givenData = data[valueRow][givenColumn];
			wantedData = data[valueRow][wantedColumn];
		} while(givenData == null || givenData.length() == 0 || wantedData == null || wantedData.length() == 0);
		
		return new QuizQuestion(headers[givenColumn], givenData, headers[wantedColumn], data[valueRow][wantedColumn]);
	}

	public int getTableWidth()
	{
		return headers.length;
	}

}
