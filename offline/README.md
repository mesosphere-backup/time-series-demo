# Offline part

We have two Docker containers running in the [pod](k8s-offlinereporting.yaml):

- The [Web UI](https://hub.docker.com/r/mhausenblas/tsdemo-offline-reporting-ui/) 
- The [S3 fetcher](https://hub.docker.com/r/mhausenblas/tsdemo-s3-fetcher/)

The offline reporting Web UI and the S3 fetcher have a shared data volume at `/tmp/tsdemo`.

![Offline reporting Web UI](../img/offline-reporting.png)

## Install offline reporting

The offline part is deployed via [Kubernetes](https://docs.mesosphere.com/services/kubernetes/), so let's install that:

    $ dcos config prepend package.sources https://github.com/mesosphere/multiverse/archive/version-1.x.zip
    $ dcos package update --validate
    $ dcos package install kubernetes

Now we need to configure Kubernetes:

    $ export KUBERNETES_MASTER=http://$MESOS_MASTER_IP/service/kubernetes/api

Next, [install](https://docs.mesosphere.com/services/kubernetes/#a-namefivealaunch-a-kubernetes-pod-and-service-by-using-kubectl) `kubectl`
and check if everything is fine:

    $  kubectl cluster-info
    Kubernetes master is running at http://54.186.126.114
    KubeDNS is running at http://54.186.126.114/api/v1/proxy/namespaces/kube-system/services/kube-dns
    KubeUI is running at http://54.186.126.114/api/v1/proxy/namespaces/kube-system/services/kube-ui
    
In order to work properly you'll need to supply the S3 fetcher with your AWS credentials, so create a file called `aws-secret.yaml` with this content:

    apiVersion: v1
    kind: Secret
    metadata:
      name: awssecret
    type: Opaque
    data:
      AWS-ACCESS-KEY-ID: $YOUR_AWS_KEY
      AWS-SECRET-ACCESS-KEY: $YOUR_AWS_SECRET

Make sure you replace `$YOUR_AWS_KEY` and `$YOUR_AWS_SECRET` with the `base64` encoded values you glean from the AWS IAM, see also this [docs](http://docs.aws.amazon.com/general/latest/gr/aws-access-keys-best-practices.html). For example: `echo -n 123456789 | base64`.


Finally we can launch our offline reporting [replication controller](http://kubernetes.io/v1.0/docs/user-guide/walkthrough/k8s201.html#replication-controllers):

    $ kubectl create -f aws-secret.yaml
    $ kubectl create -f k8s-offlinereporting-rc.yaml
    $ kubectl get rc
    CONTROLLER            CONTAINER(S)   IMAGE(S)                                  SELECTOR                REPLICAS
    offlinereporting-rc   s3-fetcher     mhausenblas/tsdemo-s3-fetcher             name=offlinereporting   1
                          webui          mhausenblas/tsdemo-offline-reporting-ui

![K8S deployment](../img/k8s-deployment.png)

Once you're done, you can remove the RC with `kubectl delete rc offlinereporting-rc`.


## Build offline reporting Web UI

The offline reporting Web UI is an automated Docker hub build, see: https://hub.docker.com/r/mhausenblas/tsdemo-offline-reporting-ui/

## Build S3 fetcher

The S3 data fetcher is an automated Docker hub build, see: https://hub.docker.com/r/mhausenblas/tsdemo-s3-fetcher/

Manually, these are the steps:

    $ docker run -it alpine:3.2 /bin/sh
    $ curl "https://s3.amazonaws.com/aws-cli/awscli-bundle.zip" -o "awscli-bundle.zip"
    $ unzip awscli-bundle.zip
    $ ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws
    $ export AWS_ACCESS_KEY_ID=<access_key>
    $ export AWS_SECRET_ACCESS_KEY=<secret_key>
    $ aws s3 cp s3://mesosphere-tsdemo/test.json test.json


