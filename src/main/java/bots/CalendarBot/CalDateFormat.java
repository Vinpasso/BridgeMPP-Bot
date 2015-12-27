package bots.CalendarBot;

import java.util.GregorianCalendar;
import java.util.Calendar;

/**
 *
 * @author Bernie
 */
public class CalDateFormat {
    
    public CalDateFormat() {}
    
    /**
     * returns minutes since {@code firstYear} (01.01.firstYear 00:00)
     * @param date - format dd.mm.yyyy hh:mm
     * @param firstYear should not be a lap year
     * @return minutes since {@code firstYear} (01.01.firstYear 00:00)
     * @throws IllegalArgumentException
     */
    public static int dateToMin (String date, int firstYear) throws IllegalArgumentException {
        String[] splitDate = new String[5];
        String[] dateSplit;
        int[] intDate = new int[5];
        try {
            //Split by "." or "/"
            dateSplit = date.split("\\.");
            if (dateSplit[0].equals(date)) {
                dateSplit = date.split("\\/");
            }
            for (int i = 0; i < 3; i++) {
                splitDate[i] = dateSplit[i];
            }
            
            //split by " "
            dateSplit = splitDate[2].split("\\ ");
            splitDate[2] = dateSplit[0];
            splitDate[3] = dateSplit[1];
            
            //split by ":"
            dateSplit = splitDate[3].split("\\:");
            splitDate[3] = dateSplit[0];
            splitDate[4] = dateSplit[1];
            
            
            //parseInt            
            for (int i = 0; i < 5; i++) {
                intDate[i] = Integer.parseInt(splitDate[i]);
            }           
            
            //checkDate
            if (!checkDate(intDate[0], intDate[1], intDate[2], intDate[3], intDate[4]) || intDate[0] == 0 || intDate[1] == 0) {
                throw new IllegalArgumentException();
            }
            
            //Date to minutes
            int min = 0;
            min += (intDate[2] - firstYear)*365;             //add (years to days)
            if (isLeapYear(firstYear)) {                      //add one day per labyear in this period
                min += numLabYears(firstYear, intDate[2]);   
            }            
            else {
                min += numLabYears(firstYear, intDate[2]-1);
            }
                     
            
            for (int i = 1; i < intDate[1]; i++) {      //add (months to days)
                min += daysOfMonth(i, intDate[2]);      
            }
            min += intDate[0] - 1;                      //add days
            min *= 24;                                  //to hours
            min += intDate[3];                          //add hours   
            min *= 60;                                  //to minutes
            min += intDate[4];                          //add minutes                    
                    
            return min;            
        }        
        catch (Exception e) {
            throw new IllegalArgumentException();
        }
        
    }
    
    /**
     * 
     * @param min number of minutes
     * @param firstYear
     * @return the date (including time) which will be in {@code min} minutes after 01.01.{@code firstYear} 00:00.
     *  index 0: day, 1: month, 2: year, 3: hour; 4: minute
     */
    public static int[] minToDateSplitted (int min, int firstYear) {
    	int[] date = new int[5];
    	int year = firstYear;
    	int month = 1;
    	int day = 1;
    	int hour = 0;
    	int minute = 0;
    	
    	//date is after firstYear
    	if (min >= 0) {
    		while (min >= 44640) {
	    		for (int i = 1; i <= 12; i++) {
					if ((daysOfMonth(i, year) * 1440) <= min) {
						min = min - daysOfMonth(i, year) * 1440;
						month++;
						if (month == 13) {
							month = 1;
							year++;
						}
					}
					else break;
				}
    		}
    		day = day + min / 1440;
    		min = min % 1440;
    		hour = min / 60;
    		minute = min % 60;
    	}
    	//date is before firstYear
    	else {
    		min = Math.abs(min);
    		while (min >= 44640) {
    			year--;
    			for (int i = 12; i > 0 ; i--) {
    				if ((daysOfMonth(i, year) * 1440) <= min) {						
						min = min - daysOfMonth(i, year) * 1440;
						month--;
						if (month == 0) month = 12;
					}
    				else break;
				}
    		}
    		if (min != 0) {      			
    			month--;
    			if (month == 0) {
    				year--;
    				month = 12;
    			}
    			day = daysOfMonth(month, year) - (min / 1440) + 1;
    			min = min % 1440; 
    		}
    		if (min != 0) {
    			day--;
    			if (day == 0) {
    				month--;
    				if (month == 0) {
    					year--;
    					month = 12;
    				}
    				day = daysOfMonth(month, year);
    			}
    			hour = 24 - min / 60;
    			min = min % 60;
    		}
    		if (min != 0) {
    			hour--;
    			if (hour == -1) {
    				day--;
        			if (day == 0) {
        				month--;
        				if (month == 0) {
        					year--;
        					month = 12;
        				}
        				day = daysOfMonth(month, year);
        			}
        			hour = 23;
    			}
    			minute = 60 - min;
    		}
    	}
    	
    	//build int[]
    	date[0] = day;
    	date[1] = month;
    	date[2] = year;
    	date[3] = hour;
    	date[4] = minute;
    	
    	return date;
    }
    
    /**
     * 
     * @param min
     * @param firstYear
     * @return
     */
    public static String minToDate (int min, int firstYear) {
    	int[] date = minToDateSplitted(min, firstYear);
    	return dateSplittedToDate (date[0], date[1], date[2], date[3], date[4]);
    }
    
    /**
     * 
     * @param day
     * @param month
     * @param year
     * @param hour
     * @param minute
     * @return
     */
    public static String dateSplittedToDate (int day, int month, int year, int hour, int minute) {
    	return "" + (day < 10 ? "0" + day : day) + "." + (month < 10 ? "0" + month : month) + "." + (year < 1000 ? "0" + (year < 100 ? "0" + (year < 10 ? "0" : "") : "") : "") + year + " "
    			+ (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
    }
    
    public static int dateToWeekday (int day, int month, int year) {
    	GregorianCalendar greg = new GregorianCalendar();
    	greg.set(year, month - 1, day);
    	return greg.get(Calendar.DAY_OF_WEEK);
    }
    
    public static String minToWeekday (int min, int firstYear) throws IllegalArgumentException {
    	int[] date = minToDateSplitted(min, firstYear);
    	int day = dateToWeekday(date[0], date[1], date[2]);
    	switch (day) {    	
    	case 1:
    		return "Sunday";
    	case 2:
    		return "Monday";
    	case 3:
    		return "Tuesday";
    	case 4:
    		return "Wednesday";
    	case 5:
    		return "Thursday";
    	case 6:
    		return "Friday";
    	case 7:
    		return "Saturday";
    	default:
    		throw new IllegalArgumentException();
    	}
    }
    
    
    /**
     * checks Date - ignore Parameters with value 0
     * @param day
     * @param month
     * @param year
     * @param hour
     * @param minute
     * @return true if date format is correct
     */
    public static boolean checkDate (int day, int month, int year, int hour, int minute) {
        //check year
        if (year < 0){
            return false;
        }
        
        //check month
        if (month <0 || month > 12) {
            return false;
        }
        
        //check day
        if (day < 0 || day > daysOfMonth(month, year)) {
            return false;
        }
        
        //check hour
        if (hour < 0 || hour > 23) {
            return false;
        }
        
        //check minute
        if (minute < 0 || minute > 59) {
            return false;
        }
        
        //
        return true;
    }
    
    
    /**
     * 
     * @param month
     * @param year
     * @return days of {@code month} of {@code year}
     */
    private static int daysOfMonth (int month, int year) {
        //February
        if (month == 2) {
            if (isLeapYear(year)) {
                return 29;
            }
            else {
                return 28;
            }
        }
        //30 days
        if ((month < 8) && (month % 2 == 0) || (month >= 8) && (month % 2 == 1)) {
            return 30;
        }
        //31 days
        return 31;
    }
    
    /**
     * 
     * @param year1
     * @param year2
     * @return number of lab years between {@code year1} and {@code year2}
     */
    private static int numLabYears (int year1, int year2) {
        return ((int) (year2 / 4) - (int) (year2 / 100) + (int) (year2 / 400)) - ((int) (year1 / 4) - (int) (year1 / 100) + (int) (year1 / 400));
    }
    
    /**
     * 
     * @param year
     * @return true if {@code year} is a lap year
     */
    private static boolean isLeapYear (int year) {
        return (year % 4 == 0) && (year % 100 != 0 || year % 400 == 0);
    }
}
