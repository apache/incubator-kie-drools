/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.lang.ast;

import org.antlr.v4.runtime.tree.TerminalNode;

public class NameRefNode
        extends BaseNode {

    public NameRefNode(TerminalNode ctx) {
        super();
        this.setStartChar( ctx.getSymbol().getStartIndex() );
        this.setStartLine( ctx.getSymbol().getLine() );
        this.setStartColumn( ctx.getSymbol().getCharPositionInLine() );
        this.setEndChar( ctx.getSymbol().getStopIndex() );
        this.setEndLine( ctx.getSymbol().getLine() );
        this.setEndColumn( ctx.getSymbol().getCharPositionInLine() + ctx.getSymbol().getText().length() );
        this.setText( ctx.getText() );
    }
}
