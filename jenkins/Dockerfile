FROM jenkins/jenkins:lts

# Install plugins
RUN /usr/local/bin/install-plugins.sh maven-plugin git workflow-aggregator gatling blueocean pipeline-stage-view gitlab pipeline-utility-steps sonar

# Maven Integration Plugin
# Git plugin
# Pipeline
# Gatling Jenkins Plugin
# Blue Ocean
# Pipeline: Stage View Plugin


# Disable plugin dialog
# RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state


# Run as root. Not so good from a security perspective, but easier to config for tool installation and access to docker socket
USER root 

# Using the docker client from the docker host (with -v /usr/bin/docker:/usr/bin/docker) no longer works, as the docker client is no longer a static binary in the default installation. Instead, install the static docker client library from github.
# Install statc docker client library. NOTE: Hardcoded docker client version.
RUN wget https://download.docker.com/linux/static/stable/x86_64/docker-18.09.0.tgz  &&\
    tar -xvzf ./docker-*.tgz &&\
    mv ./docker/docker /usr/bin/docker &&\
    chmod +x /usr/bin/docker &&\
rm -rf ./docker