/*
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
package org.kie.kogito.index.postgresql;

import java.util.Iterator;
import java.util.List;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.query.ReturnableType;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.type.BasicTypeReference;
import org.hibernate.type.SqlTypes;

public class ContainsSQLFunction extends StandardSQLFunction {

    static final String CONTAINS_NAME = "contains";
    static final String CONTAINS_ALL_NAME = "containsAll";
    static final String CONTAINS_ANY_NAME = "containsAny";

    static final String CONTAINS_SEQ = "??";
    static final String CONTAINS_ALL_SEQ = "??&";
    static final String CONTAINS_ANY_SEQ = "??|";

    private final String operator;

    private static final BasicTypeReference<Boolean> RETURN_TYPE = new BasicTypeReference<>("boolean", Boolean.class, SqlTypes.BOOLEAN);

    ContainsSQLFunction(String name, String operator) {
        super(name, RETURN_TYPE);
        this.operator = operator;
    }

    @Override
    public void render(
            SqlAppender sqlAppender,
            List<? extends SqlAstNode> args,
            ReturnableType<?> returnType,
            SqlAstTranslator<?> translator) {
        int size = args.size();
        if (size < 2) {
            throw new IllegalArgumentException("Function " + getName() + " requires at least two arguments");
        }
        Iterator<? extends SqlAstNode> iter = args.iterator();
        iter.next().accept(translator);
        sqlAppender.append(' ');
        sqlAppender.append(operator);
        sqlAppender.append(' ');
        if (size == 2) {
            iter.next().accept(translator);
        } else {
            sqlAppender.append("array[");
            do {
                iter.next().accept(translator);
                sqlAppender.append(iter.hasNext() ? ',' : ']');
            } while (iter.hasNext());
        }
    }
}
