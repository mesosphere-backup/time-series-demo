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
        
Note this address (`ip-10-0-7-124.us-west-2.compute.internal`) and the first port (`20316`) and look it up in the AWS/EC2 console. In my case the public DNS was `ec2-54-200-134-49.us-west-2.compute.amazonaws.com` so we point our browser there.

Next step is to set up InfluxDB itself. You change the port from `8086` to the second port (in my case: `22372`); this is where InfluxDB's API is. Then, log in with `admin`, `admin` and create a database called `tsdemo` as so:

    CREATE DATABASE tsdemo
    ALTER RETENTION POLICY default ON tsdemo DURATION 1d

Now use the `Write Data` link (in the WebUI) to create some data:

![Influx Write Data](../img/influx-write-data.png)

For example, enter the following values:

        crimedata0,case-no=HY467388,type=battery,lat=41.74,lon=-87.55 v=1
        crimedata0,case-no=HY467389,type=robbery,lat=41.99,lon=-87.59 v=1
        crimedata0,case-no=HY467390,type=narcotics,lat=41.81,lon=-87.67 v=1
        crimedata0,case-no=HY467391,type=robbery,lat=41.86,lon=-87.40 v=1

Of course, if you prefer a CLI-based approach that's also possible:

        curl -i -XPOST 'http://$PUBLIC_SLAVE_FQHN:22372/write?db=tsdemo' --data-binary 'crimedata0,case-no=HY467397,type=narcotics,lat=41.89,lon=-87.51 v=1'

Here, you'll have to replace `$PUBLIC_SLAVE_FQHN:22372` with your values. you 


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

## Spark Data Ingestions

For the online ingestion path (Kakfa->Spark->InfluxDB) we will need to explicitly set timestamps. The `date` column of the dataset contains the date which needs to be converted into an epoch timestamp and can then be inserted like so:

    curl -i -XPOST 'http://$PUBLIC_SLAVE_FQHN:22372/write?db=tsdemo' --data-binary 'crimedata0,case-no=HY467388,type=battery,lat=41.74,lon=-87.55 v=1 1445890350000000000'

Note the additional `1445890350000000000` at the end, after `v=1`.


