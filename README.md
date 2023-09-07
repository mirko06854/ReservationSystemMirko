# App for managing Reservations designed for the staff manager.

---
## Description: 

The Reservation System application is designed to facilitate the reservation and management of tables in a dining establishment. It aims to simplify the process of making reservations, ensure efficient utilization of available tables, and manage reservation-related tasks.

---
## Structure:

This application is composed by three packages:

*   back for the backend
  * front for the front-end
  * merged -> merges front and back end

We are using Java 17.0.1. hence the maven compiler sources and target, in the .pom file, are 17 and 17:

<b>
<span style="font-family: Arial; color: blue;">&lt;maven.compiler.source&gt; 17 &lt;/maven.compiler.source&gt;</span>
</b>

<br>

<b>
<span style="font-family: Arial; color: blue;">&lt;maven.compiler.target&gt; 17 &lt;/maven.compiler.target&gt;</span>
</b>

---
## <span style="color: red;">How to run the code</span>:

Steps:

*   clone the repo either by using terminal using: <span style="color: orange;">git clone https://github.com/mirko06854/StaffManagerProjectMirkoIsidoraNew.git </span> or using GitHub Desktop.
*   do "mvn clean install" to clean and install the packages, even though you don't need to clean since the packages shouldn' t be uploaded on git due to our .gitignore file, but perform clean just to be sure.
*  "mvn test" to run the tests
*  for javadoc read below
*  "mvn javafx:run" for running the application
* to close the app, apart pressing the button for closing it, also press "ctrl + C" since the process continue running also after having closed the app.

--- 


## Commands used for javadoc
#### (obviously running them after having done mvn clean install ( or just mvn install) :

<span style="font-family: Arial; color: green; font-size: larger"> Normal Classes: </span>

<span style="font-family: Arial; color: yellowgreen;"> mvn javadoc:javadoc: </span>

<br>
<br>

<span style="color: green; font-size: larger"> Test: </span>

<span style="color: yellowgreen;" > mvn javadoc:test-javadoc </span>

<br> <br>
<span style="font-family: Arial; color: red;"> run such command in the root folder of the project or you will get errors.</span>


Note : after executing such commands the javadoc documentation is findable online to the following path :

<div style="display: flex; justify-content: center;">
    <div style="text-align: center;">

Normal classes:

[Backend](../StaffManagerProjectMirko/target/site/apidocs/back)

[Frontend](../StaffManagerProjectMirko/target/site/apidocs/front)

[Merged](../StaffManagerProjectMirko/target/site/apidocs/merged)

Test classes:

[Backend_test](../StaffManagerProjectMirko/target/site/testapidocs/back_tests)

[Frontend_test](../StaffManagerProjectMirko/target/site/testapidocs/front_tests)

[Merged_test](../StaffManagerProjectMirko/target/site/testapidocs/merged_tests)

 </div>
</div>

---

### Reasonment for splitting the groups in two category:

I have assumed to be a Restaurant waiter. My restaurant is very small. In my restaurant there are in total 10 tables . The first five tables are designed for people , whereas the last five tables are designed for people with special needs. On the one hand, the first 5 tables have 5 sitting places for each table. On the other hand, the last 5 tables have 3 sitting place for table. We all wish to make all people sit . So I thought this way :

* CASE 1 : number of people >> number of people with disabilities implies that such group will take place in one of the first 5 tables.
* CASE 2: number of people with disabilities >> number of people implies that such group will take place in one of the last 5 tables
* SPECIAL CASE: number of people with disabilities >>  number of people && number of people with disabilities >= 3 implies that such group will take place in one of the first 5 tables


### Examples

1. For a group of 4 people and 1 person with disabilities, they will be seated at table 1 (Case 1).

2. For a group of 1 person and 4 people with disabilities, they will be seated at table 3 (Special Case).

3. For a group of 1 person and 2 people with disabilities, they will be seated at table 7 (Case 2).

By following this seating arrangement logic, we aim to provide an inclusive and comfortable dining experience for all customers.

---


### Explaination of warnings that a waiter may encounter while using the app:




1.  Overlapping Reservation Warning (showOverlapAlert):
This warning is triggered when a new reservation overlaps with an existing one. Overlapping reservations can lead to seating conflicts and customer dissatisfaction. The app considers by default leaving time of each booking as: arrivalTime + 2 hours. <br>

2.  Invalid Input Warning (showInvalidNumberAlert, showInvalidInputAlert):
These warnings are shown when the waiter enters invalid input values, such as non-numeric characters or negative values. Handling invalid inputs prevents unexpected behavior in the application and guides the waiter to enter correct and meaningful data. <br>

3.  Table Already Reserved Warning (showReservedAlert):
This warning informs the waiter that the selected table is already reserved during the chosen time slot. It helps prevent double bookings and ensures that each table is only reserved once at a given time. <br>

4.  Reservation Category and Capacity Mismatch Warning (validateTableCategory):
This warning addresses the scenario where the calculated category or capacity of the table doesn't match the selected values. Ensuring that the table's attributes align with the reservation group's characteristics avoids assigning customers to inappropriate tables. (ensuring that max 5 or 3 people can sit to the respective tables, and in case of minor capacity that the default one, the capacity must be = total people). <br>

5.  Unsuccessful Table Selection Warning (validateTableNumber):
This warning is displayed if the waiter selects a table number that is not available for reservations. It guides the waiter to choose a table with a valid number within the specified range. <br>

6.  Serialization Error Handling (serializeJsonFile, cleanJson):
These methods handle the serialization of reservations into a JSON file. Serialization errors could lead to data loss or corruption. By handling these errors and saving data consistently, you ensure that reservations are properly stored and retrieved. <br>

7. Table Unlocking (unlockTransition):
This mechanism ensures that tables are automatically unlocked after a reservation's departure time. By using a PauseTransition, the system make sure that tables become available for new reservations after the specified time has passed. <br>

---

## Challenges: 

In the implementation of the reservation system, I encountered challenges related to synchronization and delayed unlocking of reserved tables. To address these issues, I introduced a centralized time management system that ensures timely unlocking of tables based on reservation times. 

#### Problem: Synchronization and Delayed Unlocking

The initial approach relied on individual timers for each reservation, leading to synchronization problems and delayed unlocking of tables after reservations ended.

#### Solution: Centralized Time Management System

To overcome these challenges, I implemented a centralized time management system that consistently checks for unlock events at regular intervals and updates table availability based on reservation times.

#### Problem : Unintended Interference of Food Orders Between Reservations in a Restaurant Reservation System:

I faced an issue while developing the restaurant reservation system where food orders for one reservation were unintentionally affecting other reservations. The problem was initially attributed to data sharing among reservations.
There was a shared reference to the same name variable for all iterations of the loop, causing unexpected behavior in the assignment of food items to reservations. I successfully attempted a solution by using a mapping (dictionary) to store food orders separately for each reservation to store food orders independently.