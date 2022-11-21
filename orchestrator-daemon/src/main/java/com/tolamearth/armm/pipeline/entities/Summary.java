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
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity(name="summaries")
@Introspected
public class Summary {

    @GeneratedValue
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Version
    private Integer version;
    @Column(name = "summary_name")
    private String summaryName;

    @Column(name = "summary")
    private String summary; // JSON representation of the summaries
    @Column(name = "created_ts")
    @DateCreated
    private Date createdTs;
    @Column(name = "updated_ts")
    @DateUpdated
    private Date updatedTs;

    public Summary() {
    }

    public Summary(UUID id, Integer version, String summaryName, String summary) {
        this.id = id;
        this.version = version;
        this.summaryName = summaryName;
        this.summary = summary;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getSummaryName() {
        return summaryName;
    }

    public void setSummaryName(String summaryName) {
        this.summaryName = summaryName;
    }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
