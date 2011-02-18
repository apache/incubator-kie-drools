/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.definition.process;

import java.util.List;
import java.util.Map;

public interface Node {

    long getId();

    String getName();

    Map<String, List<Connection>> getIncomingConnections();

    Map<String, List<Connection>> getOutgoingConnections();

    List<Connection> getIncomingConnections(String type);

    List<Connection> getOutgoingConnections(String type);

    NodeContainer getNodeContainer();

    Map<String, Object> getMetaData();

    @Deprecated Object getMetaData(String name);

}
