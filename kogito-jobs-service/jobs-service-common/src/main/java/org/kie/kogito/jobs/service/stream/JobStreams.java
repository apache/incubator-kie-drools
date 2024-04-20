package org.kie.kogito.jobs.service.stream;

import org.kie.kogito.jobs.service.model.JobDetails;

public interface JobStreams {

    boolean isEnabled();

    void jobStatusChange(JobDetails job);

}
