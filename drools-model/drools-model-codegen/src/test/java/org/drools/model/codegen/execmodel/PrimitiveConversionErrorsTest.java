/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.model.codegen.ExecutableModelProject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

// DROOLS-6506
public class PrimitiveConversionErrorsTest {

    private static final String RULE_TEMPLATE = "" +
            "package com.example.rules\n" +
            "\n" +
            "import java.util.*\n" +
            "\n" +
            "import com.example.*\n" +
            "import " + ClassWithValue.class.getCanonicalName() + ";\n" +
            "\n" +
            "rule \"Rule that breaks ExecutableModel\"\n" +
            "    when\n" +
            "     $values:  List()\n" +
            "  codef:   ClassWithValue(\n" +
            "       $value: value<value_type> != <test_value>\n" +
            "      )\n" +
            "      \n" +
            "  exists   ClassWithValue(\n" +
            "       value<value_type> != $value\n" +
            "      )\n" +
            "    then\n" +
            "     $values.add($value);\n" +
            "end";

    public static Stream<Arguments> data() {
        return Stream.of(
                arguments(Float.class, Float.class, 4f, 5f, 6f),
                arguments(Float.class, Double.class, 4d, 5f, 6f),
                arguments(Float.class, Integer.class, 4, 5f, 6f),
                arguments(Float.class, Short.class, (short) 4, 5f, 6f),
                arguments(Float.class, Byte.class, (byte) 4, 5f, 6f),

                arguments(Double.class, Float.class, 4f, 5d, 6d),
                arguments(Double.class, Double.class, 4d, 5d, 6d),
                arguments(Double.class, Integer.class, 4, 5d, 6d),
                arguments(Double.class, Short.class, (short) 4, 5d, 6d),
                arguments(Double.class, Byte.class, (byte) 4, 5d, 6d),

                arguments(Integer.class, Float.class, 4f, 5, 6),
                arguments(Integer.class, Double.class, 4d, 5, 6),
                arguments(Integer.class, Integer.class, 4, 5, 6),
                arguments(Integer.class, Short.class, (short) 4, 5, 6),
                arguments(Integer.class, Byte.class, (byte) 4, 5, 6),

                arguments(Short.class, Float.class, 4f, (short) 5, (short) 6),
                arguments(Short.class, Double.class, 4d, (short) 5, (short) 6),
                arguments(Short.class, Integer.class, 4, (short) 5, (short) 6),
                arguments(Short.class, Short.class, (short) 4, (short) 5, (short) 6),
                arguments(Short.class, Byte.class, (byte) 4, (short) 5, (short) 6),

                arguments(Byte.class, Float.class, 4f, (byte) 5, (byte) 6),
                arguments(Byte.class, Double.class, 4d, (byte) 5, (byte) 6),
                arguments(Byte.class, Integer.class, 4, (byte) 5, (byte) 6),
                arguments(Byte.class, Short.class, (short) 4, (byte) 5, (byte) 6),
                arguments(Byte.class, Byte.class, (byte) 4, (byte) 5, (byte) 6));
    }

    @ParameterizedTest(name="value{0} != {2}: Method Returns {0}, test against {1}")
    @MethodSource("data")
    public void withExecutableModel(Class<?> valueType, Class<?> valueCheckType, Object valueCheck, Object valueA, Object valueB) throws IOException {

        KieBase kbase = loadRules(valueType, valueCheck, true);

        KieSession session = kbase.newKieSession();
        List<Object> values = new ArrayList<>();
        ClassWithValue ca = makeClassWithValue(valueA);
        ClassWithValue cb = makeClassWithValue(valueB);

        session.insert(values);
        session.insert(ca);
        session.insert(cb);

        session.fireAllRules();

        assertThat(values.size()).isEqualTo(2);
        assertThat(values.contains(valueA)).isTrue();
        assertThat(values.contains(valueB)).isTrue();
    }

    @ParameterizedTest(name="value{0} != {2}: Method Returns {0}, test against {1}")
    @MethodSource("data")
    public void withoutExecutableModel(Class<?> valueType, Class<?> valueCheckType, Object valueCheck, Object valueA, Object valueB) throws IOException {

        KieBase kbase = loadRules(valueType, valueCheck, false);

        KieSession session = kbase.newKieSession();
        List<Object> values = new ArrayList<>();
        ClassWithValue ca = makeClassWithValue(valueA);
        ClassWithValue cb = makeClassWithValue(valueB);

        session.insert(values);
        session.insert(ca);
        session.insert(cb);

        session.fireAllRules();

        assertThat(values.size()).isEqualTo(2);
        assertThat(values.contains(valueA)).isTrue();
        assertThat(values.contains(valueB)).isTrue();
    }

    private static KieBase loadRules(Class<?> valueType, Object value, boolean useExecutable) throws IOException {


        KieServices services = KieServices.Factory.get();
        KieFileSystem kfs = services.newKieFileSystem();

        String drlString =
                RULE_TEMPLATE
                        .replace("<value_type>", valueType.getSimpleName())
                        .replace("<test_value>", getValueString(value));

        Resource drl = services.getResources().newByteArrayResource(drlString.getBytes());

        kfs.write("src/main/resources/rules.drl", drl);

        KieBuilder builder = services.newKieBuilder(kfs);

        builder = useExecutable ?
                builder.buildAll(ExecutableModelProject.class) :
                builder.buildAll();

        if (builder.getResults().hasMessages(Level.ERROR)) {
            List<Message> errors = builder.getResults().getMessages(Level.ERROR);
            StringBuilder sb = new StringBuilder("Errors:");
            for (Message msg : errors) {
                sb.append("\n  " + prettyBuildMessage(msg));
            }

            throw new RuntimeException(sb.toString());
        }

        KieContainer container = services.newKieContainer(
                services.getRepository().getDefaultReleaseId());

        return container.getKieBase();
    }

    private static String getValueString(Object value) {

        if (value instanceof Float) {
            return String.format("%.2ff", value);
        } else if (value instanceof Double) {
            return String.format("%.2f", value);
        } else {
            return value.toString();
        }
    }

    private static String prettyBuildMessage(Message msg) {
        return "Message: {"
                + "id=" + msg.getId()
                + ", level=" + msg.getLevel()
                + ", path=" + msg.getPath()
                + ", line=" + msg.getLine()
                + ", column=" + msg.getColumn()
                + ", text=\"" + msg.getText() + "\""
                + "}";
    }

    private static ClassWithValue makeClassWithValue(Object value) {

        ClassWithValue cwv = new ClassWithValue();

        if (value instanceof Float) {
            cwv.setValueFloat((float) value);
        } else if (value instanceof Double) {
            cwv.setValueDouble((double) value);
        } else if (value instanceof Integer) {
            cwv.setValueInteger((int) value);
        } else if (value instanceof Short) {
            cwv.setValueShort((short) value);
        } else if (value instanceof Byte) {
            cwv.setValueByte((byte) value);
        }

        return cwv;
    }

    public static class ClassWithValue {

        private int valueInteger;
        private double valueDouble;
        private float valueFloat;
        private short valueShort;
        private byte valueByte;

        public int getValueInteger() {
            return valueInteger;
        }

        public void setValueInteger(int valueInt) {
            this.valueInteger = valueInt;
        }

        public double getValueDouble() {
            return valueDouble;
        }

        public void setValueDouble(double valueDouble) {
            this.valueDouble = valueDouble;
        }

        public float getValueFloat() {
            return valueFloat;
        }

        public void setValueFloat(float valueFloat) {
            this.valueFloat = valueFloat;
        }

        public short getValueShort() {
            return valueShort;
        }

        public void setValueShort(short valueShort) {
            this.valueShort = valueShort;
        }

        public byte getValueByte() {
            return valueByte;
        }

        public void setValueByte(byte valueByte) {
            this.valueByte = valueByte;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + valueByte;
            long temp;
            temp = Double.doubleToLongBits(valueDouble);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            result = prime * result + Float.floatToIntBits(valueFloat);
            result = prime * result + valueInteger;
            result = prime * result + valueShort;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
            ClassWithValue other = (ClassWithValue) obj;
			if (valueByte != other.valueByte) {
				return false;
			}
			if (Double.doubleToLongBits(valueDouble) != Double.doubleToLongBits(other.valueDouble)) {
				return false;
			}
			if (Float.floatToIntBits(valueFloat) != Float.floatToIntBits(other.valueFloat)) {
				return false;
			}
			if (valueInteger != other.valueInteger) {
				return false;
			}
			if (valueShort != other.valueShort) {
				return false;
			}
            return true;
        }
    }
}
