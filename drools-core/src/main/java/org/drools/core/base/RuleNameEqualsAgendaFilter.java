package org.drools.core.base;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

/**
 * Filters activations based on an exact match of a rule name.
 */
@XmlRootElement(name="rule-name-equals-agenda-filter")
@XmlAccessorType(XmlAccessType.NONE)
public class RuleNameEqualsAgendaFilter
    implements
    AgendaFilter, Serializable {

    @XmlAttribute
    private String  name;

    @XmlAttribute
    private boolean accept;

    public RuleNameEqualsAgendaFilter() {
    }

    public RuleNameEqualsAgendaFilter(final String name) {
        this( name,
              true );
    }

    public RuleNameEqualsAgendaFilter(final String name,
                                      final boolean accept) {
        this.name = name;
        this.accept = accept;
    }

    public String getName() {
        return name;
    }

    public boolean isAccept() {
        return accept;
    }

    public boolean accept( Match activation ) {
        if ( activation.getRule().getName().equals( this.name ) ) {
            return this.accept;
        } else {
            return !this.accept;
        }
    }
}
