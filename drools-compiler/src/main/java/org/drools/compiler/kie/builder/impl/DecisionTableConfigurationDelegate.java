package org.drools.compiler.kie.builder.impl;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.RuleTemplateConfiguration;

import java.util.List;
import java.util.Properties;

public class DecisionTableConfigurationDelegate implements DecisionTableConfiguration {

    private final DecisionTableConfiguration delegate;
    private final String sheetName;

    public DecisionTableConfigurationDelegate( DecisionTableConfiguration delegate, String sheetName ) {
        this.delegate = delegate;
        this.sheetName = sheetName;
    }

    @Override
    public void setInputType( DecisionTableInputType inputType ) {
        delegate.setInputType( inputType );

    }

    @Override
    public DecisionTableInputType getInputType() {
        return delegate.getInputType();
    }

    @Override
    public void setWorksheetName( String name ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getWorksheetName() {
        return sheetName;
    }

    @Override
    public void addRuleTemplateConfiguration(Resource template, int row, int col ) {
        delegate.addRuleTemplateConfiguration( template, row, col );
    }

    @Override
    public List<RuleTemplateConfiguration> getRuleTemplateConfigurations() {
        return delegate.getRuleTemplateConfigurations();
    }

    @Override
    public boolean isTrimCell() {
        return delegate.isTrimCell();
    }

    @Override
    public void setTrimCell( boolean trimCell ) {
        delegate.setTrimCell( trimCell );
    }

    @Override
    public Properties toProperties() {
        return delegate.toProperties();
    }

    @Override
    public ResourceConfiguration fromProperties(Properties prop ) {
        return delegate.fromProperties( prop );
    }
}