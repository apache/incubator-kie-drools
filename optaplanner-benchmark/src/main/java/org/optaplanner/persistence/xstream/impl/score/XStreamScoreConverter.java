/*
 * Copyright 2013 JBoss Inc
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

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

/**
 * Some {@link Score} implementations require specific subclasses:
 * For {@link BendableScore}, use {@link XStreamBendableScoreConverter}.
 */
public class XStreamScoreConverter implements Converter {

    private final ScoreDefinition scoreDefinition;

    public XStreamScoreConverter(ScoreDefinition scoreDefinition) {
        this.scoreDefinition = scoreDefinition;
    }

    public XStreamScoreConverter(Class<? extends Score> scoreClass,
            Class<? extends ScoreDefinition> scoreDefinitionClass) {
        if (BendableScore.class.equals(scoreClass)) {
            throw new IllegalArgumentException(XStreamScoreConverter.class + " is not compatible with scoreClass ("
                    + scoreClass + "), use " + XStreamBendableScoreConverter.class.getSimpleName() + " instead.");
        }
        try {
            scoreDefinition = scoreDefinitionClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("The scoreDefinitionClass (" + scoreDefinitionClass
                    + ") does not have a public no-arg constructor", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("The scoreDefinitionClass (" + scoreDefinitionClass
                    + ") does not have a public no-arg constructor", e);
        }
        if (scoreClass != scoreDefinition.getScoreClass()) {
            throw new IllegalStateException("The scoreClass (" + scoreClass + ") of the Score field to serialize to XML"
                    + " does not match the scoreDefinition's scoreClass (" + scoreDefinition.getScoreClass() + ").");
        }
    }

    public boolean canConvert(Class type) {
        return scoreDefinition.getScoreClass().isAssignableFrom(type);
    }

    public void marshal(Object scoreObject, HierarchicalStreamWriter writer, MarshallingContext context) {
        String scoreString = scoreDefinition.formatScore((Score) scoreObject);
        writer.setValue(scoreString);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String scoreString = reader.getValue();
        return scoreDefinition.parseScore(scoreString);
    }

}
