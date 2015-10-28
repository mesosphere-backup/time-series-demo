## Dependencies

* sbt

## Building

Assuming you have sbt installed, run:

   bin/build

## Push to S3

   aws s3 cp s3://<bucket> target/scala-2.11/crime-data-producer-assembly-0.1.jar

## Update marathon.json

Change the `"uris"` field:

Add the public jar S3 object URL to the `"uris"` field.

Change the `"cmd"` field:

       $(pwd)/jre*/bin/java -jar crime-data-producer-assembly-0.1.jar --brokers <broker-host>:<broker-ip>,...

## Running

    dcos marathon app add marathon.json
