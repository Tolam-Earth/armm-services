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

import javax.persistence.*;
import java.util.*;

@Entity(name="nft_pool_group")
@Introspected
public class PoolGroup {

    @JsonProperty("id")
    @GeneratedValue
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "pool_id", referencedColumnName = "id")
    private PoolMeta poolMeta;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "model_result_id", referencedColumnName = "id")
    private ModelResult modelResult;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    public PoolGroup() {
    }

    public PoolGroup(UUID id, PoolMeta poolMeta, ModelResult modelResult, Boolean isPrimary) {
        this.id = id;
        this.poolMeta = poolMeta;
        this.modelResult = modelResult;
        this.isPrimary = isPrimary;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PoolMeta getPoolMeta() {
        return poolMeta;
    }

    public void setPoolMeta(PoolMeta poolMeta) {
        this.poolMeta = poolMeta;
    }

    public ModelResult getModelResult() {
        return modelResult;
    }

    public void setModelResult(ModelResult modelResult) {
        this.modelResult = modelResult;
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }
}


