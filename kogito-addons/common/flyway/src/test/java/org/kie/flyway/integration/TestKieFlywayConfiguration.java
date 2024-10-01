/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kie.flyway.integration;

import java.util.Map;

public class TestKieFlywayConfiguration implements KieFlywayConfiguration<TestKieFlywayNamedModule> {

    private boolean enabled;
    private Map<String, TestKieFlywayNamedModule> modules;

    public TestKieFlywayConfiguration(boolean enabled, Map<String, TestKieFlywayNamedModule> modules) {
        this.enabled = enabled;
        this.modules = modules;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Map<String, TestKieFlywayNamedModule> getModules() {
        return modules;
    }
}
