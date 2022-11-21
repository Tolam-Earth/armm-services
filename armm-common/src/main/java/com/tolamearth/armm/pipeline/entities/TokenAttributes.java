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

package com.tolamearth.armm.pipeline.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@Entity(name="token_attributes")
@Introspected
public class TokenAttributes {

    @GeneratedValue
    @JsonProperty("id")
    @Column(name = "id", nullable = false)
    @Id
    private UUID id;

    @JsonProperty("nft_id")
    @Column(name = "nft_id", nullable = false)
    private String nftId; // this attribute is a concat of token_id and serial_number and is used for searches
    @JsonProperty("token_id")
    @Column(name = "token_id", nullable = false)
    private String tokenId;
    @JsonProperty("serial_number")
    @Column(name = "serial_number", nullable = false)
    private Long serialNumber;
    @JsonProperty("transaction_id")
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;
    @JsonProperty("transaction_time")
    @Column(name = "transaction_time", nullable = false)
    private Long transactionTime;
    @Column(name = "minting_owner")
    @JsonProperty("minting_owner") private String mintingOwner;

    @Column(name = "owner")
    @JsonProperty("owner") private String owner;
    @Column(name = "country")
    @JsonProperty("country") private String country;
    @Column(name = "first_subdivision")
    @JsonProperty("first_subdivision") private String firstSubdivision;
    @Column(name = "project_category")
    @JsonProperty("project_category") private String projectCategory;
    @Column(name = "project_type")
    @JsonProperty("project_type") private String projectType;
    @Column(name = "vintage_year")
    @JsonProperty("vintage_year") private Long vintageYear;
    @Column(name = "nft_age")
    @JsonProperty("nft_age") private Long nftAge;
    @Column(name = "num_owners")
    @JsonProperty("num_owners") private Integer numOwners;
    @Column(name = "avg_price")
    @JsonProperty("avg_price") private BigDecimal avgPrice;
    @Column(name = "last_price")
    @JsonProperty("last_price") private BigDecimal lastPrice;
    @Column(name = "num_price_chg")
    @JsonProperty("num_price_chg") private Integer numPriceChg;
    @Column(name = "nft_state")
    @JsonProperty("nft_state") private String nftState;
    @Column(name = "token_pool_id")
    @JsonProperty("token_pool_id") private UUID tokenPoolId;
    @Column(name = "name_pool")
    @JsonProperty("name_pool") private String namePool;
    @Column(name = "pooling_version")
    @JsonProperty("pooling_version") private String poolingVersion;
    @Column(name = "latitude")
    @JsonProperty("latitude") private BigDecimal latitude;
    @Column(name = "longitude")
    @JsonProperty("longitude") private BigDecimal longitude;

    public TokenAttributes() {
    }

    public TokenAttributes(UUID id, String nftId, String tokenId, Long serialNumber, String transactionId, Long transactionTime, String mintingOwner, String owner, String country, String firstSubdivision, String projectCategory, String projectType, Long vintageYear, Long nftAge, Integer numOwners, BigDecimal avgPrice, BigDecimal lastPrice, Integer numPriceChg, String nftState, UUID tokenPoolId, String namePool, String poolingVersion, BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.nftId = nftId;
        this.tokenId = tokenId;
        this.serialNumber = serialNumber;
        this.transactionId = transactionId;
        this.transactionTime = transactionTime;
        this.mintingOwner = mintingOwner;
        this.owner = owner;
        this.country = country;
        this.firstSubdivision = firstSubdivision;
        this.projectCategory = projectCategory;
        this.projectType = projectType;
        this.vintageYear = vintageYear;
        this.nftAge = nftAge;
        this.numOwners = numOwners;
        this.avgPrice = avgPrice;
        this.lastPrice = lastPrice;
        this.numPriceChg = numPriceChg;
        this.nftState = nftState;
        this.tokenPoolId = tokenPoolId;
        this.namePool = namePool;
        this.poolingVersion = poolingVersion;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getNftId() {
        return nftId;
    }

    public void setNftId(String nftId) {
        this.nftId = nftId;
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

    public String getMintingOwner() {
        return mintingOwner;
    }

    public void setMintingOwner(String mintingOwner) {
        this.mintingOwner = mintingOwner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFirstSubdivision() {
        return firstSubdivision;
    }

    public void setFirstSubdivision(String firstSubdivision) {
        this.firstSubdivision = firstSubdivision;
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

    public Long getVintageYear() {
        return vintageYear;
    }

    public void setVintageYear(Long vintageYear) {
        this.vintageYear = vintageYear;
    }

    public Long getNftAge() {
        return nftAge;
    }

    public void setNftAge(Long nftAge) {
        this.nftAge = nftAge;
    }

    public Integer getNumOwners() {
        return numOwners;
    }

    public void setNumOwners(Integer numOwners) {
        this.numOwners = numOwners;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Integer getNumPriceChg() {
        return numPriceChg;
    }

    public void setNumPriceChg(Integer numPriceChg) {
        this.numPriceChg = numPriceChg;
    }

    public String getNftState() {
        return nftState;
    }

    public void setNftState(String nftState) {
        this.nftState = nftState;
    }

    public UUID getTokenPoolId() {
        return tokenPoolId;
    }

    public void setTokenPoolId(UUID tokenPoolId) {
        this.tokenPoolId = tokenPoolId;
    }

    public String getNamePool() {
        return namePool;
    }

    public void setNamePool(String namePool) {
        this.namePool = namePool;
    }

    public String getPoolingVersion() {
        return poolingVersion;
    }

    public void setPoolingVersion(String poolingVersion) {
        this.poolingVersion = poolingVersion;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "TokenAttributes{" +
                "id=" + id +
                ", nftId='" + nftId + '\'' +
                ", tokenId='" + tokenId + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", transactionId=" + transactionId +
                ", transactionTime=" + transactionTime +
                ", mintingOwner='" + mintingOwner + '\'' +
                ", owner='" + owner + '\'' +
                ", country='" + country + '\'' +
                ", firstSubdivision='" + firstSubdivision + '\'' +
                ", projectCategory='" + projectCategory + '\'' +
                ", projectType='" + projectType + '\'' +
                ", vintageYear=" + vintageYear +
                ", nftAge=" + nftAge +
                ", numOwners=" + numOwners +
                ", avgPrice=" + avgPrice +
                ", lastPrice=" + lastPrice +
                ", numPriceChg=" + numPriceChg +
                ", nftState='" + nftState + '\'' +
                ", tokenPoolId=" + tokenPoolId +
                ", namePool='" + namePool + '\'' +
                ", poolingVersion='" + poolingVersion + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

