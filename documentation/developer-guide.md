## Developer Guide

This documentation is for developers to get started on the hem-armm-services micronaut application local development. 

### Developer Tool Installations 

1. Install Java 17 and assign the path to JAVA_HOME var
2. Use IntelliJ
3. Install Postgres SQL and only choose pgAdmin and CLI tools, so we can use for a db client
4. Install DBeaver for visual DB client to see data

## Docker Containers

We will use some docker container for local application development. Run the docker command below. Make sure no other installations
for these applications are present locally. Start from the root directory to run the commands

1. postgres docker container with exposed port
   1. docker-compose -f mmp/docker-compose-postgresql.yml up -d
2. Google pub/sub emulator
   1. docker run -d --rm -it -p 8681:8681 -e PUBSUB_PROJECT1=hem-integration-services,nft_details:nft_details_listener,classifier:class_listener thekevjames/gcloud-pubsub-emulator:latest
3. nft transformer package
   1. docker run -d --rm -p 8000:8080 -it --tty --workdir /app --name nft-transformer nft-transformer

## Micronaut Application Commands

### Run and Test Applicatinos
These are commands to run tests and the application from the command line. 

1. Orchestrator Daemon commands
   1. `./gradlew :orchestrator-daemon:test` - runs on h2 database in mem
   2. `./gradlew :orchestrator-daemon:dockerizedTest` - runs tests which spin up docker container
   3. `./gradlew :orchestrator-daemon:run` - runs with docker postgres database and connect to pubsub and other external points
2. Pricing Daemon commands
   1. `./gradlew :pricing-daemon:test` - runs on h2 database in mem   
   2. `./gradlew :pricing-daemon:dockerizedTest` - runs tests which spin up docker container
   3. `./gradlew :pricing-daemon:run`
3. Trader Core Commands
   1. `./gradlew :trader-evaluator:test` - all tests
   2. `./gradlew :trader-evaluator:dockerizedTest` - runs tests which spin up docker container
   3. `./gradlew :trader-evaluator:run`

### Running without Dockerized services (except PubSub & Postgres)

From the Integration-Services repo:
```
$ cd integration-services-project-root
$ HEDERA_NETWORK=testnet PUBSUB_EMULATOR_HOST=172.20.0.16:8681 API_HEM_MARKETPLACE_CLIENT_URL=<tbd> LEDGER_WORKS_API_KEY=<user-specific> \
HEM_OFFSETS_CONTRACT_ID=<user-specific> TOKEN_DISCOVERY_TOKEN_IDS=<user-specific> BUYER_OPERATOR_ID=<user-specific> BUYER_PRIVATE_KEY=<user-specific> \
$ ./gradlew :integration-orchestrator:run
```

From this repo
```
GOOGLE_CLOUD_PROJECT=hem-integration-services PUBSUB_EMULATOR_HOST=172.20.0.16:8681 TRANSFORMER_NODE=http://localhost:8000 \
CLASSIFICATION_NODE=http://localhost:8010 HISTORY_NODE=http://localhost:8089 PRICING_NODE=http://localhost:8081 DROP_TABLES=true OCI_LOG_LEVEL=DEBUG \
./gradlew :orchestrator-daemon:run
$ PRICEMODEL_NODE=http://localhost:8002 OCI_LOG_LEVEL=DEBUG ./gradlew :pricing-daemon:run
$ GOOGLE_CLOUD_PROJECT=hem-integration-services PUBSUB_EMULATOR_HOST=172.20.0.16:8681 INTEGRATION_NODE=http://localhost:8089 OCI_LOG_LEVEL=DEBUG \
./gradlew :trader-evaluator:run
```

### Building & Running Docker images with Docker Compose

To Build
```
cd <project root>
./gradlew dockerBuild
```

Running:
```
cd mmp/all-env-docker
docker-compose -f docker-compose-local.yml up
```

Note: to run `docker-compose-local.yml` you must have already built the python images from the ARMM-Data-Engineering repo: 
* `nft-classifier`
* `nft-transformer`
* `nft-pricing`

And the `integration-orchestrator` from the Integration-Services repo

## Database Client Setup

1. Open DBeaver and connect to the database using connection details in `application.yml` for armm database
   1. see the tables that are created by liquibase startup scripts
2. Open SQLShell (psql) command line
   1. enter the connection details to connect to the database
   2. run `\l` to list the databases
   3. run `\d` to list the tables
   4. run any other psql commands to see and query the database
