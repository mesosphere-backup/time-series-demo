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

    CREATE DATABASE tsdemo
    ALTER RETENTION POLICY default ON tsdemo DURATION 1d

Now use the `Write Data` link (in the WebUI) to create some data:

        crimedata0,case-no=HY467388,type=battery,lat=41.74,lon=-87.55 v=1
        crimedata0,case-no=HY467389,type=robbery,lat=41.99,lon=-87.59 v=1
        crimedata0,case-no=HY467390,type=narcotics,lat=41.81,lon=-87.67 v=1
        crimedata0,case-no=HY467391,type=robbery,lat=41.86,lon=-87.40 v=1

Then you can query it like so:

        SELECT * FROM crimedata0

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

Next, we need to connect InfluxDB to Grafana:

![Grafana Influx Setup](../img/grafana-influx-setup.png)

Note: use the second InfluxDB port (in my case `20317`) to configure the datasource. See also the [Grafana-InfluxDB setup](http://docs.grafana.org/datasources/influxdb/).

Now you can define a dashboard and add a graph:

![Grafana Graph Setup](../img/grafana-graph-setup.png)

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
