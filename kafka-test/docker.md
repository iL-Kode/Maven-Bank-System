docker-compose up -d

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
Created topic rest-requests-log.
Created topic transactions-log.

  --bootstrap-server localhost:8090 \
  --topic rest-requests-log \
  --from-beginning


  docker-compose down -v
