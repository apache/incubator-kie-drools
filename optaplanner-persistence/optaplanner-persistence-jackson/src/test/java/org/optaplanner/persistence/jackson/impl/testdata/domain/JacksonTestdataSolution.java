package org.optaplanner.persistence.jackson.impl.testdata.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.persistence.jackson.api.score.buildin.simple.SimpleScoreJacksonDeserializer;
import org.optaplanner.persistence.jackson.api.score.buildin.simple.SimpleScoreJacksonSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@PlanningSolution
public class JacksonTestdataSolution extends JacksonTestdataObject {

    public static SolutionDescriptor<JacksonTestdataSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(JacksonTestdataSolution.class, JacksonTestdataEntity.class);
    }

    private List<JacksonTestdataValue> valueList;
    private List<JacksonTestdataEntity> entityList;

    private SimpleScore score;

    public JacksonTestdataSolution() {
    }

    public JacksonTestdataSolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<JacksonTestdataValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<JacksonTestdataValue> valueList) {
        this.valueList = valueList;
    }

    @PlanningEntityCollectionProperty
    public List<JacksonTestdataEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<JacksonTestdataEntity> entityList) {
        this.entityList = entityList;
    }

    @PlanningScore
    @JsonSerialize(using = SimpleScoreJacksonSerializer.class)
    @JsonDeserialize(using = SimpleScoreJacksonDeserializer.class)
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
