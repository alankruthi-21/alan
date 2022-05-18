pipeline{
    agent any
   parameters {
    string(name: 'BRANCH_NAME', defaultValue: 'master', description: 'From which branch artifacts want to deploy?')
    string(name: 'BUILD_NUM', defaultValue: '', description: 'From which build number artifacts want to deploy?')
    string(name: 'SERVER_IP', defaultValue: '', description: 'To  which want to deploy?')

  }
    stages{
        stage("download artifact"){
            steps {
                println "Here I'm downloading artifacts from S3"
                sh """
                        
                        aws s3 cp s3://alankruthiart/application2/${BUILD_NUM}/hello-${BUILD_NUM}.war .
 
                   """
                
            }
        }
        stage("copy artifacts") {
            steps {
                println "Here I'm coping artifact from Jenkins to Tomcat servers"
                sh '''IFS=',' read -r -a ip  <<< "${Ip}"
                 for i in \"${ip[@]}\"
                 do
                 echo $i
                 
                 "scp -o StrictHostKeyChecking=no -i /tmp/alankruthi21.pem hello-${BUILD_NUM}.war ec2-user@$i:/tmp"
                 "ssh -o StrictHostKeyChecking=no -i /tmp/alankruthi21.pem ec2-user@$i \"sudo cp /tmp/hello-${BUILD_NUM}.war /var/lib/tomcat/webapps\""
               done
                 '''
            }
        }
    }
}