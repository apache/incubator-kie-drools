package org.kie.kogito.jobs;

import java.time.ZonedDateTime;
import java.util.Objects;


public class ExactExpirationTime implements ExpirationTime {

    private final ZonedDateTime expirationTime;
    
    private ExactExpirationTime(ZonedDateTime expirationTime) {
        this.expirationTime = Objects.requireNonNull(expirationTime);
    }
    
    @Override
    public ZonedDateTime get() {
        return expirationTime;
    }

    @Override
    public Long repeatInterval() {     
        return null;
    }
    
    @Override
    public Integer repeatLimit() {
        return 0;
    }
    
    public static ExactExpirationTime of(ZonedDateTime expirationTime) {
        return new ExactExpirationTime(expirationTime);
    }
    
    public static ExactExpirationTime of(String date) {
        return new ExactExpirationTime(ZonedDateTime.parse(date));
    }

    public static ExactExpirationTime now() {
        return new ExactExpirationTime(ZonedDateTime.now());
    }
}
