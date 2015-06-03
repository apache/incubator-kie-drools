/*
 * Copyright 2015 JBoss Inc
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
import org.drools.core.command.runtime.rule.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

}
