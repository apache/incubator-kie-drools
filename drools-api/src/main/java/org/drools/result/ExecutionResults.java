package org.drools.result;

import java.util.ArrayList;
import java.util.List;

public interface ExecutionResults {

    List<GenericResult> getResults();

    Object getValue( String identifier );

    Object getFactHandle( String identifier );

    List<String> getIdentifiers();

}
