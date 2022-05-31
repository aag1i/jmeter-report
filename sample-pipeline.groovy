#!/usr/bin/env groovy
pipeline
{
	agent any
	
	parameters
	{
		choice(name: 'SelectENV', choices: ['DEV','UAT'], description: 'Pick the Environment')
		string(name: 'sample', defaultValue: 'ABC', description: 'Sample Variable')
	}
	options
	{
		timestamps()
	}
	environment
	{
		JMETER_OPTS = ""
		script
		{		
		def props = readProperties file: 'test.properties'
		JMETER_OPTS = " -Jhostname=$props.hostname -Jhostport=$props.port -JsampleVar=$params.sample"
		echo " RUN OPTIONS : " + ${JMETER_OPTS}
		
		}
	}
	stages{
	
		stage('Initialising') {
			steps{
				echo "###################################"
				echo "Test automation project starting..."
				echo "Environment : ${params.SelectENV}"
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
						branches: [[name: "main"]],
						extensions: [[$class: 'CleanBeforeCheckout']]
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
				script{
					if (params.sample != 'ABC')
					{
						echo "### Sample variable changed -> ${params.sample}"
					}
					else
					{
						echo "### Sample variable has not changed -> ${params.sample}"
					}
				}
			}
		}//Stage
	} //stages
} //pipeline
