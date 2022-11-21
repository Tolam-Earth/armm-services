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

import io.micronaut.core.annotation.Introspected;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity(name="pricing_request")
@Introspected
public class PricingRequest {

    @GeneratedValue
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "request_user")
    private String requestUser;
    @Column(name = "timestamp_sec")
    private Long timestampSec = new Date().getTime();

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="request_id")
    private List<RequestNft> nfts = new ArrayList<>();

    public PricingRequest() {
    }

    public PricingRequest(UUID id, String requestUser, Long timestampSec, List<RequestNft> nfts) {
        this.id = id;
        this.requestUser = requestUser;
        this.timestampSec = timestampSec;
        this.nfts = nfts;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRequestUser() {
        return requestUser;
    }

    public void setRequestUser(String requestUser) {
        this.requestUser = requestUser;
    }

    public Long getTimestampSec() {
        return timestampSec;
    }

    public void setTimestampSec(Long timestampSec) {
        this.timestampSec = timestampSec;
    }

    public List<RequestNft> getNfts() {
        return nfts;
    }

    public void setNfts(List<RequestNft> nfts) {
        this.nfts = nfts;
    }

    @Override
    public String toString() {
        return "PricingRequest{" +
                "id=" + id +
                ", requestUser='" + requestUser + '\'' +
                ", timestampSec=" + timestampSec +
                ", nfts=" + nfts +
                '}';
    }
}
