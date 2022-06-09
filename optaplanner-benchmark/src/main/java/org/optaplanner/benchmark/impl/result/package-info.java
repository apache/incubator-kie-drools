@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(value = PolymorphicScoreJaxbAdapter.class, type = Score.class),
        @XmlJavaTypeAdapter(value = JaxbOffsetDateTimeAdapter.class, type = OffsetDateTime.class)
})
package org.optaplanner.benchmark.impl.result;

import java.time.OffsetDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.io.jaxb.adapter.JaxbOffsetDateTimeAdapter;
import org.optaplanner.persistence.jaxb.api.score.PolymorphicScoreJaxbAdapter;
