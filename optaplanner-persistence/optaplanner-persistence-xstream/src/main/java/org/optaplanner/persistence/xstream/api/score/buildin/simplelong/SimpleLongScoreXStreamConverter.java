package org.optaplanner.persistence.xstream.api.score.buildin.simplelong;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverter;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class SimpleLongScoreXStreamConverter extends AbstractScoreXStreamConverter {

    @Override
    public boolean canConvert(Class type) {
        return SimpleLongScore.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object scoreObject, HierarchicalStreamWriter writer, MarshallingContext context) {
        SimpleLongScore score = (SimpleLongScore) scoreObject;
        writer.setValue(score.toString());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String scoreString = reader.getValue();
        return SimpleLongScore.parseScore(scoreString);
    }

}
