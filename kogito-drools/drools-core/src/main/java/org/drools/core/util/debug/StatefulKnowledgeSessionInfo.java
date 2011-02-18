/**
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

package org.drools.core.util.debug;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.drools.common.NetworkNode;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Package;
import org.drools.rule.Rule;

public class StatefulKnowledgeSessionInfo {
    private ReteooWorkingMemory               session;
    private Map<NetworkNode, DefaultNodeInfo> nodesInfo;
    private List<String>                      log;

    private SimpleDateFormat                  df;

    public StatefulKnowledgeSessionInfo() {
        nodesInfo = new TreeMap<NetworkNode, DefaultNodeInfo>( new Comparator<NetworkNode>() {
            public int compare(NetworkNode o1,
                               NetworkNode o2) {
                return o1.getId() - o2.getId();
            }
        } );
        log = new LinkedList<String>();
        df = new SimpleDateFormat( "HH:mm:ss.SSS" );
    }

    public Package[] getPackages() {
        return this.session.getRuleBase().getPackages();
    }

    public int getNodeCount() {
        return ((ReteooRuleBase) this.session.getRuleBase()).getNodeCount();
    }

    public int getExternalFactCount() {
        return this.session.getObjectStore().size();
    }

    public void assign(NetworkNode snode,
                       Rule rule) {
        DefaultNodeInfo info = this.nodesInfo.get( snode );
        info.assign( rule );
    }

    public boolean visited(NetworkNode node) {
        return this.nodesInfo.containsKey( node );
    }

    public DefaultNodeInfo getNodeInfo(NetworkNode node) {
        return this.nodesInfo.get( node );
    }

    public void addNodeInfo(NetworkNode node,
                            DefaultNodeInfo dni) {
        this.nodesInfo.put( node,
                            dni );
    }

    public void info(String msg) {
        log( msg,
             " [INFO] - " );
    }

    public void warn(String msg) {
        log( msg,
             " [WARN] - " );
    }

    public void error(String msg) {
        log( msg,
             " [ERRO] - " );
    }

    private void log(String msg,
                     String lvl) {
        log.add( df.format( System.currentTimeMillis() ) + lvl + msg );
    }

    public List<String> getLog() {
        return log;
    }

    public Collection< ? extends NodeInfo> getNodeInfos() {
        return this.nodesInfo.values();
    }

    public ReteooWorkingMemory getSession() {
        return session;
    }

    public void setSession(ReteooWorkingMemory session) {
        this.session = session;
    }

}