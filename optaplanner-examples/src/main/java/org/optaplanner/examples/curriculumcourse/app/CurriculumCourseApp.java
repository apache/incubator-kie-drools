package org.optaplanner.examples.curriculumcourse.app;

import java.util.Collections;
import java.util.Set;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseExporter;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseImporter;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseSolutionFileIO;
import org.optaplanner.examples.curriculumcourse.swingui.CurriculumCoursePanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class CurriculumCourseApp extends CommonApp<CourseSchedule> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/curriculumcourse/curriculumCourseSolverConfig.xml";

    public static final String DATA_DIR_NAME = "curriculumcourse";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new CurriculumCourseApp().init();
    }

    public CurriculumCourseApp() {
        super("Course timetabling",
                "Official competition name: ITC 2007 track3 - Curriculum course scheduling\n\n" +
                        "Assign lectures to periods and rooms.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                CurriculumCoursePanel.LOGO_PATH);
    }

    @Override
    protected CurriculumCoursePanel createSolutionPanel() {
        return new CurriculumCoursePanel();
    }

    @Override
    public SolutionFileIO<CourseSchedule> createSolutionFileIO() {
        return new CurriculumCourseSolutionFileIO();
    }

    @Override
    protected Set<AbstractSolutionImporter<CourseSchedule>> createSolutionImporters() {
        return Collections.singleton(new CurriculumCourseImporter());
    }

    @Override
    protected Set<AbstractSolutionExporter<CourseSchedule>> createSolutionExporters() {
        return Collections.singleton(new CurriculumCourseExporter());
    }

}
