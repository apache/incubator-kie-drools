/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reteoo.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.drools.reteoo.test.dsl.NodeTestCase;
import org.junit.runner.RunWith;

/**
 * A test case suite to manage and run all node test cases
 */
@RunWith(JUnitNodeTestRunner.class)
public class NodeSuiteTest
    implements
    NodeTestCasesSource {
    
    /* (non-Javadoc)
     * @see org.drools.reteoo.test.NodeTestCasesSource#getTestCases()
     */
    public List<NodeTestCase> getTestCases() throws Exception {
        List<NodeTestCase> result = new ArrayList<NodeTestCase>();
        File base = new File( this.getClass().getResource( "." ).toURI() );
        for ( File file : base.listFiles( new FilenameFilter() {
            public boolean accept(File arg0,
                                  String arg1) {
                return arg1.endsWith( NodeTestCase.SUFFIX );
            }
        } ) ) {
            InputStream is = new FileInputStream( file );
            NodeTestCase tcase = ReteDslTestEngine.compile( is );
            tcase.setFileName(file.getName());
            if ( tcase.hasErrors() ) {
                throw new IllegalArgumentException( "Error parsing and loading testcase: " + file.getAbsolutePath() + "\n" + tcase.getErrors().toString() );
            }
            result.add( tcase );
            is.close();
        }

        return result;
    }

}
