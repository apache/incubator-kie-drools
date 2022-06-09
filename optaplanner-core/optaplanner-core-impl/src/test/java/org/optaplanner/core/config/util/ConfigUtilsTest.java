package org.optaplanner.core.config.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.data.Offset.offset;

import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.domain.solution.PlanningScore;

class ConfigUtilsTest {

    @Test
    void mergeProperty() {
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
    void meldProperty() {
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
    void ceilDivide() {
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
    void ceilDivideByZero() {
        assertThatExceptionOfType(ArithmeticException.class).isThrownBy(() -> ConfigUtils.ceilDivide(20, -0));
    }

    @Test
    void applyCustomProperties() {
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
        assertThat((double) bean.primitiveFloat).isEqualTo(5.5D, offset(0.0D));
        assertThat(bean.objectFloat).isEqualTo(Float.valueOf(6.6F));
        assertThat(bean.primitiveDouble).isEqualTo(7.7, offset(0.0));
        assertThat(bean.objectDouble).isEqualTo(Double.valueOf(8.8));
        assertThat(bean.bigDecimal).isEqualTo(new BigDecimal("9.9"));
        assertThat(bean.string).isEqualTo("This is a sentence.");
        assertThat(bean.configUtilsTestBeanEnum).isEqualTo(ConfigUtilsTestBeanEnum.BETA);
    }

    @Test
    void applyCustomPropertiesSubset() {
        Map<String, String> customProperties = new HashMap<>();
        customProperties.put("string", "This is a sentence.");
        ConfigUtilsTestBean bean = new ConfigUtilsTestBean();
        ConfigUtils.applyCustomProperties(bean, "bean", customProperties, "customProperties");
        assertThat(bean.string).isEqualTo("This is a sentence.");
    }

    @Test
    void applyCustomPropertiesNonExistingCustomProperty() {
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
    void newInstanceStaticInnerClass() {
        assertThat(ConfigUtils.newInstance(this, "testProperty", StaticInnerClass.class)).isNotNull();
    }

    public static class StaticInnerClass {
    }

    @Test
    void newInstanceStaticInnerClassWithArgsConstructor() {
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
    void newInstanceNonStaticInnerClass() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ConfigUtils.newInstance(this, "testProperty", NonStaticInnerClass.class))
                .withMessageContaining("inner class");
    }

    public class NonStaticInnerClass {
    }

    @Test
    void newInstanceLocalClass() {
        class LocalClass {
        }
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ConfigUtils.newInstance(this, "testProperty", LocalClass.class))
                .withMessageContaining("inner class");
    }

    @Test
    void newInstanceOwnerDescription() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ConfigUtils.newInstance(
                        this, "testProperty", StaticInnerClassWithArgsConstructor.class))
                .withMessageContaining(ConfigUtilsTest.class.getSimpleName());
        String owner = "OWNER";
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ConfigUtils.newInstance(
                        () -> owner, "testProperty", StaticInnerClassWithArgsConstructor.class))
                .withMessageContaining(owner);
    }

    @Test
    void abbreviate() {
        assertThat(ConfigUtils.abbreviate(null)).isEmpty();
        assertThat(ConfigUtils.abbreviate(Collections.emptyList())).isEmpty();
        assertThat(ConfigUtils.abbreviate(Arrays.asList("A", "B", "C"))).isEqualTo("A, B, C");
        assertThat(ConfigUtils.abbreviate(Arrays.asList("A", "B", "C", "D"))).isEqualTo("A, B, C, ...");
    }

    @Test
    void ignoreSyntheticMembers() {
        assertThat(ConfigUtils.getDeclaredMembers(ClassWithSyntheticFieldParent.ClassWithSyntheticField.class)).hasSize(1);
        assertThat(ConfigUtils.getDeclaredMembers(ClassWithSyntheticFieldParent.ClassWithSyntheticField.class))
                .noneMatch(Member::isSynthetic);
        assertThat(ConfigUtils.getAllMembers(ClassWithSyntheticFieldParent.ClassWithSyntheticField.class, PlanningScore.class))
                .hasSize(1);
        assertThat(ConfigUtils.getAllMembers(ClassWithSyntheticFieldParent.ClassWithSyntheticField.class, PlanningScore.class))
                .noneMatch(Member::isSynthetic);

        assertThat(ConfigUtils.getDeclaredMembers(ClassWithBridgeMethod.class)).hasSize(2);
        assertThat(ConfigUtils.getDeclaredMembers(ClassWithBridgeMethod.class)).noneMatch(Member::isSynthetic);
        assertThat(ConfigUtils.getAllMembers(ClassWithBridgeMethod.class, PlanningScore.class)).hasSize(1);
        assertThat(ConfigUtils.getAllMembers(ClassWithBridgeMethod.class, PlanningScore.class)).noneMatch(Member::isSynthetic);
    }

    public static class ClassWithSyntheticFieldParent {
        int x;

        public class ClassWithSyntheticField {
            @PlanningScore
            int y;
        }
    }

    public static class ClassWithBridgeMethodParent<T> {

        public T getScore() {
            return null;
        }

        public void setScore(T score) {
        }
    }

    public static class ClassWithBridgeMethod extends ClassWithBridgeMethodParent<Integer> {
        @Override
        public Integer getScore() {
            return 0;
        }

        @Override
        @PlanningScore
        public void setScore(Integer score) {
        }
    }

}
