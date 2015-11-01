# Crime Data Producer (CDP)

## Dependencies

- [sbt](http://www.scala-sbt.org/)
- [AWS CLI](http://aws.amazon.com/documentation/cli/)

## Building and deploying

Assuming you have `sbt` installed, run to build the Crime Data Producer (CDP):

    $ bin/build

Assuming you have the AWS CLI installed, copy to an S3 bucket like so:

    $ aws s3 cp target/scala-2.11/crime-data-producer-assembly-0.1.jar s3://$YOURBUCKET

In order to adjust the crime data producer to your environment, you'll have to update
the Marathon app spec [marathon-cdp.json](marathon-cdp.json) in two places.

In `marathon-cdp.json`, in the `uris` field, change the first and the last entry to your own values:

    "uris": ["https://$YOURBUCKET/crime-data-producer-assembly-0.1.jar",
             "https://downloads.mesosphere.io/java/jre-7u76-linux-x64.tar.gz",
             "https://$YOURBUCKET/crime-data-1000.csv"],

Also in `marathon-cdp.json`, adapt the `cmd` field entry. Look up where your Kafka broker runs (if you've only started one, you can trivially use `dcos kafka broker list | grep endpoint` to do this lookup) and replace `$BROKER_HOST:PORT` with the actual values:

    "cmd": "$(pwd)/jre*/bin/java -jar crime-data-producer-assembly-0.1.jar --brokers $BROKER_HOST:PORT --topic crime --uri file://$(pwd)/crime-data-1000.csv"

## Running

To launch the CDP do the following:

    $ dcos marathon app add marathon-cdp.json
