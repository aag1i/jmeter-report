#!/usr/bin/env groovy
node {
	stage('Initialising') {
        echo "###################################"
		echo "Test automation project starting..."
    }
    stage('GitSCM') {
        checkout scm: [
                $class: 'GitSCM',
                userRemoteConfigs: [
                        [
                                url: "https://github.com/aag1i/jmeter-report.git",
                                credentialsId: "github-auth-agl"
                        ]
                ],
                branches: [[name: "main"]]
        ], poll: false
    }
    stage('Running Tests') {
        dir("${WORKSPACE}") {
			sh "/jenkins/tools/apache-jmeter-5.4.3/bin/jmeter.sh -n -t sample.jmx -l report.xml -p user.properties"
        }
    }
	stage('Preparing Report') {
        dir("${WORKSPACE}") {
			sh "xsltproc report.xml sample.xslt > report.html"
			archiveArtifacts artifacts: 'report.html'
        }
    }
    stage('Complete') {
        echo "Test completed..."
    }
}
