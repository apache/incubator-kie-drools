package org.drools.core.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@XmlRootElement(name="agenda-filter")
@XmlAccessorType(XmlAccessType.FIELD)
public class RuleNameSerializationAgendaFilter implements AgendaFilter {

    protected static final transient Logger logger = LoggerFactory.getLogger(RuleNameSerializationAgendaFilter.class);

    @XmlElement
    @XmlSchemaType(name="string")
    private String matchContent;

    @XmlElement
    @XmlSchemaType(name="boolean")
    private Boolean accept;

    @XmlEnum
    public static enum AgendaFilterType {
       ENDS_WITH,
       EQUALS,
       MATCHES,
       STARTS_WITH;
    }

    @XmlElement(required=true)
    private AgendaFilterType type;

    public RuleNameSerializationAgendaFilter() {
        // JAXB constructor
    }

    public RuleNameSerializationAgendaFilter(AgendaFilter originalAgendaFilter) {
        if( originalAgendaFilter instanceof RuleNameSerializationAgendaFilter ) {
            this.matchContent = ((RuleNameSerializationAgendaFilter) originalAgendaFilter).matchContent;
            this.accept = ((RuleNameSerializationAgendaFilter) originalAgendaFilter).accept;
            this.type = ((RuleNameSerializationAgendaFilter) originalAgendaFilter).type;
        } else if( originalAgendaFilter instanceof RuleNameEndsWithAgendaFilter ) {
           this.matchContent = ((RuleNameEndsWithAgendaFilter) originalAgendaFilter).getSuffix();
           this.accept = ((RuleNameEndsWithAgendaFilter) originalAgendaFilter).isAccept();
           this.type = AgendaFilterType.ENDS_WITH;
        } else if( originalAgendaFilter instanceof RuleNameEqualsAgendaFilter ) {
           this.matchContent = ((RuleNameEqualsAgendaFilter) originalAgendaFilter).getName();
           this.accept = ((RuleNameEqualsAgendaFilter) originalAgendaFilter).isAccept();
           this.type = AgendaFilterType.EQUALS;
        } else if( originalAgendaFilter instanceof RuleNameMatchesAgendaFilter ) {
           this.matchContent = ((RuleNameMatchesAgendaFilter) originalAgendaFilter).getPattern().pattern();
           this.accept = ((RuleNameMatchesAgendaFilter) originalAgendaFilter).isAccept();
           this.type = AgendaFilterType.MATCHES;
        } else if( originalAgendaFilter instanceof RuleNameStartsWithAgendaFilter ) {
           this.matchContent = ((RuleNameStartsWithAgendaFilter) originalAgendaFilter).getPrefix();
           this.accept = ((RuleNameStartsWithAgendaFilter) originalAgendaFilter).isAccept();
           this.type = AgendaFilterType.STARTS_WITH;
        } else {
            logger.warn( originalAgendaFilter.getClass().getName() + " instance will not be serialized!");
        }
    }

    public static AgendaFilter newFrom(AgendaFilter agendaFilter) {
        return new RuleNameSerializationAgendaFilter(agendaFilter);
    }

    public AgendaFilter getOriginal() {
        AgendaFilter realAgendaFilter = null;
        switch( this.type ) {
        case ENDS_WITH: {
            RuleNameEndsWithAgendaFilter filter = new RuleNameEndsWithAgendaFilter(this.matchContent, this.accept);
            realAgendaFilter = filter;
            break;
        }
        case EQUALS: {
            RuleNameEqualsAgendaFilter filter = new RuleNameEqualsAgendaFilter(this.matchContent, this.accept);
            realAgendaFilter = filter;
            break;
        }
        case MATCHES: {
            RuleNameMatchesAgendaFilter filter = new RuleNameMatchesAgendaFilter(this.matchContent, this.accept);
            realAgendaFilter = filter;
            break;
        }
        case STARTS_WITH: {
            RuleNameStartsWithAgendaFilter filter = new RuleNameStartsWithAgendaFilter(this.matchContent, this.accept);
            realAgendaFilter = filter;
            break;
        }
        default:
            throw new IllegalStateException("Unknown " + AgendaFilter.class.getSimpleName() + " type: " + this.type.name() );
        }
        return realAgendaFilter;
    }

    @Override
    public boolean accept( Match match ) {
        return getOriginal().accept(match);
    }

    @Override
    public boolean equals( Object obj ) {
        // instanceof fails if obj == null
        if( obj instanceof RuleNameSerializationAgendaFilter ) {
            RuleNameSerializationAgendaFilter other = (RuleNameSerializationAgendaFilter) obj;
            if( objectEquals(this.accept, other.accept)
                    && objectEquals(this.matchContent, other.matchContent)
                    && objectEquals(this.type, other.type) ) {
                   return true;
            }
        }
        return false;
    }

    private static boolean objectEquals( Object orig, Object other ) {
        if( orig == null ) {
            return orig == other;
        } else {
            return orig.equals(other);
        }
    }
}
