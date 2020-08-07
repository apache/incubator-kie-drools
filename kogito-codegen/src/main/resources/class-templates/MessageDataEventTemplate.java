package $Package$;


import org.kie.kogito.services.event.AbstractProcessDataEvent;

public class $TypeName$ extends AbstractProcessDataEvent<$Type$> {
    
    private String kogitoStartFromNode;
    
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

    public $TypeName$(String type,
                      String source,
                      $Type$ body,
                      String kogitoProcessinstanceId,
                      String kogitoParentProcessinstanceId,
                      String kogitoRootProcessinstanceId,
                      String kogitoProcessId,
                      String kogitoRootProcessId,
                      String kogitoProcessinstanceState) {
        super(type,
                source,
                body,
                kogitoProcessinstanceId,
                kogitoParentProcessinstanceId,
                kogitoRootProcessinstanceId,
                kogitoProcessId,
                kogitoRootProcessId,
                kogitoProcessinstanceState,
                null);
    }

    
    public void setKogitoStartFromNode(String kogitoStartFromNode) {
        this.kogitoStartFromNode = kogitoStartFromNode;
    }
    
    public String getKogitoStartFromNode() {
        return this.kogitoStartFromNode;
    }
}
