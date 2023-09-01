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
package org.drools.compiler.kproject.models;

import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.RuleTemplateModel;

public class RuleTemplateModelImpl implements RuleTemplateModel {

    private KieBaseModelImpl kbase;

    private String dtable;
    private String template;
    private int row;
    private int col;

    public RuleTemplateModelImpl() { }

    public RuleTemplateModelImpl( KieBaseModelImpl kbase, String dtable, String template, int row, int col ) {
        this.kbase = kbase;
        this.dtable = dtable;
        this.template = template;
        this.row = row;
        this.col = col;
    }

    public void setKBase(KieBaseModel kieBaseModel) {
        this.kbase = (KieBaseModelImpl) kieBaseModel;
    }

    public String getDtable() {
        return dtable;
    }

    public void setDtable( String dtable ) {
        this.dtable = dtable;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate( String template ) {
        this.template = template;
    }

    public int getRow() {
        return row;
    }

    public void setRow( int row ) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol( int col ) {
        this.col = col;
    }
}
