### Tech assessment

**Build tool used is gradle.**

Build:
``./gradlew clean build``

Run the tests:
``./gradlew test``

Start the service: 
``./gradlew bootRun``

The endpoint should be ready to be called on http://localhost:8080/file-processor

In the **request** it accepts two request params
- `file` - this is mandatory which is used to hold the input file
- `skipValidation` - this is optional however it's ignored by the service (description was not clear what validation it should skip)

It **returns** the outcome json 

To upload a file curl can be used ``curl -X POST -F "file=@input.txt" http://localhost:8080/file-processor``

For logging in memory **H2** databased is used. 
To access the logs go to http://localhost:8080/h2-console

Username: `sa` Password: `pwd`

