# marketsurveyapi

## Getting Started
A RESTful API made by Carlos Muñoz Pelaz for [Caravelo](https://www.caravelo.com/).

This project has no useful logic, is just an example of API.

MarketsurveyAPI has 3 functionalities:

    1. Introducing market surveys.
    
    2. Retrieving market surveys according to a market survey request.
    
    3. Submitting subscriptions.
    
    4. Sending notifications to subscribiers.
    
### Prerequisites

1. An Apache Tomcat server.

2. Internet connection to access the database.
###### Hint:  You can modify database credentials in mongo_config.json (inside the .war).

### Installation

1. Download [this](https://github.com/LordCarlosMP/marketsurveyapi) GitHub repository.

2. Copy marketsurveyapi.war to tomcat "appBase" directory, usually $CATALINA_HOME/webapps.

3. Start Apache Tomcat server.

4. Install [Postman](https://www.getpostman.com/) (or similar) for your platform [here](https://www.getpostman.com/apps).

## Testing via HTTP

###### Warn: If you are running the server with IntelliJ run/debug configurations, or, for any other reason, in the ROOT directory, remove the middle /marketsurvey from the url.

#### 1 Introducing market surveys, through the PUT method in the URL:
```
http://tomcat-server-ip:port/marketsurveyapi/marketsurveys
```
The PUT body may be a JSON in this format:

```
{
  "subject": 1,
  "date": "2018-04-17",
  "country": "ES",
  "provider": "Great market surveys Inc",
  "target": {
    "genders": [
      "MALE",
      "FEMALE"
    ],
    "age": {
      "start": 18,
      "endInclusive": 90
    },
    "income": {
      "start": 15000,
      "endInclusive": 30000
    }
  }
}
```

#### 2 Retrieving market surveys according to a market survey request, through the POST method in the URL: 
 ```
 http://tomcat-server-ip:port/marketsurveyapi/marketsurveys
 ```
 
 Note that marketsurveyapi will consider as "matching" if it fit all these requirements:
 
 1. The request have the same subject id.
 
 2. The date is the same or newer.
  
 3. The age and income ranges are inside the "market survey request" ones.
 
 4. The market survey only contains the genders allowed in the request.
 
###### NOTE: the database is already provided with many market surveys, most of them with id = 1.

```
{
  "subject": 1,
  "date": "2018-04-17",
  "countries": [
    "ES",
    "ARG",
    "CHL"
  ],
  "target": {
    "genders": [
      "MALE",
      "FEMALE"
    ],
    "age": {
      "start": 17,
      "endInclusive": 80
    },
    "income": {
      "start": 10000,
      "endInclusive": 35000
    }
  }
}
```


In a market survey request, is not necessary to "fill" all the JSON, if a nullable field
 (all except for the subject are nullables) is null, means "no filter" so this is possible.


```
{
  "subject": 1,
  "date": "2018-04-17",
  "countries": [
    "ES",
    "ARG",
    "CHL"
  ],
  "target": {
    "genders": [
      "MALE",
      "FEMALE"
    ],
    "age": {
      "start": 0,
      "endInclusive": 80
    }
  }
}
```


Or this.

```
{
  "subject": 1,
  "countries": [
    "ES",
    "ARG",
    "CHL"
  ],
  "target": {
    "genders": [
      "MALE",
      "FEMALE"
    ]
  }
}
```


Or even this.

```
{
  "subject": 1
}
```

#### 3 Submitting subscriptions, through the PUT method in the URL:
```
http://tomcat-server-ip:port/marketsurveyapi/subscriptions
```

The market survey request is the request to use as reference for notifying the subscriber,
and the rest is data for the information delivery.

The PUT body may be a JSON in this format:

```
{
  "marketSurveyRequest": {
    "subject": 1,
    "date": "2018-04-17",
    "countries": [
      "ES",
      "ARG",
      "CHL"
    ],
    "target": {
      "genders": [
        "MALE",
        "FEMALE"
      ],
      "age": {
        "start": 0,
        "endInclusive": 100
      },
      "income": {
        "start": 0,
        "endInclusive": 10
      }
    }
  },
  "subscriptionType": [
    "MAIL",
    "FTP",
    "POSTAL",
    "PHONE"
  ],
  "frequency": "DAILY",
  "sendData": {
    "mail": "carlosychispas@gmail.com",
    "phone": "+34 688 92 15 81",
    "postalDirection": " 39781 C/ example address 46 5B",
    "ftp": "ftp://user:pass@somehost/afolder"
  }
}
```
#### For retrieving all the subscribers, run a GET request in the URL:
```
http://tomcat-server-ip:port/marketsurveyapi/subscriptions
```
Remember that every day/week/month/year the api send all the subscribers a notification.

#### For testing this methods just use postman, enter the URL, select the HTTP method, enter a JSON body and click send.
 

## Running the inside the IDE

To review the project inside your IDE (preferably with IntelliJ,
to avoid creating new files like .prj .Iml .idea .classpath), import
it as a [Gradle](https://gradle.org/) project.

## Compiling

Use [Gradle](https://gradle.org/) war task.

## Built With

* [Kotlin](https://kotlinlang.org/)   - The JVM language
* [Gradle](https://gradle.org/)       - Dependency Management
* [Jersey](https://jersey.github.io/) - The web framework used
* [MongoDB](https://www.mongodb.com/) - The database
