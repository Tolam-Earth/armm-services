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
import java.util.UUID;

@Entity(name="model_result")
@Introspected
public class ModelResult {

    @GeneratedValue
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "min_price")
    private Long minPrice;
    @Column(name = "max_price")
    private Long maxPrice;
    @Column(name = "timestamp_sec")
    private Long timestampSec;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "model_version_id", referencedColumnName = "id")
    private ModelVersion modelVersion;

    public ModelResult() {
    }

    public ModelResult(UUID id, Long minPrice, Long maxPrice, Long timestampSec, ModelVersion modelVersion) {
        this.id = id;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.timestampSec = timestampSec;
        this.modelVersion = modelVersion;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Long minPrice) {
        this.minPrice = minPrice;
    }

    public Long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Long getTimestampSec() {
        return timestampSec;
    }

    public void setTimestampSec(Long timestampSec) {
        this.timestampSec = timestampSec;
    }

    public ModelVersion getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(ModelVersion modelVersion) {
        this.modelVersion = modelVersion;
    }

    @Override
    public String toString() {
        return "ModelResult{" +
                "id=" + id +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", timestampSec=" + timestampSec +
                ", modelVersion=" + modelVersion +
                '}';
    }
}
