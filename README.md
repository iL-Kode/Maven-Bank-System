# SOA Service

## Getting started


### Prerequisites
Before you can run each service, you have to install the following dependencies:
- docker
- maven
- cargo
- go

## Usage
The project is split into multiple services, each requiring specific setup before use.

### Kafka
```bash
cd kafka-test

# Start docker
sudo dockerd
docker-compose up -d

# Create topics
docker exec -it kafka-test-kafka-1 kafka-topics --create \
  --bootstrap-server localhost:8090 \
  --topic rest-requests-log \
  --partitions 3 \
  --replication-factor 1

docker exec -it kafka-test-kafka-1 kafka-topics --create \
  --bootstrap-server localhost:8090 \
  --topic transactions-log \
  --partitions 3 \
  --replication-factor 1

# Start consumer
docker exec -it kafka-test-kafka-1 kafka-console-consumer \
  --bootstrap-server localhost:8090 \
  --transactions-log \
  --from-beginning
  

docker exec -it kafka-test-kafka-1 kafka-console-consumer \
  --bootstrap-server localhost:8090 \
  --rest-requests-log \
  --from-beginning
```

### Person API
```bash
cd person-service
cargo run
```

### Bank API
```bash
cd bank-service
go run server.go
```

### Springboot
``` bash
cd account-project/account-rest
mvn clean spring-boot:run
```
> 📘 **Info**
>
> Spring-boot needs to be restarted before running xfinal-test again

### Jacoco
Generating a unit test coverage report is optional and can be done with:
``` bash
cd account-project
mvn -pl '!account-xfinal-test' -am -DskipITs=true clean verify -DskipTests=false -e
```
When the verify phase has been run, you can view the coverage-report by opening the generated file at `{Project root}/aggregate-report/target/site/jacoco-aggregate/index.html` with your browser.
