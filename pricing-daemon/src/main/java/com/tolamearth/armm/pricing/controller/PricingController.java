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

import com.tolamearth.armm.pipeline.dtos.prices.ErrorResponse;
import com.tolamearth.armm.pipeline.dtos.prices.PriceInfo;
import com.tolamearth.armm.pipeline.dtos.prices.PricingRequestDTO;
import com.tolamearth.armm.pipeline.dtos.prices.PricingResponse;
import com.tolamearth.armm.pricing.controller.exceptions.MaxRequestLengthExceededException;
import com.tolamearth.armm.pricing.controller.exceptions.RequiredFieldException;
import com.tolamearth.armm.pricing.service.PricingServiceImpl;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller("/armm/v1")
public class PricingController {
    private static final Logger log = LoggerFactory.getLogger(PricingController.class);

    private final PricingServiceImpl pricingService;

    public PricingController(PricingServiceImpl pricingService) {
        this.pricingService = pricingService;
    }

    @Post("/price")
    HttpResponse<PricingResponse> getTokenPricesInfo(@Body PricingRequestDTO pricingRequestDTO) {
        log.debug("Request on /armm/v1/price \n{}", pricingRequestDTO);
        validatePricingRequest(pricingRequestDTO);
        List<PriceInfo> tokenPrices = pricingService.getTokenPrices(pricingRequestDTO.getNfts());
        if (tokenPrices.stream().anyMatch(pi -> pi.code() != null)) {
            //207 error
            return HttpResponse
                    .status(HttpStatus.MULTI_STATUS)
                    .body(new PricingResponse(pricingRequestDTO.getNfts(), tokenPrices));
        }
        return HttpResponse.ok(new PricingResponse(pricingRequestDTO.getNfts(), tokenPrices));
    }

    private void validatePricingRequest(PricingRequestDTO pricingRequestDTO) {
        if (pricingRequestDTO.getNfts().stream().anyMatch(tid ->
                tid.getNftId() == null ||
                        tid.getNftId().serialNumber() == null ||
                        tid.getNftId().tokenId() == null)) {
            throw new RequiredFieldException();
        }
    }

    @Error(global = true)
    public HttpResponse<ErrorResponse> error(HttpRequest request, Throwable e) {
        if (e instanceof RequiredFieldException) {
            ErrorResponse errorResponse = new ErrorResponse(new ErrorResponse.ErrorInfo("1001", "Missing required field"));
            log.error(errorResponse.toString(), e);
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } else if (e instanceof MaxRequestLengthExceededException) {
            ErrorResponse errorResponse = new ErrorResponse(new ErrorResponse.ErrorInfo("1005", "Requested more than maximum"));
            log.error(errorResponse.toString(), e);
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        log.error("Internal Server Error", e);
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
