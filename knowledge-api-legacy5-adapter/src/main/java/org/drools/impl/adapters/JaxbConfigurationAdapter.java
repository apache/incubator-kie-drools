package org.drools.impl.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.drools.core.builder.conf.impl.JaxbConfigurationImpl;
import org.drools.core.builder.conf.impl.ResourceConfigurationImpl;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.JaxbConfiguration;

import com.sun.tools.xjc.Language;
import com.sun.tools.xjc.Options;

public class JaxbConfigurationAdapter extends ResourceConfigurationImpl implements JaxbConfiguration {

    private static final long serialVersionUID = -1425447385459529502L;
    private final org.drools.builder.JaxbConfiguration delegate;

    public JaxbConfigurationAdapter(org.drools.builder.JaxbConfiguration delegate) {
        this.delegate = delegate;
    }

    public Options getXjcOpts() {
        return delegate.getXjcOpts();
    }

    public String getSystemId() {
        return delegate.getSystemId();
    }

    public List<String> getClasses() {
        return delegate.getClasses();
    }

    public Properties toProperties() {
        Properties prop = super.toProperties();
        prop.setProperty( "drools.jaxb.conf.systemId", getSystemId() );
        prop.setProperty( "drools.jaxb.conf.classes", getClass().toString() );
        Options xjcOpts = getXjcOpts();
        if (xjcOpts != null) {
            // how to serialize Options to a property file???
            prop.setProperty( "drools.jaxb.conf.opts.class", xjcOpts.getClass().getName() );
            if (xjcOpts.getSchemaLanguage() != null) {
                prop.setProperty( "drools.jaxb.conf.opts.lang", xjcOpts.getSchemaLanguage().toString() );
            }
        }
        return prop;
    }

    public ResourceConfiguration fromProperties(Properties prop) {
        super.fromProperties(prop);
        ((JaxbConfigurationImpl)delegate).setSystemId( prop.getProperty( "drools.jaxb.conf.systemId", null ) );
        String classesStr = prop.getProperty( "drools.jaxb.conf.classes", "[]" );
        classesStr = classesStr.substring( 1, classesStr.length()-1 ).trim();
        List<String> classes = new ArrayList<String>();
        if( classesStr != null && classesStr.length() > 1 ) {
            // can't use Arrays.asList() because have to trim() each element
            for( String clz : classesStr.split( "," ) ) {
                classes.add( clz.trim() );
            }
        }
        ((JaxbConfigurationImpl)delegate).setClasses(classes);

        // how to deserialize Options from a properties file?
        String optsClass = prop.getProperty( "drools.jaxb.conf.opts.class", null );
        if (optsClass != null) {
            try {
                Options xjcOpts = (Options) Class.forName( optsClass ).newInstance();
                String optsLang = prop.getProperty( "drools.jaxb.conf.opts.lang", null );
                if (optsLang != null) {
                    xjcOpts.setSchemaLanguage( Language.valueOf(optsLang) );
                }
                ((JaxbConfigurationImpl)delegate).setXjcOpts(xjcOpts);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }
}
