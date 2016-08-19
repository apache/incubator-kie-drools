/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command.runtime.rule;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.drools.core.ClassObjectFilter;
import org.drools.core.ClassObjectSerializationFilter;
import org.drools.core.base.RuleNameEndsWithAgendaFilter;
import org.drools.core.base.RuleNameEqualsAgendaFilter;
import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.GetGlobalCommand;
import org.drools.core.command.runtime.SetGlobalCommand;
import org.drools.core.command.runtime.process.AbortWorkItemCommand;
import org.drools.core.command.runtime.process.CompleteWorkItemCommand;
import org.drools.core.command.runtime.process.SignalEventCommand;
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.DisconnectedFactHandle;
import org.drools.core.xml.jaxb.util.JaxbListWrapper;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.command.Setter;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.FactHandle;

public class CommandSerializationTest {

    private Class<?>[] annotatedJaxbClasses = { JaxbListWrapper.class };

    // HELPER METHODS -------------------------------------------------------------------------------------------------------------

    private void verifyDisconnectedFactHandle( DisconnectedFactHandle orig, DisconnectedFactHandle copy ) {
        assertNotNull("copy disconnected fact handle is null", copy);
        assertEquals("id", orig.getId(), copy.getId());
        assertEquals("identity hash code", orig.getIdentityHashCode(), copy.getIdentityHashCode());
        assertEquals("object hash code", orig.getObjectHashCode(), copy.getObjectHashCode());
        assertEquals("recency", orig.getRecency(), copy.getRecency());
        assertEquals("entry point id", orig.getEntryPointId(), copy.getEntryPointId());
        assertEquals("trait type", orig.getTraitType(), copy.getTraitType());
    }

    private <T> T roundTrip( Object obj ) throws Exception {
        Class[] classes = { obj.getClass() };
        JAXBContext ctx = getJaxbContext(classes);
        String xmlOut = marshall(ctx, obj);
        return unmarshall(ctx, xmlOut);
    }

    private <T> T unmarshall( JAXBContext ctx, String xmlIn ) throws Exception {
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlIn.getBytes(Charset.forName("UTF-8")));
        Object out = ctx.createUnmarshaller().unmarshal(xmlStrInputStream);
        return (T) out;
    }

    private String marshall( JAXBContext ctx, Object obj ) throws Exception {
        StringWriter writer = new StringWriter();
        ctx.createMarshaller().marshal(obj, writer);
        return writer.getBuffer().toString();
    }

    private JAXBContext getJaxbContext( Class<?>... classes ) throws Exception {
        List<Class<?>> jaxbClassList = new ArrayList<Class<?>>();
        jaxbClassList.addAll(Arrays.asList(classes));
        jaxbClassList.addAll(Arrays.asList(annotatedJaxbClasses));
        Class<?>[] jaxbClasses = jaxbClassList.toArray(new Class[jaxbClassList.size()]);
        return JAXBContext.newInstance(jaxbClasses);
    }

    // TESTS ----------------------------------------------------------------------------------------------------------------------

    @Test
    public void updateCommandTest() throws Exception {
        DisconnectedFactHandle discFactHandle = new DisconnectedFactHandle(2, 3, 4, 5l, "entry-point-id", "str-obj", true);

        DisconnectedFactHandle copyDiscFactHandle = roundTrip(discFactHandle);
        verifyDisconnectedFactHandle(discFactHandle, copyDiscFactHandle);

        UpdateCommand cmd = new UpdateCommand(discFactHandle, "new-str-object");
        UpdateCommand copyCmd = roundTrip(cmd);

        verifyDisconnectedFactHandle(discFactHandle, copyCmd.getHandle());
        assertEquals("entry point", cmd.getEntryPoint(), copyCmd.getEntryPoint());
        assertEquals("object", cmd.getObject(), copyCmd.getObject());
    }

    @Test
    public void insertObjectCommandTest() throws Exception {
        InsertObjectCommand cmd = new InsertObjectCommand("obj", "out-id");
        cmd.setReturnObject(false);
        cmd.setEntryPoint("entry-point");

        InsertObjectCommand copyCmd = roundTrip(cmd);

        assertEquals( "object", cmd.getObject(), copyCmd.getObject());
        assertEquals( "out id", cmd.getOutIdentifier(), copyCmd.getOutIdentifier() );
        assertEquals( "return obj", cmd.isReturnObject(), copyCmd.isReturnObject() );
        assertEquals( "entry point", cmd.getEntryPoint(), copyCmd.getEntryPoint() );
        assertEquals( "disconnected", cmd.isDisconnected(), copyCmd.isDisconnected() );
    }

    @Test
    public void insertObjectCommandListTest() throws Exception {
        List<String> objectList = new ArrayList<String>();
        objectList.add("obj");
        InsertObjectCommand cmd = new InsertObjectCommand(objectList, "out-id");

        InsertObjectCommand copyCmd = roundTrip(cmd);

        assertNotNull(copyCmd);
        assertThat(copyCmd.getObject(), is(instanceOf(List.class)));
        assertEquals( "object", cmd.getObject(), copyCmd.getObject());
    }

    @Test
    public void insertObjectCommandEmptyListTest() throws Exception {
        List<String> objectList = new ArrayList<String>();
        objectList.add("one-element");
        InsertObjectCommand cmd = new InsertObjectCommand(objectList, "out-id");

        // test list with 1 element
        InsertObjectCommand copyCmd = roundTrip(cmd);

        assertNotNull(copyCmd);
        assertThat(copyCmd.getObject(), is(instanceOf(List.class)));
        assertEquals( "object", cmd.getObject(), copyCmd.getObject());

        // test empty list
        objectList.clear();
        copyCmd = roundTrip(cmd);

        assertNotNull(copyCmd);
        assertThat(copyCmd.getObject(), is(instanceOf(List.class)));
        assertEquals( "object", cmd.getObject(), copyCmd.getObject());

    }

    private static final Random random = new Random();

    private static String randomString() {
        return UUID.randomUUID().toString();
    }

    @Test
    @Ignore
    public void batchExecutionImplSerializationTest() throws Exception {

        DefaultFactHandle factHandle = new DefaultFactHandle(13, "entry-point-id",
                                                             42, 84, 400l, "fact");

        BatchExecutionCommandImpl batchCmd = new BatchExecutionCommandImpl();
        batchCmd.setLookup("lookup");
        {
            AbortWorkItemCommand cmd = new AbortWorkItemCommand(23l);

            batchCmd.getCommands().add(cmd);
        }
        {
            String externalForm = factHandle.toExternalForm();
            assertEquals( "FactHandle string", externalForm,
                          DisconnectedFactHandle.newFrom(factHandle).toExternalForm() );
            DeleteCommand cmd = new DeleteCommand(factHandle);

            batchCmd.getCommands().add(cmd);
        }
        {
            GetGlobalCommand cmd = new GetGlobalCommand("global-id");
            cmd.setOutIdentifier("out-id");

            batchCmd.getCommands().add(cmd);
        }
        {
            SetGlobalCommand cmd = new SetGlobalCommand("global-id", new Integer(23));
            cmd.setOutIdentifier("out-id");

            batchCmd.getCommands().add(cmd);
        }
        {
            InsertElementsCommand cmd = new InsertElementsCommand();
            cmd.setEntryPoint("entry-point");
            cmd.setOutIdentifier("out-id");
            cmd.setReturnObject(true);
            Map<String, Object> mapObj = new HashMap<String, Object>();
            mapObj.put("key", "value");
            List<Object> objects = new ArrayList<Object>(1);
            objects.add(mapObj);
            cmd.setObjects(objects);

            batchCmd.getCommands().add(cmd);
        }
        {
            QueryCommand cmd = new QueryCommand();
            List<Object> args = new ArrayList<Object>(3);
            args.add("this");
            args.add(42);
            args.add("other");
            cmd.setArguments(args);
            cmd.setName("query-name");
            cmd.setOutIdentifier("out-id");

            batchCmd.getCommands().add(cmd);
        }
        {
            InsertObjectCommand cmd = new InsertObjectCommand();
            cmd.setEntryPoint("entry-point");
            cmd.setOutIdentifier("out-id");
            cmd.setReturnObject(true);
            cmd.setObject("object");

            batchCmd.getCommands().add(cmd);
        }
        {
            ModifyCommand cmd = new ModifyCommand();
            cmd.setFactHandle(DisconnectedFactHandle.newFrom(factHandle));
            List<Setter> setters = new ArrayList<Setter>(2);
            Setter setter = new Setter() {

                @Override
                public String getValue() {
                    return "blue";
                }

                @Override
                public String getAccessor() {
                    return "heart";
                }
            };
            setters.add(setter);
            setter = new Setter() {

                @Override
                public String getValue() {
                    return "hot";
                }

                @Override
                public String getAccessor() {
                    return "fingers";
                }
            };
            setters.add(setter);
            cmd.setSetters(setters);

            batchCmd.getCommands().add(cmd);
        }
        {
            GetObjectCommand cmd = new GetObjectCommand(factHandle, "out-id");

            batchCmd.getCommands().add(cmd);
        }

        // TODO: implement serialization for agenda filters
        {
            AgendaFilter [] filters = new AgendaFilter[4];
            filters[0] = new RuleNameEndsWithAgendaFilter("suffix", false);
            filters[1] = new RuleNameEqualsAgendaFilter("name", true);
            filters[2] = new RuleNameMatchesAgendaFilter("regexp", false);
            filters[3] = new RuleNameStartsWithAgendaFilter("prefix", false);

            for( AgendaFilter filter : filters ) {
                FireAllRulesCommand cmd = new FireAllRulesCommand(randomString(), random.nextInt(1000), filter);
                batchCmd.getCommands().add(cmd);
            }
        }
        {
            AgendaFilter[] filters = new AgendaFilter[4];
            filters[0] = new RuleNameEndsWithAgendaFilter("suffix", false);
            filters[1] = new RuleNameEqualsAgendaFilter("name", true);
            filters[2] = new RuleNameMatchesAgendaFilter("regexp", false);
            filters[3] = new RuleNameStartsWithAgendaFilter("prefix", false);

            for (AgendaFilter filter : filters) {
                FireUntilHaltCommand cmd = new FireUntilHaltCommand(filter);
                batchCmd.getCommands().add(cmd);
            }
        }
        {
            Map<String, Object> results = new HashMap<String, Object>(1);
            List<String> resultValList = new ArrayList<String>(2);
            resultValList.add("yellow");
            resultValList.add("chances");
            results.put("list", resultValList);
            CompleteWorkItemCommand cmd = new CompleteWorkItemCommand(random.nextInt(1000), results);

            batchCmd.getCommands().add(cmd);
        }
        {
            ClassObjectFilter filter = new ClassObjectFilter(String.class);
            GetObjectsCommand cmd = new GetObjectsCommand(filter, "out-id");

            batchCmd.getCommands().add(cmd);
        }
        {
            AgendaGroupSetFocusCommand cmd = new AgendaGroupSetFocusCommand(randomString());
            batchCmd.getCommands().add(cmd);
        }
        {
            ClearActivationGroupCommand cmd = new ClearActivationGroupCommand(randomString());
            batchCmd.getCommands().add(cmd);
        }
        {
            ClearAgendaCommand cmd = new ClearAgendaCommand();
            batchCmd.getCommands().add(cmd);
        }
        {
            ClearAgendaGroupCommand cmd = new ClearAgendaGroupCommand(randomString());
            batchCmd.getCommands().add(cmd);
        }
        {
            ClearRuleFlowGroupCommand cmd = new ClearRuleFlowGroupCommand(randomString());
            batchCmd.getCommands().add(cmd);
        }


        BatchExecutionCommandImpl batchCmdCopy = roundTrip(batchCmd);
        assertEquals( "Batch cmd lookup", batchCmd.getLookup(), batchCmdCopy.getLookup() );
        assertEquals( "Batch cmd num commands", batchCmd.getCommands().size(), batchCmdCopy.getCommands().size() );
        // How many times have I written this type of code?
        // This code should use the utility in kie-test-util when it finally gets moved there..
        for( GenericCommand copyCmd : batchCmdCopy.getCommands() ) {
            for( GenericCommand origCmd : batchCmd.getCommands() ) {
                Class cmdClass = origCmd.getClass();
                if( copyCmd.getClass().equals(cmdClass) ) {
                    if( cmdClass.equals(DeleteCommand.class) ) {
                        compareFactHandles(((DeleteCommand) origCmd).getFactHandle(), ((DeleteCommand) copyCmd).getFactHandle(),
                                           DeleteCommand.class );
                    } else if( cmdClass.equals(FireAllRulesCommand.class) ) {
                        AgendaFilter origFilter = ((FireAllRulesCommand) origCmd).getAgendaFilter();
                        AgendaFilter copyFilter = ((FireAllRulesCommand) copyCmd).getAgendaFilter();
                        if( ! origFilter.getClass().equals(copyFilter.getClass()) ) {
                            continue;
                        }
                        Class agendaFilterClass = origFilter.getClass();
                        for( Field agendaFilterField : agendaFilterClass.getDeclaredFields() ) {
                            agendaFilterField.setAccessible(true);
                            Object afFieldOrigVal = agendaFilterField.get(origFilter);
                            Object afFieldCopyVal = agendaFilterField.get(copyFilter);
                            if( afFieldOrigVal instanceof Pattern ) {
                                afFieldOrigVal = ((Pattern) afFieldOrigVal).pattern();
                                afFieldCopyVal = ((Pattern) afFieldCopyVal).pattern();
                            }
                            assertEquals( agendaFilterClass.getSimpleName() + "." + agendaFilterField.getName(),
                                          afFieldOrigVal,
                                          afFieldCopyVal);
                        }
                        assertEquals( FireAllRulesCommand.class.getSimpleName() + ".max",
                                      ((FireAllRulesCommand) origCmd).getMax(),
                                      ((FireAllRulesCommand) copyCmd).getMax() );
                        assertEquals( FireAllRulesCommand.class.getSimpleName() + ".outIdentifier",
                                      ((FireAllRulesCommand) origCmd).getOutIdentifier(),
                                      ((FireAllRulesCommand) copyCmd).getOutIdentifier());
                    } else if (cmdClass.equals(FireUntilHaltCommand.class)) {
                        AgendaFilter origFilter = ((FireUntilHaltCommand) origCmd).getAgendaFilter();
                        AgendaFilter copyFilter = ((FireUntilHaltCommand) copyCmd).getAgendaFilter();
                        if (!origFilter.getClass().equals(copyFilter.getClass())) {
                            continue;
                        }
                        Class agendaFilterClass = origFilter.getClass();
                        for (Field agendaFilterField : agendaFilterClass.getDeclaredFields()) {
                            agendaFilterField.setAccessible(true);
                            Object afFieldOrigVal = agendaFilterField.get(origFilter);
                            Object afFieldCopyVal = agendaFilterField.get(copyFilter);
                            if (afFieldOrigVal instanceof Pattern) {
                                afFieldOrigVal = ((Pattern) afFieldOrigVal).pattern();
                                afFieldCopyVal = ((Pattern) afFieldCopyVal).pattern();
                            }
                            assertEquals(agendaFilterClass.getSimpleName() + "." + agendaFilterField.getName(), afFieldOrigVal, afFieldCopyVal);
                        }
                    } else {
                        for( Field cmdField : cmdClass.getDeclaredFields() ) {
                            cmdField.setAccessible(true);
                            if( Modifier.isTransient(cmdField.getModifiers()) ) {
                                continue;
                            }
                            Object origVal = cmdField.get(origCmd);
                            assertNotNull( cmdClass.getSimpleName() + "." + cmdField.getName(), origVal );
                            Object copyVal = cmdField.get(copyCmd);
                            assertNotNull( "Round-tripped " + cmdClass.getSimpleName() + "." + cmdField.getName(), copyVal );
                            if( origVal instanceof FactHandle ) {
                                compareFactHandles((FactHandle) origVal, (FactHandle) copyVal, cmdClass);
                            } else if( origVal instanceof ClassObjectSerializationFilter ) {
                                assertEquals( "Original compared to Round-tripped " + cmdClass.getSimpleName() + "." + cmdField.getName(),
                                              ((ClassObjectSerializationFilter) origVal).getClass(),
                                              ((ClassObjectSerializationFilter) copyVal).getClass());
                            } else if( origVal instanceof List ) {
                                List origList = (List) origVal;
                                if( ((List) copyVal).isEmpty() ) {
                                    assertTrue( "Original compared to Round-tripped " + cmdClass.getSimpleName() + "." + cmdField.getName(),
                                                origList.isEmpty() );
                                } else {
                                    if( origList.get(0) instanceof Setter ) {
                                        for( Object obj : (List) origVal )  {
                                            assertTrue( "Expected a " + Setter.class.getSimpleName() + " instance (not " + obj.getClass().getSimpleName() + " in " + cmdClass.getSimpleName() + "." + cmdField.getName(),
                                                        obj instanceof Setter );
                                            Iterator<Object> iter = ((List) copyVal).iterator();
                                            while( iter.hasNext() ) {
                                                Setter copySetter = (Setter) iter.next();
                                                if( ((Setter)obj).getAccessor().equals(copySetter.getAccessor()) ) {
                                                    assertEquals( "Original compared to Round-tripped " + cmdClass.getSimpleName() + "." + cmdField.getName(),
                                                                  ((Setter) obj).getValue(), copySetter.getValue());
                                                    iter.remove();
                                                }
                                            }
                                        }
                                        assertTrue( "Original compared to Round-tripped " + cmdClass.getSimpleName() + "." + cmdField.getName(),
                                                    ((List) copyVal).isEmpty() );
                                    } else if( origList.get(0) instanceof Map ) {
                                        Map copyMap = (Map) ((List) copyVal).get(0);
                                        for( Object entry : ((Map) origList.get(0)).entrySet() ) {
                                            assertTrue( "Original compared to Round-tripped " + cmdClass.getSimpleName() + "." + cmdField.getName(),
                                                        (copyMap).containsKey(((Entry) entry).getKey()) );
                                        }
                                    }
                                }
                            } else {
                                assertTrue( "Original compared to Round-tripped " + cmdClass.getSimpleName() + "." + cmdField.getName(),
                                            origVal.equals(copyVal));
                            }
                        }
                    }
               }
            }
        }



        // verify that BatchExecutionCommandImpl.commands has been filled with all
        // of the different types
        Field commandsField = BatchExecutionCommandImpl.class.getDeclaredField("commands");
        XmlElements xmlElemsAnno = commandsField.getAnnotation(XmlElements.class);
        List<Class> cmdTypes = new ArrayList<Class>(xmlElemsAnno.value().length);
        for( XmlElement xmlElem : xmlElemsAnno.value() ) {
            cmdTypes.add(xmlElem.type());
        }

        cmdTypes.remove(SignalEventCommand.class);  // already thoroughly tested..
        cmdTypes.remove(StartProcessCommand.class); // already thoroughly tested..
        for( GenericCommand cmd : batchCmd.getCommands() ) {
           cmdTypes.remove(cmd.getClass());
        }
        String cmdInstName = cmdTypes.isEmpty() ? "null" : cmdTypes.get(0).getSimpleName();
        assertTrue( "Please add a " + cmdInstName + " instance to the " + BatchExecutionCommandImpl.class.getSimpleName() + " commands!",
                    cmdTypes.isEmpty());

        // other tests for this as part of the REST integration tests..
    }

    private static void compareFactHandles( FactHandle orig, FactHandle copy, Class cmdClass) {
        String origFHString = orig.toExternalForm();
        origFHString = origFHString.substring(0, origFHString.lastIndexOf(":"));
        String copyFHString = copy.toExternalForm();
        copyFHString = copyFHString.substring(0, copyFHString.lastIndexOf(":"));
        assertEquals( cmdClass.getSimpleName() + ".facthandle string",
                      origFHString, copyFHString);
    }
}
