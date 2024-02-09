/**
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
package org.kie.maven.plugin.helpers;

import java.util.List;

import org.apache.maven.model.Dependency;

import static java.util.Arrays.asList;
import static org.kie.maven.plugin.enums.ExecModelMode.WITHANC;
import static org.kie.maven.plugin.enums.ExecModelMode.WITHDRL_MVEL;
import static org.kie.maven.plugin.enums.ExecModelMode.WITHMVEL;
import static org.kie.maven.plugin.enums.ExecModelMode.YES;
import static org.kie.maven.plugin.enums.ExecModelMode.YES_WITHDRL;
import static org.kie.maven.plugin.enums.ExecModelMode.valueOf;

public class ExecModelModeHelper {

    public static boolean execModelParameterEnabled(String s) {
        return asList(YES, YES_WITHDRL, WITHMVEL, WITHDRL_MVEL, WITHANC).contains(valueOf(s.toUpperCase()));
    }

    public static boolean ancEnabled(String s) {
        return List.of(WITHANC).contains(valueOf(s.toUpperCase()));
    }

    public static boolean isModelCompilerInClassPath(List<Dependency> dependencies) {
        return dependencies.stream()
                           .anyMatch(d -> d.getGroupId().equals("org.drools") &&
                                          (d.getArtifactId().equals("drools-model-compiler") ||
                                           d.getArtifactId().equals("drools-engine") ||
                                           d.getArtifactId().equals("drools-ruleunits-engine")));
    }

    public static boolean shouldValidateMVEL(String s) {
        return asList(WITHMVEL, WITHDRL_MVEL).contains(valueOf(s.toUpperCase()));
    }

    public static boolean shouldDeleteFile(String s) {
        return asList(YES, WITHMVEL).contains(valueOf(s.toUpperCase()));
    }
}
