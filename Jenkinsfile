// --- Utilidad: mapear rama -> ambiente de despliegue ---
def envFromBranch(String branch) {
  switch (branch) {
    case 'dev':    return 'dev'
    case 'uat':    return 'qa'     // tu rama uat despliega con docker-compose.qa.yml
    case 'main':
    case 'master': return 'prod'   // master → producción
    default:       return 'dev'    // feature/*, etc. → dev (puedes cambiar si quieres fallar)
  }
}

pipeline {
  agent any

  tools {
    jdk    'JDK17'      // Temurin 17 en Global Tool Configuration
    maven  'Maven3.9'   // Maven 3.9.x
    nodejs 'Node20'     // Node 20.x (tu comentario decía 18, pero tienes 'Node20')
  }

  environment {
    SONAR_SERVER = 'SonarQube' // nombre del server en Manage Jenkins → System
  }

  options { timestamps() }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Versions') {
      steps {
        sh '''
          echo "JAVA_HOME=$JAVA_HOME"
          java -version
          mvn -version
          node -v
          npm -v
        '''
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
              mvn -B clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar \
                -Dsonar.projectKey=${SONAR_KEY_BE} \
                -Dsonar.projectVersion=${BUILD_VER} \
                -Dsonar.host.url=${SONAR_HOST_URL} \
                -Dsonar.token=${SONAR_AUTH_TOKEN}
            """
          }
        }
      }
    }

    stage('Quality Gate (Backend)') {
      steps {
        timeout(time: 10, unit: 'MINUTES') {
          script {
            def qg = waitForQualityGate()  // requiere webhook Sonar→Jenkins
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
        script {
          def scannerHome = tool name: 'SonarScanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
          withSonarQubeEnv("${SONAR_SERVER}") {
            sh """
              cd frontend
              "${scannerHome}/bin/sonar-scanner" \
                -Dsonar.projectKey=${SONAR_KEY_FE} \
                -Dsonar.projectVersion=${BUILD_VER} \
                -Dsonar.host.url=${SONAR_HOST_URL} \
                -Dsonar.token=${SONAR_AUTH_TOKEN}
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

    // (Opcional) Smoke de SSH antes del deploy; puedes comentarlo tras validar una vez.
    // stage('Test SSH to host') {
    //   steps {
    //     sshagent(credentials: ['ssh-deploy-host']) {
    //       sh "ssh -o StrictHostKeyChecking=no gregorio05@host.docker.internal 'echo OK && whoami && docker ps | head'"
    //     }
    //   }
    // }

    stage('Deploy') {
      when {
        anyOf {
          branch 'dev'; branch 'uat'; branch 'main'; branch 'master'
        }
      }
      steps {
        script {
          // Si llegamos aquí es porque todo lo anterior pasó en verde.
          def envName   = envFromBranch(env.BRANCH_NAME)
          def DEPLOY_HOST = 'gregorio05@host.docker.internal'        // << cambia si prefieres IP fija
          def APP_DIR     = '/home/gregorio05/Documentos/proyectoArquitectura'

          sshagent(credentials: ['ssh-deploy-host']) {
            if (envName == 'prod') {
              input message: "¿Desplegar a PRODUCCIÓN ahora?", ok: "Desplegar"
            }

            sh """
              ssh -o StrictHostKeyChecking=no ${DEPLOY_HOST} '
                set -euo pipefail
                cd ${APP_DIR}
                git fetch --all
                git checkout ${env.BRANCH_NAME}
                git pull origin ${env.BRANCH_NAME}
                chmod +x scripts/deploy.sh
                ./scripts/deploy.sh ${envName}
              '
            """
          }
        }
      }
    }
  }

  post {
    success {
      emailext(
        subject: "[CI][DEPLOY OK] ${env.JOB_NAME} #${env.BUILD_NUMBER} (${env.BRANCH_NAME})",
        to: "josegregoriocoronelcolombo@gmail.com, jflores@unis.edu.gt",
        body: """\
Hola,

El pipeline y el despliegue terminaron **OK** en *${env.JOB_NAME}* #${env.BUILD_NUMBER} (rama ${env.BRANCH_NAME}).
Revisar consola: ${env.BUILD_URL}

"""
      )
    }
    failure {
      emailext(
        subject: "[CI][FALLO] ${env.JOB_NAME} #${env.BUILD_NUMBER} (${env.BRANCH_NAME})",
        to: "josegregoriocoronelcolombo@gmail.com, jflores@unis.edu.gt",
        body: """\
Hola,

El pipeline ha fallado en *${env.JOB_NAME}* #${env.BUILD_NUMBER} (${env.BRANCH_NAME}).
Revisar consola: ${env.BUILD_URL}

"""
      )
    }
    unstable {
      emailext(
        subject: "[CI][INESTABLE] ${env.JOB_NAME} #${env.BUILD_NUMBER} (${env.BRANCH_NAME})",
        to: "josegregoriocoronelcolombo@gmail.com, jflores@unis.edu.gt",
        body: """\
Hola,

El pipeline ha quedado inestable en *${env.JOB_NAME}* #${env.BUILD_NUMBER} (${env.BRANCH_NAME}).
Revisar consola: ${env.BUILD_URL}

"""
      )
    }
    always {
      echo "Build URL: ${env.BUILD_URL}"
    }
  }
}
