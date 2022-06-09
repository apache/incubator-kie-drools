
package org.optaplanner.examples.conferencescheduling.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;

class ConferenceSchedulingSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<ConferenceSolution> {

    @Override
    protected CommonApp<ConferenceSolution> createCommonApp() {
        return new ConferenceSchedulingApp();
    }
}
