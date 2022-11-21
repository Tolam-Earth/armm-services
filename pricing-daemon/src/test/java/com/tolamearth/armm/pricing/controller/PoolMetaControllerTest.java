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
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PoolMetaControllerTest {


    @Inject
    @Client("/")
    private HttpClient httpClient;

    @Test
    @Order(1)
    void testLatest() {
        HttpRequest request = HttpRequest.GET("/armm/v1/poolMeta/latest");
        PoolMeta poolMeta = httpClient.toBlocking().retrieve(request, PoolMeta.class);

        assertNotNull(poolMeta);
        assertEquals(555555199, poolMeta.getDtPool());
        assertEquals(593, poolMeta.getnPool());
    }

    @Test
    @Order(2)
    void testFindById() {
        // find one already stored
        PoolMeta alreadyPersistedPoolMeta = getAlreadyPersistedPoolMeta();

        // find by id
        HttpRequest requestId = HttpRequest.GET("/armm/v1/poolMeta/" + alreadyPersistedPoolMeta.getId().toString());
        PoolMeta poolMeta = httpClient.toBlocking().retrieve(requestId, PoolMeta.class);
        assertNotNull(poolMeta);
        assertEquals(613L, poolMeta.getnPool());
        assertEquals(1.0, poolMeta.getWeight());
    }

    @Test
    void testListAll() {
        HttpRequest request = HttpRequest.GET("/armm/v1/poolMeta/list");
        List<PoolMeta> poolMetas = httpClient.toBlocking().retrieve(request, Argument.of(List.class, PoolMeta.class));
        assertTrue(poolMetas.size() > 5);
    }

    @Test
    void testListSomeByPages() {
        HttpRequest request = HttpRequest.GET("/armm/v1/poolMeta/list?size=4&page=0");
        List<PoolMeta> poolMetas = httpClient.toBlocking().retrieve(request, Argument.of(List.class, PoolMeta.class));
        assertTrue(poolMetas.size() == 4);
    }

    @Test
    void testUpdateWeightAndDtPool() {
        // find one already stored
        PoolMeta alreadyPersistedPoolMeta = getAlreadyPersistedPoolMeta();

        HttpRequest request = HttpRequest.PUT("/armm/v1/poolMeta/updateWeight",
                new PoolMetaUpdateWeightCommand(alreadyPersistedPoolMeta.getId(),0.99));
        HttpResponse<?> response = httpClient.toBlocking().exchange(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        assertEquals("/armm/v1/poolMeta/cc251a50-a1ac-4ddc-92a8-2bdb57df29ab", response.getHeaders().get(HttpHeaders.LOCATION));
    }


    /******************* helper **************************************************/

    private PoolMeta getAlreadyPersistedPoolMeta() {
        HttpRequest requestList = HttpRequest.GET("/armm/v1/poolMeta/list");
        List<PoolMeta> poolMetas = httpClient.toBlocking().retrieve(requestList, Argument.of(List.class, PoolMeta.class));
        return poolMetas.get(0);
    }
}

