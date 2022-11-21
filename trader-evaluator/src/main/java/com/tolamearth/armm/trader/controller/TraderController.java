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

package com.tolamearth.armm.trader.controller;

import com.tolamearth.armm.pipeline.dtos.prices.ErrorResponse;
import com.tolamearth.armm.trader.config.RuleBean;
import com.tolamearth.armm.trader.service.TraderEvaluatorImpl;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Controller("/armm/v1/trader")
public class TraderController {
    private static final Logger log = LoggerFactory.getLogger(TraderController.class);
    private final TraderEvaluatorImpl traderEvaluatorService;
    private final RuleBean ruleBean;

    public TraderController(TraderEvaluatorImpl traderEvaluatorService, RuleBean ruleBean) {
        this.traderEvaluatorService = traderEvaluatorService;
        this.ruleBean = ruleBean;
    }


    @Post(value = "/rules/upload", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
    public HttpResponse<String> uploadRule(@QueryValue("ruleName") String ruleName, @Part("file") CompletedFileUpload fileUpload) throws IOException {
        log.debug("****************** uploadRule");
        if (fileUpload.getFilename().endsWith(".xlsx") || fileUpload.getFilename().endsWith(".xls")) {
            traderEvaluatorService.saveRuleFile(ruleName, fileUpload.getFilename(), fileUpload.getBytes());
            ruleBean.refreshRuleContainer();
            return HttpResponse.ok("Pending");
        }
        return HttpResponse.badRequest("This file does not contains rules");
    }

    @Error(global = true)
    public HttpResponse<ErrorResponse> error(HttpRequest request, Throwable e) {
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
