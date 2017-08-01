/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.services.task.jaxb;

import org.yaml.snakeyaml.Yaml;

public class YamlTaskSerializationTest extends AbstractTaskSerializationTest {

    public TestType getType() { 
        return TestType.YAML;
    }
    
    private Yaml yaml = new Yaml();
    
    public <T> T testRoundTrip(T in) throws Exception {
        String output = yaml.dump(in);
        logger.debug(output);
        return (T) yaml.load(output);
    }

    @Override
    public void addClassesToSerializationContext(Class<?>... extraClass) {
        // no-op
    }

}
