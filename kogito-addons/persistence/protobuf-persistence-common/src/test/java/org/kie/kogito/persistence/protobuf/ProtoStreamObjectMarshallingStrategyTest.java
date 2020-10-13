/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.persistence.protobuf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

public class ProtoStreamObjectMarshallingStrategyTest {

    private ProtoStreamObjectMarshallingStrategy protoStreamMarshallerStrategy = new ProtoStreamObjectMarshallingStrategy(null);    
    
    @Test
    public void testStringMarshalling() throws Exception {
        
        String value = "here is simple string value";
        
        boolean accepted = protoStreamMarshallerStrategy.accept(value);
        assertTrue(accepted, "String type should be accepted");
        
        byte[] data = protoStreamMarshallerStrategy.marshal(null, null, value);
        assertNotNull(data, "Marshalled content should not be null");
        
        Object returned = protoStreamMarshallerStrategy.unmarshal("kogito.String", null, null, data, this.getClass().getClassLoader());
        assertNotNull(returned, "Unmarshalled value should not be null");
        assertEquals(value, returned, "Values should be the same");
        
    }
    
    @Test
    public void testIntegerMarshalling() throws Exception {
        
        Integer value = 25;
        
        boolean accepted = protoStreamMarshallerStrategy.accept(value);
        assertTrue(accepted, "Integer type should be accepted");
        
        byte[] data = protoStreamMarshallerStrategy.marshal(null, null, value);
        assertNotNull(data, "Marshalled content should not be null");
        
        Object returned = protoStreamMarshallerStrategy.unmarshal("kogito.Integer", null, null, data, this.getClass().getClassLoader());
        assertNotNull(returned, "Unmarshalled value should not be null");
        assertEquals(value, returned, "Values should be the same");
        
    }

    @Test
    public void testLongMarshalling() throws Exception {
        
        Long value = 555L;
        
        boolean accepted = protoStreamMarshallerStrategy.accept(value);
        assertTrue(accepted, "Long type should be accepted");
        
        byte[] data = protoStreamMarshallerStrategy.marshal(null, null, value);
        assertNotNull(data, "Marshalled content should not be null");
        
        Object returned = protoStreamMarshallerStrategy.unmarshal("kogito.Long", null, null, data, this.getClass().getClassLoader());
        assertNotNull(returned, "Unmarshalled value should not be null");
        assertEquals(value, returned, "Values should be the same");
        
    }

    @Test
    public void testDoubleMarshalling() throws Exception {
        
        Double value = 55.5;
        
        boolean accepted = protoStreamMarshallerStrategy.accept(value);
        assertTrue(accepted, "Double type should be accepted");
        
        byte[] data = protoStreamMarshallerStrategy.marshal(null, null, value);
        assertNotNull(data, "Marshalled content should not be null");
        
        Object returned = protoStreamMarshallerStrategy.unmarshal("kogito.Double", null, null, data, this.getClass().getClassLoader());
        assertNotNull(returned, "Unmarshalled value should not be null");
        assertEquals(value, returned, "Values should be the same");
        
    }

    @Test
    public void testFloatMarshalling() throws Exception {
        
        Float value = 55.5f;
        
        boolean accepted = protoStreamMarshallerStrategy.accept(value);
        assertTrue(accepted, "Float type should be accepted");
        
        byte[] data = protoStreamMarshallerStrategy.marshal(null, null, value);
        assertNotNull(data, "Marshalled content should not be null");
        
        Object returned = protoStreamMarshallerStrategy.unmarshal("kogito.Float", null, null, data, this.getClass().getClassLoader());
        assertNotNull(returned, "Unmarshalled value should not be null");
        assertEquals(value, returned, "Values should be the same");
        
    }    

    @Test
    public void testBooleanMarshalling() throws Exception {
        
        Boolean value = true;
        
        boolean accepted = protoStreamMarshallerStrategy.accept(value);
        assertTrue(accepted, "Boolean type should be accepted");
        
        byte[] data = protoStreamMarshallerStrategy.marshal(null, null, value);
        assertNotNull(data, "Marshalled content should not be null");
        
        Object returned = protoStreamMarshallerStrategy.unmarshal("kogito.Boolean", null, null, data, this.getClass().getClassLoader());
        assertNotNull(returned, "Unmarshalled value should not be null");
        assertEquals(value, returned, "Values should be the same");
        
    }

    @Test
    public void testDateMarshalling() throws Exception {
        
        Date value = new Date();
        
        boolean accepted = protoStreamMarshallerStrategy.accept(value);
        assertTrue(accepted, "Date type should be accepted");
        
        byte[] data = protoStreamMarshallerStrategy.marshal(null, null, value);
        assertNotNull(data, "Marshalled content should not be null");
        
        Object returned = protoStreamMarshallerStrategy.unmarshal("kogito.Date", null, null, data, this.getClass().getClassLoader());
        assertNotNull(returned, "Unmarshalled value should not be null");
        assertEquals(value, returned, "Values should be the same");
        
    }
}
