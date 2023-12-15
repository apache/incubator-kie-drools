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
package org.kie.kogito.legacy.rules;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;

@Path("/test-tms")
public class TmsEndpoint {
    @Inject
    KieRuntimeBuilder kieRuntimeBuilder;

    private KieSession session;

    private final Map<String, FactHandle> map = new HashMap<>();

    @PostConstruct
    void init() {
        this.session = kieRuntimeBuilder.newKieSession();
    }

    @GET()
    public int executeQuery() {
        return session.getObjects(Integer.class::isInstance).stream().map(Integer.class::cast).mapToInt(Integer::intValue).findFirst().orElse(-1);
    }

    @POST
    public int insert(@QueryParam("string") String string) {
        map.put(string, session.insert(string));
        return session.fireAllRules();
    }

    @DELETE
    public int delete(@QueryParam("string") String string) {
        session.delete(map.get(string));
        return session.fireAllRules();
    }
}
