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

package com.tolamearth.armm.pricing.controller;

import com.tolamearth.armm.pipeline.entities.PoolMeta;
import com.tolamearth.armm.pricing.repository.PoolMetaRepository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import javax.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller("/armm/v1/poolMeta")
public class PoolMetaController {

    private final PoolMetaRepository poolMetaRepository;

    public PoolMetaController(PoolMetaRepository poolMetaRepository) {
        this.poolMetaRepository = poolMetaRepository;
    }

    @ExecuteOn(TaskExecutors.IO)
    @Get("/{id}")
    public Optional<PoolMeta> findPoolMetaById(UUID id) {
        return poolMetaRepository.findById(id);
    }

    @ExecuteOn(TaskExecutors.IO)
    @Get("/list")
    public List<PoolMeta> list(@Valid Pageable pageable) {
        return poolMetaRepository.findAll(pageable).getContent();
    }

    @ExecuteOn(TaskExecutors.IO)
    @Get("/latest")
    public PoolMeta latest() {
        Pageable pageable = Pageable.from(0, 1, Sort.of(Sort.Order.desc("dtPool")));
        return poolMetaRepository.findAll(pageable).getContent().get(0);
    }

    @ExecuteOn(TaskExecutors.IO)
    @Put("/updateWeight")
    public HttpResponse updateWeight(@Body PoolMetaUpdateWeightCommand poolMetaUpdateWeightCommand) {
        poolMetaRepository.update(poolMetaUpdateWeightCommand.getId(), Instant.now().getEpochSecond(), poolMetaUpdateWeightCommand.getWeight());
        return HttpResponse.noContent().header(HttpHeaders.LOCATION, URI.create("/armm/v1/poolMeta/" + poolMetaUpdateWeightCommand.getId()).getPath());
    }
}
