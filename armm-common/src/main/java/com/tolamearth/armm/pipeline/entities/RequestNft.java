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
import java.util.List;
import java.util.UUID;

@Entity(name="request_nft")
@Introspected
public class RequestNft {

    @GeneratedValue
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "nft_token_id")
    private String nftTokenId;
    @Column(name = "nft_serial_num")
    private Long nftSerialNum;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "request_groups",
            joinColumns = { @JoinColumn(name = "request_nft_id") },
            inverseJoinColumns = { @JoinColumn(name = "pool_group_id") })
    private List<PoolGroup> poolGroups = new ArrayList<>();

    public RequestNft() {
    }

    public RequestNft(UUID id, String nftTokenId, Long nftSerialNum) {
        this.id = id;
        this.nftTokenId = nftTokenId;
        this.nftSerialNum = nftSerialNum;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNftTokenId() {
        return nftTokenId;
    }

    public void setNftTokenId(String nftTokenId) {
        this.nftTokenId = nftTokenId;
    }

    public Long getNftSerialNum() {
        return nftSerialNum;
    }

    public void setNftSerialNum(Long nftSerialNum) {
        this.nftSerialNum = nftSerialNum;
    }

    public List<PoolGroup> getPoolGroups() {
        return poolGroups;
    }
}





