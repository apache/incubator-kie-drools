/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.data.Offset.offset;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ConfigUtilsTest {

    @Test
    public void mergeProperty() {
        Integer a = null;
        Integer b = null;
        assertThat(ConfigUtils.mergeProperty(a, b)).isEqualTo(null);
        a = Integer.valueOf(1);
        assertThat(ConfigUtils.mergeProperty(a, b)).isEqualTo(null);
        b = Integer.valueOf(10);
        assertThat(ConfigUtils.mergeProperty(a, b)).isEqualTo(null);
        b = Integer.valueOf(1);
        assertThat(ConfigUtils.mergeProperty(a, b)).isEqualTo(Integer.valueOf(1));
        a = null;
        assertThat(ConfigUtils.mergeProperty(a, b)).isEqualTo(null);
    }

    @Test
    public void meldProperty() {
        Integer a = null;
        Integer b = null;
        assertThat(ConfigUtils.meldProperty(a, b)).isEqualTo(null);
        a = Integer.valueOf(1);
        assertThat(ConfigUtils.meldProperty(a, b)).isEqualTo(Integer.valueOf(1));
        b = Integer.valueOf(10);
        assertThat(ConfigUtils.meldProperty(a, b))
                .isEqualTo(ConfigUtils.mergeProperty(Integer.valueOf(1), Integer.valueOf(10)));
        a = null;
        assertThat(ConfigUtils.meldProperty(a, b)).isEqualTo(Integer.valueOf(10));
    }

    @Test
    public void ceilDivide() {
        assertThat(ConfigUtils.ceilDivide(19, 2)).isEqualTo(10);
        assertThat(ConfigUtils.ceilDivide(20, 2)).isEqualTo(10);
        assertThat(ConfigUtils.ceilDivide(21, 2)).isEqualTo(11);

        assertThat(ConfigUtils.ceilDivide(19, -2)).isEqualTo(-9);
        assertThat(ConfigUtils.ceilDivide(20, -2)).isEqualTo(-10);
        assertThat(ConfigUtils.ceilDivide(21, -2)).isEqualTo(-10);

        assertThat(ConfigUtils.ceilDivide(-19, 2)).isEqualTo(-9);
        assertThat(ConfigUtils.ceilDivide(-20, 2)).isEqualTo(-10);
        assertThat(ConfigUtils.ceilDivide(-21, 2)).isEqualTo(-10);

        assertThat(ConfigUtils.ceilDivide(-19, -2)).isEqualTo(10);
        assertThat(ConfigUtils.ceilDivide(-20, -2)).isEqualTo(10);
        assertThat(ConfigUtils.ceilDivide(-21, -2)).isEqualTo(11);
    }

    @Test
    public void ceilDivideByZero() {
        assertThatExceptionOfType(ArithmeticException.class).isThrownBy(() -> ConfigUtils.ceilDivide(20, -0));
    }

    @Test
    public void applyCustomProperties() {
        Map<String, String> customProperties = new HashMap<>();
        customProperties.put("primitiveBoolean", "true");
        customProperties.put("objectBoolean", "true");
        customProperties.put("primitiveInt", "1");
        customProperties.put("objectInteger", "2");
        customProperties.put("primitiveLong", "3");
        customProperties.put("objectLong", "4");
        customProperties.put("primitiveFloat", "5.5");
        customProperties.put("objectFloat", "6.6");
        customProperties.put("primitiveDouble", "7.7");
        customProperties.put("objectDouble", "8.8");
        customProperties.put("bigDecimal", "9.9");
        customProperties.put("string", "This is a sentence.");
        customProperties.put("configUtilsTestBeanEnum", "BETA");
        ConfigUtilsTestBean bean = new ConfigUtilsTestBean();
        ConfigUtils.applyCustomProperties(bean, "bean", customProperties, "customProperties");
        assertThat(bean.primitiveBoolean).isTrue();
        assertThat(bean.objectBoolean).isEqualTo(Boolean.TRUE);
        assertThat(bean.primitiveInt).isEqualTo(1);
        assertThat(bean.objectInteger).isEqualTo(Integer.valueOf(2));
        assertThat(bean.primitiveLong).isEqualTo(3L);
        assertThat(bean.objectLong).isEqualTo(Long.valueOf(4L));
        assertThat((double) bean.primitiveFloat).isEqualTo((double) 5.5F, offset((double) 0.0F));
        assertThat(bean.objectFloat).isEqualTo(Float.valueOf(6.6F));
        assertThat(bean.primitiveDouble).isEqualTo(7.7, offset(0.0));
        assertThat(bean.objectDouble).isEqualTo(Double.valueOf(8.8));
        assertThat(bean.bigDecimal).isEqualTo(new BigDecimal("9.9"));
        assertThat(bean.string).isEqualTo("This is a sentence.");
        assertThat(bean.configUtilsTestBeanEnum).isEqualTo(ConfigUtilsTestBeanEnum.BETA);
    }

    @Test
    public void applyCustomPropertiesSubset() {
        Map<String, String> customProperties = new HashMap<>();
        customProperties.put("string", "This is a sentence.");
        ConfigUtilsTestBean bean = new ConfigUtilsTestBean();
        ConfigUtils.applyCustomProperties(bean, "bean", customProperties, "customProperties");
        assertThat(bean.string).isEqualTo("This is a sentence.");
    }

    @Test
    public void applyCustomPropertiesNonExistingCustomProperty() {
        Map<String, String> customProperties = new HashMap<>();
        customProperties.put("doesNotExist", "This is a sentence.");
        ConfigUtilsTestBean bean = new ConfigUtilsTestBean();
        assertThatIllegalStateException().isThrownBy(
                () -> ConfigUtils.applyCustomProperties(bean, "bean", customProperties, "customProperties"));
    }

    private static class ConfigUtilsTestBean {

        private boolean primitiveBoolean;
        private Boolean objectBoolean;
        private int primitiveInt;
        private Integer objectInteger;
        private long primitiveLong;
        private Long objectLong;
        private float primitiveFloat;
        private Float objectFloat;
        private double primitiveDouble;
        private Double objectDouble;
        private BigDecimal bigDecimal;
        private String string;
        private ConfigUtilsTestBeanEnum configUtilsTestBeanEnum;

        public void setPrimitiveBoolean(boolean primitiveBoolean) {
            this.primitiveBoolean = primitiveBoolean;
        }

        public void setObjectBoolean(Boolean objectBoolean) {
            this.objectBoolean = objectBoolean;
        }

        public void setPrimitiveInt(int primitiveInt) {
            this.primitiveInt = primitiveInt;
        }

        public void setObjectInteger(Integer objectInteger) {
            this.objectInteger = objectInteger;
        }

        public void setPrimitiveLong(long primitiveLong) {
            this.primitiveLong = primitiveLong;
        }

        public void setObjectLong(Long objectLong) {
            this.objectLong = objectLong;
        }

        public void setPrimitiveFloat(float primitiveFloat) {
            this.primitiveFloat = primitiveFloat;
        }

        public void setObjectFloat(Float objectFloat) {
            this.objectFloat = objectFloat;
        }

        public void setPrimitiveDouble(double primitiveDouble) {
            this.primitiveDouble = primitiveDouble;
        }

        public void setObjectDouble(Double objectDouble) {
            this.objectDouble = objectDouble;
        }

        public void setBigDecimal(BigDecimal bigDecimal) {
            this.bigDecimal = bigDecimal;
        }

        public void setString(String string) {
            this.string = string;
        }

        public void setConfigUtilsTestBeanEnum(ConfigUtilsTestBeanEnum configUtilsTestBeanEnum) {
            this.configUtilsTestBeanEnum = configUtilsTestBeanEnum;
        }

    }

    private enum ConfigUtilsTestBeanEnum {
        ALPHA,
        BETA,
        GAMMA
    }

    @Test
    public void newInstanceStaticInnerClass() {
        assertThat(ConfigUtils.newInstance(this, "testProperty", StaticInnerClass.class)).isNotNull();
    }

    public static class StaticInnerClass {
    }

    @Test
    public void newInstanceStaticInnerClassWithArgsConstructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ConfigUtils.newInstance(
                        this, "testProperty", StaticInnerClassWithArgsConstructor.class))
                .withMessageContaining("no-arg constructor.");
    }

    public static class StaticInnerClassWithArgsConstructor {

        public StaticInnerClassWithArgsConstructor(int i) {
        }

    }

    @Test
    public void newInstanceNonStaticInnerClass() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ConfigUtils.newInstance(this, "testProperty", NonStaticInnerClass.class))
                .withMessageContaining("inner class");
    }

    public class NonStaticInnerClass {
    }

    @Test
    public void newInstanceLocalClass() {
        class LocalClass {
        }
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ConfigUtils.newInstance(this, "testProperty", LocalClass.class))
                .withMessageContaining("inner class");
    }

}
