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

import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;

public class KieBaseUpdatersImpl implements KieBaseUpdaters {

    private volatile Collection<KieBaseUpdaterFactory> children;

    @Override
    public Collection<KieBaseUpdaterFactory> getChildren() {
        if (children == null) {
            synchronized (this) {
                if (children == null) {
                    children = new ArrayList<>();
                    ServiceLoader<KieBaseUpdaterFactory> loader = ServiceLoader.load(KieBaseUpdaterFactory.class);
                    for (KieBaseUpdaterFactory kieBaseUpdaterFactory : loader) {
                        children.add(kieBaseUpdaterFactory);
                    }
                }
            }
        }
        return children;
    }
}
