/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.ruleset2dmn.cli;

import org.drools.core.util.Drools;
import picocli.CommandLine.IVersionProvider;

public class RuleSet2DMNVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        return new String[]{Drools.getFullVersion()};
    }

}
