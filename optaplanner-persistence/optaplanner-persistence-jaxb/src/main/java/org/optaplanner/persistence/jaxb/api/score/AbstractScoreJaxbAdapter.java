package org.optaplanner.persistence.jaxb.api.score;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.optaplanner.core.api.score.Score;

/**
 * JAXB binding support for a {@link Score} type.
 * <p>
 * For example: use {@code @XmlJavaTypeAdapter(HardSoftScoreJaxbAdapter.class)}
 * on a {@code HardSoftScore score} field and it will marshalled to XML as {@code <score>-999hard/-999soft</score>}.
 *
 * @see Score
 * @param <Score_> the actual score type
 */
public abstract class AbstractScoreJaxbAdapter<Score_ extends Score<Score_>> extends XmlAdapter<String, Score_> {

    @Override
    public String marshal(Score_ score) {
        return score.toString();
    }

}
