/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.testdata.util;

import java.io.Serializable;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang.SerializationUtils;

public class SerializationTestUtils {

    public static <T> void serializeAndDeserializeWithAll(T input, OutputAsserter<T> outputAsserter) {
        outputAsserter.assertOutput(serializeAndDeserializeWithJavaSerialization(input));
        outputAsserter.assertOutput(serializeAndDeserializeWithXStream(input));
    }

    public static <T> T serializeAndDeserializeWithJavaSerialization(T input) {
        byte[] bytes = SerializationUtils.serialize((Serializable) input);
        return (T) SerializationUtils.deserialize(bytes);
    }

    public static <T> T serializeAndDeserializeWithXStream(T input) {
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        String xmlString = xStream.toXML(input);
        return (T) xStream.fromXML(xmlString);
    }

    private SerializationTestUtils() {
    }

    public static interface OutputAsserter<T> {

        void assertOutput(T output);

    }

}
