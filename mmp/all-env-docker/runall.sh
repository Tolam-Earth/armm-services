$ docker-compose -f docker-compose-base.yml up
$ cd project root
$ GOOGLE_CLOUD_PROJECT=hem-integration-services PUBSUB_EMULATOR_HOST=172.20.0.16:8681 TRANSFORMER_NODE=http://localhost:8000 \
  CLASSIFICATION_NODE=http://localhost:8010 HISTORY_NODE=http://localhost:8089 PRICING_NODE=http://localhost:8081 DROP_TABLES=true OCI_LOG_LEVEL=DEBUG \
  ./gradlew :orchestrator-daemon:run
$ PRICEMODEL_NODE=http://localhost:8002 OCI_LOG_LEVEL=DEBUG ./gradlew :pricing-daemon:run
$ GOOGLE_CLOUD_PROJECT=hem-integration-services PUBSUB_EMULATOR_HOST=172.20.0.16:8681 INTEGRATION_NODE=http://localhost:8089 OCI_LOG_LEVEL=DEBUG \
  ./gradlew :trader-evaluator:run



$ cd project root
$ ./gradlew dockerBuild
$ cd mmp/all-env-docker
$ docker-compose -f docker-compose-local.yml up





$ cd integration-services-project-root
$ HEDERA_NETWORK=testnet PUBSUB_EMULATOR_HOST=172.20.0.16:8681 API_HEM_MARKETPLACE_CLIENT_URL=<tbd> LEDGER_WORKS_API_KEY=<user-specific> \
  HEM_OFFSETS_CONTRACT_ID=<user-specific> TOKEN_DISCOVERY_TOKEN_IDS=<user-specific> BUYER_OPERATOR_ID=<user-specific> BUYER_PRIVATE_KEY=<user-specific> \
  ./gradlew :integration-orchestrator:run