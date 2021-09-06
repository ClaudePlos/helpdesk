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

3. In shell put commane: \
docker build -t nap_tenders . << this dot is important 

4. docker images (you have list of images)
5. Run with ports and add to the list containers: \
docker run -p8181:8181 nap_tenders \
( now you have run and you can test on browser https://localhost:8181 If you close cmd in windows or shell the container will stop )
6. Now you can start container and close cmd: \
docker ps -a \
get ID \
docker container start ID \
and close cmd :) 
7. Close to coitarnet from new cmd: \
docker ps \
get ID \
docker container stop ID 

## Update project 
1.

# COMMANDS
docker images \
docker ps (only run container)
docker ps -a (all container, run and stop container)

