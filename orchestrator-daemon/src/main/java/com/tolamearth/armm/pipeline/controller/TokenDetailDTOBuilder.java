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

import java.time.LocalDateTime;

public class TokenDetailDTOBuilder {
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
    private Long vintageYear;
    private String owner;
    private String newOwner;

    public TokenDetailDTOBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public TokenDetailDTOBuilder setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }

    public TokenDetailDTOBuilder setTokenId(String tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    public TokenDetailDTOBuilder setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public TokenDetailDTOBuilder setMintingOwner(String mintingOwner) {
        this.mintingOwner = mintingOwner;
        return this;
    }

    public TokenDetailDTOBuilder setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public TokenDetailDTOBuilder setTransactionMemo(String transactionMemo) {
        this.transactionMemo = transactionMemo;
        return this;
    }

    public TokenDetailDTOBuilder setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
        return this;
    }

    public TokenDetailDTOBuilder setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
        return this;
    }

    public TokenDetailDTOBuilder setProjectType(String projectType) {
        this.projectType = projectType;
        return this;
    }

    public TokenDetailDTOBuilder setQuality(Long quality) {
        this.quality = quality;
        return this;
    }

    public TokenDetailDTOBuilder setCountry(String country) {
        this.country = country;
        return this;
    }

    public TokenDetailDTOBuilder setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public TokenDetailDTOBuilder setGuardianId(String guardianId) {
        this.guardianId = guardianId;
        return this;
    }

    public TokenDetailDTOBuilder setFirstSubdivision(String firstSubdivision) {
        this.firstSubdivision = firstSubdivision;
        return this;
    }

    public TokenDetailDTOBuilder setListingPrice(Long listingPrice) {
        this.listingPrice = listingPrice;
        return this;
    }

    public TokenDetailDTOBuilder setPurchasePrice(Long purchasePrice) {
        this.purchasePrice = purchasePrice;
        return this;
    }

    public TokenDetailDTOBuilder setVintageYear(Long vintageYear) {
        this.vintageYear = vintageYear;
        return this;
    }

    public TokenDetailDTOBuilder setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public TokenDetailDTOBuilder setNewOwner(String newOwner) {
        this.newOwner = newOwner;
        return this;
    }

    public TokenDetailDTO createTokenDetailDTO() {
        return new TokenDetailDTO(id, msgType, tokenId, serialNumber, mintingOwner, transactionId, transactionMemo, transactionTime, projectCategory, projectType, quality, country, deviceId, guardianId, firstSubdivision, listingPrice, purchasePrice, vintageYear, owner, newOwner);
    }
}
