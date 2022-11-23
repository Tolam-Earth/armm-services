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

import com.tolamearth.armm.pipeline.entities.TraderUserConfig;
import com.tolamearth.armm.trader.service.TraderUserConfigService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import static com.tolamearth.armm.pipeline.enums.TraderUserConfigName.BUY_ON;
import static com.tolamearth.armm.pipeline.enums.TraderUserConfigValue.FALSE;
import static com.tolamearth.armm.pipeline.enums.TraderUserConfigValue.TRUE;

@Controller("/armm/v1/trader")
public class TraderUserConfigController {

    private final TraderUserConfigService traderUserConfigService;

    public TraderUserConfigController(TraderUserConfigService traderUserConfigService) {
        this.traderUserConfigService = traderUserConfigService;
    }

    @ExecuteOn(TaskExecutors.IO)
    @Post("/buyOn")
    TraderUserConfig buyOn() {
        TraderUserConfig traderUserConfig = traderUserConfigService.findConfigAndSet(BUY_ON, TRUE);
        return traderUserConfig;
    }

    @ExecuteOn(TaskExecutors.IO)
    @Post("/buyOff")
    TraderUserConfig buyOff() {
        TraderUserConfig traderUserConfig = traderUserConfigService.findConfigAndSet(BUY_ON, FALSE);
        return traderUserConfig;
    }

}
