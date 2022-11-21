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

package com.tolamearth.armm.trader.service;

import com.tolamearth.armm.pipeline.entities.TraderUserConfig;
import com.tolamearth.armm.pipeline.enums.TraderUserConfigName;
import com.tolamearth.armm.pipeline.enums.TraderUserConfigValue;
import com.tolamearth.armm.trader.repository.TraderUserConfigRepository;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;

import static com.tolamearth.armm.pipeline.enums.TraderUserConfigName.BUY_ON;

@Singleton
public class TraderUserConfigService {
    private static final Logger log = LoggerFactory.getLogger(TraderUserConfigService.class);

    private final TraderUserConfigRepository traderUserConfigRepository;

    public TraderUserConfigService(TraderUserConfigRepository traderUserConfigRepository) {
        this.traderUserConfigRepository = traderUserConfigRepository;
    }

    @Transactional
    public TraderUserConfig findConfigAndSet(TraderUserConfigName traderUserConfigName, TraderUserConfigValue traderUserConfigValue) {
        TraderUserConfig traderUserConfig = traderUserConfigRepository.find(traderUserConfigName);
        if (traderUserConfig == null) {
            traderUserConfig = new TraderUserConfig(null, traderUserConfigName, traderUserConfigValue);
            traderUserConfigRepository.save(traderUserConfig);
        } else {
            traderUserConfig.setConfigValue(traderUserConfigValue);
            traderUserConfigRepository.update(traderUserConfig);
        }
        return traderUserConfig;
    }

    @ReadOnly
    public Boolean isBuyOnActivated() {
        TraderUserConfig traderUserConfig = traderUserConfigRepository.find(BUY_ON);
        if (traderUserConfig != null) {
            return traderUserConfig.getConfigValue() == TraderUserConfigValue.TRUE;
        } else {
            return true;
        }
    }
}
