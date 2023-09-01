package org.drools.model.functions;

public class ScriptBlock implements BlockN {

    private final Class<?> ruleClass;
    private final String script;

    public ScriptBlock(Class<?> ruleClass, String script) {
        this.ruleClass = ruleClass;
        this.script = script;
    }

    @Override
    public void execute(Object... objs) {
        throw new UnsupportedOperationException("Script Block is not expected to execute out of the box.");
    }

    public String getScript() {
        return script;
    }

    public Class<?> getRuleClass() {
        return ruleClass;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof ScriptBlock) ) return false;

        ScriptBlock that = ( ScriptBlock ) o;

        return script.equals( that.script );
    }

    @Override
    public int hashCode() {
        return script.hashCode();
    }
}
