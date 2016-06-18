/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.jaxb.impl.score;

import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;
import org.optaplanner.core.impl.score.buildin.bendablebigdecimal.BendableBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.bendablelong.BendableLongScoreDefinition;
import org.optaplanner.core.impl.score.definition.AbstractBendableScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.persistence.jaxb.impl.score.buildin.bendable.BendableScoreJaxbXmlAdapter;
import org.optaplanner.persistence.jaxb.impl.score.buildin.bendablebigdecimal.BendableBigDecimalScoreJaxbXmlAdapter;
import org.optaplanner.persistence.jaxb.impl.score.buildin.bendablelong.BendableLongScoreJaxbXmlAdapter;
import org.optaplanner.persistence.jaxb.impl.score.buildin.hardmediumsoft.HardMediumSoftScoreJaxbXmlAdapter;
import org.optaplanner.persistence.jaxb.impl.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreJaxbXmlAdapter;
import org.optaplanner.persistence.jaxb.impl.score.buildin.hardsoft.HardSoftScoreJaxbXmlAdapter;
import org.optaplanner.persistence.jaxb.impl.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreJaxbXmlAdapter;
import org.optaplanner.persistence.jaxb.impl.score.buildin.hardsoftdouble.HardSoftDoubleScoreJaxbXmlAdapter;
import org.optaplanner.persistence.jaxb.impl.score.buildin.hardsoftlong.HardSoftLongScoreJaxbXmlAdapter;
import org.optaplanner.persistence.jaxb.impl.score.buildin.simple.SimpleScoreJaxbXmlAdapter;
import org.optaplanner.persistence.jaxb.impl.score.buildin.simplebigdecimal.SimpleBigDecimalScoreJaxbXmlAdapter;
import org.optaplanner.persistence.jaxb.impl.score.buildin.simpledouble.SimpleDoubleScoreJaxbXmlAdapter;
import org.optaplanner.persistence.jaxb.impl.score.buildin.simplelong.SimpleLongScoreJaxbXmlAdapter;

/**
 * JAXB binding support for a {@link Score} type.
 * <p>
 * For example: use {@code @XmlJavaTypeAdapter(HardSoftScoreJaxbXmlAdapter.class)}
 * on a {@code HardSoftScore score} field and it will marshalled to XML as {@code <score>-999hard/-999soft</score>}.
 * @see Score
 * @param <Score_> the actual score type
 */
public abstract class AbstractScoreJaxbXmlAdapter<Score_ extends Score<Score_>> extends XmlAdapter<String, Score_> {

    public static List<Class<? extends AbstractScoreJaxbXmlAdapter>> XML_ADAPTER_LIST = Arrays.asList(
            SimpleScoreJaxbXmlAdapter.class,
            SimpleLongScoreJaxbXmlAdapter.class,
            SimpleDoubleScoreJaxbXmlAdapter.class,
            SimpleBigDecimalScoreJaxbXmlAdapter.class,
            HardSoftScoreJaxbXmlAdapter.class,
            HardSoftLongScoreJaxbXmlAdapter.class,
            HardSoftDoubleScoreJaxbXmlAdapter.class,
            HardSoftBigDecimalScoreJaxbXmlAdapter.class,
            HardMediumSoftScoreJaxbXmlAdapter.class,
            HardMediumSoftLongScoreJaxbXmlAdapter.class,
            BendableScoreJaxbXmlAdapter.class,
            BendableLongScoreJaxbXmlAdapter.class,
            BendableBigDecimalScoreJaxbXmlAdapter.class
            );

    @Override
    public String marshal(Score_ score) {
        return score.toString();
    }

}
