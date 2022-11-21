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

package com.tolamearth.armm.pricing;

public class ArmmPubsubConfig {
    public Pricing pricing;

    public static class Pricing {
        public Pubsub pubsub;
    }

    public static class Project {
        public String name;
    }

    public static class Pubsub {
        public Project project;
        public Topics topics;
    }

    public static class Topics {
        public NameListener classificator;
    }

    public static class NameListener {
        public String name;
        public String listener;
    }
}
