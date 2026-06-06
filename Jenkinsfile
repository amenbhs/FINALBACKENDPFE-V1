pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        TRIVY_TIMEOUT = "10m"
        TRIVY_STATUS  = 'OK'
    }

    triggers {
        githubPush()
    }

    stages {

        // ===========================
        // 1. CLONE LE REPO
        // ===========================
        stage('Clone Repository') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    extensions: [
                        [$class: 'CloneOption', timeout: 60, shallow: true, depth: 1],
                        [$class: 'CleanBeforeCheckout']
                    ],
                    userRemoteConfigs: [[url: 'https://github.com/amenbhs/FINALBACKENDPFE-V1']]
                ])
            }
        }

        // ===========================
        // 2. BUILD MAVEN
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
            }
        }

        // ===========================
        // 5. TRIVY - SCAN DE SÉCURITÉ (ne bloque PAS le pipeline)
        // ===========================
        stage('Trivy Scan - Check Critical') {
            steps {
                script {
                    def scanResult = bat(
                        script: """
                        trivy fs . ^
                        --scanners vuln ^
                        --severity CRITICAL ^
                        --timeout %TRIVY_TIMEOUT% ^
                        --exit-code 1
                        """,
                        returnStatus: true
                    )
                    if (scanResult != 0) {
                        echo '⚠️ Vulnérabilités CRITICAL détectées !'
                        env.TRIVY_STATUS = 'CRITICAL_FOUND'
                    } else {
                        echo '✅ Aucune vulnérabilité CRITICAL détectée'
                    }
                }
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
        // 7. TRIVY - GÉNÉRER LE RAPPORT HTML (TOUJOURS)
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
        // 8. ARCHIVER LES RÉSULTATS (TOUJOURS)
        // ===========================
        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'trivy-report.html', fingerprint: true
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: true
            }
        }

        // ===========================
        // 9. VÉRIFIER LE RÉSULTAT TRIVY
        // ===========================
        stage('Security Gate') {
            steps {
                script {
                    if (env.TRIVY_STATUS == 'CRITICAL_FOUND') {
                        error '❌ Pipeline échoué : Vulnérabilités CRITICAL détectées ! Consultez trivy-report.html'
                    }
                }
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
            echo '📄 Le rapport Trivy est quand même disponible dans Build Artifacts'
            echo '============================================'
        }
        always {
            echo "Pipeline terminé - Build #${BUILD_NUMBER}"
        }
    }
}
