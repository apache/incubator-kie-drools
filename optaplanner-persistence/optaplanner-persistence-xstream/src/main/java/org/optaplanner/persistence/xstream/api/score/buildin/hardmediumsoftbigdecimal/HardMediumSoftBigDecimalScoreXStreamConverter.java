package org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftbigdecimal;

import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverter;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class HardMediumSoftBigDecimalScoreXStreamConverter extends AbstractScoreXStreamConverter {

    @Override
    public boolean canConvert(Class type) {
        return HardMediumSoftBigDecimalScore.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object scoreObject, HierarchicalStreamWriter writer, MarshallingContext context) {
        HardMediumSoftBigDecimalScore score = (HardMediumSoftBigDecimalScore) scoreObject;
        writer.setValue(score.toString());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String scoreString = reader.getValue();
        return HardMediumSoftBigDecimalScore.parseScore(scoreString);
    }

}
