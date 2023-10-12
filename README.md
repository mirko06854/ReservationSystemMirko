# App for managing Reservations designed for the staff manager.

---
## Description: 

The Reservation System application is designed to facilitate the reservation and management of tables in a dining establishment. It aims to simplify the process of making reservations, ensure efficient utilization of available tables, and manage reservation-related tasks. Moreover is also possible to add a food list for each reservation.

---

## Reservation App User Guide:


### Reservation Time Limit

Each reservation in the app has a fixed time limit of 2 hours.
Reservations cannot overlap within this 2-hour time frame.
Manual deletion of reservations is required to free up time slots.

### Reservation Actions:

1. Select Dishes

Click the "Select Dishes" button to choose dishes for a selected reservation. Please note that this action is only available when a single reservation is selected.

2. Show Paid Dishes

Use the "Show Paid Dishes" button to view the dishes that require payment.
This action is available for each reservation.

### Reservation Management:

1. Delete a Reservation

To delete a reservation, locate the "Delete" button. Click this button to remove the selected reservation. Works for each reservation selected.

2. Clean All Reservations

You can clean all reservations at once by using the "Clean All Reservations" button. This action clears all reservations from the app.

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
* SPECIAL CASE: number of people with disabilities >>  number of people && number of people with disabilities >= 3 implies that such group will take place in one of the first 5 tables. But not when we have 5 people with Special Needs and 0 people. In such case we have an alert forcing us calling such users later on and make such people sitting in 2 tables designed for such people . So the solution in this case is logically to have :

one table designed for people with Special Needs composed by 3 of such people of the last group + another table composed by the other 2 people with disabilities and another person ( another person : that can be either another person with disabilities or a person).



### Examples

1. For a group of 4 people and 1 person with disabilities, they will be seated at table 1 (Case 1).

2. For a group of 1 person and 4 people with disabilities, they will be seated at table 3 (Special Case).

3. For a group of 1 person and 2 people with disabilities, they will be seated at table 7 (Case 2).

By following this seating arrangement logic, we aim to provide an inclusive and comfortable dining experience for all customers.

---


### Explanation of warnings that a waiter may encounter while using the app:




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

#### Problem : When deleting foods for each reservation if the reservation is not selected before deleting ordered food there is a bug

The bug is that one reservation or more keep staying and the food list cannot be seen. So to see the code working properly you must select the reservation and only than select the food, otherwise is the system don't see the selection there is such bug.

FIX: the problem was in the method firma. We need also the reservation display in this way:

showOrderedFoodDialog(reservation,reservationDisplay);

#### Problem : When attaching foods for each reservation in a second moment, all the other ones, even the not payed one were overridden.

FIX: uploaded both the method " openDishesPopUp(Reservation reservation) " in this way:

~~~

...

// Append the new plates and quantities to the existing selectedPlatesMap
String plateName = plate.getName();
int existingQuantity = selectedPlatesMap.getOrDefault(plateName, 0);
selectedPlatesMap.put(plateName, existingQuantity + quantity);
}
}
// Update the reservation with the updated selectedPlatesMap
reservation.setPlatesMap(selectedPlatesMap);
}

...
~~~

as well as modified this in the constructor of Reservation class:
~~~
this.platesMap = new HashMap<>();
~~~

before I had:

~~~
this.platesMap = platesMap;
~~~

which didn't initialise anything.

---

# Programming Techniques in My Application

## 1. Collections
I utilize JavaFX's Observable List to manage and display reservations in a table view, ensuring smooth interaction and data management.

## 2. Custom Exceptions
I have implemented custom exceptions in my code, including `IllegalArgumentException` for input validation, and a custom exception called `ReservationSystemException` for specific reservation-related errors.

## 3. Try-Catch Blocks
To ensure robust error handling, I employ try-catch blocks for handling exceptions in my code, enhancing the application's stability.

## 4. Method Overloading
I leverage method overloading in various event handlers, such as `reserveButton.setOnAction(...)`, and `showInvalidInputAlert(...)`, allowing for flexibility in handling different types of inputs.

## 5. Lambda Expressions
I use lambda expressions for event handling within my code. For instance, in `reserveButton.setOnAction(e -> { ... })`, lambda expressions enhance the readability and conciseness of the code.

## 6. File I/O Operations
My application incorporates file I/O operations to read and write reservation data to and from a JSON file, ensuring persistent data storage.

## 7. Serialization (to JSON)
For JSON serialization, I rely on Jackson's ObjectMapper, enabling me to convert objects to JSON format for storage and transmission purposes.

## 8. Deserialization (from JSON)
I use Jackson's ObjectMapper for JSON deserialization, allowing me to parse JSON data back into objects within my application, ensuring seamless data retrieval.

## 9. Resource Sharing (Between Threads)
I manage resource sharing between threads using the `tableTimers` map, ensuring proper synchronization and preventing conflicts in concurrent operations.

## 10. Test Hooks
In my code, I incorporate test hooks for various testing purposes, including setup and teardown procedures, facilitating efficient and comprehensive testing.

## 11. Graphical User Interface (JavaFX)
My application features a JavaFX graphical user interface, providing an interactive platform for users to interact with the system, enhancing user experience.

## 12. Regular Expressions (Regex)
I employ regular expressions to split and validate time (hours/minutes) input, ensuring accurate and reliable data processing.

---

## Learning outcomes:

Coding this reservation system was an enlightening journey. I have particularly enjoyed and developed serialization and deserialization skills. Additionally, my testing abilities, which I have improved following a testing specific course at university, received a significant boost. This project gave me the opportunity to put in practice what I have learnt in such course. 