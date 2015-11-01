# Crime Data Producer

## Dependencies

- [sbt](http://www.scala-sbt.org/)
- [AWS CLI](http://aws.amazon.com/documentation/cli/)

## Building

Assuming you have `sbt` installed, run:

    $ bin/build

### Push to S3

Assuming you have the AWS CLI installed, run:

    $ aws s3 cp s3://$YOURBUCKET target/scala-2.11/crime-data-producer-assembly-0.1.jar

### Update Marathon app spec

In order to set up the crime data producer with your environment, do the following:

Change the first and last `uris` field entry in `marathon-cdp.json`:

    "uris": ["https://$YOURBUCKET/crime-data-producer-assembly-0.1.jar",
             "https://downloads.mesosphere.io/java/jre-7u76-linux-x64.tar.gz",
             "https://$YOURBUCKET/crime-data-1000.csv"],

Change the `cmd` field entry in `marathon-cdp.json`. Look up where your Kafka broker runs and replace `$BROKER_HOST:PORT` with the actual values:

    $(pwd)/jre*/bin/java -jar crime-data-producer-assembly-0.1.jar --brokers $BROKER_HOST:PORT --topic crime --uri file://$(pwd)/crime-data-1000.csv

## Running

    $ dcos marathon app add marathon-cdp.json
