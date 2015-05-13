package org.kie.internal.builder;



public class ResourceChange {
    public static enum Type {
        RULE, DECLARATION, FUNCTION, GLOBAL, PROCESS;
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
    private final ChangeType action;
    private final ResourceChange.Type type;
    private final String name;
    public ResourceChange(ChangeType action,
                          ResourceChange.Type type,
                          String name) {
        super();
        this.action = action;
        this.type = type;
        this.name = name;
    }
    public ChangeType getChangeType() {
        return action;
    }
    public ResourceChange.Type getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ResourceChange other = (ResourceChange) obj;
        if ( action != other.action ) return false;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        if ( type != other.type ) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "ResourceChange [action=" + action + ", type=" + type + ", name=" + name + "]";
    }
    
}
