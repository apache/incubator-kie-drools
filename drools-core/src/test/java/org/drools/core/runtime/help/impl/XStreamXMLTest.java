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
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.drools.core.command.runtime.rule.AgendaGroupSetFocusCommand;
import org.drools.core.command.runtime.rule.ClearActivationGroupCommand;
import org.drools.core.command.runtime.rule.ClearAgendaCommand;
import org.drools.core.command.runtime.rule.ClearAgendaGroupCommand;
import org.drools.core.command.runtime.rule.ClearRuleFlowGroupCommand;
import org.drools.core.command.runtime.rule.DeleteCommand;
import org.drools.core.command.runtime.rule.GetFactHandlesCommand;
import org.drools.core.command.runtime.rule.ModifyCommand;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.metadata.Modify;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public void testModifyCommand() {

        FactHandle factHandle = DefaultFactHandle.createFromExternalFormat("0:1:10:10:20:null:NON_TRAIT:null");

        ModifyCommand cmd = new ModifyCommand(factHandle, Arrays.asList(new ModifyCommand.SetterImpl("name", "value")));
        String xmlString = xstream.toXML( cmd );
        Assert.assertEquals( "<modify fact-handle=\"0:1:10:10:20:null:NON_TRAIT:null\">\n" +
                             "  <set accessor=\"name\" value=\"value\"/>\n" +
                             "</modify>", xmlString );

        ModifyCommand cmd2 = (ModifyCommand) xstream.fromXML( xmlString );
        Assert.assertEquals( factHandle.toExternalForm(), cmd2.getFactHandle().toExternalForm() );
        Assert.assertEquals( 1, cmd2.getSetters().size() );
        Assert.assertEquals( "name", cmd2.getSetters().get(0).getAccessor() );
        Assert.assertEquals( "value", cmd2.getSetters().get(0).getValue() );
    }

    @Test
    public void testDeleteCommand() {

        FactHandle factHandle = DefaultFactHandle.createFromExternalFormat("0:1:10:10:20:null:NON_TRAIT:null");

        DeleteCommand cmd = new DeleteCommand(factHandle);
        String xmlString = xstream.toXML( cmd );
        Assert.assertEquals( "<delete fact-handle=\"0:1:10:10:20:null:NON_TRAIT:null\"/>", xmlString );

        DeleteCommand cmd2 = (DeleteCommand) xstream.fromXML( xmlString );
        Assert.assertEquals( factHandle.toExternalForm(), cmd2.getFactHandle().toExternalForm() );
    }

    @Test
    public void testGetFactHandlesCommand() {
        GetFactHandlesCommand cmd = new GetFactHandlesCommand();
        String xmlString = xstream.toXML( cmd );
        Assert.assertEquals( "<get-fact-handles disconnected=\"false\"/>", xmlString );

        GetFactHandlesCommand cmd2 = (GetFactHandlesCommand) xstream.fromXML( xmlString );
        Assert.assertNull(cmd2.getOutIdentifier() );
    }

    @Test
    public void testGetFactHandlesCommandWithOutIdentifier() {
        GetFactHandlesCommand cmd = new GetFactHandlesCommand();
        cmd.setOutIdentifier("facts");
        String xmlString = xstream.toXML( cmd );
        Assert.assertEquals( "<get-fact-handles disconnected=\"false\" out-identifier=\"facts\"/>", xmlString );

        GetFactHandlesCommand cmd2 = (GetFactHandlesCommand) xstream.fromXML( xmlString );
        Assert.assertEquals("facts", cmd2.getOutIdentifier());
    }

    @Test
    public void testExecutionResults() {

        final Message msg = new Message("Hello World!");
        final FactHandle msgHandle = new DefaultFactHandle( 1,
                null,
                10,
                10,
                20,
                msg );

        final Message msg2 = new Message("Hello World again!");
        final FactHandle msgHandle2 = new DefaultFactHandle( 2,
                null,
                10,
                10,
                20,
                msg2 );

        HashMap<String, Object> factHandles = new LinkedHashMap<String, Object>();
        factHandles.put("first", msgHandle);
        factHandles.put("second", msgHandle2);

        ExecutionResultImpl executionResult = new ExecutionResultImpl();
        executionResult.setFactHandles(factHandles);

        HashMap<String, Object> results = new LinkedHashMap<String, Object>();
        results.put("message1", msg);
        results.put("message2", msg2);

        executionResult.setResults(results);

        String xmlString = xstream.toXML(executionResult);
        Assert.assertEquals(
                "<execution-results>\n" +
                "  <result identifier=\"message1\">\n" +
                "    <org.drools.core.runtime.help.impl.XStreamXMLTest_-Message>\n" +
                "      <msg>Hello World!</msg>\n" +
                "    </org.drools.core.runtime.help.impl.XStreamXMLTest_-Message>\n" +
                "  </result>\n" +
                "  <result identifier=\"message2\">\n" +
                "    <org.drools.core.runtime.help.impl.XStreamXMLTest_-Message>\n" +
                "      <msg>Hello World again!</msg>\n" +
                "    </org.drools.core.runtime.help.impl.XStreamXMLTest_-Message>\n" +
                "  </result>\n" +
                "  <fact-handle identifier=\"first\" external-form=\"0:1:10:10:20:null:NON_TRAIT:org.drools.core.runtime.help.impl.XStreamXMLTest$Message\"/>\n" +
                "  <fact-handle identifier=\"second\" external-form=\"0:2:10:10:20:null:NON_TRAIT:org.drools.core.runtime.help.impl.XStreamXMLTest$Message\"/>\n" +
                "</execution-results>",
                xmlString );

        ExecutionResultImpl executionResult2 = (ExecutionResultImpl) xstream.fromXML( xmlString );
        Assert.assertEquals(executionResult.getFactHandles().size(), executionResult2.getFactHandles().size());
        Assert.assertEquals(executionResult.getResults().size(), executionResult2.getResults().size());
    }

    @Test
    public void testGetFactHandlesExecutionResults() {

        final Message msg = new Message("Hello World!");
        final FactHandle msgHandle = new DefaultFactHandle( 1,
                null,
                10,
                10,
                20,
                msg );

        final Message msg2 = new Message("Hello World again!");
        final FactHandle msgHandle2 = new DefaultFactHandle( 2,
                null,
                10,
                10,
                20,
                msg2 );

        List<FactHandle> factHandleList = new ArrayList<FactHandle>();
        factHandleList.add(msgHandle);
        factHandleList.add(msgHandle2);

        HashMap<String, Object> factHandles = new LinkedHashMap<String, Object>();

        ExecutionResultImpl executionResult = new ExecutionResultImpl();
        executionResult.setFactHandles(factHandles);

        HashMap<String, Object> results = new LinkedHashMap<String, Object>();
        results.put("facts", factHandleList);


        executionResult.setResults(results);

        String xmlString = xstream.toXML(executionResult);
        Assert.assertEquals(
                "<execution-results>\n" +
                        "  <result identifier=\"facts\">\n" +
                        "    <list>\n" +
                        "      <fact-handle external-form=\"0:1:10:10:20:null:NON_TRAIT:org.drools.core.runtime.help.impl.XStreamXMLTest$Message\"/>\n" +
                        "      <fact-handle external-form=\"0:2:10:10:20:null:NON_TRAIT:org.drools.core.runtime.help.impl.XStreamXMLTest$Message\"/>\n" +
                        "    </list>\n" +
                        "  </result>\n" +
                        "</execution-results>",
                xmlString );

        ExecutionResultImpl executionResult2 = (ExecutionResultImpl) xstream.fromXML( xmlString );
        Assert.assertEquals(executionResult.getFactHandles().size(), executionResult2.getFactHandles().size());
        Assert.assertEquals(executionResult.getResults().size(), executionResult2.getResults().size());
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
