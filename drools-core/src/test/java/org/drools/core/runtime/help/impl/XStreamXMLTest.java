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
import org.junit.Assert;
import org.junit.Test;

public class XStreamXMLTest {

    @Test
    public void testMarshallStartProcessCmd() {
        XStream xstream = new XStream();
        xstream.registerConverter(new XStreamXML.StartProcessConvert(xstream));
        StartProcessCommand cmd = new StartProcessCommand("some-process-id", "some-out-identifier");
        String xmlString = xstream.toXML(cmd);
        Assert.assertTrue(xmlString.contains("processId=\"some-process-id\""));
        Assert.assertTrue(xmlString.contains("out-identifier=\"some-out-identifier\""));
    }

    @Test
    public void testMarshallStartProcessCmdWithNoOutIdentifier() {
        // the "out-identifier" is optional -> the marshalling should succeed even if it is null
        XStream xstream = new XStream();
        xstream.registerConverter(new XStreamXML.StartProcessConvert(xstream));
        StartProcessCommand cmd = new StartProcessCommand("some-process-id");
        String xmlString = xstream.toXML(cmd);
        Assert.assertTrue(xmlString.contains("processId=\"some-process-id\""));
    }

    @Test
    public void testUnMarshallStartProcessCmdWithNoOutIdentifier() {
        // the "out-identifier" is optional -> the unmarshalling should create valid object
        XStream xstream = new XStream();
        xstream.registerConverter(new XStreamXML.StartProcessConvert(xstream));
        Object obj = xstream.fromXML(
                "<org.drools.core.command.runtime.process.StartProcessCommand processId=\"some-process-id\"/>");
        Assert.assertEquals(StartProcessCommand.class, obj.getClass());
        StartProcessCommand cmd = (StartProcessCommand)obj;
        Assert.assertEquals("some-process-id", cmd.getProcessId());
    }

}
