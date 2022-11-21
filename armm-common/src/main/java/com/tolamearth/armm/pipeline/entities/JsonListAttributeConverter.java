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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.ConversionContext;
import io.micronaut.data.model.runtime.convert.AttributeConverter;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class JsonListAttributeConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper om;

    public JsonListAttributeConverter(ObjectMapper om) {
        this.om = om;
    }

    @Override
    public String convertToPersistedValue(@Nullable List<String> entityValue, @NonNull ConversionContext context) {
        if (null == entityValue) return null;
        try {
            return om.writeValueAsString(entityValue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> convertToEntityValue(@Nullable String persistedValue, @NonNull ConversionContext context) {
        if (null == persistedValue) return null;
        TypeFactory typeFactory = om.getTypeFactory();
        try {
            return om.readValue(persistedValue, typeFactory.constructCollectionType(List.class, String.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
