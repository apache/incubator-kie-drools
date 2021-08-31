/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.io.impl.ReaderResource;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProcessForEachTest extends AbstractBaseTest {

    @Test
    public void testForEach() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"ForEach\" id=\"org.drools.ForEach\" package-name=\"org.drools\" >\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "    <variables>\n" +
                        "      <variable name=\"collection\" >\n" +
                        "        <type name=\"org.jbpm.process.core.datatype.impl.type.ObjectDataType\" className=\"java.util.List\" />\n" +
                        "      </variable>\n" +
                        "    </variables>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <forEach id=\"2\" name=\"ForEach\" variableName=\"item\" collectionExpression=\"collection\" >\n" +
                        "      <nodes>\n" +
                        "    <actionNode id=\"1\" name=\"Action\" >\n" +
                        "        <action type=\"expression\" dialect=\"java\" >myList.add(item);</action>\n" +
                        "    </actionNode>\n" +
                        "      </nodes>\n" +
                        "      <connections>\n" +
                        "      </connections>\n" +
                        "      <in-ports>\n" +
                        "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
                        "      </in-ports>\n" +
                        "      <out-ports>\n" +
                        "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
                        "      </out-ports>\n" +
                        "    </forEach>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "  </connections>\n" +
                        "</process>");
        builder.add(new ReaderResource(source), ResourceType.DRF);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> myList = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("myList", myList);
        List<String> collection = new ArrayList<String>();
        collection.add("one");
        collection.add("two");
        collection.add("three");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("collection", collection);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.ForEach", params);
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(3, myList.size());
    }

    @Test
    public void testForEachLargeList() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"ForEach\" id=\"org.drools.ForEach\" package-name=\"org.drools\" >\n" +
                        "  <header>\n" +
                        "    <variables>\n" +
                        "      <variable name=\"collection\" >\n" +
                        "        <type name=\"org.jbpm.process.core.datatype.impl.type.ObjectDataType\" className=\"java.util.List\" />\n" +
                        "      </variable>\n" +
                        "    </variables>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <forEach id=\"2\" name=\"ForEach\" variableName=\"item\" collectionExpression=\"collection\" >\n" +
                        "      <nodes>\n" +
                        "        <workItem id=\"1\" name=\"Log\" >\n" +
                        "          <work name=\"Log\" >\n" +
                        "            <parameter name=\"Message\" >\n" +
                        "              <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
                        "            </parameter>\n" +
                        "          </work>\n" +
                        "          <mapping type=\"in\" from=\"item\" to=\"Message\" />" +
                        "        </workItem>\n" +
                        "      </nodes>\n" +
                        "      <connections>\n" +
                        "      </connections>\n" +
                        "      <in-ports>\n" +
                        "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
                        "      </in-ports>\n" +
                        "      <out-ports>\n" +
                        "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
                        "      </out-ports>\n" +
                        "    </forEach>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "  </connections>\n" +
                        "</process>");
        builder.add(new ReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        final List<String> myList = new ArrayList<String>();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Log", new KogitoWorkItemHandler() {
            public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                String message = (String) workItem.getParameter("Message");
                myList.add(message);
                manager.completeWorkItem(workItem.getStringId(), null);
            }

            public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
            }
        });
        List<String> collection = new ArrayList<String>();
        for (int i = 0; i < 10000; i++) {
            collection.add(i + "");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("collection", collection);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.ForEach", params);
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(10000, myList.size());
    }

    @Test
    public void testForEachEmptyList() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"ForEach\" id=\"org.drools.ForEach\" package-name=\"org.drools\" >\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "    <variables>\n" +
                        "      <variable name=\"collection\" >\n" +
                        "        <type name=\"org.jbpm.process.core.datatype.impl.type.ObjectDataType\" className=\"java.util.List\" />\n" +
                        "      </variable>\n" +
                        "    </variables>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <forEach id=\"2\" name=\"ForEach\" variableName=\"item\" collectionExpression=\"collection\" >\n" +
                        "      <nodes>\n" +
                        "    <actionNode id=\"1\" name=\"Action\" >\n" +
                        "        <action type=\"expression\" dialect=\"java\" >myList.add(item);</action>\n" +
                        "    </actionNode>\n" +
                        "      </nodes>\n" +
                        "      <connections>\n" +
                        "      </connections>\n" +
                        "      <in-ports>\n" +
                        "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
                        "      </in-ports>\n" +
                        "      <out-ports>\n" +
                        "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
                        "      </out-ports>\n" +
                        "    </forEach>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "  </connections>\n" +
                        "</process>");
        builder.add(new ReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> myList = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("myList", myList);
        List<String> collection = new ArrayList<String>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("collection", collection);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.ForEach", params);
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testForEachNullList() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"ForEach\" id=\"org.drools.ForEach\" package-name=\"org.drools\" >\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "    <variables>\n" +
                        "      <variable name=\"collection\" >\n" +
                        "        <type name=\"org.jbpm.process.core.datatype.impl.type.ObjectDataType\" className=\"java.util.List\" />\n" +
                        "      </variable>\n" +
                        "    </variables>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <forEach id=\"2\" name=\"ForEach\" variableName=\"item\" collectionExpression=\"collection\" >\n" +
                        "      <nodes>\n" +
                        "    <actionNode id=\"1\" name=\"Action\" >\n" +
                        "        <action type=\"expression\" dialect=\"java\" >myList.add(item);</action>\n" +
                        "    </actionNode>\n" +
                        "      </nodes>\n" +
                        "      <connections>\n" +
                        "      </connections>\n" +
                        "      <in-ports>\n" +
                        "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
                        "      </in-ports>\n" +
                        "      <out-ports>\n" +
                        "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
                        "      </out-ports>\n" +
                        "    </forEach>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "  </connections>\n" +
                        "</process>");
        builder.add(new ReaderResource(source), ResourceType.DRF);
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> myList = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("myList", myList);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.ForEach");
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testForEachWithEventNode() {
        Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                        "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                        "         type=\"RuleFlow\" name=\"ForEach\" id=\"org.drools.ForEach\" package-name=\"org.drools\" >\n" +
                        "  <header>\n" +
                        "    <globals>\n" +
                        "      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
                        "    </globals>\n" +
                        "    <variables>\n" +
                        "      <variable name=\"collection\" >\n" +
                        "        <type name=\"org.jbpm.process.core.datatype.impl.type.ObjectDataType\" className=\"java.util.List\" />\n" +
                        "      </variable>\n" +
                        "    </variables>\n" +
                        "  </header>\n" +
                        "\n" +
                        "  <nodes>\n" +
                        "    <forEach id=\"2\" name=\"ForEach\" variableName=\"item\" collectionExpression=\"collection\" >\n" +
                        "      <nodes>\n" +
                        "        <eventNode id=\"2\" name=\"OrderPreparation Response\" >\n" +
                        "          <eventFilters>\n" +
                        "            <eventFilter type=\"eventType\" eventType=\"MyEvent\" />\n" +
                        "          </eventFilters>\n" +
                        "        </eventNode>\n" +
                        "        <actionNode id=\"3\" name=\"ORDER_PREP\" >\n" +
                        "          <action type=\"expression\" dialect=\"java\" >System.out.println(\"action1\");</action>\n" +
                        "        </actionNode>\n" +
                        "        <join id=\"4\" name=\"Join\" type=\"1\" />\n" +
                        "        <actionNode id=\"5\" name=\"ORDER_VALIDATION\" >\n" +
                        "          <action type=\"expression\" dialect=\"java\" >System.out.println(\"action2\");myList.add(item);</action>\n" +
                        "        </actionNode>\n" +
                        "      </nodes>\n" +
                        "      <connections>\n" +
                        "        <connection from=\"3\" to=\"4\" />\n" +
                        "        <connection from=\"2\" to=\"4\" />\n" +
                        "        <connection from=\"4\" to=\"5\" />\n" +
                        "      </connections>\n" +
                        "      <in-ports>\n" +
                        "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"3\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
                        "      </in-ports>\n" +
                        "      <out-ports>\n" +
                        "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"5\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
                        "      </out-ports>\n" +
                        "    </forEach>\n" +
                        "    <start id=\"1\" name=\"Start\" />\n" +
                        "    <end id=\"3\" name=\"End\" />\n" +
                        "  </nodes>\n" +
                        "\n" +
                        "  <connections>\n" +
                        "    <connection from=\"1\" to=\"2\" />\n" +
                        "    <connection from=\"2\" to=\"3\" />\n" +
                        "  </connections>\n" +
                        "</process>");
        builder.add(new ReaderResource(source), ResourceType.DRF);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> myList = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("myList", myList);
        List<String> collection = new ArrayList<String>();
        collection.add("one");
        collection.add("two");
        collection.add("three");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("collection", collection);
        KogitoProcessInstance processInstance = kruntime.startProcess("org.drools.ForEach", params);
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        processInstance.signalEvent("MyEvent", null);
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(3, myList.size());
    }

}
