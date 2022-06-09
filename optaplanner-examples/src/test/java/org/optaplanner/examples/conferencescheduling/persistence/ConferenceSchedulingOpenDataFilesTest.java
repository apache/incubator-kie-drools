
package org.optaplanner.examples.conferencescheduling.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.conferencescheduling.app.ConferenceSchedulingApp;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;

class ConferenceSchedulingOpenDataFilesTest extends OpenDataFilesTest<ConferenceSolution> {

    @Override
    protected CommonApp<ConferenceSolution> createCommonApp() {
        return new ConferenceSchedulingApp();
    }
}
