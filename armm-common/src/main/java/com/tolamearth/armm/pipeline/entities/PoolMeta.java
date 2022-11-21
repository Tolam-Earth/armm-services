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
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity(name="pool_meta")
@Introspected
public class PoolMeta {

    @JsonProperty("id")
    @GeneratedValue
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @JsonProperty("version_pool")
    @Column(name = "version_pool", nullable = false)
    private String versionPool;
    @Column(name = "dt_pool")
    @JsonProperty("dt_pool") private Long dtPool;
    @Column(name = "name_pool")
    @JsonProperty("name_pool") private String namePool;
    @Column(name = "attributes_pool")
    //@MappedProperty(converter = JsonListAttributeConverter.class)
    @Convert(converter = JsonListConverter.class)
    @JsonProperty("attributes_pool") private List<String> attributesPool;
    @Column(name = "category_pool")
    @Convert(converter = JsonListConverter.class)
    @JsonProperty("category_pool") private List<String> categoryPool;
    @Column(name = "mean_pool")
    @Convert(converter = JsonListConverter.class)
    @JsonProperty("mean_pool") private List<BigDecimal> meanPool;
    @Column(name = "median_pool")
    @Convert(converter = JsonListConverter.class)
    @JsonProperty("median_pool") private List<BigDecimal> medianPool;
    @Column(name = "var_pool")
    @Convert(converter = JsonListConverter.class)
    @JsonProperty("var_pool") private List<BigDecimal> varPool;
    @Column(name = "stdev_pool")
    @Convert(converter = JsonListConverter.class)
    @JsonProperty("stdev_pool") private List<BigDecimal> stdevPool;
    @Column(name = "n_pool")
    @JsonProperty("n_pool") private Long nPool;
    @Column(name = "weight")
    @JsonProperty("weight") private double weight;

    public PoolMeta() {
    }

    public PoolMeta(UUID id, String versionPool, Long dtPool, String namePool, List<String> attributesPool, List<String> categoryPool, List<BigDecimal> meanPool, List<BigDecimal> medianPool, List<BigDecimal> varPool, List<BigDecimal> stdevPool,
                    Long nPool, double weight) {
        this.id = id;
        this.versionPool = versionPool;
        this.dtPool = dtPool;
        this.namePool = namePool;
        this.attributesPool = attributesPool;
        this.categoryPool = categoryPool;
        this.meanPool = meanPool;
        this.medianPool = medianPool;
        this.varPool = varPool;
        this.stdevPool = stdevPool;
        this.nPool = nPool;
        this.weight = weight;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getVersionPool() {
        return versionPool;
    }

    public void setVersionPool(String versionPool) {
        this.versionPool = versionPool;
    }

    public Long getDtPool() {
        return dtPool;
    }

    public void setDtPool(Long dtPool) {
        this.dtPool = dtPool;
    }

    public String getNamePool() {
        return namePool;
    }

    public void setNamePool(String namePool) {
        this.namePool = namePool;
    }

    public List<String> getAttributesPool() {
        return attributesPool;
    }

    public void setAttributesPool(List<String> attributesPool) {
        this.attributesPool = attributesPool;
    }

    public List<String> getCategoryPool() {
        return categoryPool;
    }

    public void setCategoryPool(List<String> categoryPool) {
        this.categoryPool = categoryPool;
    }

    public List<BigDecimal> getMeanPool() {
        return meanPool;
    }

    public void setMeanPool(List<BigDecimal> meanPool) {
        this.meanPool = meanPool;
    }

    public List<BigDecimal> getMedianPool() {
        return medianPool;
    }

    public void setMedianPool(List<BigDecimal> medianPool) {
        this.medianPool = medianPool;
    }

    public List<BigDecimal> getVarPool() {
        return varPool;
    }

    public void setVarPool(List<BigDecimal> varPool) {
        this.varPool = varPool;
    }

    public List<BigDecimal> getStdevPool() {
        return stdevPool;
    }

    public void setStdevPool(List<BigDecimal> stdevPool) {
        this.stdevPool = stdevPool;
    }

    public Long getnPool() {
        return nPool;
    }

    public void setnPool(Long nPool) {
        this.nPool = nPool;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "PoolMeta{" +
                "id=" + id +
                ", versionPool='" + versionPool + '\'' +
                ", dtPool='" + dtPool + '\'' +
                ", namePool='" + namePool + '\'' +
                ", attributesPool=" + attributesPool +
                ", categoryPool=" + categoryPool +
                ", meanPool=" + meanPool +
                ", medianPool=" + medianPool +
                ", varPool=" + varPool +
                ", stdevPool=" + stdevPool +
                ", nPool=" + nPool +
                ", weight=" + weight +
                '}';
    }
}


