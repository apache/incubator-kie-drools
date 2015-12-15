/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.xstream.impl.score;

import java.io.Serializable;
import java.math.BigDecimal;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardsoftdouble.HardSoftDoubleScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;
import org.optaplanner.core.impl.score.buildin.bendablebigdecimal.BendableBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.bendablelong.BendableLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardmediumsoft.HardMediumSoftScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoftdouble.HardSoftDoubleScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoftlong.HardSoftLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simplebigdecimal.SimpleBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simpledouble.SimpleDoubleScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simplelong.SimpleLongScoreDefinition;

import static org.junit.Assert.*;

public class XStreamScoreConverterTest {

    // ************************************************************************
    // Simple
    // ************************************************************************

    @Test
    public void simpleScore() {
        testXStreaming(null, new TestSimpleScoreWrapper(null));
        SimpleScore score = SimpleScore.valueOf(1234);
        testXStreaming(score, new TestSimpleScoreWrapper(score));
    }

    public static class TestSimpleScoreWrapper extends TestScoreWrapper<SimpleScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {SimpleScoreDefinition.class})
        private SimpleScore score;

        public TestSimpleScoreWrapper(SimpleScore score) {
            this.score = score;
        }

        public SimpleScore getScore() {
            return score;
        }

    }

    @Test
    public void simpleLongScore() {
        testXStreaming(null, new TestSimpleLongScoreWrapper(null));
        SimpleLongScore score = SimpleLongScore.valueOf(1234L);
        testXStreaming(score, new TestSimpleLongScoreWrapper(score));
    }

    public static class TestSimpleLongScoreWrapper extends TestScoreWrapper<SimpleLongScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {SimpleLongScoreDefinition.class})
        private SimpleLongScore score;

        public TestSimpleLongScoreWrapper(SimpleLongScore score) {
            this.score = score;
        }

        public SimpleLongScore getScore() {
            return score;
        }

    }

    @Test
    public void simpleDoubleScore() {
        testXStreaming(null, new TestSimpleDoubleScoreWrapper(null));
        SimpleDoubleScore score = SimpleDoubleScore.valueOf(1234.4321);
        testXStreaming(score, new TestSimpleDoubleScoreWrapper(score));
    }

    public static class TestSimpleDoubleScoreWrapper extends TestScoreWrapper<SimpleDoubleScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {SimpleDoubleScoreDefinition.class})
        private SimpleDoubleScore score;

        public TestSimpleDoubleScoreWrapper(SimpleDoubleScore score) {
            this.score = score;
        }

        public SimpleDoubleScore getScore() {
            return score;
        }

    }

    @Test
    public void simpleBigDecimalScore() {
        testXStreaming(null, new TestSimpleBigDecimalScoreWrapper(null));
        SimpleBigDecimalScore score = SimpleBigDecimalScore.valueOf(new BigDecimal("1234.4321"));
        testXStreaming(score, new TestSimpleBigDecimalScoreWrapper(score));
    }

    public static class TestSimpleBigDecimalScoreWrapper extends TestScoreWrapper<SimpleBigDecimalScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {SimpleBigDecimalScoreDefinition.class})
        private SimpleBigDecimalScore score;

        public TestSimpleBigDecimalScoreWrapper(SimpleBigDecimalScore score) {
            this.score = score;
        }

        public SimpleBigDecimalScore getScore() {
            return score;
        }

    }

    // ************************************************************************
    // HardSoft
    // ************************************************************************

    @Test
    public void hardSoftScore() {
        testXStreaming(null, new TestHardSoftScoreWrapper(null));
        HardSoftScore score = HardSoftScore.valueOf(1200, 34);
        testXStreaming(score, new TestHardSoftScoreWrapper(score));
    }

    public static class TestHardSoftScoreWrapper extends TestScoreWrapper<HardSoftScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftScoreDefinition.class})
        private HardSoftScore score;

        public TestHardSoftScoreWrapper(HardSoftScore score) {
            this.score = score;
        }

        public HardSoftScore getScore() {
            return score;
        }

    }

    @Test
    public void hardSoftLongScore() {
        testXStreaming(null, new TestHardSoftLongScoreWrapper(null));
        HardSoftLongScore score = HardSoftLongScore.valueOf(1200L, 34L);
        testXStreaming(score, new TestHardSoftLongScoreWrapper(score));
    }

    public static class TestHardSoftLongScoreWrapper extends TestScoreWrapper<HardSoftLongScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftLongScoreDefinition.class})
        private HardSoftLongScore score;

        public TestHardSoftLongScoreWrapper(HardSoftLongScore score) {
            this.score = score;
        }

        public HardSoftLongScore getScore() {
            return score;
        }

    }

    @Test
    public void hardSoftDoubleScore() {
        testXStreaming(null, new TestHardSoftDoubleScoreWrapper(null));
        HardSoftDoubleScore score = HardSoftDoubleScore.valueOf(1200.0021, 34.4300);
        testXStreaming(score, new TestHardSoftDoubleScoreWrapper(score));
    }

    public static class TestHardSoftDoubleScoreWrapper extends TestScoreWrapper<HardSoftDoubleScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftDoubleScoreDefinition.class})
        private HardSoftDoubleScore score;

        public TestHardSoftDoubleScoreWrapper(HardSoftDoubleScore score) {
            this.score = score;
        }

        public HardSoftDoubleScore getScore() {
            return score;
        }

    }


    @Test
    public void hardSoftBigDecimalScore() {
        testXStreaming(null, new TestHardSoftBigDecimalScoreWrapper(null));
        HardSoftBigDecimalScore score = HardSoftBigDecimalScore.valueOf(new BigDecimal("1200.0021"), new BigDecimal("34.4300"));
        testXStreaming(score, new TestHardSoftBigDecimalScoreWrapper(score));
    }

    public static class TestHardSoftBigDecimalScoreWrapper extends TestScoreWrapper<HardSoftBigDecimalScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftBigDecimalScoreDefinition.class})
        private HardSoftBigDecimalScore score;

        public TestHardSoftBigDecimalScoreWrapper(HardSoftBigDecimalScore score) {
            this.score = score;
        }

        public HardSoftBigDecimalScore getScore() {
            return score;
        }

    }

    // ************************************************************************
    // HardMediumSoft
    // ************************************************************************

    @Test
    public void hardMediumSoftScore() {
        testXStreaming(null, new TestHardMediumSoftScoreWrapper(null));
        HardMediumSoftScore score = HardMediumSoftScore.valueOf(1200, 30, 4);
        testXStreaming(score, new TestHardMediumSoftScoreWrapper(score));
    }

    public static class TestHardMediumSoftScoreWrapper extends TestScoreWrapper<HardMediumSoftScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {HardMediumSoftScoreDefinition.class})
        private HardMediumSoftScore score;

        public TestHardMediumSoftScoreWrapper(HardMediumSoftScore score) {
            this.score = score;
        }

        public HardMediumSoftScore getScore() {
            return score;
        }

    }

    @Test
    public void hardMediumSoftLongScore() {
        testXStreaming(null, new TestHardMediumSoftLongScoreWrapper(null));
        HardMediumSoftLongScore score = HardMediumSoftLongScore.valueOf(1200L, 30L, 4L);
        testXStreaming(score, new TestHardMediumSoftLongScoreWrapper(score));
    }

    public static class TestHardMediumSoftLongScoreWrapper extends TestScoreWrapper<HardMediumSoftLongScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {HardMediumSoftLongScoreDefinition.class})
        private HardMediumSoftLongScore score;

        public TestHardMediumSoftLongScoreWrapper(HardMediumSoftLongScore score) {
            this.score = score;
        }

        public HardMediumSoftLongScore getScore() {
            return score;
        }

    }

    // ************************************************************************
    // Bendable
    // ************************************************************************

    @Test
    public void bendableScore() {
        testXStreaming(null, new TestBendableScoreWrapper(null));
        BendableScore score = BendableScore.valueOf(new int[]{1000, 200}, new int[]{34});
        testXStreaming(score, new TestBendableScoreWrapper(score));
    }

    public static class TestBendableScoreWrapper extends TestScoreWrapper<BendableScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {BendableScoreDefinition.class}, ints = {2, 1})
        private BendableScore score;

        public TestBendableScoreWrapper(BendableScore score) {
            this.score = score;
        }

        public BendableScore getScore() {
            return score;
        }

    }

    @Test
    public void bendableLongScore() {
        testXStreaming(null, new TestBendableLongScoreWrapper(null));
        BendableLongScore score = BendableLongScore.valueOf(new long[]{1000L, 200L}, new long[]{34L});
        testXStreaming(score, new TestBendableLongScoreWrapper(score));
    }

    public static class TestBendableLongScoreWrapper extends TestScoreWrapper<BendableLongScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {BendableLongScoreDefinition.class}, ints = {2, 1})
        private BendableLongScore score;

        public TestBendableLongScoreWrapper(BendableLongScore score) {
            this.score = score;
        }

        public BendableLongScore getScore() {
            return score;
        }

    }

    @Test
    public void bendableBigDecimalScore() {
        testXStreaming(null, new TestBendableBigDecimalScoreWrapper(null));
        BendableBigDecimalScore score = BendableBigDecimalScore.valueOf(
                new BigDecimal[]{new BigDecimal("1000.0001"), new BigDecimal("200.0020")}, new BigDecimal[]{new BigDecimal("34.4300")});
        testXStreaming(score, new TestBendableBigDecimalScoreWrapper(score));
    }

    public static class TestBendableBigDecimalScoreWrapper extends TestScoreWrapper<BendableBigDecimalScore> {

        @XStreamConverter(value = XStreamScoreConverter.class, types = {BendableBigDecimalScoreDefinition.class}, ints = {2, 1})
        private BendableBigDecimalScore score;

        public TestBendableBigDecimalScoreWrapper(BendableBigDecimalScore score) {
            this.score = score;
        }

        public BendableBigDecimalScore getScore() {
            return score;
        }

    }

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected <S extends Score, W extends TestScoreWrapper<S>> void testXStreaming(S expectedScore, W input) {
        W output = serializeAndDeserializeWithXStream(input);
        assertEquals(expectedScore, output.getScore());
        if (expectedScore != null) {
            assertXStreamXml("<([\\w\\-\\.]+)>\\s*<score>" + expectedScore.toString() + "</score>\\s*</\\1>", input);
        } else {
            assertXStreamXml("<([\\w\\-\\.]+)/>", input);
        }
    }

    public static <T> T serializeAndDeserializeWithXStream(T input) {
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.processAnnotations(input.getClass());
        String xmlString = xStream.toXML(input);
        return (T) xStream.fromXML(xmlString);
    }

    public static <T> void assertXStreamXml(String regex, T input) {
        XStream xStream = new XStream();
        xStream.setMode(XStream.NO_REFERENCES);
        xStream.processAnnotations(input.getClass());
        String xml = xStream.toXML(input);
        if (!xml.matches(regex)) {
            fail("Regular expression match failed.\nExpected regular expression: " + regex + "\nActual: " + xml);
        }
    }

    public static abstract class TestScoreWrapper<S extends Score> implements Serializable {

        public abstract S getScore();
    }

}
