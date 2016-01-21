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

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;
import org.optaplanner.core.impl.score.buildin.bendablebigdecimal.BendableBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.bendablelong.BendableLongScoreDefinition;
import org.optaplanner.core.impl.score.definition.AbstractBendableScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

/**
 * @see Score
 */
public class XStreamScoreConverter implements Converter {

    private final ScoreDefinition scoreDefinition;

    /**
     * Useful to register as a general {@link Converter} in XStream.
     * Not used by the {@code @XStreamConverter} annotation.
     * @param scoreDefinition never null
     */
    public XStreamScoreConverter(ScoreDefinition scoreDefinition) {
        this.scoreDefinition = scoreDefinition;
    }

    /**
     * Called through reflection by for example this code:
     * {@code @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftScoreDefinition.class})}
     * @param scoreClass never null
     * @param scoreDefinitionClass never null. That implementation class must have a no-args constructor.
     */
    public XStreamScoreConverter(Class<? extends Score> scoreClass,
            Class<? extends ScoreDefinition> scoreDefinitionClass) {
        if (AbstractBendableScoreDefinition.class.isAssignableFrom(scoreDefinitionClass)) {
            throw new IllegalArgumentException("This constructor is not compatible with scoreClass ("
                    + scoreClass + "), use the other constructor with 2 int parameters instead.");
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

    /**
     * Called through reflection by for example this code:
     * {@code @XStreamConverter(value = XStreamScoreConverter.class, types = {BendableScoreDefinition.class}, ints = {2, 1})}
     * @param scoreClass never null
     * @param scoreDefinitionClass never null. That class must be a known {@link AbstractBendableScoreDefinition}.
     * @param hardLevelsSize {@code >= 0}
     * @param softLevelsSize {@code >= 0}
     */
    public XStreamScoreConverter(Class<? extends Score> scoreClass,
            Class<? extends ScoreDefinition> scoreDefinitionClass, int hardLevelsSize, int softLevelsSize) {
        if (!AbstractBendableScoreDefinition.class.isAssignableFrom(scoreDefinitionClass)) {
            throw new IllegalArgumentException("This constructor is not compatible with scoreClass ("
                    + scoreClass + "), use the other constructor with no int parameters instead.");
        }
        if (BendableScoreDefinition.class.equals(scoreDefinitionClass)) {
            scoreDefinition = new BendableScoreDefinition(hardLevelsSize, softLevelsSize);
        } else if (BendableLongScoreDefinition.class.equals(scoreDefinitionClass)) {
            scoreDefinition = new BendableLongScoreDefinition(hardLevelsSize, softLevelsSize);
        } else if (BendableBigDecimalScoreDefinition.class.equals(scoreDefinitionClass)) {
            scoreDefinition = new BendableBigDecimalScoreDefinition(hardLevelsSize, softLevelsSize);
        } else {
            throw new IllegalArgumentException("The scoreDefinitionClass (" + scoreDefinitionClass
                    + ") is not yet supported in " + this.getClass().getSimpleName() + ".");
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
