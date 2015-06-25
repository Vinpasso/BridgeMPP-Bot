package bots.CalendarBot;

import java.util.LinkedList;

/**
 * 
 * @author Bernie
 *
 */
public class Reminder extends Thread {
	private LinkedList<Event> nextReminds;
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
		LinkedList<Event> nextRepeats = new LinkedList<Event>();
		nextReminds = new LinkedList<Event>();
		int date = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear);
		for (int i = 0; i < calendar.length; i++) {
			for (int j = 0; j < calendar[i].getEvents().length; j++) {
				
				//set nextEvents
				if(calendar[i].getEvents()[j].getNextRepeat() > date) {					
					if (nextRepeats.size() == 0 ) {
						nextRepeats.add(calendar[i].getEvents()[j]);
					}
					else if (calendar[i].getEvents()[j].getNextRepeat() == nextRepeats.get(0).getNextRepeat()) {					
						nextRepeats.add(calendar[i].getEvents()[j]);
					}
					else if (calendar[i].getEvents()[j].getNextRepeat() < nextRepeats.get(0).getNextRepeat()) {
						nextRepeats = new LinkedList<Event>();
						nextRepeats.add(calendar[i].getEvents()[j]);
					}
				}				
				
				//set nextReminds
				if(calendar[i].getEvents()[j].getRemind() > 0 && calendar[i].getEvents()[j].getNextRemind() > date) {
					if (nextReminds.size() == 0 ) {
						nextReminds.add(calendar[i].getEvents()[j]);
					}
					else if (calendar[i].getEvents()[j].getNextRemind() == nextReminds.get(0).getNextRemind()) {					
						nextReminds.add(calendar[i].getEvents()[j]);
					}
					else if (calendar[i].getEvents()[j].getNextRemind() < nextReminds.get(0).getNextRemind()) {
						nextReminds = new LinkedList<Event>();
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
	 * @return the events to remind
	 */
	public Event[] getNextRemind () {
		try {
			int date = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear);
			if (date < nextReminds.get(0).getNextRemind() || date < nextReminds.get(0).getNextRepeat()) {
				return (nextReminds.toArray(new Event[nextReminds.size()]));
			}
			return null;
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 
	 * @return minutes until next Reminder/ Event 
	 *  or -1 if there is no upcoming event 
	 *  or -2 if there is no event
	 */
	public int minToRemind () {
		int date = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear);
		try {			
			if (nextReminds.get(0).getNextRemind() > date) {
				return nextReminds.get(0).getNextRemind() - date;
			} 
			else if (nextReminds.get(0).getNextRepeat() > date) {
				return nextReminds.get(0).getNextRepeat() - date;
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
		while (true) {
			setNextReminds();
			try {
				if (minToRemind() < 0) {
					break;
				} else {
					Thread.sleep((long) (minToRemind()) * 60000);
				}
			} catch (InterruptedException e) {
				break;
			}		
		
			try {
				for (int i = 0; i < nextReminds.size(); i++) {
					if (isRemindRepeat(nextReminds.get(i)) == 2 && alertson) {
						CalendarBot.printMessage(nextReminds.get(i).toStringRemind(firstYear));
					}
					else if ((isRemindRepeat(nextReminds.get(i)) == 1 || isRemindRepeat(nextReminds.get(i)) == 3) && alertson) {
						CalendarBot.printMessage(nextReminds.get(i).toStringRepeat(firstYear));
					}
				}
			} catch (Exception e) {
				
			}
		}
	}
	
	/**
	 * 
	 * @param event
	 * @return 0: else, 1: if event is now, 2: if event should be remind now, 3: if both
	 */
	public int isRemindRepeat (Event event) {
		int date = CalDateFormat.dateToMin(CurrentDate.getDateWTime(), firstYear);			
		if (event.getNextRemind() == event.getNextRepeat() && event.getNextRemind() <= date) {
			return 3;
		} 
		else if (event.getNextRepeat() <= date) {
				return 1;
		}
		else if (event.getNextRemind() <= date) {
			return 2;
		}
		
		return 0;	
	}
	
	/**
	 * 
	 */
	public void alertson () {
		alertson = true;
	}
	
	/**
	 * 
	 */
	public void alertsoff () {
		alertson = false;
	}
	
}
