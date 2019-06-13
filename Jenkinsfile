pipeline {
    // In order to make the 'Build-Finished' Gitlab phase work, we need an agent with a git clone.
    agent { label 'master' }

    options {
        // Use the GitLab connection named 'gitlab-connection', available in Endeavour Jenkins buildservers
        gitLabConnection("gitlab")
        gitlabBuilds(builds: ['Build-Finished'])
        // Show timestamps in logging. To be able to spot bottlenecks in the build
        timestamps()
        // No concurrent builds of this branch
        disableConcurrentBuilds()
    }

    triggers {
        // Make gitlab trigger builds on all code changes
        gitlab(triggerOnPush: true, triggerOnMergeRequest: true, branchFilterType: "All")
        // Also build daily to make sure the product still has a valid build
        cron('@daily')
    }

    // Update commitstatus to Gitlab to enable auto-merging of the build in case of success
    post {
        success {
            updateGitlabCommitStatus name: 'Build-Finished', state: 'success'
        }
        failure {
            updateGitlabCommitStatus name: 'Build-Finished', state: 'failed'
        }
    }

    stages {
        stage("Run with JDK 12 and maven") {
            // Run all maven commands in a separate Docker container supplying maven
            agent {
                docker {
                    //docker image with maven and jdk 12 installed to complete these stages
                    image 'maven:3.6.0-jdk-8'
                    // It's possible to add extra volumes to the host here. The volumes to /root/.m2 and /root/.sonar are already present in Endeavour Jenkins buildservers
                }
            }
            stages {
                stage("Build") {
                    steps {
                        gitlabCommitStatus(name: STAGE_NAME) {
                            /*********************************************************
                            * Compile the software, run the tests and code coverage *
                            *********************************************************/
                            // Ignoring failed tests, because sonar will generate a view of the tests
                            sh "mvn clean install -e"

                            // Stash the repo including files needed for the sonarqube Analysis
                            stash name: 'All', includes: '**'
                        }
                    }
                }
            }
        }

		stage("Verify") {
            agent {
                docker {
                    //docker image with maven and jdk 12 installed to complete these stages
                    image 'maven:3.6.0-jdk-8'
                    args '--network="shopizer_default"' // This is important for demo purposes
                    // It's possible to add extra volumes to the host here. The volumes to /root/.m2 and /root/.sonar are already present in Endeavour Jenkins buildservers
                }
            }
            steps {
                gitlabCommitStatus(name: STAGE_NAME) {
                    unstash 'All'
                    /**************************************************************************
                    * Run SonarQube analysis and make the build fail on failing quality gate *
                    **************************************************************************/
                    withSonarQubeEnv("sonarqube") {
                        sh "mvn sonar:sonar" || true
                    }
                    sleep(60) // Another hack because of webhook issues
                    timeout(time: 30, unit: "MINUTES") {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }
        }
    }
}
