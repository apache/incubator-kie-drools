package org.drools.verifier.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

public class ScopesAgendaFilter
    implements
    AgendaFilter {

    public final static String             VERIFYING_SCOPE_SINGLE_RULE       = "single-rule";
    public final static String             VERIFYING_SCOPE_DECISION_TABLE    = "decision-table";
    public final static String             VERIFYING_SCOPE_KNOWLEDGE_PACKAGE = "knowledge-package";

    public final static Collection<String> ALL_SCOPES                        = new ArrayList<String>() {

                                                                                 private static final long serialVersionUID = 510l;

                                                                                 {
                                                                                     add( VERIFYING_SCOPE_DECISION_TABLE );
                                                                                     add( VERIFYING_SCOPE_SINGLE_RULE );
                                                                                     add( VERIFYING_SCOPE_KNOWLEDGE_PACKAGE );
                                                                                 }
                                                                             };

    public static final String             VERIFYING_SCOPES                  = "verifying_scopes";

    private final boolean                  acceptEmpty;

    private final Collection<String>       scopes;

    /**
     * 
     * @param acceptEmpty true accepts rules that do not have scope set.
     * @param scopes Valid scope values.
     */
    public ScopesAgendaFilter(boolean acceptEmpty,
                              Collection<String> scopes) {
        this.acceptEmpty = acceptEmpty;
        this.scopes = scopes;
    }

    public ScopesAgendaFilter(boolean acceptEmpty,
                              String scope) {
        this.acceptEmpty = acceptEmpty;

        Collection<String> list = new ArrayList<>();
        list.add( scope );
        this.scopes = list;
    }

    public boolean accept(Match activation) {
        if ( acceptEmpty && activation.getRule().getMetaData().isEmpty() ) {
            return true;
        }

        if ( activation.getRule().getMetaData().containsKey( VERIFYING_SCOPES ) ) {
            List<String> values = (List< String >) activation.getRule().getMetaData().get( VERIFYING_SCOPES );

            for ( String value : values ) {
                if ( scopes.contains( value.trim() ) ) {
                    return true;
                }
            }
        }

        return false;
    }

}
