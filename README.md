# Time Series DCOS Demo: Crime Buster!

## Overview

In this demo we take the [crime](https://data.cityofchicago.org/Public-Safety/Crimes-2001-to-present/ijzp-q8t2) dataset from the City of Chicago, turn it into a streaming data source and process the data in two paths:

- an [online](online/) path, using a time series database (InfluxDB) and visualize the crime types in Grafana.
- an [offline](offline/) part, using Spark jobs to create an overlay heat map of aggregated crimes on Google Maps.

![Architecture](img/architecture-overview.png)

## Usage

First you need to clone this repo:

    $ git clone https://github.com/mesosphere/time-series-demo.git && cd time-series-demo/

Then you can set up and launch the components:

1. Set up [Kafka and Spark](stream-processing/).
1. Set up and launch [the Crime Data Producer](crime-data-producer/).
1. Set up [InfluxDB and Grafana online path](online/), configure it and launch it.
1. Set up [Kubernetes and the offline reporting Web app](offline/) and launch it.

The live set up and launch steps are as follows:

[![Part 1](https://asciinema.org/a/29080.png)](https://asciinema.org/a/29080)


[![Part 1](https://asciinema.org/a/dlkzmpamxuoaye5c0pn95h5lz.png)](https://asciinema.org/a/dlkzmpamxuoaye5c0pn95h5lz)
[![Part 2](https://asciinema.org/a/9ta8kl3xaq24jyriychzavqbv.png)](https://asciinema.org/a/9ta8kl3xaq24jyriychzavqbv)


![DCOS dashboard](img/dcos-dashboard.png)


### Dependencies

- Mesosphere [DCOS 1.3](https://mesosphere.com/product/) {ALL}
 - [Marathon 0.11.1](https://mesosphere.github.io/marathon/) {ALL}
 - [Spark 1.5](https://spark.apache.org/) {ALL}
 - [Kubernetes 1.0.6](https://github.com/kubernetes/kubernetes/releases/tag/v1.0.6) {OFFLINE}
 - [InfluxDB 0.9.4](https://influxdb.com/) {ONLINE}
 - [Grafana 2.1.3](http://grafana.org/) {ONLINE}
- [heatmap.js 2.0](http://www.patrick-wied.at/static/heatmapjs/) {OFFLINE}
- AWS S3 and the [CLI](http://aws.amazon.com/documentation/cli/) {OFFLINE}
- Docker Hub
 - [Offline reporting Web UI](https://hub.docker.com/r/mhausenblas/tsdemo-offline-reporting-ui/) {OFFLINE}
 - [S3 fetcher](https://hub.docker.com/r/mhausenblas/tsdemo-s3-fetcher/) {OFFLINE}

- Add documentation (asciinema) for overall setup (Michael H9)
 - Part 1 https://asciinema.org/a/dlkzmpamxuoaye5c0pn95h5lz
 - Part 2 https://asciinema.org/a/9ta8kl3xaq24jyriychzavqbv

To do:

- Create video walkthrough (Michael H9)
- Add real timestamps to InfluxDB data (Michael G)
