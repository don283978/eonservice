def readProperties()
{

	def properties_file_path = "${workspace}" + "@script/properties.yml"
	def property = readYaml file: properties_file_path
	env.APP_NAME = property.APP_NAME
        env.MS_NAME = property.MS_NAME
        env.BRANCH = property.BRANCH
        env.GIT_SOURCE_URL = property.GIT_SOURCE_URL
        env.SCR_CREDENTIALS = property.SCR_CREDENTIALS
	env.GIT_CREDENTIALS = property.GIT_CREDENTIALS
        env.SONAR_HOST_URL = property.SONAR_HOST_URL
        env.CODE_QUALITY = property.CODE_QUALITY
        env.UNIT_TESTING = property.UNIT_TESTING
        env.CODE_COVERAGE = property.CODE_COVERAGE
        env.FUNCTIONAL_TESTING = property.FUNCTIONAL_TESTING
        env.SECURITY_TESTING = property.SECURITY_TESTING
	env.PERFORMANCE_TESTING = property.PERFORMANCE_TESTING
	env.TESTING = property.TESTING
	env.QA = property.QA
	env.PT = property.PT
	env.User = property.User
    env.DOCKER_REGISTRY = property.DOCKER_REGISTRY
    env.DOCKER_REPO=property.DOCKER_REPO
	env.mailrecipient = property.mailrecipient
	env.DELTA_BRANCH_COVERAGE = property.JACOCO_DELTA_BRANCH_COVERAGE
	env.DELTA_CLASS_COVERAGE = property.JACOCO_DELTA_CLASS_COVERAGE 
	env.DELTA_COMPLEXITY_COVERAGE = property.JACOCO_DELTA_COMPLEXITY_COVERAGE 
	env.DELTA_INSTRUCTION_COVERAGE = property.JACOCO_DELTA_INSTRUCTION_COVERAGE
	env.DELTA_LINE_COVERAGE = property.JACOCO_DELTA_LINE_COVERAGE 
	env.DELTA_METHOD_COVERAGE = property.JACOCO_DELTA_METHOD_COVERAGE 
	
	
    
}



def FAILED_STAGE
podTemplate(cloud:'openshift',label: 'docker', nodeSelector:'kubernetes.io/hostname=devopenshift1-node01',
  containers: [
    containerTemplate(
      name: 'jnlp',
      image: 'manya97/jenkins_tryout',
      alwaysPullImage: true,
      args: '${computer.jnlpmac} ${computer.name}',
      ttyEnabled: true
    )],volumes: [hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock'),hostPathVolume(hostPath: '/etc/docker/daemon.json', mountPath: '/etc/docker/daemon.json')] )
{
    
    podTemplate(cloud:'openshift',label: 'selenium',nodeSelector:'kubernetes.io/hostname=devopenshift1-node01',
  containers: [
     containerTemplate(
      name: 'jnlp',
      image: 'cloudbees/jnlp-slave-with-java-build-tools',
      alwaysPullImage: true,
      args: '${computer.jnlpmac} ${computer.name}'
    )])
{
node 
{
   def MAVEN_HOME = tool "Maven_HOME"
   def JAVA_HOME = tool "JAVA_HOME"
   env.PATH="${env.PATH}:${MAVEN_HOME}/bin:${JAVA_HOME}/bin"
   try{
   stage('Checkout')
   {
       FAILED_STAGE=env.STAGE_NAME
       readProperties()
       checkout([$class: 'GitSCM', branches: [[name: "*/${BRANCH}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: "${SCR_CREDENTIALS}", url: "${GIT_SOURCE_URL}"]]])
       sh 'oc set image --local=true -f Orchestration/deployment.yaml ${MS_NAME}=${DOCKER_REGISTRY}/eonspecific-dev-apps/${MS_NAME}:eonspecific-dev-apps --dry-run -o yaml >> Orchestration/deployment-eonspecific-dev.yaml'

   }

   stage('Initial Setup')
   {   
       FAILED_STAGE=env.STAGE_NAME
       sh 'mvn -s Maven/setting clean compile'
   }
   if(env.UNIT_TESTING == 'True')
   {
   	stage('Unit Testing')
   	{
        	
        	FAILED_STAGE=env.STAGE_NAME
        	sh 'mvn test'
   	}
   }
   
   if(env.CODE_QUALITY == 'True')
   {
   	stage('Code Quality Analysis')
   	{
       		FAILED_STAGE=env.STAGE_NAME
       		sh 'mvn sonar:sonar -Dsonar.host.url="${SONAR_HOST_URL}"'
   	}
   }
   
   if(env.CODE_COVERAGE == 'True')
   {
   	stage('Code Coverage')
   	{
		FAILED_STAGE=env.STAGE_NAME
		sh 'mvn package'
		jacoco(deltaBranchCoverage: "${DELTA_BRANCH_COVERAGE}", deltaClassCoverage: "${DELTA_CLASS_COVERAGE}", deltaComplexityCoverage: "${DELTA_COMPLEXITY_COVERAGE}", deltaInstructionCoverage: "${DELTA_INSTRUCTION_COVERAGE}", deltaLineCoverage: "${DELTA_LINE_COVERAGE}", deltaMethodCoverage: "${DELTA_METHOD_COVERAGE}")
   	}
   }
   
  if(env.SECURITY_TESTING == 'True')
  {
	stage('Security Testing')
	{
		FAILED_STAGE=env.STAGE_NAME
		sh 'mvn findbugs:findbugs'
	}	
  }
   
   stage('Build and Tag Image for Dev')
   {
   		script {
        withCredentials([
            usernamePassword(credentialsId: 'DockerID', usernameVariable: 'username', passwordVariable: 'password')
          ])
        {  
   		
	   FAILED_STAGE=env.STAGE_NAME
	   sh 'oc config use-context eonspecific-devops'
	  node('docker')
    {
        
        container('jnlp')
        {
         	sh 'git clone ${GIT_SOURCE_URL}'
            sh "docker build -t ${MS_NAME}:latest '/home/jenkins/workspace/${env.JOB_NAME}/${MS_NAME}'"
             sh 'docker tag ${MS_NAME}:latest ${DOCKER_REGISTRY}/${DOCKER_REPO}/$MS_NAME:eonspecific-dev-apps'
           sh 'docker login ${DOCKER_REGISTRY} --username $username  --password $password'
			sh 'docker push ${DOCKER_REGISTRY}/${DOCKER_REPO}/$MS_NAME:eonspecific-dev-apps'
            
        }
    }
    
    }
	 } 
    
	   
   }
   

   
   stage('Dev - Deploy Application')
   {   
       FAILED_STAGE=env.STAGE_NAME
       
       sh 'chmod 777 Orchestration/dev/deploy.sh'
       sh 'Orchestration/dev/deploy.sh'
       
       /*sh 'oc config use-context eonspecific-dev-apps'
       sh 'oc config view'
       sh 'oc apply -f Orchestration/deployment-eonspecific-dev.yaml -n=eonspecific-dev-apps'
       sh 'oc apply -f Orchestration/service.yaml -n=eonspecific-dev-apps' */
       
   }
	
   

   

   

   
   
   
   }
	catch(e){
		echo "Pipeline has failed"
		emailext body: "${env.BUILD_URL} has result ${currentBuild.result} at stage ${FAILED_STAGE} with error" + e.toString(), subject: "Failure of pipeline: ${currentBuild.fullDisplayName}", to: "${mailrecipient}"
	    throw e
	
	}
	     
}
}	
}
