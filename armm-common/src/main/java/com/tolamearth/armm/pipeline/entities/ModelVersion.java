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

@Entity(name="model_version")
@Introspected
public class ModelVersion {

    @GeneratedValue
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "name")
    private String name;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "is_primary")
    private Boolean isPrimary;
    @Column(name = "endpoint_id")
    private Long endpointId;
    @Column(name = "created_ts")
    private Long createdTs;
    @Column(name = "updated_ts\t")
    private Long updatedTs	;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "model_id", referencedColumnName = "id")
    private Model model;

    public ModelVersion() {
    }

    public ModelVersion(UUID id, String name, Boolean isActive, Boolean isPrimary, Long endpointId, Long createdTs, Long updatedTs, Model model) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.isPrimary = isPrimary;
        this.endpointId = endpointId;
        this.createdTs = createdTs;
        this.updatedTs = updatedTs;
        this.model = model;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }

    public Long getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(Long endpointId) {
        this.endpointId = endpointId;
    }

    public Long getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(Long createdTs) {
        this.createdTs = createdTs;
    }

    public Long getUpdatedTs() {
        return updatedTs;
    }

    public void setUpdatedTs(Long updatedTs) {
        this.updatedTs = updatedTs;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "ModelVersion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                ", isPrimary=" + isPrimary +
                ", endpointId=" + endpointId +
                ", createdTs=" + createdTs +
                ", updatedTs=" + updatedTs +
                ", model=" + model +
                '}';
    }
}
