# INSTALL
sudo yum install docker-ce docker-ce-cli containerd.io \
## Start:
sudo systemctl start docker \
sudo docker run hello-world \

## Build project
1. On the linux create new folder, for example deckerImages/nap_tenders ann put there nap_tenders-1.0-SNAPSHOT.jar
2. In this folder deckerImages/nap_tenders, create file name Dockerfile and add there: \
FROM openjdk:8-jdk-alpine \
VOLUME /tmp \
ADD nap_tenders-1.0-SNAPSHOT.jar app.jar \
ENTRYPOINT ["java","-jar","/app.jar"]

3. 

# COMMANDS
docker images \
docker ps -a

