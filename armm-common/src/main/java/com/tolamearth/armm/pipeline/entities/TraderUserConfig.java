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

package com.tolamearth.armm.pipeline.entities;

import com.tolamearth.armm.pipeline.enums.TraderUserConfigName;
import com.tolamearth.armm.pipeline.enums.TraderUserConfigValue;
import io.micronaut.core.annotation.Introspected;

import javax.persistence.*;
import java.util.UUID;

import static javax.persistence.EnumType.STRING;

@Entity(name="trader_user_config")
@Introspected
public class TraderUserConfig {
    @GeneratedValue
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "config_name")
    @Enumerated(STRING)
    private TraderUserConfigName configName;

    @Column(name = "config_value")
    @Enumerated(STRING)
    private TraderUserConfigValue configValue;

    public TraderUserConfig() {
    }

    public TraderUserConfig(UUID id, TraderUserConfigName configName, TraderUserConfigValue configValue) {
        this.id = id;
        this.configName = configName;
        this.configValue = configValue;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TraderUserConfigName getConfigName() {
        return configName;
    }

    public void setConfigName(TraderUserConfigName configName) {
        this.configName = configName;
    }

    public TraderUserConfigValue getConfigValue() {
        return configValue;
    }

    public void setConfigValue(TraderUserConfigValue configValue) {
        this.configValue = configValue;
    }
}
