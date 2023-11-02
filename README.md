# 🐢 Turtle Metrics

Turtle Metrics is a console system that processes and analyses real data of bike rentals. Most of the implementation is not tied to the reality of the examples of use, instead the statistics will be obtained from the data that the application obtains in execution from the input file.

The user can obtain the amount of trips between two stations, the stations in which the longest trips started, the longest trip for every station and the positive, negative and neutral afflux for every station. The queries are run by four different executables.

All query applications and the server are written in Java. Both of them use Hazelcast under the hood.

<hr>

* [1. Prerequisites](#1-prerequisites)
* [2. Compiling](#2-compiling)
* [3. Executing Turtle Metrics](#3-executing-turtle-metrics)
  * [3.1. Server](#31-server)
  * [3.2. Query 1. Trips between stations](#32-query-1-trips-between-stations)
  * [3.3. Query 2. Top N stations with the largest average approximate distance](#33-query-2-top-n-stations-with-the-largest-average-approximate-distance)
  * [3.4. Query 3. Longest trip by station](#34-query-3-longest-trip-by-station)
  * [3.5. Query 4. Positive, neutral and negative afflux days by station](#35-query-4-positive-neutral-and-negative-afflux-days-by-station)
* [4. Final Remarks](#4-final-remarks)

<hr>

## 1. Prerequisites

The following prerequisites are needed to run the server executable as well as the client applications:

* Maven
* Java 19

## 2. Compiling

To compile the project and get all executables, `cd` into the root of the project, and run the following command:

```Bash
mvn clean package
```

This will create two `.tar.gz` files, that contain all of the files necessary to run the clients and the server respectively.  Their location is:
* **Client**: `./client/target/tpe2-g2-client-1.0-SNAPSHOT-bin.tar.gz`
* **Server**: `./server/target/tpe2-g2-server-1.0-SNAPSHOT-bin.tar.gz`

## 3. Executing Turtle Metrics

Unpack both the server and the client using:

```Bash
tar -xf <file.tar.gz>
```

> ⚠️ From now on, it is assumed that the server files are located inside the `./server` directory and the client files are located inside the `./client` directory.

Then, give all executables the needed permissions to be executed:

```Bash
chmod u+x ./client/query* ./server/server
```

### 3.1. Server

> 🚨 The current working directory **must** be `./server`.

Must be running for the query executables to work. Can be run N times, in different hosts on the same network, to create a cluster of Hazelcast instances.

```
./server [ -DsubnetMask="<mask>" ]
```

* The argument must be a valid subnet mask. It limits the IP addresses that this instance of the server will use to connect to other Hazelcast clusters.

### 3.2. Query 1. Trips between stations

> 🚨 The current working directory **must** be `./client`.

This executable will obtain the amount of trips for each pair of starting station and ending station. 

To run the query, use the following:

```Bash
./query1 -Daddresses="<addresses>" -DinPath="<path>" -DoutPath="<path>"
```

* `-Daddresses` refers to the IP addresses of the Hazelcast nodes with their ports (one or more, separated by semicolons). 
* `-DinPath` indicates the path where the input files `bikes.csv` and `stations.csv` are located.
* `-DoutPath` indicates the path where both output files `query1.csv` and `time1.txt` will be.

The file `bikes.csv` must be a comma separated values file, using `;` as the delimiter, containing the following fields:

* `start_date`: Date and time of bike rental (start of trip) in `yyyy-MM-dd HH:mm:ss` format.
* `emplacement_pk_start`: Start station identifier (integer).
* `end_date`: Date and time of bike return (end of trip) in `yyyy-MM-dd HH:mm:ss` format.
* `emplacement_pk_end`: End station identifier (integer).
* `is_member`: If the rental user is a member of the rental system (0 if not a member, 1 if a member).

The file `stations.csv` must be a comma separated values file, using `;` as the delimiter, containing the following fields:

* `pk`: Station identifier (integer).
* `name`: Station name (string).
* `latitude`: Latitude of station location (double).
* `longitude`: Longitude of station location (double).

The results will be written in a comma separated values file, using `;` as the delimiter, named `query1.csv`. The file will have the following format:

```Text
station_a;station_b;trips_between_a_b
...
```

A file with the timestamps of the start and the end of some of the tasks of the executable will be created, with the name `time1.txt`.

### 3.3. Query 2. Top N stations with the largest average approximate distance

> 🚨 The current working directory **must** be `./client`.

This executable will obtain the top N stations with the largest average of distance of trips started in that station. The distance is calculated using the Haversine formula.

To run the query, use the following:

```Bash
./query2 -Daddresses="<addresses>" -DinPath="<path>" -DoutPath="<path>" -Dn="<number>"
```

* `-Daddresses` refers to the IP addresses of the Hazelcast nodes with their ports (one or more, separated by semicolons). 
* `-DinPath` indicates the path where the input files `bikes.csv` and `stations.csv` are located.
* `-DoutPath` indicates the path where both output files `query2.csv` and `time2.txt` will be.
* `-Dn` indicates the quantity of results.

The file `bikes.csv` must be a comma separated values file, using `;` as the delimiter, containing the following fields:

* `start_date`: Date and time of bike rental (start of trip) in `yyyy-MM-dd HH:mm:ss` format.
* `emplacement_pk_start`: Start station identifier (integer).
* `end_date`: Date and time of bike return (end of trip) in `yyyy-MM-dd HH:mm:ss` format.
* `emplacement_pk_end`: End station identifier (integer).
* `is_member`: If the rental user is a member of the rental system (0 if not a member, 1 if a member).

The file `stations.csv` must be a comma separated values file, using `;` as the delimiter, containing the following fields:

* `pk`: Station identifier (integer).
* `name`: Station name (string).
* `latitude`: Latitude of station location (double).
* `longitude`: Longitude of station location (double).

The results will be written in a comma separated values file, using `;` as the delimiter, named `query2.csv`. The file will have the following format:

```Text
station;avg_distance
...
```

A file with the timestamps of the start and the end of some of the tasks of the executable will be created, with the name `time2.txt`.

### 3.4. Query 3. Longest trip by station

> 🚨 The current working directory **must** be `./client`.

This executable will obtain the longest trip for each station. The duration will be expressed in minutes.

To run the query, use the following:

```Bash
./query3 -Daddresses="<addresses>" -DinPath="<path>" -DoutPath="<path>"
```

* `-Daddresses` refers to the IP addresses of the Hazelcast nodes with their ports (one or more, separated by semicolons). 
* `-DinPath` indicates the path where the input files `bikes.csv` and `stations.csv` are located.
* `-DoutPath` indicates the path where both output files `query3.csv` and `time3.txt` will be.

The file `bikes.csv` must be a comma separated values file, using `;` as the delimiter, containing the following fields:

* `start_date`: Date and time of bike rental (start of trip) in `yyyy-MM-dd HH:mm:ss` format.
* `emplacement_pk_start`: Start station identifier (integer).
* `end_date`: Date and time of bike return (end of trip) in `yyyy-MM-dd HH:mm:ss` format.
* `emplacement_pk_end`: End station identifier (integer).
* `is_member`: If the rental user is a member of the rental system (0 if not a member, 1 if a member).

The file `stations.csv` must be a comma separated values file, using `;` as the delimiter, containing the following fields:

* `pk`: Station identifier (integer).
* `name`: Station name (string).
* `latitude`: Latitude of station location (double).
* `longitude`: Longitude of station location (double).

The results will be written in a comma separated values file, using `;` as the delimiter, named `query3.csv`. The file will have the following format:

```Text
start_station;end_station;start_date;minutes
...
```

A file with the timestamps of the start and the end of some of the tasks of the executable will be created, with the name `time3.txt`.

### 3.5. Query 4. Positive, neutral and negative afflux days by station

> 🚨 The current working directory **must** be `./client`.

This executable will obtain the positive, neutral and negative afflux days for each station, given a date interval.

To run the query, use the following:

```Bash
./query4 -Daddresses="<addresses>" -DinPath="<path>" -DoutPath="<path>" -DstartDate="<date>" -DendDate="<date>"
```

* `-Daddresses` refers to the IP addresses of the Hazelcast nodes with their ports (one or more, separated by semicolons). 
* `-DinPath` indicates the path where the input files `bikes.csv` and `stations.csv` are located.
* `-DoutPath` indicates the path where both output files `query4.csv` and `time4.txt` will be.
* `-DstartDate` indicates the start date of the interval where to calculate the afflux.
* `-DendDate` indicates the end date of the interval where to calculate the afflux.

The file `bikes.csv` must be a comma separated values file, using `;` as the delimiter, containing the following fields:

* `start_date`: Date and time of bike rental (start of trip) in `yyyy-MM-dd HH:mm:ss` format.
* `emplacement_pk_start`: Start station identifier (integer).
* `end_date`: Date and time of bike return (end of trip) in `yyyy-MM-dd HH:mm:ss` format.
* `emplacement_pk_end`: End station identifier (integer).
* `is_member`: If the rental user is a member of the rental system (0 if not a member, 1 if a member).

The file `stations.csv` must be a comma separated values file, using `;` as the delimiter, containing the following fields:

* `pk`: Station identifier (integer).
* `name`: Station name (string).
* `latitude`: Latitude of station location (double).
* `longitude`: Longitude of station location (double).

The results will be written in a comma separated values file, using `;` as the delimiter, named `query4.csv`. The file will have the following format:

```Text
station;pos_afflux;neutral_afflux;negative_afflux
...
```

A file with the timestamps of the start and the end of some of the tasks of the executable will be created, with the name `time4.txt`.

## 4. Final Remarks

This project was done in an academic environment, as part of the curriculum of Distributed Objects Programming from Instituto Tecnológico de Buenos Aires (ITBA)

The project was carried out by:

* Alejo Flores Lucey
* Andrés Carro Wetzel
* Nehuén Gabriel Llanos
