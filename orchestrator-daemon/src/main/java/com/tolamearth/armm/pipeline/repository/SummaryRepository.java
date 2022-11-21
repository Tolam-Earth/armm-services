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

package com.tolamearth.armm.pipeline.repository;

import com.tolamearth.armm.pipeline.entities.Summary;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.util.UUID;

@Repository
public interface SummaryRepository extends CrudRepository<Summary, UUID> {
    @Nullable
    Summary findBySummaryName(String name, LockModeType optimistic);
}
