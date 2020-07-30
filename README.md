# CityConnect
**CityConnect** app determines whether the given 2 cities are connected.

**API**
Single REST GET API provided to get the connection between the cities. 

### Input
City connections is read from a local resource file which is configured in the application.properties 

### Framework
Spring REST used to provide the API service.

### Setup

Prerequisite: OpenJDK Java 11 , Git, Maven

To install and launch the app, open a terminal and do the following:

1. cd to the dir in which you want to install the project (i.e., the project's parent dir).
2. clone the github project via  
   `git clone https://github.com/vamsikuchi/mastercard.git`
3. cd to the project home dir  'cd mastercard'
4. Launch the app via  
   `./mvnw spring-boot:run`  
   This will run the application. While Starting the application, city connections data is loaded.
Logs get written to standard output. Overrides to default log levels are set in `src/main/resources/application.properties`

Unit tests may be run from the command line via `mvn test`

### Usage

Send HTTP GET requests to `localhost` port `8080`, naming origin and destination cities, thus:

> http://localhost:8080/connected?origin=city1&destination=city2

If `city1` is connected to `city2` by any path along known roads, the response will be:

> yes

If not, or if the request is not formatted properly, the response will be:

> no

Other HTTP Methods are restricted and response code 405 will be thrown.

### Design

List of cities are maintained in a list in lowercase.
Connected cities are appended and stored as List<Set<Strong>>.
Each item(Set<String>) in the list contains Set of city names which are connected.

Pros:
+ No Duplication of data, connected cities are maintained in one Set.
+ Low memory usage.
+ As List of cities are maintained, requested cities are verified is the exsisting cities list to avoid unnecessary search on the connections.
+ Unmodifiable List used , so thread safe.

Cons:
- If all disconnected cities are loaded, still search happens in the list. Though it's not a real scenario, flag can be used to fix this.


### Improvements:
1. How cities are connected is not maintained. 
   If city conncetions are changed at run time, System will not provide accurate information, because network of each city is not maintained.
2. Weak Memory map can be used to cache recent searches.
3. Response text can be changed to more understandable. 

### Implementation Details
CityConnect app developed on Window OS , using IntelliJ Idea 2020.2 , Git 2.25, Maven3.6.3, OpenJDK Java 11.
Base code generated via [Spring Initializr](https://start.spring.io/) on 25th Jul 2020.

   



