public class AccumulateInlineFunction implements org.kie.api.runtime.rule.AccumulateFunction<CONTEXT_DATA_GENERIC> {

    public static class ContextData implements java.io.Serializable {
        // context fields will go here.
    }

    public void readExternal(java.io.ObjectInput in) throws java.io.IOException, ClassNotFoundException {
        // functions are stateless, so nothing to serialize
    }

    public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
        // functions are stateless, so nothing to serialize
    }

    public ContextData createContext() {
        return new ContextData();
    }

    public void init(ContextData data) {
    }

    public void accumulate(ContextData data, Object $single) {
    }

    public void reverse(ContextData data, Object $single) {
    }

    public Object getResult(ContextData data) {
    }

    public boolean supportsReverse() {
    }

    public Class<?> getResultType() {
    }
}