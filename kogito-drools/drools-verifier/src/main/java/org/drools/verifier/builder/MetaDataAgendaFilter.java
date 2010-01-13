package org.drools.verifier.builder;

import java.util.Collection;

import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.AgendaFilter;

/**
 * 
 * @author rikkola
 *
 */
public class MetaDataAgendaFilter
    implements
    AgendaFilter {

    private final boolean            acceptEmpty;
    private final String             metadata;

    private final Collection<String> validValues;

    /**
     * 
     * @param acceptEmpty true accepts rules that do not have metadata set.
     * @param metadata Name of the meta data.
     * @param validValues Valid values that the meta data can have.
     */
    public MetaDataAgendaFilter(boolean acceptEmpty,
                                String metadata,
                                Collection<String> validValues) {
        this.acceptEmpty = acceptEmpty;
        this.metadata = metadata;
        this.validValues = validValues;
    }

    public boolean accept(Activation activation) {
        if ( acceptEmpty && activation.getRule().listMetaAttributes().isEmpty() ) {
            return true;
        }

        if ( activation.getRule().listMetaAttributes().contains( metadata ) ) {
            String[] values = activation.getRule().getMetaAttribute( metadata ).split( "," );

            for ( String value : values ) {
                if ( validValues.contains( value.trim() ) ) {
                    return true;
                }
            }
        }

        return false;
    }

}
