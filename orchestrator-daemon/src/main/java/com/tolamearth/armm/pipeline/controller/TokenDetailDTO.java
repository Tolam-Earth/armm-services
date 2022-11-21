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

package com.tolamearth.armm.pipeline.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class TokenDetailDTO {
    private String id;
    private String msgType;
    private String tokenId;
    private Long serialNumber;
    private String mintingOwner;
    private String transactionId;
    private String transactionMemo;
    private LocalDateTime transactionTime;
    private String projectCategory;
    private String projectType;
    private Long quality;
    private String country;
    private String deviceId;
    private String guardianId;
    private String firstSubdivision;
    private Long listingPrice;
    private Long purchasePrice;
    @JsonProperty("vintage_year") private Long vintageYear;
    private String owner;
    private String newOwner;

    public TokenDetailDTO() {
    }

    public TokenDetailDTO(String id, String msgType, String tokenId, Long serialNumber, String mintingOwner, String transactionId, String transactionMemo, LocalDateTime transactionTime, String projectCategory, String projectType, Long quality, String country, String deviceId, String guardianId, String firstSubdivision, Long listingPrice, Long purchasePrice, Long vintageYear, String owner, String newOwner) {
        this.id = id;
        this.msgType = msgType;
        this.tokenId = tokenId;
        this.serialNumber = serialNumber;
        this.mintingOwner = mintingOwner;
        this.transactionId = transactionId;
        this.transactionMemo = transactionMemo;
        this.transactionTime = transactionTime;
        this.projectCategory = projectCategory;
        this.projectType = projectType;
        this.quality = quality;
        this.country = country;
        this.deviceId = deviceId;
        this.guardianId = guardianId;
        this.firstSubdivision = firstSubdivision;
        this.listingPrice = listingPrice;
        this.purchasePrice = purchasePrice;
        this.vintageYear = vintageYear;
        this.owner = owner;
        this.newOwner = newOwner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
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

    public String getMintingOwner() {
        return mintingOwner;
    }

    public void setMintingOwner(String mintingOwner) {
        this.mintingOwner = mintingOwner;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionMemo() {
        return transactionMemo;
    }

    public void setTransactionMemo(String transactionMemo) {
        this.transactionMemo = transactionMemo;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public Long getQuality() {
        return quality;
    }

    public void setQuality(Long quality) {
        this.quality = quality;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getGuardianId() {
        return guardianId;
    }

    public void setGuardianId(String guardianId) {
        this.guardianId = guardianId;
    }

    public String getFirstSubdivision() {
        return firstSubdivision;
    }

    public void setFirstSubdivision(String firstSubdivision) {
        this.firstSubdivision = firstSubdivision;
    }

    public Long getListingPrice() {
        return listingPrice;
    }

    public void setListingPrice(Long listingPrice) {
        this.listingPrice = listingPrice;
    }

    public Long getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Long purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Long getVintageYear() {
        return vintageYear;
    }

    public void setVintageYear(Long vintageYear) {
        this.vintageYear = vintageYear;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNewOwner() {
        return newOwner;
    }

    public void setNewOwner(String newOwner) {
        this.newOwner = newOwner;
    }
}
