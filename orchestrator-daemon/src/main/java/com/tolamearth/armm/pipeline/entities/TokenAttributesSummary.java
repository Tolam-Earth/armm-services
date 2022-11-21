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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class TokenAttributesSummary {
    private Set<String> countries = new HashSet<>();
    private Set<String> firstSubdivisions = new HashSet<>();
    private Set<String> projectCategories = new HashSet<>();
    private Set<String> projectTypes = new HashSet<>();
    private Set<String> nftStates = new HashSet<>();
    private NumPair vintageYears = new NumPair(Long.MAX_VALUE, Long.MIN_VALUE);
    private NumPair numOwners = new NumPair(Long.MAX_VALUE, Long.MIN_VALUE);
    private NumPair nftAge = new NumPair(Long.MAX_VALUE, Long.MIN_VALUE);
    private NumPairBigDecimal avgPrice = new NumPairBigDecimal(BigDecimal.valueOf(Long.MAX_VALUE), BigDecimal.valueOf(Long.MIN_VALUE));
    private NumPair lastPrice = new NumPair(Long.MAX_VALUE, Long.MIN_VALUE);
    private NumPair numPriceChg = new NumPair(Long.MAX_VALUE, Long.MIN_VALUE);
    private NumPairBigDecimal latitude = new NumPairBigDecimal(BigDecimal.valueOf(Long.MAX_VALUE), BigDecimal.valueOf(Long.MIN_VALUE));
    private NumPairBigDecimal longitude = new NumPairBigDecimal(BigDecimal.valueOf(Long.MAX_VALUE), BigDecimal.valueOf(Long.MIN_VALUE));

    public NumPair getNftAge() {
        return nftAge;
    }

    public void setNftAge(NumPair nftAge) {
        this.nftAge = nftAge;
    }

    public NumPairBigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(NumPairBigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    public NumPair getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(NumPair lastPrice) {
        this.lastPrice = lastPrice;
    }

    public NumPair getNumPriceChg() {
        return numPriceChg;
    }

    public void setNumPriceChg(NumPair numPriceChg) {
        this.numPriceChg = numPriceChg;
    }

    public NumPairBigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(NumPairBigDecimal latitude) {
        this.latitude = latitude;
    }

    public NumPairBigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(NumPairBigDecimal longitude) {
        this.longitude = longitude;
    }

    public TokenAttributesSummary() {
    }

    public Set<String> getCountries() {
        return countries;
    }

    public Set<String> getFirstSubdivisions() {
        return firstSubdivisions;
    }

    public Set<String> getProjectCategories() {
        return projectCategories;
    }


    public Set<String> getProjectTypes() {
        return projectTypes;
    }


    public NumPair getVintageYears() {
        return vintageYears;
    }

    public void setVintageYears(NumPair vintageYears) {
        this.vintageYears = vintageYears;
    }

    public NumPair getNumOwners() {
        return numOwners;
    }

    public void setNumOwners(NumPair numOwners) {
        this.numOwners = numOwners;
    }

    public Set<String> getNftStates() {
        return nftStates;
    }


    public static class NumPair {
        private Long minValue;
        private Long maxValue;

        public NumPair() {
        }

        public NumPair(Long minValue, Long maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public Long getMinValue() {
            return minValue;
        }

        public Long getMaxValue() {
            return maxValue;
        }

    }
    public static class NumPairBigDecimal {
        private BigDecimal minValue;
        private BigDecimal maxValue;

        public NumPairBigDecimal() {
        }

        public NumPairBigDecimal(BigDecimal minValue, BigDecimal maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public BigDecimal getMinValue() {
            return minValue;
        }

        public BigDecimal getMaxValue() {
            return maxValue;
        }

    }

}
