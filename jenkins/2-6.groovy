template = '''
apiVersion: v1
kind: Pod
metadata:
  labels:
    run: kubernetes
  name: kubernetes
spec:
  serviceAccount: kubernetes
  containers:
  - image: kaizenacademy/command:2.0
    name: kubernetes
'''

podTemplate(cloud: 'kubernetes', label: 'kubernetes', yaml: template) {
    node ("kubernetes") {
        container ("kubernetes") {
    stage ("Checkout SCM"){
        sh """
        kubectl create deploy hello --image=munara241/apache:${params.tag} 
        """
    }
    }
        }
        }