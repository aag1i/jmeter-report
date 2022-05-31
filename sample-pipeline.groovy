#!/usr/bin/env groovy
pipeline
{
	parameters
	{
		choice(name: 'global.ENV', choices: ['DEV','UAT'], description: 'Pick the Environment')
	}
	
	stages{
		
		agent any
		
		stage('Initialising') {
			steps{
				echo "###################################"
				echo "Test automation project starting..."
				echo "Environment : ${params.global.ENV}"
			}			
		}
		
		stage('GitSCM') {
			
			steps{
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

}
