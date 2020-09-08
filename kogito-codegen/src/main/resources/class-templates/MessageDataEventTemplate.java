package $Package$;


import org.kie.kogito.services.event.AbstractProcessDataEvent;

public class $TypeName$ extends AbstractProcessDataEvent<$Type$> {
    
    
    public $TypeName$() { }
    
    
    public $TypeName$(String source,
                      $Type$ body,
                      String kogitoProcessinstanceId,
                      String kogitoParentProcessinstanceId,
                      String kogitoRootProcessinstanceId,
                      String kogitoProcessId,
                      String kogitoRootProcessId,
                      String kogitoProcessinstanceState,
                      String kogitoReferenceId) {
        this(
            null,
            source,
            body,
            kogitoProcessinstanceId,
            kogitoParentProcessinstanceId,
            kogitoRootProcessinstanceId,
            kogitoProcessId,
            kogitoRootProcessId,
            kogitoProcessinstanceState,
            kogitoReferenceId);
    }
    
    
    public $TypeName$(String type, String source,
                      $Type$ body,
                      String kogitoProcessinstanceId,
                      String kogitoParentProcessinstanceId,
                      String kogitoRootProcessinstanceId,
                      String kogitoProcessId,
                      String kogitoRootProcessId,
                      String kogitoProcessinstanceState,
                      String kogitoReferenceId) {
        super(
            type,
            source,
            body,
            kogitoProcessinstanceId,
            kogitoParentProcessinstanceId,
            kogitoRootProcessinstanceId,
            kogitoProcessId,
            kogitoRootProcessId,
            kogitoProcessinstanceState,
            null,
            kogitoReferenceId);
    }
}
