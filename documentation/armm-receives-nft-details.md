# ARMM Services Receives NFT Details

The Armm Services subscribes to a topic which provides NFTs that are newly minted or NFTs that will be priced. 
Integration Services provides the NFTs, and the Orchestration Daemon component of ARMM Services reads the message and continues processing.

A component diagram illustrates the components and topics for ARMM Service. Please view [ARMM_services_component_diagram.drawio](../diagrams/drawio/ARMM_services_component_diagram.drawio)

## Technical Details

This document describes at a high level the interfaces of the Orchestrator Daemon which process the NFT Details.
In the event that the NFT Details get amended with new data, data is deleted, or the structure changes, a developer
can reference these portions of the appropriate portions code that serve as interfaces.

## Subscribe To Receive NFT Details

The Orchestrator Daemon listens on a topic to receive NFT Details. `DataPiplineListener.java` is the PubSub subscriber which
processes the message. It is a serialized byte array. It is serialized from the `ArmmEvent.java`, which is a Google Protocol Buffer.
Essentially, on arrival the NFT Details is separated where each token is a separate message, and is then send on to the classification topic.

The interface for the subscription is on the pubsub project `hem-integration-services`.
The publisher is defined as `nft_details`
The subscriber is defined as `nft_details_listener`

## Subscribe to Classification Topic

The Orchestrator Daemon also listens to the classification topic for each individual NFT. This was done for asynchronization purposes.
At this stage the data is also a serialized byte array from `ArmmEvent.Transaction.java`.  The entire process is initiated on each transaction
with `DataPipelineService.java`

The interface for the subscription is on the pubsub project `hem-integration-services`.
The publisher is defined as `classifier`
The subscriber is defined as `class_listener`

## Data Pipeline Service processing

`DataPipelineService.java` serves as the processing class to send NFT Transaction to the History, Transformer, and Classification Nodes.

The endpoints for these are the following:

- `/integration/v1/offsets/{token_id},{serial_num}/transactions`
- `/arm/v1/data_transformer`
- `/arm/v1/classification/scheduled_pool`

