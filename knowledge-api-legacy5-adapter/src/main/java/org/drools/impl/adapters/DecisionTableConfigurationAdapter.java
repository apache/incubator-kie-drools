package org.drools.impl.adapters;

import java.util.Properties;

import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.core.builder.conf.impl.ResourceConfigurationImpl;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;


public class DecisionTableConfigurationAdapter extends ResourceConfigurationImpl implements DecisionTableConfiguration {
    
    private static final long serialVersionUID = -2052308765193190359L;
    
    private final org.drools.builder.DecisionTableConfiguration delegate;
    
    public DecisionTableConfigurationAdapter( org.drools.builder.DecisionTableConfiguration delegate ) {
        super.setResourceType(ResourceType.DTABLE);
        this.delegate = delegate;
    }

    public void setInputType(org.drools.builder.DecisionTableInputType inputType) {
        delegate.setInputType(inputType);
    }

    public DecisionTableInputType getInputType() {
        return delegate.getInputType() == org.drools.builder.DecisionTableInputType.CSV ? DecisionTableInputType.CSV : DecisionTableInputType.XLS;
    }

    public void setWorksheetName(String name) {
        delegate.setWorksheetName(name);
    }

    public String getWorksheetName() {
        return delegate.getWorksheetName();
    }

    public Properties toProperties() {
        Properties prop = super.toProperties();
        prop.setProperty( DecisionTableConfigurationImpl.DROOLS_DT_TYPE, getInputType().toString() );
        if( getWorksheetName() != null ) {
            prop.setProperty( DecisionTableConfigurationImpl.DROOLS_DT_WORKSHEET, getWorksheetName() );
        }
        return prop;
    }

    public ResourceConfiguration fromProperties(Properties prop) {
        super.fromProperties(prop);
        setInputType( DecisionTableInputType.valueOf( prop.getProperty( DecisionTableConfigurationImpl.DROOLS_DT_TYPE, DecisionTableInputType.XLS.toString() ) ) );
        setWorksheetName( prop.getProperty( DecisionTableConfigurationImpl.DROOLS_DT_WORKSHEET, null ) );
        return this;
    }

    @Override
    public void setInputType(DecisionTableInputType inputType) {
        delegate.setInputType( inputType == DecisionTableInputType.CSV ? org.drools.builder.DecisionTableInputType.CSV : org.drools.builder.DecisionTableInputType.XLS);
    }
}
