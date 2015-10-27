# Offline part

To do (Michael H9):

- Docker image nginx S3 interface

In this part we will deploy a Docker-based nginx app in Marathon, acting as the UI for the offline reporting:

![Offline reporting Web UI](../img/offline-reporting.png)

## Build & install offline reporting Web UI

The offline reporting Web UI is an automated Docker hub build, see: https://hub.docker.com/r/mhausenblas/tsdemo-offline-reporting-ui/

Deploy the offline reporting Web UI in Marathon:

    $ dcos marathon app add marathon-offlinereporting.json

OK, now, where does the WebUI run and how can I access it?

    $ OFFLINEREPORTING_TASK_ID=`dcos marathon task list | grep offlinereporting | awk '{print $5}'`
    APP                HEALTHY          STARTED                             HOST                    ID
    /grafana             True   2015-10-26T19:52:37.733Z  ip-10-0-6-140.us-west-2.compute.internal  grafana.0d96dc8f-7c1b-11e5-863e-06ff3f135d7f
    /influxdb            True   2015-10-26T19:46:54.532Z  ip-10-0-6-140.us-west-2.compute.internal  influxdb.40a1fd4e-7c1a-11e5-863e-06ff3f135d7f
    /kafka               True   2015-10-26T22:29:47.827Z  ip-10-0-3-202.us-west-2.compute.internal  kafka.0cdcb251-7c31-11e5-863e-06ff3f135d7f
    /offlinereporting    True   2015-10-27T16:53:20.907Z  ip-10-0-6-140.us-west-2.compute.internal  offlinereporting.363a16c2-7ccb-11e5-863e-06ff3f135d7f
    /spark               True   2015-10-26T22:04:34.103Z  ip-10-0-3-201.us-west-2.compute.internal  spark.63375000-7c2d-11e5-863e-06ff3f135d7f
    
    $ dcos marathon task show $OFFLINEREPORTING_TASK_ID
    {
      "appId": "/offlinereporting",
      "host": "ip-10-0-6-140.us-west-2.compute.internal",
      "id": "offlinereporting.363a16c2-7ccb-11e5-863e-06ff3f135d7f",
      "ports": [
        2782
      ],
      "servicePorts": [
        10006
      ],
      "slaveId": "20151026-171728-1510342666-5050-1260-S3",
      "stagedAt": "2015-10-27T16:53:13.466Z",
      "startedAt": "2015-10-27T16:53:20.907Z",
      "version": "2015-10-27T16:53:13.389Z"
    }

### Data ingestion via S3

    $ docker run -it alpine:3.2 /bin/sh
    $ pip install -U boto


