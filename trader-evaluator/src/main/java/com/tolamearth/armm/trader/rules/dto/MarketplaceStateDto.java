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

package com.tolamearth.armm.trader.rules.dto;

public class MarketplaceStateDto {
    private String projectCategory;
    private Long price;
    private boolean executePurchase;
    private Long armmMaxPrice;

    public MarketplaceStateDto() {
    }

    public MarketplaceStateDto(String projectCategory, Long price) {
        this.projectCategory = projectCategory;
        this.price = price;
    }

    public String getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getArmmMaxPrice() {
        return armmMaxPrice;
    }

    public void setArmmMaxPrice(Long armmMaxPrice) {
        this.armmMaxPrice = armmMaxPrice;
    }

    public boolean isExecutePurchaseBasedOnConfigRules() {
        return executePurchase;
    }

    public boolean isPriceLessThanEqualToArmmMaxPrice() {
        return this.price <= this.armmMaxPrice;
    }

    @Override
    public String toString() {
        return "MarketplaceStateDto{" +
                "projectCategory='" + projectCategory + '\'' +
                ", price=" + price +
                ", armmMaxPrice=" + armmMaxPrice +
                ", executePurchase=" + executePurchase +
                '}';
    }

    public void setExecutePurchase(Boolean executePurchase) {
        this.executePurchase = executePurchase;
    }
}
