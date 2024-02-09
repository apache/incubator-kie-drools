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
package org.drools.mvel.parser;

import java.io.IOException;
import java.nio.file.Paths;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.Test;

import static org.assertj.core.api.Assertions.fail;
import static org.drools.mvel.parser.Providers.provider;

public class DrlxParserTest {

    @Test
    public void testA () throws IOException {
        ParseStart<CompilationUnit> context = ParseStart.DRLX_COMPILATION_UNIT;
        MvelParser mvelParser = new MvelParser(new ParserConfiguration(), false);
        ParseResult<CompilationUnit> parse =
                mvelParser.parse(context,
                                 provider(Paths.get("src/test/resources/org/drools/mvel/parser/Example.drlx")));

        if (!parse.isSuccessful()) {
            fail(parse.toString());
        }
    }

}
