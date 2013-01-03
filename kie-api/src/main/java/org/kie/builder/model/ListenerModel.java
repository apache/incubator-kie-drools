package org.kie.builder.model;

import static java.util.EnumSet.allOf;

/**
 * ListenerModel is a model allowing to programmatically define a Listener and wire it to a KieSession
 */
public interface ListenerModel {

    public enum Kind {
        AGENDA_EVENT_LISTENER("agendaEventListener"),
        WORKING_MEMORY_EVENT_LISTENER("workingMemoryEventListener"),
        PROCESS_EVENT_LISTENER("processEventListener");

        private final String name;

        private Kind(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Kind fromString(String name) {
            for (Kind kind : allOf(Kind.class)) {
                if (kind.toString().equals(name)) {
                    return kind;
                }
            }
            return null;
        }
    }

    /**
     * Returns the type of this ListenerModel
     * (i.e. the name of the class implementing the listener)
     */
    String getType();

    /**
     * Returns the Kind of this ListenerModel
     */
    ListenerModel.Kind getKind();

    QualifierModel getQualifierModel();

    QualifierModel newQualifierModel(String type);
}
