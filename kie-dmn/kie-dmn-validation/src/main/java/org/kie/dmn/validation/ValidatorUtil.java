package org.kie.dmn.validation;

import java.util.List;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.model.api.Association;
import org.kie.dmn.model.api.AuthorityRequirement;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.InformationRequirement;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.KnowledgeRequirement;
import org.kie.dmn.model.api.NamedElement;

public final class ValidatorUtil {

    public static String rightOfHash(final String input) {
        return input.substring(input.indexOf("#") + 1);
    }

    public static String leftOfHash(final String input) {
        return input.substring(0, input.indexOf("#"));
    }

    public static ItemDefinition getRootItemDef(final ItemDefinition id) {
        ItemDefinition root = id;
        while (!(root.getParent() instanceof Definitions)) {
            root = (ItemDefinition) root.getParent();
        }
        return root;
    }

    public static String formatMessages(final List<DMNMessage> messages) {
        return messages.stream().map(Object::toString).collect( Collectors.joining( System.lineSeparator() ) );
    }

    public static boolean doesDefinitionsContainIdForDMNEdge(Definitions definitions, String id) {
        boolean result = definitions.getArtifact().stream().anyMatch(a -> (a.getId().equals(id) && a instanceof Association));
        if (result) {
            return true;
        }
        return definitions.getDrgElement()
                          .stream()
                          .flatMap(e -> e.getChildren().stream())
                          .filter(e -> (e instanceof InformationRequirement || e instanceof KnowledgeRequirement || e instanceof AuthorityRequirement))
                          .map(DMNElement.class::cast)
                          .anyMatch(e -> (e.getId().equals(id)));
    }
    
    public static String nameOrIDOfTable(DecisionTable sourceDT) {
        if (sourceDT.getOutputLabel() != null && !sourceDT.getOutputLabel().isEmpty()) {
            return sourceDT.getOutputLabel();
        } else if (sourceDT.getParent() instanceof NamedElement) { // DT is decision logic of Decision, and similar cases.
            return ((NamedElement) sourceDT.getParent()).getName();
        } else if (sourceDT.getParent() instanceof FunctionDefinition && sourceDT.getParent().getParent() instanceof NamedElement) { // DT is decision logic of BKM.
            return ((NamedElement) sourceDT.getParent().getParent()).getName();
        } else {
            return new StringBuilder("[ID: ").append(sourceDT.getId()).append("]").toString();
        }
    }

    private ValidatorUtil() {
        // It is forbidden to create new instances of util classes.
    }
}
