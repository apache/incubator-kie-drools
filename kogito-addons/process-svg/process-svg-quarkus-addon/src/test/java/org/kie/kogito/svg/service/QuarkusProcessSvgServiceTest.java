/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.svg.service;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.svg.AbstractProcessSvgService;
import org.kie.kogito.svg.ProcessSvgServiceTest;
import org.kie.kogito.svg.dataindex.DataIndexClient;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class QuarkusProcessSvgServiceTest extends ProcessSvgServiceTest {

    private QuarkusProcessSvgService tested;
    private DataIndexClient dataIndexClient;

    @BeforeEach
    public void setup() {
        dataIndexClient = mock(DataIndexClient.class);

        tested = spy(new QuarkusProcessSvgService(dataIndexClient,
                                                  Optional.empty(),
                                                  "#C0C0C0",
                                                  "#030303",
                                                  "#FF0000"
        ));
    }

    @Override
    protected AbstractProcessSvgService getTestedProcessSvgService() {
        return tested;
    }
}