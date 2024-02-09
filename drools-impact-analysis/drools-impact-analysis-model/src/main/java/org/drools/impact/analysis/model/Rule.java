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
package org.drools.impact.analysis.model;

import java.util.Objects;

import org.drools.impact.analysis.model.left.LeftHandSide;
import org.drools.impact.analysis.model.right.RightHandSide;

public class Rule {

    private final String pkg;
    private final String name;
    private final String resource;

    private final LeftHandSide lhs = new LeftHandSide();
    private final RightHandSide rhs = new RightHandSide();

    public Rule( String pkg, String name, String resource ) {
        this.pkg = pkg;
        this.name = name;
        this.resource = resource;
    }

    public String getPkg() {
        return pkg;
    }

    public String getResource() {
        return resource;
    }

    public String getName() {
        return name;
    }


    public LeftHandSide getLhs() {
        return lhs;
    }

    public RightHandSide getRhs() {
        return rhs;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "pkg='" + pkg + '\'' +
                ", name='" + name + '\'' +
                ", resource='" + resource + '\'' +
                ",\n lhs=" + lhs +
                ",\n rhs=" + rhs +
                '}';
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Rule rule = ( Rule ) o;
        return pkg.equals( rule.pkg ) && name.equals( rule.name );
    }

    @Override
    public int hashCode() {
        return Objects.hash( pkg, name );
    }
}
