package org.optaplanner.persistence.xstream.api.score.buildin.bendablelong;

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverter;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class BendableLongScoreXStreamConverter extends AbstractScoreXStreamConverter {

    @Override
    public boolean canConvert(Class type) {
        return BendableLongScore.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object scoreObject, HierarchicalStreamWriter writer, MarshallingContext context) {
        BendableLongScore score = (BendableLongScore) scoreObject;
        writer.setValue(score.toString());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String scoreString = reader.getValue();
        return BendableLongScore.parseScore(scoreString);
    }

}
