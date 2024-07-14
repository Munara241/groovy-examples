properties([
    parameters([
        string(description: 'Enter IP', name: 'IP', trim: true)
        ])
        ])

node {
    withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master', keyFileVariable: 'SSH_PRIVATE_KEY', usernameVariable: 'SSH_USER')]) {
    
    stage("Install Java") {
        sh "ssh -o StrictHostKeyChecking=False -i $SSH_PRIVATE_KEY $SSH_USER@${params.IP} yum install java-11-openjdk -y"
    }
    }
}

