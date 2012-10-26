package org.drools.lang;

import org.antlr.runtime.RecognitionException;
import org.drools.compiler.DroolsParserException;
import org.drools.io.Resource;
import org.drools.lang.descr.PackageDescr;

import java.util.LinkedList;
import java.util.List;

public interface DRLParser {

    PackageDescr compilationUnit() throws RecognitionException;
    PackageDescr compilationUnit(Resource resource) throws RecognitionException;

    void enableEditorInterface();
    LinkedList<DroolsSentence> getEditorInterface();

    List<DroolsParserException> getErrors();
    boolean hasErrors();
    List<String> getErrorMessages();

    String chunk( int leftDelimiter, int rightDelimiter, int location );
}
