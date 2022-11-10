package org.optaplanner.core.impl.testdata.domain.extended.entity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedEntity;

@PlanningSolution
public class TestdataExtendedEntitySolution extends TestdataSolution {

    public static SolutionDescriptor<TestdataExtendedEntitySolution> buildExtendedEntitySolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataExtendedEntitySolution.class, TestdataEntity.class);
    }

    public static EntityDescriptor<TestdataExtendedEntitySolution> buildEntityDescriptor() {
        return buildExtendedEntitySolutionDescriptor().findEntityDescriptorOrFail(TestdataEntity.class);
    }

    /**
     * Construct uninitialized solution with 1 entity, 1 subEntity and parameterized sizes of entityList and subEntityList.
     * The valueList is not set.
     *
     * @param entityListSize number of entities in entityList
     * @param subEntityListSize number of subEntities in subEntityList
     * @return solution
     */
    public static TestdataExtendedEntitySolution generateSolution(
            int entityListSize, int subEntityListSize, int rawEntityListSize) {
        TestdataExtendedEntitySolution solution = new TestdataExtendedEntitySolution("solution");

        solution.setEntity(new TestdataEntity("entity-singleton"));
        solution.setSubEntity(new TestdataUnannotatedExtendedEntity("subEntity-singleton"));

        solution.setEntityList(IntStream.range(0, entityListSize)
                .mapToObj(i -> "entity" + i)
                .map(TestdataEntity::new)
                .collect(Collectors.toList()));

        solution.setSubEntityList(IntStream.range(0, subEntityListSize)
                .mapToObj(i -> "subEntity" + i)
                .map(TestdataUnannotatedExtendedEntity::new)
                .collect(Collectors.toList()));

        solution.setRawEntityList(IntStream.range(0, rawEntityListSize)
                .mapToObj(i -> "subEntity" + i + "-R")
                .map(TestdataUnannotatedExtendedEntity::new)
                .collect(Collectors.toList()));
        return solution;
    }

    private TestdataEntity entity;
    private TestdataUnannotatedExtendedEntity subEntity;
    private List<TestdataUnannotatedExtendedEntity> subEntityList;
    private List rawEntityList;

    public TestdataExtendedEntitySolution() {
    }

    public TestdataExtendedEntitySolution(String code) {
        super(code);
    }

    @PlanningEntityProperty
    public TestdataEntity getEntity() {
        return entity;
    }

    public void setEntity(TestdataEntity entity) {
        this.entity = entity;
    }

    @PlanningEntityProperty
    public TestdataUnannotatedExtendedEntity getSubEntity() {
        return subEntity;
    }

    public void setSubEntity(TestdataUnannotatedExtendedEntity subEntity) {
        this.subEntity = subEntity;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataUnannotatedExtendedEntity> getSubEntityList() {
        return subEntityList;
    }

    public void setSubEntityList(List<TestdataUnannotatedExtendedEntity> subEntityList) {
        this.subEntityList = subEntityList;
    }

    @PlanningEntityCollectionProperty
    public List getRawEntityList() {
        return rawEntityList;
    }

    public void setRawEntityList(List rawEntityList) {
        this.rawEntityList = rawEntityList;
    }
}
