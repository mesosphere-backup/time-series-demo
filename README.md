# Time Series Demo

Todo:

- Select basis for synthetic stream data source (geolocations?)
- Write stream data source
  - Docker image (Alpine?)
  - synthetic stream data generator
  - tee with S3 ingest (sds.s3) and online output (sds.influx)
- Set up InfluxDB and Grafana, connect to sds.influx
- Implement Spark batch job
- Implement Chronos job
- Connect Spark job to sds.s3
- Documentation (asciinema) + video walkthrough
- Add to DCOS Demo

## Overview

Takes the input of a stream data source and process data in two paths: an [online](online/) path using a time series database and an [offline](offline/) part using batch jobs to create reports.

The synthetic stream data source has the following characteristics:

    2015-10-25T22:29:02+0000 LAT0 LON0 VAL0
    2015-10-25T22:29:03+0000 LAT0 LON0 VAL1
    2015-10-25T22:29:04+0000 LAT0 LON0 VAL2

Note that we're using ISO 8601 timestamps here, as in: `STRICT_ISO_8601='%Y-%m-%dT%H:%M:%S%z'`.

## Architecture

![Architecture](img/architecture.png)

## Tooling

- Marathon
- Docker tee + S3 and InfluxDB ingest
- InfluxDB + Grafana
- Chronos + Spark