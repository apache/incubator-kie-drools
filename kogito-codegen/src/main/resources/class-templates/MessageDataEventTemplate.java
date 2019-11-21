package $Package$;


import org.kie.kogito.services.event.AbstractProcessDataEvent;

public class $TypeName$ extends AbstractProcessDataEvent<$Type$> {
    
    public $TypeName$() {
        super(null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null);
    }
    
    public $TypeName$(String source, 
                           $Type$ body,
                           String kogitoProcessinstanceId,
                           String kogitoParentProcessinstanceId,
                           String kogitoRootProcessinstanceId,
                           String kogitoProcessId,
                           String kogitoRootProcessId,
                           String kogitoProcessinstanceState) {
        super(source,
              body,
              kogitoProcessinstanceId,
              kogitoParentProcessinstanceId,
              kogitoRootProcessinstanceId,
              kogitoProcessId,
              kogitoRootProcessId,
              kogitoProcessinstanceState,
              null);
    }

}
