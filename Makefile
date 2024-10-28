start:
	docker compose -f infra/kafka/common.yml -f infra/kafka/zookeeper.yml -f infra/kafka/kafka_cluster.yml up -d
	docker compose -f infra/es/docker-compose.yml up -d

stop:
	docker compose -f infra/kafka/common.yml -f infra/kafka/zookeeper.yml -f infra/kafka/kafka_cluster.yml down
	docker compose -f infra/es/docker-compose.yml down