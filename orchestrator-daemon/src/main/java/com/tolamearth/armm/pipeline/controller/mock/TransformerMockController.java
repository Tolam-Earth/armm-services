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

package com.tolamearth.armm.pipeline.controller.mock;

import com.tolamearth.armm.pipeline.services.DataTransformerClient;
import com.tolamearth.integration.armm.ArmmEvent;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class TransformerMockController {

    @Post("/arm/v1/data_transformer")
    DataTransformerClient.TransformationResponse transformData(@Body DataTransformerClient.TransformationRequest assets) {
        return new DataTransformerClient.TransformationResponse(
                List.of(assets.nftId()),
                List.of("1.0.0.1", "1.0.0.2"),
                List.of(1L,2L),
                List.of(1L,2L),
                List.of(BigDecimal.valueOf(2.15),BigDecimal.valueOf(5.1)),
                List.of(10L,12L),
                List.of(2L,3L),
                List.of(ArmmEvent.EventType.LISTED.toString(), ArmmEvent.EventType.PURCHASED.toString()),
                List.of(BigDecimal.valueOf(2.151212),BigDecimal.valueOf(5.1232323)),
                List.of(BigDecimal.valueOf(2.151212),BigDecimal.valueOf(5.1232323))
        );
    }

}
