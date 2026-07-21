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
package org.jbpm.flow.serialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import org.jbpm.util.JbpmClassLoaderUtil;

public class ObjectMarshallerStrategyHelper {

    public static ObjectMarshallerStrategy[] defaultStrategies() {
        List<ObjectMarshallerStrategy> strats = new ArrayList<>();
        ServiceLoader<ObjectMarshallerStrategy> loader = ServiceLoader.load(ObjectMarshallerStrategy.class, JbpmClassLoaderUtil.findClassLoader());

        for (ObjectMarshallerStrategy strategy : loader) {
            strats.add(strategy);
        }
        Collections.sort(strats);
        return strats.stream().toArray(ObjectMarshallerStrategy[]::new);
    }

}
