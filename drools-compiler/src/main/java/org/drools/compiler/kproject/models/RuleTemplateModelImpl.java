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
