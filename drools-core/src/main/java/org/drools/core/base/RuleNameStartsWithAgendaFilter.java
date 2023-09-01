package org.drools.core.base;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

/**
 * Filters activations based on a specified rule name prefix.
 */
@XmlRootElement(name="rule-name-starts-with-agenda-filter")
@XmlAccessorType(XmlAccessType.NONE)
public class RuleNameStartsWithAgendaFilter
    implements
    AgendaFilter, Serializable {

    @XmlAttribute
    private String prefix;

    @XmlAttribute
    private boolean accept;

    public RuleNameStartsWithAgendaFilter() {
    }

    public RuleNameStartsWithAgendaFilter(final String prefix) {
        this( prefix,
              true );
    }

    public RuleNameStartsWithAgendaFilter(final String prefix,
                                          final boolean accept) {
        this.prefix = prefix;
        this.accept = accept;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isAccept() {
        return accept;
    }

    public boolean accept( Match activation ) {
        if ( activation.getRule().getName().startsWith( this.prefix ) ) {
            return this.accept;
        } else {
            return !this.accept;
        }
    }
}
