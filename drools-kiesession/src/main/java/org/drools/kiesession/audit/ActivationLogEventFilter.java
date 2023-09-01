package org.drools.kiesession.audit;

/**
 * An event filter that can be used to filter assertion events.
 * By default, all events are allowed.  You can filter out any of the
 * four types of assertion events by setting the allow boolean
 * for that type to false.
 */
public class ActivationLogEventFilter
    implements
    ILogEventFilter {

    private boolean allowActivationCreatedEvents    = true;
    private boolean allowActivationCancelledEvents  = true;
    private boolean allowBeforeActivationFireEvents = true;
    private boolean allowAfterActivationFireEvents  = true;

    public ActivationLogEventFilter(final boolean allowActivationCreatedEvents,
                                    final boolean allowActivationCancelledEvents,
                                    final boolean allowBeforeActivationFireEvents,
                                    final boolean allowAfterActivationFireEvents) {
        setAllowActivationCreatedEvents( allowActivationCreatedEvents );
        setAllowActivationCancelledEvents( allowActivationCancelledEvents );
        setAllowBeforeActivationFireEvents( allowBeforeActivationFireEvents );
        setAllowAfterActivationFireEvents( allowAfterActivationFireEvents );
    }

    /**
     * @see org.kie.audit.event.ILogEventFilter
     */
    public boolean acceptEvent(final LogEvent event) {
        switch ( event.getType() ) {
            case LogEvent.ACTIVATION_CREATED :
                return this.allowActivationCreatedEvents;
            case LogEvent.ACTIVATION_CANCELLED :
                return this.allowActivationCancelledEvents;
            case LogEvent.BEFORE_ACTIVATION_FIRE :
                return this.allowBeforeActivationFireEvents;
            case LogEvent.AFTER_ACTIVATION_FIRE :
                return this.allowAfterActivationFireEvents;
            default :
                return true;
        }
    }

    public void setAllowActivationCreatedEvents(final boolean allowActivationCreatedEvents) {
        this.allowActivationCreatedEvents = allowActivationCreatedEvents;
    }

    public void setAllowActivationCancelledEvents(final boolean allowActivationCancelledEvents) {
        this.allowActivationCancelledEvents = allowActivationCancelledEvents;
    }

    public void setAllowBeforeActivationFireEvents(final boolean allowBeforeActivationFireEvents) {
        this.allowBeforeActivationFireEvents = allowBeforeActivationFireEvents;
    }

    public void setAllowAfterActivationFireEvents(final boolean allowAfterActivationFireEvents) {
        this.allowAfterActivationFireEvents = allowAfterActivationFireEvents;
    }
}
