
# ParkTool
### Manage the city parking spaces

## Overview
Park tool delivers a web interface that meets following requirements:
1. As a driver, I want to start the parking meter, so I don’t have to pay the fine for the invalid
parking.
2. As a parking operator, I want to check if the vehicle has started the parking meter.
3. As a driver, I want to stop the parking meter, so that I pay only for the actual parking time
4. As a driver, I want to know how much I have to pay for parking.
5. As a parking owner, I want to know how much money was earned during a given day.
6. The parking rates are:
  
| Driver type | 1st hour | 2nd hour | 3rd and each next hour      |   
|-------------|----------|----------|-----------------------------|
| Regular     | 1 PLN    | 2 PLN    | 2 x more than hour before   |
| VIP         | free     | 2 PLN    | 1.5 x more than hour before | 

## Architecture
![parktool-architecture](https://www.bikepics.com/pics/2018/03/12/bikepics-2806601-full.jpg)


ParkTool is build with the following design patterns:  
___
### Mircoservice
ParkTool consists of 4 applications:  

**1. ParkTool**  
An Akka HTTP application serving rest endpoint for starting and stoping parking meters. After receiving request, the event is published to Kafka broker.  
 
**2. ParkRegistry**  
An Akka application with registered listener of start and stop events. On event receive, the information is persisted in Postgres database. Only registry app performs writes to database. 

**3. ParkCalculator**  
An Akka HTTP application responsible for calculating pricing rates. It serves rest endpoints for getting parking fee, chcecking if vehicle is started and and endpoint with report of daily income. 

**4. ParkProxy**  
A simple multi host reverse proxy written in Go. It's purpose is to "hide" ParkTool and ParkCalculator APIs under single host.
___
### Reactive programming
The ParkTool API is non blocking. Http requests are forwarded to Akka Actors. Communication between components and microservices is indirect. There are no remote calls, only a bus of data stream with delegated observers.
___
### [Railway](https://fsharpforfunandprofit.com/rop/) oriented programming
Using Kleisli composition for combining functions returning monadic value (Either) results in a neat error handling. A good example is `FeeCalculator` service. No nested ifs and elses needed. 

___
### Functional paradigm
- There are only values, no `var`s. Even the actors state is held by recursion.  
- Side effects are separated and moved to the boundries. E.g. `GetFee` is a pure function, besides the fact it calculates the data taken from "outside". The input data is handled by separate services. 

- Function composition is used in SQL queries (Slick), error handling and dependency injection.

 
## Installation

Run app in container:
```
$ docker-compose up -d
```

## Usage
```
$ curl -H "ContentType: application/json" -X POST http://localhost:3000/parking-meters/start -d '{"parkingMeterId":1, "vehicleId": "WWANA01}'
=>{"message":"Started PM#1 for WWANA01 at 2018-03-12T06:39:03.214Z."}

$ curl -H "ContentType: application/json" -X POST http://localhost:3000/parking-meters/stop -d '{"parkingMeterId":1, "vehicleId": "WWANA01}'
=>{"message":"Stopped PM#1 for WWANA01 at 2018-03-12T06:46:03.214Z."}

$ curl http://localhost:3000/parking-fees/WWANA01?currency=USD
=>{"vehicleId":"WWANA01","duration":6,"fee":1.0,"currency":"USD"}
```
## TODOs
0. Authorization
1. Access roles (admin, operator, driver)
2. Dependency Injection for persistance service.
3. Tests for registry app.
4. DDL query migrations with versioning.
5. Endpoints for daily income and started vehicle check.
6. Data serialization with proto buffers.

## History
Version 0.1

## Docker images
- `nadberezny/parktool`
- `nadberezny/parkregistry`
- `nadberezny/parkcalculator`
- `nadberezny/parkproxy`
 
## License
The MIT License (MIT)

Copyright (c) 2018 Juliusz Nadbereżny

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.