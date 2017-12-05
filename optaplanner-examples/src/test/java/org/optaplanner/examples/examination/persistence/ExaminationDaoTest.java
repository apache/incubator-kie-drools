/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.examination.persistence;

import java.io.File;
import java.util.Collection;

import org.junit.runners.Parameterized;
import org.optaplanner.examples.common.persistence.SolutionDaoTest;
import org.optaplanner.examples.examination.app.ExaminationApp;
import org.optaplanner.examples.examination.domain.Examination;

public class ExaminationDaoTest extends SolutionDaoTest<Examination> {

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> getSolutionFilesAsParameters() {
        return getSolutionFilesAsParameters(new ExaminationApp());
    }

    public ExaminationDaoTest(File solutionFile) {
        super(new ExaminationApp(), solutionFile);
    }

}
