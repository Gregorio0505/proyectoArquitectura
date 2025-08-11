pipeline {
  agent any

  environment {
    JAVA_HOME    = tool name: 'JDK17', type: 'hudson.model.JDK'
    MAVEN_HOME   = tool name: 'Maven3.9', type: 'hudson.tasks.Maven$MavenInstallation'
    NODEJS_HOME  = tool name: 'Node18', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
    SCANNER_HOME = tool name: 'SonarScanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
    PATH         = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${NODEJS_HOME}/bin:${SCANNER_HOME}/bin:${env.PATH}"
    SONAR_SERVER = 'SonarQube'
  }

  options { timestamps() }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Verificar Java y Maven') {
      steps {
        script {
          sh """
            echo "=== Verificando versiones ==="
            echo "JAVA_HOME: ${JAVA_HOME}"
            echo "MAVEN_HOME: ${MAVEN_HOME}"
            java -version
            mvn -version
            echo "=== Fin verificación ==="
          """
        }
      }
    }

    stage('Resolver branch y projectKeys de Sonar') {
      steps {
        script {
          def b = env.BRANCH_NAME ?: 'dev'
          if (!(b in ['dev','uat','master'])) { b = 'dev' } // feature/* -> dev

          env.SONAR_KEY_BE = (b=='dev') ? 'pharmacy-backend-dev' :
                             (b=='uat') ? 'pharmacy-backend-qa'  :
                                         'pharmacy-backend-main'

          env.SONAR_KEY_FE = (b=='dev') ? 'pharmacy-frontend-dev' :
                             (b=='uat') ? 'pharmacy-frontend-qa'  :
                                         'pharmacy-frontend-main'

          env.BUILD_VER = "${env.BUILD_NUMBER}"
          echo "BRANCH=${b} | BE=${env.SONAR_KEY_BE} | FE=${env.SONAR_KEY_FE} | VER=${env.BUILD_VER}"
        }
      }
    }

    stage('Backend - Build, Tests y Sonar') {
      steps {
        dir('pharmacy') {
          withSonarQubeEnv("${SONAR_SERVER}") {
            sh """
              export JAVA_HOME="${JAVA_HOME}"
              export PATH="${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"
              mvn -B clean verify sonar:sonar \
                -Dsonar.projectKey=${SONAR_KEY_BE} \
                -Dsonar.projectVersion=${BUILD_VER} \
                -Dmaven.compiler.source=17 \
                -Dmaven.compiler.target=17
            """
          }
        }
      }
    }

    stage('Quality Gate (Backend)') {
      steps {
        timeout(time: 10, unit: 'MINUTES') {
          script {
            def qg = waitForQualityGate() // requiere webhook Sonar->Jenkins
            if (qg.status != 'OK') error "Quality Gate FAILED (Backend): ${qg.status}"
          }
        }
      }
    }

    stage('Frontend - Build & Sonar (sin tests)') {
      steps {
        dir('frontend') {
          sh "npm ci"
          sh "npm run build -- --configuration=production"
        }
        withSonarQubeEnv("${SONAR_SERVER}") {
          sh """
            cd frontend
            sonar-scanner \
              -Dsonar.projectKey=${SONAR_KEY_FE} \
              -Dsonar.projectVersion=${BUILD_VER}
          """
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
  }

  post {
    always { echo "Build URL: ${env.BUILD_URL}" }
  }
}
