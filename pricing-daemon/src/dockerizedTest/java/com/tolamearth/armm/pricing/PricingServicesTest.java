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

package com.tolamearth.armm.pricing;

import com.tolamearth.armm.pipeline.dtos.NftId;
import com.tolamearth.armm.pipeline.dtos.prices.PricingRequestDTO;
import com.tolamearth.armm.pipeline.dtos.prices.TokenId;
import com.tolamearth.armm.pipeline.entities.Model;
import com.tolamearth.armm.pipeline.entities.ModelVersion;
import com.tolamearth.armm.pipeline.entities.PricingRequest;
import com.tolamearth.armm.pricing.controller.client.PriceModelClient;
import com.tolamearth.armm.pricing.repository.ModelRepository;
import com.tolamearth.armm.pricing.repository.PricingRequestRepository;
import com.tolamearth.armm.pricing.service.PricingServiceImpl;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.h2.tools.Server;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
class PricingServicesTest {

    @Inject
    private PricingRequestRepository pricingRequestRepository;
    @Inject
    private ModelRepository modelRepository;

    @Inject
    PriceModelClient priceModelClient;
    @Inject
    EmbeddedApplication<?> application;
    @Inject
    @Client("/")
    private HttpClient httpClient;

    BlockingHttpClient blockingClient = null;

    @BeforeAll
    static void beforeAll() throws SQLException {
        Server.createWebServer().start();
    }


    @BeforeEach
    public void setup() {
        blockingClient = httpClient.toBlocking();
    }

    @Test
    void testGetPrices() {
        generateTestData();
        List<TokenId> parameters = Arrays.asList(
                new TokenId(new NftId("1.2.3", 1L)),
                new TokenId(new NftId("0.0.1", 2L)),
                new TokenId(new NftId("0.0.1", 3L))
        );
        when(priceModelClient.getPriceFromModel(any())).thenReturn(new PriceModelClient.PriceResponse(10L, 20L));
        when(modelRepository.findByEndpointId(any())).thenReturn(List.of(
                new ModelVersion(
                        null, "model version 1", true, true, 1234L, new Date().getTime(), new Date().getTime(),
                        new Model(
                                null, "model 1", "type 1", new Date().getTime()
                        )
                )));


        PricingRequestDTO pricingRequestDTO = new PricingRequestDTO(parameters);
        HttpRequest<PricingRequestDTO> request = HttpRequest.POST("/armm/v1/price", pricingRequestDTO);
        blockingClient.exchange(request);

        List<PricingRequest> prs =
                StreamSupport.stream(pricingRequestRepository.findAll().spliterator(), false).toList();
        //Thread.sleep(100000000);


        assertEquals(1, prs.size());
        assertEquals(1, prs.get(0).getNfts().size());
        assertEquals(1, prs.get(0).getNfts().get(0).getPoolGroups().size());
        assertTrue(prs.get(0).getNfts().get(0).getPoolGroups().get(0).getPrimary());
        assertNotNull(prs.get(0).getNfts().get(0).getPoolGroups().get(0).getModelResult());
        assertNotNull(prs.get(0).getNfts().get(0).getPoolGroups().get(0).getModelResult().getModelVersion());
        assertNotNull(prs.get(0).getNfts().get(0).getPoolGroups().get(0).getModelResult().getModelVersion().getModel());
    }

    private List<String> stringifyTokenIds(List<TokenId> parameters) {
        return parameters.stream().map(PricingServiceImpl::getConcatenatedTokenIdAndSerialNumber).collect(Collectors.toList());
    }

    public void generateTestData() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:mem:armmdb;DB_CLOSE_ON_EXIT=TRUE;MODE=PostgreSQL", "sa", "");
            Instant instant = LocalDateTime.of(2022, 8, 1, 0, 0)
                    .toInstant(ZoneOffset.UTC);
            Statement s = connection.createStatement();
            s.execute("delete from token_attributes where nft_id in ('1.2.3-1', '0.0.1-2', '0.0.1-3')");
            s.execute("INSERT INTO public.token_attributes (id, nft_id, transaction_id, transaction_time, minting_owner, owner,\n" +
                    "                                     country, first_subdivision, project_category, project_type, vintage_year, nft_age,\n" +
                    "                                     num_owners, avg_price, last_price, num_price_chg, nft_state, token_pool_id,\n" +
                    "                                     name_pool, pooling_version, latitude, longitude)\n" +
                    "VALUES" +
                    " ('05df2bcd-035d-4295-963f-b64050c1d5ee', '1.2.3-1', '46661e35-c90c-46d0-9381-d1212c2231cf', 165969318, '0.0.22',\n" +
                    "        '0.0.22', 'USA', 'GD', 'Kategory super', 'Type 1', 0, 0, 1, 0.0, 0, 0, 'MINTED',\n" +
                    "        'e49920d3-bffc-4985-8e81-30c54d03b244',\n" +
                    "        'KEN_NIC_HND_USA_KHM_FRA_IND_ETH_BGR_IDN_GBR_GTM_GIN_MOZ_MMR_RWA_PER_FOREST_CONSERV_AGG_LAND_MGMT_b77a1789-f5e6-4216-bc0d-3dbd43e404c9',\n" +
                    "        '0.0.0.0', 0.0, 0.0);\n");
            s.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @MockBean(ModelRepository.class)
    ModelRepository modelRepository() {
        return mock(ModelRepository.class);
    }
    @MockBean(PriceModelClient.class)
    PriceModelClient priceModelClient() {
        return mock(PriceModelClient.class);
    }

}
