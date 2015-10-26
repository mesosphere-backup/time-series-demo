# Time Series Demo: Crime Buster!

- Implement Spark batch job
- Implement Chronos job
- Connect Spark job to sds.s3
- Documentation (asciinema) + video walkthrough
- Add to DCOS Demo

## Overview

Takes the input of a stream data source and process data in two paths: an [online](online/) path using a time series database and an [offline](offline/) part using batch jobs to create reports.

Data: [crime](https://data.cityofchicago.org/Public-Safety/Crimes-2001-to-present/ijzp-q8t2) dataset from the City of Chicago.

## Architecture

![Architecture](img/architecture.png)

## Tooling

- Marathon
- Docker tee + S3 and InfluxDB ingest
- InfluxDB + Grafana
- Chronos + Spark