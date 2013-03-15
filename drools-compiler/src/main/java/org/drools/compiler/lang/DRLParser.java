package org.drools.compiler.lang;

import org.antlr.runtime.RecognitionException;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.io.Resource;

import java.util.LinkedList;
import java.util.List;

public interface DRLParser {

    PackageDescr compilationUnit() throws RecognitionException;
    PackageDescr compilationUnit(Resource resource) throws RecognitionException;

    void enableEditorInterface();
    void disableEditorInterface();
    LinkedList<DroolsSentence> getEditorInterface();

    List<DroolsParserException> getErrors();
    boolean hasErrors();
    List<String> getErrorMessages();

    void reportError( RecognitionException ex );
    void reportError( Exception ex );

    String chunk( int leftDelimiter, int rightDelimiter, int location );

}
