/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.compiler.kproject.models;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
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

    public static class RuleTemplateConverter extends AbstractXStreamConverter {

        public RuleTemplateConverter() {
            super(RuleTemplateModelImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            RuleTemplateModelImpl rtm = (RuleTemplateModelImpl) value;
            writer.addAttribute( "dtable", rtm.getDtable() );
            writer.addAttribute( "template", rtm.getTemplate() );
            writer.addAttribute( "row", "" + rtm.getRow() );
            writer.addAttribute( "col", "" + rtm.getCol() );
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            RuleTemplateModelImpl rtm = new RuleTemplateModelImpl();
            rtm.setDtable(reader.getAttribute("dtable"));
            rtm.setTemplate(reader.getAttribute("template"));
            rtm.setRow(Integer.parseInt( reader.getAttribute("row")) );
            rtm.setCol(Integer.parseInt( reader.getAttribute( "col" ) ) );
            return rtm;
        }
    }
}
