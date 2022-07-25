docker build . --tag mihirmehta2/eth-connector
docker rmi -f mihirmehta2/eth-connector:0.1
docker image tag mihirmehta2/eth-connector:latest mihirmehta2/eth-connector:0.1
docker push mihirmehta2/eth-connector:0.1
docker images
docker run -t mihirmehta2/eth-connector:0.1 -p 8080:8080
