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

package com.tolamearth.armm.trader.entities;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity(name="rules")
public class Rules {
    @GeneratedValue
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "file_name")
    private String fileName;
    @Column(name = "created_ts")
    private Date createdAt;

    @Column(name = "rule_name")
    private String ruleName;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "rule", nullable = false)
    private byte[] ruleContent;

    public Rules() {
    }

    public Rules(UUID id, String fileName, Date createdAt, String ruleName, byte[] ruleContent) {
        this.id = id;
        this.fileName = fileName;
        this.createdAt = createdAt;
        this.ruleName = ruleName;
        this.ruleContent = ruleContent;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(byte[] ruleContent) {
        this.ruleContent = ruleContent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    @Override
    public String toString() {
        return "Rules{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", createdAt=" + createdAt +
                ", ruleName='" + ruleName + '\'' +
                ", ruleContent=" + ruleContent +
                '}';
    }
}
