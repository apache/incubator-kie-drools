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
package org.drools.compiler.kie.builder.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.api.conf.Option;

// An Option map wrapper class so that modularized containers don't have to depend on concrete configuration
public class KieBaseUpdaterOptions {

    private final Map<Class<? extends Option>, Option> optionMap = new HashMap<>();

    public KieBaseUpdaterOptions(OptionEntry... options) {
        for (OptionEntry o : options) {
            optionMap.put(o.key, o.value);
        }
    }

    public KieBaseUpdaterOptions(List<OptionEntry> options) {
        this(options.toArray(new OptionEntry[0]));
    }

    public Optional<Option> getOption(Class<? extends Option> optionClazz) {
        return Optional.ofNullable(optionMap.get(optionClazz));
    }

    public static class OptionEntry {
        final Class<? extends Option> key;
        final Option value;

        public OptionEntry(Class<? extends Option> key, Option value) {
            this.key = key;
            this.value = value;
        }
    }
}
