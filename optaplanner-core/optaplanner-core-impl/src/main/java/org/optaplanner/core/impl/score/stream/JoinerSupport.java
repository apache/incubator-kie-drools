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

package org.optaplanner.core.impl.score.stream;

import java.util.Iterator;
import java.util.ServiceLoader;

public final class JoinerSupport {

    private static volatile JoinerService INSTANCE;

    public static JoinerService getJoinerService() {
        if (INSTANCE == null) {
            synchronized (JoinerSupport.class) {
                if (INSTANCE == null) {
                    Iterator<JoinerService> servicesIterator = ServiceLoader.load(JoinerService.class).iterator();
                    if (!servicesIterator.hasNext()) {
                        throw new IllegalStateException("Joiners not found.\n"
                                + "Maybe include org.optaplanner:optaplanner-constraint-streams dependency in your project?\n"
                                + "Maybe ensure your uberjar bundles META-INF/services from included JAR files?");
                    } else {
                        INSTANCE = servicesIterator.next();
                    }
                }
            }
        }
        return INSTANCE;
    }
}
