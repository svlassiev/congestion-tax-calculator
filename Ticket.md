# Congestion Tax Fees service
This file contains some assumptions regarding the assignment and provides a description to the HTTP Service that calculates congestion tax fees.



## API
Congestion Tax Fees service provides simple internal API
For the simplicity we don't use any database here and _assuming_ that for each vehicle its type and relevant dates are stored externally and passed to this service only to calculate the value.
```
POST /{vehicleType}/dates <dates> 
```

or

```
POST /task {vehicleType:{type},dates:[{date}]}
GET /task/{id}
```

*TODO* 
* Make service to handle its own data 