package bots.CalendarBot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import bridgempp.bot.wrapper.Schedule;

/**
 * 
 * @author Bernie
 *
 */
public class Reminder implements Runnable {
	private ArrayList<Event> nextReminds;
	private Calendar[] calendar;
	private int firstYear;
	public boolean alertson;
	
	public Reminder (Calendar[] calendar, int firstYear, boolean alertson) {
		this.calendar = calendar;
		this.firstYear = firstYear;
		this.alertson = alertson;
		setNextReminds();
	}
	
	/**
	 * add next Event(s) and/ or Reminder(s) to {@code nextReminds}
	 */
	public void setNextReminds() {
		ArrayList<Event> nextRepeats = new ArrayList<>();
		nextReminds = new ArrayList<>();
		int dateNow = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear);
		for (int i = 0; i < calendar.length; i++) {
			for (int j = 0; j < calendar[i].getEvents().length; j++) {
				
				//set nextEvents
				if(calendar[i].getEvents()[j].getNextRepeat() > dateNow) {					
					if (nextRepeats.size() == 0 ) {
						nextRepeats.add(calendar[i].getEvents()[j]);
					}
					else if (calendar[i].getEvents()[j].getNextRepeat() == nextRepeats.get(0).getNextRepeat()) {					
						nextRepeats.add(calendar[i].getEvents()[j]);
					}
					else if (calendar[i].getEvents()[j].getNextRepeat() < nextRepeats.get(0).getNextRepeat()) {
						nextRepeats = new ArrayList<Event>();
						nextRepeats.add(calendar[i].getEvents()[j]);
					}
				}				
				
				//set nextReminds
				if(calendar[i].getEvents()[j].getRemind() > 0 && calendar[i].getEvents()[j].getNextRemind() > dateNow) {
					if (nextReminds.size() == 0 ) {
						nextReminds.add(calendar[i].getEvents()[j]);
					}
					else if (calendar[i].getEvents()[j].getNextRemind() == nextReminds.get(0).getNextRemind()) {					
						nextReminds.add(calendar[i].getEvents()[j]);
					}
					else if (calendar[i].getEvents()[j].getNextRemind() < nextReminds.get(0).getNextRemind()) {
						nextReminds = new ArrayList<Event>();
						nextReminds.add(calendar[i].getEvents()[j]);
					}
				}
			}
		}
		//compare nextRepeats Reminds
		if (nextRepeats.size() > 0 && nextReminds.size() > 0) {
			if (nextRepeats.get(0).getNextRepeat() < nextReminds.get(0).getNextRemind()) {
				nextReminds = nextRepeats;
			}
			else if (nextRepeats.get(0).getNextRepeat() == nextReminds.get(0).getNextRemind()) {
				nextReminds.addAll(nextRepeats);
			}		
		}
		else if (nextReminds.size() == 0) {
			nextReminds = nextRepeats;
		}
	}
	
	/**
	 * 
	 * @return minutes until next Reminder/ Event 
	 *  or -1 if there is no upcoming event 
	 *  or -2 if there is no event
	 */
	public int minToRemind () {
		int dateNow = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear);
		try {			
			if (nextReminds.get(0).getNextRemind() > dateNow) {
				return nextReminds.get(0).getNextRemind() - dateNow;
			} 
			else if (nextReminds.get(0).getNextRepeat() > dateNow) {
				return nextReminds.get(0).getNextRepeat() - dateNow;
			}
			else return -1;
		} 
		catch (Exception e) {
			return -2;
		}
	}
	
	/**
	 * sleeps until next Reminder and prints Reminder
	 */
	@Override
	public void run() {
//		while (true) {
//			setNextReminds();
//			try {
//				if (minToRemind() < 0) {
//					break;
//				} else {
//					Thread.sleep((long) (minToRemind()) * 60000);
//				}
//			} catch (InterruptedException e) {
//				break;
//			}		
		
			try {
				for (int i = 0; i < nextReminds.size(); i++) {
					if (isRemindRepeat(nextReminds.get(i)) == 2 && alertson) {
						CalendarBot.printMessageTumSpam(nextReminds.get(i).toStringRemind());
					}
					else if (isRemindRepeat(nextReminds.get(i)) == 1 && alertson) {
						CalendarBot.printMessageTumSpam(nextReminds.get(i).toStringRepeat());
					}
				}
			} catch (Exception e) {
				
			}
			scheduleNextReminder();
		//}
	}
	
	/**
	 * Schedules the Notification of the next Event to the BotWrapper Schedule
	 */
	public void scheduleNextReminder()
	{
		setNextReminds();
		Schedule.scheduleOnce(this, minToRemind(), TimeUnit.MINUTES);
	}

	/**
	 * 
	 * @param event
	 * @return 0: else, 1: if event is now, 2: if event should be remind now
	 */
	public int isRemindRepeat (Event event) {
		int dateNow = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear);			
		if (event.getNextRepeat() <= dateNow) {
				return 1;
		}
		else if (event.getNextRemind() <= dateNow) {
			return 2;
		}
		
		return 0;	
	}
	
}
