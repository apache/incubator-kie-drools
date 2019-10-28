package org.kie.kogito.jobs;

import java.time.ZonedDateTime;

public interface ExpirationTime {

    ZonedDateTime get();
    
    Long repeatInterval();
    
    Integer repeatLimit();
}
