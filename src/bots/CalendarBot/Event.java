package bots.CalendarBot;

/**
 * 
 * @author Bernie
 *
 */
public class Event {
	protected String wat;
	protected int date, repeat, remind, nextRepeat, nextRemind;
	
	/**
	 * 
	 * @param wat description of event
	 * @param date date of event (minutes since 01.01.2000 00:00)
	 * @param repeat number of days to repeat event (-1 = off)
	 * @param remind number of minutes to remind before event starts (-1 = off)
	 * @param currentDate current date as past minutes since 01.01.{@code firstYear} 
	 * @param firstYear 
	 */
	public Event (String wat, int date, int repeat, int remind, int currentDate, int firstYear) {
		this.wat = wat;
		this.date = date;
		this.repeat = repeat;
		this.remind = remind;
		setNextRepeat(currentDate, firstYear);
		setNextRemind(firstYear);
	}
	
	
	public void setWat (String wat) {
		this.wat = wat;
	}
	
	public void setDate (int date) {
		this.date = date;
	}
	
	public void setRepeat (int repeat) {
		this.repeat = repeat;
	}
	
	public void setRemind (int remind) {
		this.remind = remind;
	}
	
	public void setNextRepeat (int currentDate, int firstYear) {
		nextRepeat = date;
		int[] nextRepeatSplitted = CalDateFormat.minToDateSplitted(date, firstYear);
		int correctDay = nextRepeatSplitted[0];
		while (nextRepeat < currentDate && repeat > 0) {
			if (repeat % 360 == 0) {
				nextRepeatSplitted[0] = correctDay;
				for (int i = 0; i < repeat / 360; i++) {					
					nextRepeatSplitted[2] += 1;
				}
				while (!CalDateFormat.checkDate(nextRepeatSplitted[0], nextRepeatSplitted[1], nextRepeatSplitted[2], nextRepeatSplitted[3], nextRepeatSplitted[4]) && nextRepeatSplitted[0] > 0) {
					nextRepeatSplitted[0]--;
				}
				nextRepeat = CalDateFormat.dateToMin(CalDateFormat.dateSplittedToDate(nextRepeatSplitted[0], nextRepeatSplitted[1], nextRepeatSplitted[2], nextRepeatSplitted[3], nextRepeatSplitted[4]), firstYear);				
			}
			else if (repeat % 30 == 0) {
				nextRepeatSplitted[0] = correctDay;
				for (int i = 0; i < repeat / 30; i++) {
					nextRepeatSplitted[1] += 1;
					if (nextRepeatSplitted[1] == 13) {
						nextRepeatSplitted[1] = 1;
						nextRepeatSplitted[2] += 1; 
					}
				}
				while (!CalDateFormat.checkDate(nextRepeatSplitted[0], nextRepeatSplitted[1], nextRepeatSplitted[2], nextRepeatSplitted[3], nextRepeatSplitted[4]) && nextRepeatSplitted[0] > 0) {
					nextRepeatSplitted[0]--;
				}
				nextRepeat = CalDateFormat.dateToMin(CalDateFormat.dateSplittedToDate(nextRepeatSplitted[0], nextRepeatSplitted[1], nextRepeatSplitted[2], nextRepeatSplitted[3], nextRepeatSplitted[4]), firstYear);
			}
			else {
				nextRepeat += repeat * 1440;
			}
		}
	}
	
	public void setNextRemind (int firstYear) {		
		nextRemind = nextRepeat;
		int[] nextRemindSplitted = CalDateFormat.minToDateSplitted(nextRepeat, firstYear);
		int correctDay = nextRemindSplitted[0];
		//525600 = minutes of a year
		if (remind % 525600 == 0 && remind > 0) {
			nextRemindSplitted[0] = correctDay;
			for (int i = 0; i < remind / 525600; i++) {
				nextRemindSplitted[2] += -1;
			}
			while (!CalDateFormat.checkDate(nextRemindSplitted[0], nextRemindSplitted[1], nextRemindSplitted[2], nextRemindSplitted[3], nextRemindSplitted[4]) && nextRemindSplitted[0] > 0) {
				nextRemindSplitted[0]--;
			}
			nextRemind = CalDateFormat.dateToMin(CalDateFormat.dateSplittedToDate(nextRemindSplitted[0], nextRemindSplitted[1], nextRemindSplitted[2], nextRemindSplitted[3], nextRemindSplitted[4]), firstYear);
		}
		//43200 = minutes of a month
		else if (remind % 43200 == 0 && remind > 0) {
			nextRemindSplitted[0] = correctDay;
			for (int i = 0; i < remind / 43200; i++) {				
				nextRemindSplitted[1] += -1;
				if (nextRemindSplitted[1] == 0) {
					nextRemindSplitted[1] = 12;
					nextRemindSplitted[2] += -1; 
				}
			}
			while (!CalDateFormat.checkDate(nextRemindSplitted[0], nextRemindSplitted[1], nextRemindSplitted[2], nextRemindSplitted[3], nextRemindSplitted[4]) && nextRemindSplitted[0] > 0) {
				nextRemindSplitted[0]--;
			}
			nextRemind = CalDateFormat.dateToMin(CalDateFormat.dateSplittedToDate(nextRemindSplitted[0], nextRemindSplitted[1], nextRemindSplitted[2], nextRemindSplitted[3], nextRemindSplitted[4]), firstYear);
		}
		else if (remind >= 0){
			nextRemind = nextRepeat - remind;
		}
	}
	
	
	public String getWat () {
		return wat;
	}
	
	public int getDate () {
		return date;
	}
	
	public int getRepeat () {
		return repeat;
	}
	
	public int getRemind () {
		return remind;
	}
	
	public int getNextRepeat() {
		return nextRepeat;
	}
	
	public int getNextRemind() {
		return nextRemind;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		Event event;
		try {
			event = (Event) (obj);
		}
		catch (Exception e) {
			return false;
		}
		return event.wat == this.wat && event.date == this.date && event.repeat == this.repeat && event.remind == this.remind;
	}
	
	@Override
	public String toString() {
		return date + " " + wat + " " + repeat + " " + remind;
	}
	
	public String toStringList (int firstYear) {
		return CalDateFormat.minToDate(date, firstYear) + ": " + wat + ", repeat: " + repeatToString() + ", remind: " + remindToString();
	}
	
	/**
	 * 
	 * @param firstYear
	 * @return reminiscence of event
	 */
	public String toStringRemind (int firstYear) {
		return CalDateFormat.minToWeekday(nextRepeat, firstYear) + " " + CalDateFormat.minToDate(nextRepeat, firstYear) + ": " + wat;
	}
	
	/**
	 * 
	 * @param firstYear
	 * @return
	 */
	public String toStringRepeat (int firstYear) {
		return "Today " + CalDateFormat.minToDate(nextRepeat, firstYear).substring(11, 16) + ": " + wat;
	}
	
	protected String repeatToString () {
		if (repeat % 360 == 0) {
			return (repeat / 360) + (repeat == 360 ? " year" : " years");
		}
		if (repeat % 30 == 0) {
			return (repeat / 30) + (repeat == 30 ? " month" : " months");
		}
		if (repeat % 7 == 0) {
			return (repeat / 7) + (repeat == 7 ? " week" : " weeks");
		}
		return repeat + " days";		
	}
	
	protected String remindToString () {
		if (remind % 525600 == 0) {
			return "" + (remind / 525600) + (remind == 525600 ? " year" : " years");
		}
		if (remind % 43200 == 0) {
			return "" + (remind / 43200) + (remind == 43200 ? " month" : " months");
		}
		if (remind % 10080 == 0) {
			return "" + (remind / 10080) + (remind == 10080 ? " week" : " weeks");
		}
		if (remind % 1440 == 0) {
			return "" + (remind / 1440) + (remind == 1440 ? " day" : " days");
		}
		if (remind % 60 == 0) {
			return "" + (remind / 60) + (remind == 60 ? " hour" : " hours");
		}
		return remind + " minutes";
	}
}
