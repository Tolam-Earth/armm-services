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

import com.tolamearth.armm.trader.config.RuleBean;
import com.tolamearth.armm.trader.controller.client.IntegrationBuyClient;
import com.tolamearth.armm.trader.repository.RulesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class TraderEvaluatorTest {

    private TraderEvaluator traderEvaluator;
    private RulesRepository rulesRepository;

    @BeforeEach
    void setup() {
        rulesRepository = mock(RulesRepository.class);
        RuleBean ruleBean = mock(RuleBean.class);
        IntegrationBuyClient integrationBuyClient = mock(IntegrationBuyClient.class);
        traderEvaluator = new TraderEvaluatorImpl(rulesRepository, ruleBean, integrationBuyClient);
    }

    @AfterEach
    void cleanup() {
        traderEvaluator = null;
    }

    @Test
    public void testSaveRulesFile() {
        traderEvaluator.saveRuleFile("test", "test.xsl", new byte[1]);
        verify(rulesRepository, times(1)).save(any());
    }

}
