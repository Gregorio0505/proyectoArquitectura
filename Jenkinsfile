pipeline {
  agent any

  environment {
    JAVA_HOME    = tool name: 'JDK17', type: 'hudson.model.JDK'
    MAVEN_HOME   = tool name: 'Maven3.8.7', type: 'hudson.tasks.Maven$MavenInstallation'
    NODEJS_HOME  = tool name: 'Node23.7.0', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
    SCANNER_HOME = tool name: 'SonarScanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
    PATH         = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${NODEJS_HOME}/bin:${SCANNER_HOME}/bin:${env.PATH}"
    SONAR_SERVER = 'SonarQube'
  }

  options { timestamps() }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Verificar Herramientas Disponibles') {
      steps {
        script {
          echo "=== Verificando herramientas disponibles ==="
          
          // Listar todas las herramientas disponibles
          def tools = Jenkins.instance.getExtensionList('hudson.tools.ToolDescriptor')
          tools.each { tool ->
            echo "Herramienta disponible: ${tool.getDisplayName()}"
          }
          
          // Verificar herramientas específicas
          try {
            def javaTool = tool name: 'JDK17', type: 'hudson.model.JDK'
            echo "✅ JDK17 encontrado: ${javaTool}"
          } catch (Exception e) {
            echo "❌ JDK17 no encontrado: ${e.message}"
          }
          
          try {
            def mavenTool = tool name: 'Maven3.8.7', type: 'hudson.tasks.Maven$MavenInstallation'
            echo "✅ Maven3.8.7 encontrado: ${mavenTool}"
          } catch (Exception e) {
            echo "❌ Maven3.8.7 no encontrado: ${e.message}"
          }
          
          try {
            def nodeTool = tool name: 'NodeJS', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
            echo "✅ NodeJS encontrado: ${nodeTool}"
          } catch (Exception e) {
            echo "❌ NodeJS no encontrado: ${e.message}"
          }
          
          try {
            def scannerTool = tool name: 'SonarScanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
            echo "✅ SonarScanner encontrado: ${scannerTool}"
          } catch (Exception e) {
            echo "❌ SonarScanner no encontrado: ${e.message}"
          }
          
          echo "=== Fin verificación de herramientas ==="
        }
      }
    }

    stage('Verificar Herramientas') {
      steps {
        script {
          // Verificar que las herramientas estén disponibles
          if (!env.JAVA_HOME) {
            error "ERROR: JDK17 no está configurado en Jenkins. Verifica que la herramienta 'JDK17' esté instalada."
          }
          if (!env.MAVEN_HOME) {
            error "ERROR: Maven3.8.7 no está configurado en Jenkins. Verifica que la herramienta 'Maven3.8.7' esté instalada."
          }
          if (!env.NODEJS_HOME) {
            error "ERROR: NodeJS no está configurado en Jenkins. Verifica que la herramienta 'NodeJS' esté instalada."
          }
          if (!env.SCANNER_HOME) {
            error "ERROR: SonarScanner no está configurado en Jenkins. Verifica que la herramienta 'SonarScanner' esté instalada."
          }
          
          echo "=== Herramientas configuradas ==="
          echo "JAVA_HOME: ${env.JAVA_HOME}"
          echo "MAVEN_HOME: ${env.MAVEN_HOME}"
          echo "NODEJS_HOME: ${env.NODEJS_HOME}"
          echo "SCANNER_HOME: ${env.SCANNER_HOME}"
          echo "=== Fin verificación de herramientas ==="
        }
      }
    }

    stage('Verificar Java y Maven') {
      steps {
        script {
          sh """
            echo "=== Verificando versiones ==="
            echo "JAVA_HOME: ${env.JAVA_HOME}"
            echo "MAVEN_HOME: ${env.MAVEN_HOME}"
            ${env.JAVA_HOME}/bin/java -version
            ${env.MAVEN_HOME}/bin/mvn -version
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
              export JAVA_HOME="${env.JAVA_HOME}"
              export PATH="${env.JAVA_HOME}/bin:${env.MAVEN_HOME}/bin:${env.PATH}"
              ${env.MAVEN_HOME}/bin/mvn -B clean verify sonar:sonar \
                -Dsonar.projectKey=${env.SONAR_KEY_BE} \
                -Dsonar.projectVersion=${env.BUILD_VER} \
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
          sh """
            export PATH="${env.NODEJS_HOME}/bin:${env.PATH}"
            npm ci
            npm run build -- --configuration=production
          """
        }
        withSonarQubeEnv("${SONAR_SERVER}") {
          sh """
            export PATH="${env.SCANNER_HOME}/bin:${env.PATH}"
            cd frontend
            sonar-scanner \
              -Dsonar.projectKey=${env.SONAR_KEY_FE} \
              -Dsonar.projectVersion=${env.BUILD_VER}
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
