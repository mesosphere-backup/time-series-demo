# Online part

## InfluxDB

Deploy InfluxDB in Marathon:

    $ dcos marathon app add marathon-influxdb.json

Where does it run?

    $ dcos marathon app show influxdb | grep -w tasks -A 8
    "tasks": [
      {
        "appId": "/influxdb",
        "host": "ip-10-0-7-124.us-west-2.compute.internal",
        "id": "influxdb.3bb5669e-5baa-11e5-98ec-6ec316fc94b6",
        "ports": [
          20316,
          20317
        ],
        
Note this address (`ip-10-0-7-124.us-west-2.compute.internal`) and the first port (`20316`) and look it up in the AWS/EC2 console. In my case the public DNS was `ec2-52-27-229-31.us-west-2.compute.amazonaws.com` so we point our browser there.

Next step is to set up InfluxDB. We need to change the port to the second port (`20317`).

Create a database called `ts` as so:

    CREATE DATABASE ts
    ALTER RETENTION POLICY default ON ts DURATION 1d

Now use the `Write Data` link (in the WebUI) to create some data:

        src0,lat=40,lon=2 v=1
        src0,lat=40,lon=2 v=2
        src0,lat=40,lon=2 v=5
        src0,lat=40,lon=2 v=3
        src0,lat=40,lon=2 v=8

Then you can query it like so:

        SELECT * FROM src0

Last step is to create an administrator user with `admin`|`admin`. See also the [InfluxDB guide](https://influxdb.com/docs/v0.9/introduction/overview.html).

## Grafana

Deploy Grafana in Marathon:

    $ dcos marathon app add marathon-grafana.json
    $ dcos marathon app show grafana | grep -w tasks -A 8
    "tasks": [
    {
      "appId": "/grafana",
      "host": "ip-10-0-7-124.us-west-2.compute.internal",
      "id": "grafana.c393d2af-5bae-11e5-98ec-6ec316fc94b6",
      "ports": [
        21403
      ],

And again, following the same steps as above we discover Grafana running on `http://ec2-52-27-229-31.us-west-2.compute.amazonaws.com:21403` (log in with `admin`|`admin`).

Next, we need to connect InfluxDB to Grafana.

Note: use the second InfluxDB port (in my case `20317`) to configure the datasource. See also the [Grafana-InfluxDB setup](http://docs.grafana.org/datasources/influxdb/).

## Teeing it all together

Conceptually here's what happens:

    in >---[tee]---> out (InfluxDB)
             |
             |
             v
       /tmp/tsdata

With this commands:

    $ touch /tmp/tsdata
    $ docker run -it -v /tmp/tsdata:/tmp/tsdata ubuntu:14.04 tee -a /tmp/tsdata

## Notes

1. Deploy `marathon-influxdb.json` and config InfluxDB
1. Deploy `marathon-grafana.json` and config Grafana
