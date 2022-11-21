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
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static com.tolamearth.armm.pipeline.enums.TraderUserConfigName.BUY_ON;
import static com.tolamearth.armm.pipeline.enums.TraderUserConfigValue.FALSE;
import static com.tolamearth.armm.pipeline.enums.TraderUserConfigValue.TRUE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class TraderUserConfigServiceTest {

    @Inject
    private TraderUserConfigService traderUserConfigService;

    @Test
    void testIsBuyOnActivated() {
        TraderUserConfig configAndSet = traderUserConfigService.findConfigAndSet(BUY_ON, TRUE);
        Boolean result = traderUserConfigService.isBuyOnActivated();
        assertTrue(result);

        traderUserConfigService.findConfigAndSet(BUY_ON, FALSE);
        result = traderUserConfigService.isBuyOnActivated();
        assertFalse(result);
    }

    @Test
    void testIsBuyOnActivated_NoConfigExistsYet() {
        Boolean result = traderUserConfigService.isBuyOnActivated();
        assertTrue(result);
    }
}

