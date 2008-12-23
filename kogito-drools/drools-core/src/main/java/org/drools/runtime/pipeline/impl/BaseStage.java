package org.drools.runtime.pipeline.impl;

import org.drools.definition.pipeline.Stage;
import org.drools.definition.pipeline.StageExceptionHandler;

public class BaseStage implements Stage {
    private StageExceptionHandler exceptionHandler;

    public BaseStage() {
        super();
    }
    
    public void setStageExceptionHandler(StageExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
    
    protected void handleException(Stage stage, Object object, Exception exception) {
        if ( this.exceptionHandler != null ) {
            this.exceptionHandler.handleException( stage, object, exception );
        } else {
            throw new RuntimeException( exception );
        }
    }

}
