/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.lang;

import org.antlr.runtime.RecognitionException;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.api.io.Resource;

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
