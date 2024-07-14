template = '''
apiVersion: v1
kind: Pod
metadata:
  labels:
    run: terraform
  name: terraform
spec:
  containers:
  - command:
    - sleep
    - "3600"
    image: hashicorp/terraform
    name: terraform
    '''

podTemplate(cloud: 'kubernetes', label: 'terraform', yaml: template) {
    node ("terraform") {
        container ("terraform") {
    stage ("Checkout  SCM") {
        git branch: 'main', url: 'https://github.com/Munara241/jenkins-terraform.git'
    }

    stage ("Init") {
        sh "terraform init"
    }

    withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
    stage ("Apply") {
        sh "terraform apply --auto-approve"
    }
}
    
  }
}
}

