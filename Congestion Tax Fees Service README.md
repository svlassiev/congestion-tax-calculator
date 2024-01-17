# Congestion Tax Fees Service
This file provides a description to the HTTP Service that calculates congestion tax fees.

To start HTTP server at http://127.0.0.1:8080 go to java root dir and run application with gradle.
```
cd congestion-tax-calculator\java
.\gradlew.bat run
```
or for Unix systems (haven't checked it on my machine which is Windows)
```
cd congestion-tax-calculator/java
./gradlew run
```

## API
Congestion Tax Fees service provides simple internal API
For the simplicity we don't use any database here and _assuming_ that for each vehicle its type and relevant dates are stored externally and passed to this service only to calculate the value.

1. Create task that calculates congestion tax for particular type of vehicle, which passed tolls at the particular dates
```
POST /task {vehicleType:{type},tollPasses:[<unix time in UTC>]}

Response:
{
  "taskId": "1f8eda27-7d66-426d-add4-21fd0ec0801a",
  "vehicleType": "car",
  "tollPasses": [
    1376487961000,
    1376473321000,
    1376496253000
  ]
}
```

2. Get result for the congestion tax calculation for the task created in `POST` request. `{taskId}` is taken from `POST` response.
```
GET /task/{taskId}

Response:
{
  "taskId": "1f8eda27-7d66-426d-add4-21fd0ec0801a",
  "taxInSEK": 34
}
```
