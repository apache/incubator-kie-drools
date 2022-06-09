package org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong;

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverter;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class HardMediumSoftLongScoreXStreamConverter extends AbstractScoreXStreamConverter {

    @Override
    public boolean canConvert(Class type) {
        return HardMediumSoftLongScore.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object scoreObject, HierarchicalStreamWriter writer, MarshallingContext context) {
        HardMediumSoftLongScore score = (HardMediumSoftLongScore) scoreObject;
        writer.setValue(score.toString());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String scoreString = reader.getValue();
        return HardMediumSoftLongScore.parseScore(scoreString);
    }

}
