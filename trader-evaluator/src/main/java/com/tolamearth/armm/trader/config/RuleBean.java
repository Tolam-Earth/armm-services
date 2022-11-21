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

package com.tolamearth.armm.trader.config;

import com.tolamearth.armm.trader.entities.Rules;
import com.tolamearth.armm.trader.repository.RulesRepository;
import jakarta.inject.Singleton;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.utils.KieHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Singleton
public class RuleBean {

    private final RulesRepository rulesRepository;

    private KieBase kieBase;

    public RuleBean(RulesRepository rulesRepository) {
        this.rulesRepository = rulesRepository;
    }

    public StatelessKieSession getKieSession() {
        if (kieBase == null) { kieBase = loadRules();}
        return kieBase.newStatelessKieSession();    }

    public void refreshRuleContainer() {
        kieBase = loadRules();
    }

    private KieBase loadRules() {
        InputStream is;
        Rules rulesFromDb = rulesRepository.findTop1ByRuleNameOrderByCreatedAtDesc("test");
        if (null == rulesFromDb) throw new RuntimeException(); // TODO add proper exception
        is = new ByteArrayInputStream(rulesFromDb.getRuleContent());
        SpreadsheetCompiler sc = new SpreadsheetCompiler();
        String rules = sc.compile(is, InputType.XLS);
        return new KieHelper().addContent(rules, ResourceType.DRL).build();
    }
}
