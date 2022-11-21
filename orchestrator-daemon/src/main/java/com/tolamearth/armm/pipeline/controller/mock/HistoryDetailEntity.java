/*
 * Copyright 2022 Tolam Earth
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tolamearth.armm.pipeline.controller.mock;


import io.micronaut.core.annotation.Introspected;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity(name="demo_history")
@Introspected
public class HistoryDetailEntity {

    @GeneratedValue
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "token_id")
    private String tokenId;
    @Column(name = "serial_number")
    private Long serialNumber;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "transaction_time")
    private Long transactionTime;
    @Column(name = "msg_type")
    private String msgType;
    @Column(name = "owner")
    private String owner;
    @Column(name = "price")
    private Long price;

    public HistoryDetailEntity() {
    }

    public HistoryDetailEntity(UUID id, String tokenId, Long serialNumber, String transactionId, Long transactionTime, String msgType, String owner, Long price) {
        this.id = id;
        this.tokenId = tokenId;
        this.serialNumber = serialNumber;
        this.transactionId = transactionId;
        this.transactionTime = transactionTime;
        this.msgType = msgType;
        this.owner = owner;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Long transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
