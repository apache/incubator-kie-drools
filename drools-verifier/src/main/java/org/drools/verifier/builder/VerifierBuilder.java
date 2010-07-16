/**
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

package org.drools.verifier.builder;

import java.util.List;

import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.report.VerifierReportConfiguration;

public interface VerifierBuilder {

    public VerifierConfiguration newVerifierConfiguration();

    public VerifierReportConfiguration newVerifierReportConfiguration();

    public Verifier newVerifier();

    public Verifier newVerifier(VerifierConfiguration conf);

    boolean hasErrors();

    List<VerifierBuilderError> getErrors();

}
