FROM alpine:3.2
MAINTAINER Michael Hausenblas "michael.hausenblas@mesosphere.io"
ENV REFRESHED_AT 2015-10-28

RUN apk --update add python curl && \
    curl "https://s3.amazonaws.com/aws-cli/awscli-bundle.zip" -o "awscli-bundle.zip" && \
    unzip awscli-bundle.zip && \
    ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws && \

WORKDIR /tmp/tsdemo

CMD AWS_ACCESS_KEY_ID=$(< /tmp/aws/AWS-ACCESS-KEY-ID) AWS_SECRET_ACCESS_KEY=$(< /tmp/aws/AWS-SECRET-ACCESS-KEY) aws s3 cp s3://mesosphere-tsdemo/test.json test.json