#CHANGELOG


unreleased:
--------
##### ADDED:
- Calendar Official Holiday


v. 2.1.4 - 2016-01-13:
--------
##### FIXED:
- First use of calendarbot could not save calendars
- First use of a new created calendar could not save any events

v. 2.1.3 - 2016-01-12:
--------
##### ADDED:
- ErrorMessages:
	<br> - "Error: Could not save Calendars ..."
	<br> - "Error: No commands allowed! Because of the failure of loading calendars, you are not allowed to run any commands..."
- StatusMessages for commands "alertson" and "alertsoff": "Alerts: on/off"

##### FIXED:
- duplicate events (in properties file) were loaded twice

##### SECURITY:
- Events of a calendar which could not been loaded, were possibly deleted while saving current events of that calendar
- Calendars which could not been loaded, were possibly removed while saving all calendars


v. 2.1.2 - 2016-01-12:
--------
##### ADDED:
- ErrorMessage: "Error: Could not load events of calendar ..."

##### FIXED:
- Internet calendars could not be removed


v. 2.1.1 - 2016-01-06:
--------
##### CHANGED:
- new style for changelog.md 

##### FIXED:
- version number of command version was shown wrongly
- status message of command caledit was shown wrongly
- names of person in Birthday calendar which end with "ß", "z" or "ce" were listed wrongly


v. 2.1.0 - 2016-01-06:
--------
##### ADDED:
- CalendarBot can now subscribe to Internet Calendars. The primitive implementation supports the following features:
- Importing an Internet Calendar by specifying the ICS URL as the Calendar Name
- Can read VEVENT DTSTART Tags (Sets to Event's Date and Time)
- Can read VEVENT SUMMARY Tags (Sets to Event's Name)
- Uses the Calendar's default Repeat and Remind for all Events


v. 2.0.1 - 2015-12-28:
--------
##### FIXED:
- status message of command caledit were shown wrongly


v. 2.0.0 - 2015-12-28:
--------
(downward compatible to version 1.0.0 and later)

##### ADDED:
- TumTum integration
- command caledit: Edits the settings of an existing calendar
- command alert: Shows whether the alerts are on
	
##### CHANGED:
- Command calcr: new parameter tumtum
- Setting Alerts: now only for TumTum-Chat

##### REMOVED:
- Birthday cakes have been removed
	
#####FIXED:
- RR Bug: if remind/ repeat is 0 command eventls shows repeat/ remind wrongly
- Alex Cal Bug: command calls shows "repeat" and "remind" wrongly
- BMPP Schedule Bug: events are reminded several times at the same time


v. 1.1.2 - 2015-12-27:
--------
##### FIXED: 
- Udo's Travel Catastrophe (UTC) Bug: wrong time unit (UTC+0)


v. 1.1.1 - 2015-12-27:
--------
##### CHANGED:
- Command help: added new help message: "Type "?cal %command% help" for more information"

##### FIXED:
- Crashed at initializing
- Crashed at command without parameters
- Crashed if command is help command or contains help


v. 1.1.0 - 2015-12-26:
--------
##### ADDED:
- command "version" (now visible)
- age-related birthday wishes (ages 18, 20, others)
- new birthday wishes for ages 18 and 20

##### CHANGED:
- Refactoring

##### FIXED:
- command "eventls [calendar] next" showed wrong date
- Error "unknown command" showed wrong command


v. 1.0.8 - 2015-10-23:
---------
##### FIXED: 
- S-Bug: names of person in Birthday calendar which end with s or x are listed wrongly


v. 1.0.7 - 2015-07-08:
---------
##### FIXED:
- Next Bug: command "eventls next" shows the oldest repeating event instead of the next repeating event


v. 1.0.6 - 2015-07-02:
---------
##### FIXED: 
- Alex Bug: command eventls shows "repeat" and "remind" of events as number of days/ minutes


v. 1.0.5 - 2015-06-30:
---------
##### ADDED:
- command "auxilium" for help


v. 1.0.4 - 2015-06-30:
---------
##### FIXED:
- birthday cakes were not shown


v. 1.0.3 - 2015-06-28:
---------
##### CHANGED:
- birthdays will now be reminded at 12 pm

v. 1.0.2 - 2015-06-28:
---------
##### FIXED:
- birthdays were repeated each 365 days (leap years have 366 days) instead of each year

v. 1.0.1 - 2015-06-26:
---------
##### ADDED:
- birthday cakes (are shown at birthdays)



