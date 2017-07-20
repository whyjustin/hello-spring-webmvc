import groovy.json.JsonOutput

node {
  def commitId, commitDate, pom, version
  def gitHubApiToken

  def postGitHub = { state, context, description, targetUrl = null ->
    def payload = JsonOutput.toJson(
            state: state,
            context: context,
            description: description,
            target_url: targetUrl
    )
    sh "curl -H \"Authorization: token ${gitHubApiToken}\" --request POST --data '${payload}' https://api.github.com/repos/whyjustin/hello-spring-webmvc/statuses/${commitId} > /dev/null"
  }

  stage('Preparation') {
    deleteDir()

    checkout scm

    commitId = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
    commitDate = sh(returnStdout: true, script: "git show -s --format=%cd --date=format:%Y%m%d%H-%M%S ${commitId}").trim()

    pom = readMavenPom file: 'pom.xml'
    version = pom.version.replace("-SNAPSHOT", ".${commitDate}.${commitId.substring(0, 7)}")

    currentBuild.displayName = "#${currentBuild.number} - ${version}"

    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'github-api-token',
                      usernameVariable: 'GITHUB_API_USERNAME', passwordVariable: 'GITHUB_API_PASSWORD']]) {
      gitHubApiToken = env.GITHUB_API_PASSWORD
    }
  }
  stage('Build') {
    postGitHub 'pending', 'build', 'Build is running'

    withMaven(jdk: 'JDK8u121', maven: 'M3', mavenSettingsConfig: 'nexus-settings') {
      sh 'mvn clean install'
    }

    if (currentBuild.result == 'FAILURE') {
      postGitHub 'failure', 'build', 'Build failed'
      return
    } else {
      postGitHub 'success', 'build', 'Build succeeded'
    }
  }
  stage('Nexus Lifecycle Analysis') {
    postGitHub 'pending', 'analysis', 'Nexus Lifecycle Analysis is running'

    def evaluation = nexusPolicyEvaluation failBuildOnNetworkError: false, iqApplication: 'vertical', iqStage: 'build', jobCredentialsId: ''

    if (currentBuild.result == 'FAILURE') {
      postGitHub 'failure', 'analysis', 'Nexus Lifecycle Analysis failed', "${evaluation.applicationCompositionReportUrl}"
      return
    } else {
      postGitHub 'success', 'analysis', 'Nexus Lifecycle Analysis passed', "${evaluation.applicationCompositionReportUrl}"
    }
  }
  if (currentBuild.result == 'FAILURE')
  {
    return
  }
  stage('Deploy') {
    withMaven(jdk: 'JDK8u121', maven: 'M3', mavenSettingsConfig: 'nexus-settings') {
      sh "mvn -Darguments=-DskipTests -DreleaseVersion=${version} -DdevelopmentVersion=${pom.version} -DpushChanges=false -DlocalCheckout=true -DpreparationGoals=initialize release:prepare release:perform -B"
    }
    sh "git tag -d ${pom.artifactId}-${version}"
    sh 'git clean -f && git reset --hard'
  }
}
