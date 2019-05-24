class Template {
    Object f = new org.jbpm.workflow.core.node.SubProcessFactory<$Type$>() {
        public $Type$ bind(org.kie.api.runtime.process.ProcessContext kcontext) {
            return null;
        }
        public org.kie.kogito.process.ProcessInstance<$Type$> createInstance($Type$ model) {
            return null;
        }
        public void unbind(org.kie.api.runtime.process.ProcessContext kcontext, $Type$ model) {

        }
    };
}
