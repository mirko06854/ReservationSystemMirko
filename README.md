# App for managing Reservations designed for the staff manager.

This application is composed by three packages:

*   back for the backend
  * front for the front-end
  * merged -> merges front and back end

We are using Java 17.0.1. hence the maven compiler sources and target are 17 and 17:

<b>
<span style="font-family: Arial; color: blue;">&lt;maven.compiler.source&gt; 17 &lt;/maven.compiler.source&gt;</span>
</b>

<br>

<b>
<span style="font-family: Arial; color: blue;">&lt;maven.compiler.target&gt; 17 &lt;/maven.compiler.target&gt;</span>
</b>

## <span style="color: red;">How to run the code</span>:

Steps:

*   clone the repo either by using terminal using: <span style="color: orange;">git clone https://github.com/mirko06854/StaffManagerProjectMirkoIsidoraNew.git </span> or using GitHub Desktop.
*   do "mvn clean install" to clean and install the packages, even though you don't need to clean since the packages shouldn' t be uploaded on git due to our .gitignore file, but perform clean just to be sure.
*  "mvn test" to run the tests
*  for javadoc read below
*  "mvn javafx:run" for running the application




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

[Backend](../StaffManagerProjectMirkoIsidoraNew/target/site/apidocs/back)

[Frontend](../StaffManagerProjectMirkoIsidoraNew/target/site/apidocs/front)

[Merged](../StaffManagerProjectMirkoIsidoraNew/target/site/apidocs/merged)

Test classes:

[Backend_test](../StaffManagerProjectMirkoIsidoraNew/target/site/testapidocs/back_tests)

[Frontend_test](../StaffManagerProjectMirkoIsidoraNew/target/site/testapidocs/front_tests)

[Merged_test](../StaffManagerProjectMirkoIsidoraNew/target/site/testapidocs/merged_tests)

 </div>
</div>

### Some issues I faced:

MainMergedTest had problematic tests:

The tests were problematic due to using JavaFX timers in a non-JavaFX environment. These issues arise from timing discrepancies, lack of thread synchronization, and the absence of a JavaFX thread.
To overcome these problems and ensure reliable testing for JavaFX timers, I used the <b> TestFX library. </b>
This library creates a JavaFX environment during tests, enabling proper synchronization and accurate timer behavior
verification. By using TestFX's sleep method, tests can simulate timer duration and improve testing accuracy.

Now the test of MainMerged works in this way. The app is opened and the mouse moves automatically to book some tables. So one example table is booked and tested if after one minute is available or not. So you have to wait one minute for the test to be run. 

