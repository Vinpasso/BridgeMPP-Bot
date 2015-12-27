---CHANGELOG---

v. 1.0.6:
---------
- Bugfixes: Alex Bug

Alex Bug: command eventls shows "repeat" and "remind" of events as number of days/ minutes

v. 1.0.7:
---------
- Bugfixes: Next Bug

Next Bug: command "eventls next" shows the oldest repeating event instead of the next repeating event

v. 1.0.8:
---------
- Bugifxes: S-Bug

S-Bug: names of person in Birthday calendar which end with s or x are listed wrongly

v. 1.1.0:
--------
- New:
	- command "version" (now visible)
	- age-related birthday wishes (ages 18, 20, others)
	- new birthday wishes for ages 18 and 20

- Refactoring

- Multiple Bugfixes

Bugs:
	- command "eventls [calendar] next" shows wrong date
	- Error "unknown command" shows wrong command

v. 1.1.1:
--------
- Changes:
	- Command help: added new help message: "Type "?cal %command% help" for more information"

- Multiple Bugfixes

Bugs:
	- Crashes at initializing
	- Crashes at command without parameters
	- Crashes if command is help command or contains help

v. 1.1.2:
--------
Bugfixes: 
	- Udo's Travel Catastrophe (UTC) Bug: wrong time unit (UTC+0)
	
v. 2.0.0:
--------
(downward compatible to version 1.0.0 and later)
- New:
	- TumTum integration
	- command caledit: Edits the settings of an existing calendar
	- command alert: Shows whether the alerts are on
	
- Changes:
	- Birthday cakes have been removed
	- Command calcr: new parameter tumtum
	- Setting Alerts: now only for TumTum-Chat
	
- Bugfixes:
	- RR Bug: if remind/ repeat is 0 command eventls shows repeat/ remind wrongly
	- Alex Cal Bug: command calls shows "repeat" and "remind" wrongly
	- BMPP Schedule Bug: events are reminded several times at the same time
	
v. 2.0.1:
--------
Bugfixes:
	- status message of command caledit is shown wrongly
	
