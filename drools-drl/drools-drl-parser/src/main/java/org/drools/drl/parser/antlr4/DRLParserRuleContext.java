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
package org.drools.drl.parser.antlr4;

import org.antlr.v4.runtime.ParserRuleContext;
import org.drools.drl.ast.descr.BasicDescrContext;

/**
 * This is a super class of generated Context classes to provide basic context information for Descr object.
 */
public class DRLParserRuleContext extends ParserRuleContext implements BasicDescrContext {

    public DRLParserRuleContext() {
        super();
    }

    public DRLParserRuleContext(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public int getStartCharacter() {
        return this.getStart().getStartIndex();
    }

    @Override
    public int getEndCharacter() {
        // TODO: Current DRL6Parser adds +1 for EndCharacter but it doesn't look reasonable. At the moment, I don't add. Instead, I fix unit tests.
        //       I will revisit if this is the right approach.
        return this.getStop().getStopIndex();
    }

    @Override
    public int getLine() {
        return this.getStart().getLine();
    }

    @Override
    public int getColumn() {
        return this.getStart().getCharPositionInLine();
    }

    @Override
    public int getEndLine() {
        return this.getStop().getLine();
    }

    @Override
    public int getEndColumn() {
        // last column of the end token
        return this.getStop().getCharPositionInLine() + this.getStop().getText().length() - 1;
    }
}
