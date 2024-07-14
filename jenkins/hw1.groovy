def template = '''
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
    
properties([
    parameters([
        choice(choices: ['apply', 'destroy'], description: 'Pick the action', name: 'action'),
        choice(choices: ['us-east-1', 'us-east-2', 'us-west-1', 'us-west-2'], description: 'Pick the AWS region', name: 'region'),
        choice(choices: ['us-east-1a', 'us-east-1b', 'us-east-1c', 'us-east-2a', 'us-east-2b', 'us-east-2c', 'us-west-1a', 'us-west-1b', 'us-west-1c', 'us-west-2a', 'us-west-2b', 'us-west-2c'], description: 'Pick the AWS Availability Zone', name: 'az'),
        string(description: 'Enter the AMI ID', name: 'ami_id', defaultValue: 'ami-08be1e3e6c338b037')
    ])
])

podTemplate(cloud: 'kubernetes', label: 'terraform', yaml: template) {
    node("terraform") {
        container("terraform") {
            stage("Checkout SCM") {
                git branch: 'main', url: 'https://github.com/Munara241/jenkins-terraform.git'
            }

    withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                stage("Init") {
                    sh "terraform init -backend-config='key=${params.region}/${params.az}/terraform.tfstate'"
                }

                def tfvars = """
                region = "${params.region}"
                ami_id = "${params.ami_id}"
                az = "${params.az}"
                """
                writeFile file: 'hello.tfvars', text: tfvars

                if (params.action == "apply") {
                    stage("Apply") {
                        sh "terraform apply -var-file hello.tfvars --auto-approve"
                    }
                } else {
                    stage("Destroy") {
                        sh "terraform destroy -var-file hello.tfvars --auto-approve"
                    }
                }
            }
        }
    }
}
