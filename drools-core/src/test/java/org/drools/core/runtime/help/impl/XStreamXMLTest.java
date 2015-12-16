/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.runtime.help.impl;

import com.thoughtworks.xstream.XStream;
import org.drools.core.QueryResultsImpl;
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.drools.core.command.runtime.rule.*;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.drools.core.spi.FactHandleFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;

import java.util.*;

public class XStreamXMLTest {

    private XStream xstream;

    @Before
    public void setup() {
        xstream = XStreamXML.newXStreamMarshaller( new XStream() );
    }

    @Test
    public void testMarshallStartProcessCmd() {
        StartProcessCommand cmd = new StartProcessCommand("some-process-id", "some-out-identifier");
        String xmlString = xstream.toXML(cmd);
        Assert.assertTrue(xmlString.contains("processId=\"some-process-id\""));
        Assert.assertTrue(xmlString.contains("out-identifier=\"some-out-identifier\""));
    }

    @Test
    public void testMarshallStartProcessCmdWithNoOutIdentifier() {
        // the "out-identifier" is optional -> the marshalling should succeed even if it is null
        StartProcessCommand cmd = new StartProcessCommand("some-process-id");
        String xmlString = xstream.toXML(cmd);
        Assert.assertTrue(xmlString.contains("processId=\"some-process-id\""));
    }

    @Test
    public void testUnMarshallStartProcessCmdWithNoOutIdentifier() {
        // the "out-identifier" is optional -> the unmarshalling should create valid object
        Object obj = xstream.fromXML(
                "<start-process processId=\"some-process-id\"/>");
        Assert.assertEquals(StartProcessCommand.class, obj.getClass());
        StartProcessCommand cmd = (StartProcessCommand)obj;
        Assert.assertEquals( "some-process-id", cmd.getProcessId() );
    }

    @Test
    public void testMarshallAgendaGroupSetFocusCommand() {
        AgendaGroupSetFocusCommand cmd = new AgendaGroupSetFocusCommand("foo-group");
        String xmlString = xstream.toXML( cmd );
        Assert.assertEquals( "<set-focus name=\"foo-group\"/>", xmlString );
        AgendaGroupSetFocusCommand cmd2 = (AgendaGroupSetFocusCommand) xstream.fromXML( xmlString );
        Assert.assertEquals( cmd.getName(), cmd2.getName() );
    }

    @Test
    public void testClearActivationGroupCommand() {
        ClearActivationGroupCommand cmd = new ClearActivationGroupCommand("foo-group");
        String xmlString = xstream.toXML( cmd );
        Assert.assertEquals( "<clear-activation-group name=\"foo-group\"/>", xmlString );

        ClearActivationGroupCommand cmd2 = (ClearActivationGroupCommand) xstream.fromXML( xmlString );
        Assert.assertEquals( cmd.getName(), cmd2.getName() );
    }

    @Test
    public void testClearAgendaGroupCommand() {
        ClearAgendaGroupCommand cmd = new ClearAgendaGroupCommand("foo-group");
        String xmlString = xstream.toXML( cmd );
        Assert.assertEquals( "<clear-agenda-group name=\"foo-group\"/>", xmlString );

        ClearAgendaGroupCommand cmd2 = (ClearAgendaGroupCommand) xstream.fromXML( xmlString );
        Assert.assertEquals(cmd.getName(), cmd2.getName());
    }

    @Test
    public void testClearAgendaCommand() {
        ClearAgendaCommand cmd = new ClearAgendaCommand();
        String xmlString = xstream.toXML(cmd);
        Assert.assertEquals( "<clear-agenda/>", xmlString );

        ClearAgendaCommand cmd2 = (ClearAgendaCommand) xstream.fromXML( xmlString );
    }

    @Test
    public void testClearRuleFlowGroupCommand() {
        ClearRuleFlowGroupCommand cmd = new ClearRuleFlowGroupCommand("foo-group");
        String xmlString = xstream.toXML(cmd);
        Assert.assertEquals( "<clear-ruleflow-group name=\"foo-group\"/>", xmlString );

        ClearRuleFlowGroupCommand cmd2 = (ClearRuleFlowGroupCommand) xstream.fromXML( xmlString );
        Assert.assertEquals(cmd.getName(), cmd2.getName());
    }

    @Test
    public void testQueryResultsConverter() {
        final Message msg = new Message("Hello World!");
        final FactHandle msgHandle = new DefaultFactHandle( 1,
                                                            null,
                                                            10,
                                                            10,
                                                            20,
                                                            msg );
        Set<String> identifiers = new HashSet<String>(  ) {{
            add("greeting");
        }};
        ArrayList<Map<String, FactHandle>> idFactHandleMaps = new ArrayList<Map<String, FactHandle>>(  ) {{
            add( new HashMap<String, FactHandle>(  ) {{
                put( "greeting", msgHandle );
            }} );
        }};
        ArrayList<Map<String, Object>> factHandleResultMap = new ArrayList<Map<String, Object>>(  ) {{
            add( new HashMap<String, Object>(  ) {{
                put( "greeting", msg );
            }} );
        }};

        final String EXPECTED_XML = "<query-results>\n"
                                    + "  <identifiers>\n"
                                    + "    <identifier>greeting</identifier>\n"
                                    + "  </identifiers>\n"
                                    + "  <row>\n"
                                    + "    <identifier id=\"greeting\">\n"
                                    + "      <org.drools.core.runtime.help.impl.XStreamXMLTest_-Message>\n"
                                    + "        <msg>Hello World!</msg>\n"
                                    + "      </org.drools.core.runtime.help.impl.XStreamXMLTest_-Message>\n"
                                    + "      <fact-handle external-form=\"0:1:10:10:20:null:NON_TRAIT:org.drools.core.runtime.help.impl.XStreamXMLTest$Message\"/>\n"
                                    + "    </identifier>\n"
                                    + "  </row>\n"
                                    + "</query-results>";

        QueryResults results = new FlatQueryResults( identifiers, idFactHandleMaps, factHandleResultMap );
        String xmlString = xstream.toXML( results );
        Assert.assertEquals( EXPECTED_XML, xmlString );

        QueryResults results2 = (QueryResults) xstream.fromXML( xmlString );
        Assert.assertEquals( results, results2 );
    }

    private static class Message {
        String msg;

        public Message() {}

        public Message(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public boolean equals(Object o) {
            if ( this == o ) return true;
            if ( !(o instanceof Message) ) return false;

            Message message = (Message) o;

            return !(msg != null ? !msg.equals( message.msg ) : message.msg != null);

        }

        @Override
        public int hashCode() {
            return msg != null ? msg.hashCode() : 0;
        }
    }

}
