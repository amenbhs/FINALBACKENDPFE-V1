pipeline {
    agent any

    tools {
        maven 'Maven'   // Doit être configuré dans Jenkins (Manage Jenkins > Tools)
    }

    environment {
        TRIVY_TIMEOUT = "10m"
    }

    triggers {
        // Se déclenche automatiquement quand tu push sur GitHub
        // (nécessite le webhook GitHub configuré — voir instructions plus bas)
        githubPush()
    }

    stages {

        // ===========================
        // 1. CLONE LE REPO
        // ===========================
        stage('Clone Repository') {
            steps {
                cleanWs()
                git branch: 'main',
                    url: 'https://github.com/amenbhs/FINALBACKENDPFE-V1'
            }
        }

        // ===========================
        // 2. BUILD MAVEN (compile le projet)
        // ===========================
        stage('Build Project') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        // ===========================
        // 3. TESTS UNITAIRES
        // ===========================
        stage('Run Tests') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        // ===========================
        // 4. VÉRIFICATION DES DÉPENDANCES POM.XML
        // ===========================
        stage('Dependency Check') {
            steps {
                bat 'mvn dependency:tree'
                bat 'mvn versions:display-dependency-updates'
            }
        }

        // ===========================
        // 5. TRIVY - SCAN DE SÉCURITÉ (échoue si CRITICAL)
        // ===========================
        stage('Trivy Scan - Fail on Critical') {
            steps {
                bat """
                trivy fs . ^
                --scanners vuln ^
                --severity CRITICAL ^
                --timeout %TRIVY_TIMEOUT% ^
                --exit-code 1
                """
            }
        }

        // ===========================
        // 6. TRIVY - TÉLÉCHARGER LE TEMPLATE HTML
        // ===========================
        stage('Download Trivy HTML Template') {
            steps {
                bat 'curl -L -o html.tpl https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/html.tpl'
            }
        }

        // ===========================
        // 7. TRIVY - GÉNÉRER LE RAPPORT HTML
        // ===========================
        stage('Generate Trivy HTML Report') {
            steps {
                bat """
                trivy fs . ^
                --scanners vuln ^
                --timeout %TRIVY_TIMEOUT% ^
                --format template ^
                --template "@html.tpl" ^
                -o trivy-report.html
                """
            }
        }

        // ===========================
        // 8. ARCHIVER LES RÉSULTATS
        // ===========================
        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'trivy-report.html', fingerprint: true
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo '============================================'
            echo '✅ BUILD & SECURITY SCAN SUCCESSFUL'
            echo '✅ Aucune vulnérabilité CRITICAL détectée'
            echo '✅ Tests passés avec succès'
            echo '✅ Rapport Trivy généré: trivy-report.html'
            echo '============================================'
        }
        failure {
            echo '============================================'
            echo '❌ PIPELINE ÉCHOUÉ'
            echo '❌ Vulnérabilités CRITICAL détectées ou tests échoués'
            echo '============================================'
        }
        always {
            echo "Pipeline terminé - Build #${BUILD_NUMBER}"
        }
    }
}
