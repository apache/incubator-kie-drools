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
package org.drools.drl.extensions;

import org.drools.base.common.MissingDependencyException;
import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;

public class YamlFactory {

    private static final String NO_YAML = "You're trying to parse a rule file in YAML format without having drools yaml support. Please add the module org.drools:drools-drlonyaml-todrl to your classpath.";

    private static class YamlProviderHolder {
        private static final YamlProvider provider = KieService.load(YamlProvider.class);
    }

    public static YamlProvider getYamlProvider() {
        if (YamlProviderHolder.provider == null) {
            throwExceptionForMissingYaml();
        }
        return YamlProviderHolder.provider;
    }

    public static String loadFromResource(Resource resource) {
        return getYamlProvider().loadFromResource(resource);
    }

    private static void throwExceptionForMissingYaml() {
        throw new MissingDependencyException(NO_YAML);
    }
}
