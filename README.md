# Facility Management System via Facebook

### Introduction
This project implements a Facility Management System using facebook and deals with various use cases with the help of Facebook API.

### Instructions
- To run this project we need to setup MySql database on our local system using the db_query.txt file.
- Also we need a facebook page to handle queries.

### Commands
- ADDS <phone> <Roll No.>	:	To register a user on the system
- ADDE <phone> <type>	:	To register an employee on the system
	- Types available:
		- ELECTRICIAN
		- SWEEPER
		- GARDENER
		- CARPENTER
		- MASON
- REQ <location> <type of service> <additional info>	:	To make a request to the FMS system
- DONE		:	To inform the system via comment that the service has been taken care of. When asked for the feedback, only an integer value is expected between 0 - 5

### Advantages:
- This system can be used to cater to user needs without making them to go through special efforts to download a special app or sign up
- This system allows for cheap implementation of the service as there is no need to spend resources to maintain the front end.
- It can be used at scenarios like this to handle service requests like, power cut at certain facility, or unavailability of internet connection.

### Parameters
- ACCESS_TOKEN : Stored in a file AccessToken.txt
- PAGE_ID: Facebook page unique id
- TIME_MARGIN: Time window for a successful request completion (in mins)
- REPORT_THRESHOLD: The threshold value for difference between total tasks assigned and tasks completed within time with good review
- REFRESH_RATE: The frequency at which system check facebook page (in milliseconds)

### Output instructions
- Program notifies via a comment on the post whenever a user gets registered
- Notifications are sent for every request that gets assigned an employee
- After completion of a task, system asks for a feedback in the same post.
- Defaulter Employees are reported in the console at every iteration.
- Any unauthorized access or fail cases are reported by the program in the console.

### Software Requirements
- JAVA 1.8 - primary programming language
- MySQL - database
- Facebook Graph API - for facebook integration
- RestFB - Java implementation of the Facebook API

### Author
* [**Chirag Khurana**](http://chiragkhurana.com) - [Github](https://github.com/ckhurana)
