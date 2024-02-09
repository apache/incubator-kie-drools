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
package org.kie.dmn.xls2dmn.cli;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.kie.api.builder.Message.Level;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.feel.util.Either;
import org.kie.dmn.validation.DMNValidatorFactory;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils {

    static final Logger LOG = LoggerFactory.getLogger(TestUtils.class);

    public static DMNRuntime getRuntime(Consumer<String[]> command, File outFile, String[] parameters) throws Exception {
        command.accept(parameters);

        return validateRuntime(outFile);
    }

    public static DMNRuntime validateRuntime(File outFile) {
        List<DMNMessage> validate = DMNValidatorFactory.newValidator().validate(outFile);
        assertThat(validate.stream().filter(m -> m.getLevel() == Level.ERROR).count()).isEqualTo(0L);

        Either<Exception, DMNRuntime> fromResources = DMNRuntimeBuilder.fromDefaults()
                                                                       .buildConfiguration()
                                                                       .fromResources(Collections.singletonList(ResourceFactory.newFileResource(outFile)));

        LOG.info("{}", System.getProperty("java.io.tmpdir"));
        LOG.info("{}", outFile);
        DMNRuntime dmnRuntime = fromResources.getOrElseThrow(RuntimeException::new);
        return dmnRuntime;
    }

}