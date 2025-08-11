pipeline {
  agent any
  environment {
    JAVA_HOME   = tool name: 'JDK17', type: 'hudson.model.JDK'
    MAVEN_HOME  = tool name: 'Maven3.9', type: 'hudson.tasks.Maven$MavenInstallation'
    NODEJS_HOME = tool name: 'Node18', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
    PATH        = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${NODEJS_HOME}/bin:${env.PATH}"
    SONAR_SERVER = 'SonarQube'
  }
  options { timestamps(); ansiColor('xterm') }

  stages {
    stage('Checkout') { steps { checkout scm } }

    stage('Set Sonar Keys by Branch') {
      steps {
        script {
          def b = env.BRANCH_NAME
          if (!(b in ['dev','uat','master'])) { b = 'dev' }   // feature/* cuentan como dev
          env.SONAR_KEY_BE = (b=='dev') ? 'pharmacy-backend-dev' :
                             (b=='uat') ? 'pharmacy-backend-qa'  :
                                         'pharmacy-backend-main'
          env.SONAR_KEY_FE = (b=='dev') ? 'pharmacy-frontend-dev' :
                             (b=='uat') ? 'pharmacy-frontend-qa'  :
                                         'pharmacy-frontend-main'
          env.BUILD_VER = "${env.BUILD_NUMBER}"
        }
      }
    }

    stage('Backend - Build, Test & Sonar') {
      steps {
        dir('pharmacy') {
          withSonarQubeEnv("${SONAR_SERVER}") {
            sh """
              mvn -B clean verify sonar:sonar \
                -Dsonar.projectKey=${SONAR_KEY_BE} \
                -Dsonar.projectVersion=${BUILD_VER}
            """
          }
        }
      }
    }

    stage('Quality Gate (Backend)') {
      steps {
        timeout(time: 10, unit: 'MINUTES') {
          script {
            def qg = waitForQualityGate()   // requiere webhook Sonar→Jenkins
            if (qg.status != 'OK') error "Quality Gate FAILED (Backend): ${qg.status}"
          }
        }
      }
    }

    stage('Frontend - Build & Sonar (sin tests)') {
      steps {
        dir('Frontend') {
          sh "npm ci && npm run build -- --configuration=production"
        }
        script {
          def scannerHome = tool name: 'SonarScanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
          withSonarQubeEnv("${SONAR_SERVER}") {
            sh """
              cd Frontend
              "${scannerHome}/bin/sonar-scanner" \
                -Dsonar.projectKey=${SONAR_KEY_FE} \
                -Dsonar.projectVersion=${BUILD_VER}
            """
          }
        }
      }
    }

    stage('Quality Gate (Frontend)') {
      steps {
        timeout(time: 10, unit: 'MINUTES') {
          script {
            def qg = waitForQualityGate()
            if (qg.status != 'OK') error "Quality Gate FAILED (Frontend): ${qg.status}"
          }
        }
      }
    }

    stage('Deploy por rama') {
      when { anyOf { branch 'dev'; branch 'uat'; branch 'master' } }
      steps {
        script {
          if (env.BRANCH_NAME == 'dev')      { sh './deploy/dev/deploy.sh' }
          else if (env.BRANCH_NAME == 'uat') { sh './deploy/uat/deploy.sh' }
          else                                { sh './deploy/prod/deploy.sh' }
        }
      }
    }
  }

  post {
    always { echo "Build URL: ${env.BUILD_URL}" }
  }
}
