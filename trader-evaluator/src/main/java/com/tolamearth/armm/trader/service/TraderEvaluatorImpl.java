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

import com.tolamearth.armm.pipeline.dtos.NftId;
import com.tolamearth.armm.trader.MarketplaceState;
import com.tolamearth.armm.trader.config.RuleBean;
import com.tolamearth.armm.trader.controller.client.IntegrationBuyClient;
import com.tolamearth.armm.trader.entities.Rules;
import com.tolamearth.armm.trader.repository.RulesRepository;
import com.tolamearth.armm.trader.rules.dto.MarketplaceStateDto;
import jakarta.inject.Singleton;
import org.drools.core.ClassObjectFilter;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Singleton
public class TraderEvaluatorImpl implements TraderEvaluator {
    private static final Logger log = LoggerFactory.getLogger(TraderEvaluatorImpl.class);
    private final RulesRepository rulesRepository;
    private final RuleBean ruleBean;
    private final IntegrationBuyClient integrationBuyClient;

    public TraderEvaluatorImpl(RulesRepository rulesRepository, RuleBean ruleBean, IntegrationBuyClient integrationBuyClient) {
        this.rulesRepository = rulesRepository;
        this.ruleBean = ruleBean;
        this.integrationBuyClient = integrationBuyClient;
    }

    @Override
    @Transactional
    public void saveRuleFile(String ruleName, String fileUploadFilename, byte[] rulesFile) {
        Rules rules = new Rules(null, fileUploadFilename, new Date(), ruleName, rulesFile);
        rulesRepository.save(rules);
    }

    @Override
    public void executePurchaseOrder(MarketplaceState marketplaceState) {
        // Determine if buy will occur with rules
        MarketplaceStateDto marketplaceStateDto = evaluateAgainstConfigurableRules(marketplaceState);
        marketplaceStateDto = evaluateAgainstMaxPrice(marketplaceState, marketplaceStateDto);

        if (marketplaceStateDto.isExecutePurchaseBasedOnConfigRules() &&
            marketplaceStateDto.isPriceLessThanEqualToArmmMaxPrice()) {
            IntegrationBuyClient.BuyRequest request = new IntegrationBuyClient.BuyRequest(
                    "0.0.0.1", // TODO this shouldn't be constant
                    new IntegrationBuyClient.Asset(
                            new NftId(marketplaceState.getNftId().getTokenId(), marketplaceState.getNftId().getSerialNumber()),
                            marketplaceState.getListingPrice()
                    )
            );
            integrationBuyClient.executeBuy(request);
        }
    }

    private MarketplaceStateDto evaluateAgainstConfigurableRules(MarketplaceState marketplaceState) {
        StatelessKieSession kieSession = ruleBean.getKieSession();
        MarketplaceStateDto stateDto = new MarketplaceStateDto(marketplaceState.getProjectCategory(), marketplaceState.getListingPrice());
        List<Command> cmds = new ArrayList<>();
        cmds.add(CommandFactory.newInsert(stateDto));
        cmds.add(CommandFactory.newFireAllRules());
        cmds.add(CommandFactory.newGetObjects(new ClassObjectFilter(MarketplaceStateDto.class), "output"));
        ExecutionResults results = kieSession.execute(CommandFactory.newBatchExecution(cmds));
        stateDto = (MarketplaceStateDto) ((ArrayList) results.getValue("output")).get(0);
        log.info("\nCalculated state:\n"+stateDto);
        return stateDto;
    }

    private MarketplaceStateDto evaluateAgainstMaxPrice(MarketplaceState marketplaceState, MarketplaceStateDto marketplaceStateDto) {
        marketplaceStateDto.setArmmMaxPrice(marketplaceState.getMaxPrice());
        return marketplaceStateDto;
    }
}
