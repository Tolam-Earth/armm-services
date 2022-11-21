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
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity(name="error_log")
@Introspected
public class ErrorLog {

    @GeneratedValue
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "nft_id")
    private String naftId;

    @Column(name = "error_msg")
    private String errorMsg;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "msg")
    private byte[] msg;

    @Column(name = "created_ts")
    @DateCreated
    private Date createdTs;

    public ErrorLog() {
    }

    public ErrorLog(UUID id, String naftId, String errorMsg, byte[] msg) {
        this.id = id;
        this.naftId = naftId;
        this.errorMsg = errorMsg;
        this.msg = msg;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNaftId() {
        return naftId;
    }

    public void setNaftId(String naftId) {
        this.naftId = naftId;
    }

    public byte[] getMsg() {
        return msg;
    }

    public void setMsg(byte[] msg) {
        this.msg = msg;
    }

    public Date getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(Date createdTs) {
        this.createdTs = createdTs;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
