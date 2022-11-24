package org.optaplanner.examples.curriculumcourse.app;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.examples.common.app.AbstractConstructionHeuristicTest;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;

class CurriculumCourseConstructionHeuristicTest extends AbstractConstructionHeuristicTest<CourseSchedule> {

    @Override
    protected Predicate<ConstructionHeuristicType> includeConstructionHeuristicType() {
        /*
         * TODO Delete this temporary workaround to ignore ALLOCATE_TO_VALUE_FROM_QUEUE,
         * see https://issues.redhat.com/browse/PLANNER-486
         */
        return constructionHeuristicType -> constructionHeuristicType != ConstructionHeuristicType.ALLOCATE_TO_VALUE_FROM_QUEUE;
    }

    @Override
    protected CommonApp<CourseSchedule> createCommonApp() {
        return new CurriculumCourseApp();
    }

    @Override
    protected Stream<String> unsolvedFileNames() {
        return Stream.of("toy01.json");
    }
}
