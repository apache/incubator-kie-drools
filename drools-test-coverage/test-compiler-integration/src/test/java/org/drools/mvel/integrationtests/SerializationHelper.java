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
package org.drools.mvel.integrationtests;

import java.io.IOException;

import org.drools.core.util.DroolsStreamUtils;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * Marshalling helper class to perform serialize/de-serialize a given object
 */
public class SerializationHelper {

    public static <T> T serializeObject(final T obj) throws IOException,
            ClassNotFoundException {
        return serializeObject(obj, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T serializeObject(final T obj,
                                        final ClassLoader classLoader) throws IOException,
            ClassNotFoundException {
        return (T) DroolsStreamUtils.streamIn(DroolsStreamUtils.streamOut(obj), classLoader);
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession( final KieSession ksession,
                                                                                  final boolean dispose) throws Exception {
        return (StatefulKnowledgeSession)ksession;
    }

    public static StatefulKnowledgeSession getSerialisedStatefulKnowledgeSession(final KieSession ksession,
                                                                                 final boolean dispose,
                                                                                 final boolean testRoundTrip) throws Exception {
        return (StatefulKnowledgeSession)ksession;
    }

}
