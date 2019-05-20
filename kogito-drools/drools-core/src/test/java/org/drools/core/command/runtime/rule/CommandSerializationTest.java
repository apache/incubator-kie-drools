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

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBContext;

import org.drools.core.common.DisconnectedFactHandle;
import org.drools.core.xml.jaxb.util.JaxbListWrapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommandSerializationTest {

    private Class<?>[] annotatedJaxbClasses = { JaxbListWrapper.class };

    // HELPER METHODS -------------------------------------------------------------------------------------------------------------

    private void verifyDisconnectedFactHandle( DisconnectedFactHandle orig, DisconnectedFactHandle copy ) {
        assertNotNull(copy);
        assertEquals(orig.getId(), copy.getId());
        assertEquals(orig.getIdentityHashCode(), copy.getIdentityHashCode());
        assertEquals(orig.getObjectHashCode(), copy.getObjectHashCode());
        assertEquals(orig.getRecency(), copy.getRecency());
        assertEquals(orig.getEntryPointId(), copy.getEntryPointId());
        assertEquals(orig.getTraitType(), copy.getTraitType());
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
        assertEquals(cmd.getObject(), copyCmd.getObject(), "entry point");
    }

    @Test
    public void insertObjectCommandTest() throws Exception {
        InsertObjectCommand cmd = new InsertObjectCommand("obj", "out-id");
        cmd.setReturnObject(false);
        cmd.setEntryPoint("entry-point");

        InsertObjectCommand copyCmd = roundTrip(cmd);

        assertEquals(cmd.getObject(), copyCmd.getObject());
        assertEquals(cmd.getOutIdentifier(), copyCmd.getOutIdentifier() );
        assertEquals(cmd.isReturnObject(), copyCmd.isReturnObject() );
        assertEquals(cmd.getEntryPoint(), copyCmd.getEntryPoint() );
        assertEquals(cmd.isDisconnected(), copyCmd.isDisconnected() );
    }

    @Test
    public void insertObjectCommandListTest() throws Exception {
        List<String> objectList = new ArrayList<String>();
        objectList.add("obj");
        InsertObjectCommand cmd = new InsertObjectCommand(objectList, "out-id");

        InsertObjectCommand copyCmd = roundTrip(cmd);

        assertNotNull(copyCmd);
        assertThat(copyCmd.getObject()).isInstanceOf(List.class);
        assertEquals(cmd.getObject(), copyCmd.getObject());
    }

    @Test
    public void insertObjectCommandEmptyListTest() throws Exception {
        List<String> objectList = new ArrayList<String>();
        objectList.add("one-element");
        InsertObjectCommand cmd = new InsertObjectCommand(objectList, "out-id");

        // test list with 1 element
        InsertObjectCommand copyCmd = roundTrip(cmd);

        assertNotNull(copyCmd);
        assertThat(copyCmd.getObject()).isInstanceOf(List.class);
        assertEquals(cmd.getObject(), copyCmd.getObject());

        // test empty list
        objectList.clear();
        copyCmd = roundTrip(cmd);

        assertNotNull(copyCmd);
        assertThat(copyCmd.getObject()).isInstanceOf(List.class);
        assertEquals(cmd.getObject(), copyCmd.getObject());

    }

}
