package org.kie.builder;

import static java.util.EnumSet.allOf;

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

    String getType();

    ListenerModel.Kind getKind();

    QualifierModel getQualifierModel();

    QualifierModel newQualifierModel(String type);
}
