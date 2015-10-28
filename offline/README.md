# Offline part

The offline part is deployed via [Kubernetes](https://docs.mesosphere.com/services/kubernetes/), so let's install that:

    $ dcos config prepend package.sources https://github.com/mesosphere/multiverse/archive/version-1.x.zip
    $ dcos package update --validate
    $ dcos package install kubernetes

We have two Docker containers running in the [pod](k8s-offlinereporting.yaml):

- The [Web UI](https://hub.docker.com/r/mhausenblas/tsdemo-offline-reporting-ui/) 
- The [S3 fetcher](https://hub.docker.com/r/mhausenblas/tsdemo-s3-fetcher/)

The offline reporting Web UI and the S3 fetcher have a shared data volume at `/tmp/tsdemo`.

![Offline reporting Web UI](../img/offline-reporting.png)

## Build & install offline reporting Web UI

The offline reporting Web UI is an automated Docker hub build, see: https://hub.docker.com/r/mhausenblas/tsdemo-offline-reporting-ui/

## Data ingestion via S3

The S3 data fetcher is an automated Docker hub build, see: https://hub.docker.com/r/mhausenblas/tsdemo-s3-fetcher/

Manually, these are the steps:

    $ docker run -it alpine:3.2 /bin/sh
    $ curl "https://s3.amazonaws.com/aws-cli/awscli-bundle.zip" -o "awscli-bundle.zip"
    $ unzip awscli-bundle.zip
    $ ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws
    $ export AWS_ACCESS_KEY_ID=<access_key>
    $ export AWS_SECRET_ACCESS_KEY=<secret_key>
    $ aws s3 cp s3://mesosphere-tsdemo/test.json test.json
