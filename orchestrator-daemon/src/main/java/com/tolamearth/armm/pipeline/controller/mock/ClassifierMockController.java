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

import com.tolamearth.armm.pipeline.dtos.NftId;
import com.tolamearth.armm.pipeline.services.ClassificationServiceClient;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

import java.util.List;
import java.util.UUID;

@Controller
public class ClassifierMockController {

    @Post("/arm/v1/classification/scheduled-pool")
    ClassificationServiceClient.ClassificationResponse classify(@Body ClassificationServiceClient.ClassificationRequest classificationRequest) {
        return new ClassificationServiceClient.ClassificationResponse(
                List.of(new NftId(classificationRequest.nftIds().get(0).tokenId(), classificationRequest.nftIds().get(0).serialNumber())),
                List.of("0.0.9401@1602138343.335616988"),
                List.of(new ClassificationServiceClient.HederaTimestamp(1602138343L, 335616988L)),
                List.of(UUID.randomUUID()),
                List.of("RENEW_ENERGY-SOLAR-CHN-bb898c7c-45f4-4e9a-a35f-d7dcb0f684cd"),
                "v1.1.1-202208311301"
        );
    }

}
