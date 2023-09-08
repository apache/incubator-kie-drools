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
package org.drools.drlonyaml.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.MVELExprDescr;
import org.drools.drl.ast.descr.PatternDescr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = Pattern.class) // see https://stackoverflow.com/a/34128468/893991 TODO maybe enforce this check somehow
public class Pattern implements Base {
    @JsonProperty(required = true)
    private String given;
    @JsonInclude(Include.NON_EMPTY)
    private String as;
    @JsonInclude(Include.NON_EMPTY)
    private List<String> having = new ArrayList<>();
    @JsonInclude(Include.NON_EMPTY)
    private String from;
    
    public static Pattern from(PatternDescr o) {
        Objects.requireNonNull(o);
        Pattern result = new Pattern();
        result.given = o.getObjectType();
        if (o.getAllBoundIdentifiers().isEmpty()) {
            // do nothing, as expected.
        } else if (o.getAllBoundIdentifiers().size() == 1) {
            result.as = o.getAllBoundIdentifiers().get(0);
        } else {
            result.as = o.getAllBoundIdentifiers().get(0); // TODO check the index=0 is always the pattern one
        }
        for (BaseDescr c: o.getConstraint().getDescrs()) {
            if (c instanceof MVELExprDescr) {
                result.having.add(((MVELExprDescr) c).getExpression());
            } if (c instanceof ExprConstraintDescr) {
                result.having.add(((ExprConstraintDescr) c).getExpression());
            } else {
                throw new UnsupportedOperationException();
            }
        }
        if (o.getSource() != null) {
            if (o.getSource() instanceof FromDescr) {                
                result.from = ((FromDescr) o.getSource()).getDataSource().getText();
            } else {
                throw new UnsupportedOperationException("unknown patternSourceDescr");
            }
        }
        return result;
    }

    public String getGiven() {
        return given;
    }

    public String getAs() {
        return as;
    }

    public List<String> getHaving() {
        return having;
    }

    public String getFrom() {
        return from;
    }
}
