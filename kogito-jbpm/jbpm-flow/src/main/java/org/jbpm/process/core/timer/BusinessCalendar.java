package org.jbpm.process.core.timer;

import java.util.Date;
/**
 * BusinessCalendar allows for defining custom definitions of working days, hours and holidays
 * to be taken under consideration when scheduling time based activities such as timers or deadlines.
 */
public interface BusinessCalendar {

    /**
     * Calculates given time expression into duration in milliseconds based on calendar configuration.
     * 
     * @param timeExpression time expression that is supported by business calendar implementation.
     * @return duration expressed in milliseconds
     */
    public long calculateBusinessTimeAsDuration(String timeExpression);
    
    /**
     * Calculates given time expression into target date based on calendar configuration.
     * @param timeExpression time expression that is supported by business calendar implementation.
     * @return date when given time expression will match in the future
     */
    public Date calculateBusinessTimeAsDate(String timeExpression);
}
