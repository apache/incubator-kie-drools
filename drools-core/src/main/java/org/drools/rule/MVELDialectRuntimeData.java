package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.mvel.ast.Function;
import org.mvel.integration.VariableResolver;
import org.mvel.integration.impl.MapVariableResolverFactory;

public class MVELDialectRuntimeData implements DialectRuntimeData, Externalizable {
	private MapFunctionResolverFactory functionFactory;

	/**
	 * Default constructor - for Externalizable. This should never be used by a
	 * user, as it will result in an invalid state for the instance.
	 */
	public MVELDialectRuntimeData() {
	}

	public MVELDialectRuntimeData(final DialectRuntimeRegistry datas) {
		this.functionFactory = new MapFunctionResolverFactory();
	}

    public DialectRuntimeData clone(DialectRuntimeRegistry registry) {
        DialectRuntimeData clone = new MVELDialectRuntimeData(registry);
        clone.merge(registry, this);
        return clone;
    }

	public MapFunctionResolverFactory getFunctionFactory() {
		return this.functionFactory;
	}

	public void removeRule(Package pkg, Rule rule) {
	}

	public void addFunction(Function function) {
		this.functionFactory.addFunction(function);
	}

	public void removeFunction(Package pkg, org.drools.rule.Function function) {
		this.functionFactory.removeFunction(function.getName());

	}

	public boolean isDirty() {
		return false;
	}

    public void setDirty(boolean dirty) {
    }

    public void merge(DialectRuntimeRegistry registry, DialectRuntimeData newData) {
	}

	public void reload() {
	}

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public static class MapFunctionResolverFactory extends
                                                   MapVariableResolverFactory implements Externalizable {

        public MapFunctionResolverFactory() {
            super(new HashMap<String, Object>());
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( this.variables );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this.variables = ( Map ) in.readObject();
        }

        public void addFunction(Function function) {
            this.variables.put(function.getName(), function);
        }

        public void removeFunction(String functionName) {
            this.variables.remove(functionName);
            this.variableResolvers.remove(functionName);
        }

        public VariableResolver createVariable(String name, Object value) {
            throw new RuntimeException(
                    "variable is a read-only function pointer");
        }

        public VariableResolver createIndexedVariable(int index, String name,
                                                      Object value, Class<?> type) {
            throw new RuntimeException(
                    "variable is a read-only function pointer");
        }
    }
}
