/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.verifier.report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.drools.util.IoUtils;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

public class PlainTextReportWriter
    implements
    VerifierReportWriter {

    public void writeReport(OutputStream out,
                            VerifierReport result) throws IOException {

        StringBuilder str = new StringBuilder();

        for ( Severity severity : Severity.values() ) {
            Collection<VerifierMessageBase> messages = result.getBySeverity( severity );

            str.append( "************* " );
            str.append( severity.getTuple() );
            str.append( " " );

            str.append( messages.size() );
            str.append( " ******************\n" );
            for ( VerifierMessageBase message : messages ) {
                str.append( message );
                str.append( "\n" );
            }
            str.append( "\n" );
        }

        out.write( str.toString().getBytes( IoUtils.UTF8_CHARSET ) );
    }

}
