#!/usr/bin/env groovy
pipeline
{
	agent any
	
	parameters
	{
		choice(name: 'Select-ENV', choices: ['DEV','UAT'], description: 'Pick the Environment')
		string(name: 'My_VAR', defaultValue: 'sample', description: 'Sample Variable')
	}
	options
	{
		timestamps()
	}
	stages{
	
		stage('Initialising') {
			steps{
				echo "###################################"
				echo "Test automation project starting..."
				echo "Environment : ${params.Select-ENV}"
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
			steps{
				dir("${WORKSPACE}") {
					sh "/jenkins/tools/apache-jmeter-5.4.3/bin/jmeter.sh -n -t sample.jmx -l report.xml -p sample.properties"
				}
			}			
		}
		stage('Preparing Report') {
			steps{
				dir("${WORKSPACE}") {
					sh "xsltproc report.xml sample.xslt > report.html"
					archiveArtifacts artifacts: 'report.html'
				}
			}
		}
		stage('Complete') {
			steps {
				if (params.My_VAR != 'sample')
				{
					echo "### Sample variable changed -> ${params.My_VAR}"
				}
				else
				{
					echo "### Sample variable has not changed -> ${params.My_VAR}"
				}			
			}
		}//Stage
	} //stages
} //pipeline
