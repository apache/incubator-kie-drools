/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.games.adventures;

import org.apache.commons.io.IOUtils;
import org.drools.core.util.IoUtils;
import org.drools.games.adventures.model.Command;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.mvel2.MVEL;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEngine {

    KieSession ksession;

    Map<String, Map> data;


    public void createGame(KieContainer kc) {

        ksession = kc.newKieSession("TextAdventureKS");

        Counter c = new Counter();
        ksession.setGlobal("counter",
                           c);
        Map vars = new HashMap();
        vars.put("c",
                 c);
        try {
            String mvelContent = new String(IOUtils.toByteArray(getClass().getResource("data.mvel").openStream()), IoUtils.UTF8_CHARSET);
            data = (Map<String, Map>) MVEL.executeExpression(MVEL.compileExpression(mvelContent), vars);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Object o : data.get("rooms").values()) {
            ksession.insert( o );
        }

        for ( Object o : data.get( "doors" ).values() ) {
            ksession.insert( o );
        }

        for ( Object o : data.get( "characters" ).values() ) {
            ksession.insert( o );
        }

        for ( Object o : data.get( "items" ).values() ) {
            ksession.insert( o );
        }

//        for ( Object o : data.get( "keys" ).values() ) {
//            ksession.insert( o );
//        }
//
//        for ( Object o : data.get( "locks" ).values() ) {
//            ksession.insert( o );
//        }

        for ( Object o : data.get( "locations" ).values() ) {
            ksession.insert( o );
        }
    }

    public Map<String, Map> getData() {
        return data;
    }

    public void receiveMessage(UserSession session,
                               List cmdList) {
        try {
            Class<Command> cls = (Class<Command>) cmdList.get(0);
            Class[] constructorParamTypes = new Class[cmdList.size()-1];
            for ( int i = 1; i < cmdList.size(); i++) {
                constructorParamTypes[i-1] = cmdList.get(i).getClass();
            }

            Object[] args = cmdList.subList(1, cmdList.size() ).toArray();

            Command cmd = (Command) cls.getDeclaredConstructors()[0].newInstance(args);
            cmd.setSession( session );
            ksession.insert( cmd );
            ksession.fireAllRules();
        } catch ( Exception e ) {
            e.printStackTrace();
            session.getChannels().get( "output" ).send( "Unable to Execute Command: " + cmdList );
        }

    }

    public KieSession getKieSession() {
        return ksession;
    }

}
