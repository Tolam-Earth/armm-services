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

package com.tolamearth.armm.pipeline.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tolamearth.armm.pipeline.entities.Summary;
import com.tolamearth.armm.pipeline.entities.TokenAttributesSummary;
import com.tolamearth.armm.pipeline.repository.SummaryRepository;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.LockModeType;

@Controller("/arm/v1/orchestrator")
public class OrchestratorController {
    private static final Logger log = LoggerFactory.getLogger(OrchestratorController.class);

    private final SummaryRepository summaryRepository;
    private final ObjectMapper objectMapper;

    public OrchestratorController(SummaryRepository summaryRepository, ObjectMapper objectMapper) {
        this.summaryRepository = summaryRepository;
        this.objectMapper = objectMapper;
    }

    @ExecuteOn(TaskExecutors.IO)
    @Get("/summary")
    public TokenAttributesSummary attributesSummary(@QueryValue("name") String name) throws JsonProcessingException {
        Summary summary = summaryRepository.findBySummaryName(name, LockModeType.OPTIMISTIC);
        if (summary != null) {
            return objectMapper.readValue(summary.getSummary(), TokenAttributesSummary.class);
        }
        return new TokenAttributesSummary();
    }

}
