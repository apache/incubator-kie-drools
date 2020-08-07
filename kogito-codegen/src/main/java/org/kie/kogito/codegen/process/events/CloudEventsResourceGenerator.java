package org.kie.kogito.codegen.process.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;

import static com.github.javaparser.StaticJavaParser.parse;

public class CloudEventsResourceGenerator {

    public static final String EMITTER_PREFIX = "emitter_";
    static final String EMITTER_TYPE = "Emitter<String>";
    private static final String RESOURCE_TEMPLATE = "/class-templates/events/CloudEventsListenerResource.java";
    private static final String CLASS_NAME = "CloudEventListenerResource";

    // even if we only support Quarkus for now, this will come in handy when we add SpringBoot support.
    private final DependencyInjectionAnnotator annotator;
    private final List<TriggerMetaData> triggers;

    public CloudEventsResourceGenerator(final List<ProcessExecutableModelGenerator> generators, final DependencyInjectionAnnotator annotator) {
        this.triggers = this.filterTriggers(generators);
        this.annotator = annotator;
    }

    // constructor shortcode used on unit tests
    CloudEventsResourceGenerator(final List<ProcessExecutableModelGenerator> generators) {
        this.triggers = this.filterTriggers(generators);
        this.annotator = new CDIDependencyInjectionAnnotator();
    }

    protected String getResourceTemplate() {
        return RESOURCE_TEMPLATE;
    }

    /**
     * Triggers used to generate the channels
     *
     * @return
     */
    List<TriggerMetaData> getTriggers() {
        return triggers;
    }

    /**
     * Gets the full class name in path format like <code>org/my/ns/Class.java</code>
     *
     * @return
     */
    public String generatedFilePath() {
        return String.format("%s/%s.java", ApplicationGenerator.DEFAULT_PACKAGE_NAME.replace(".", "/"), CLASS_NAME);
    }

    /**
     * Generates the source code for a CloudEventListenerResource
     *
     * @return
     */
    public String generate() {
        final CompilationUnit clazz = this.parseTemplate();
        final ClassOrInterfaceDeclaration template = clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));
        template.setName(CLASS_NAME);
        this.addChannels(template);
        this.addInjection(template);

        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

    /**
     * Filter TriggerMetadata to keep only  {@link org.jbpm.compiler.canonical.TriggerMetaData.TriggerType#ConsumeMessage}
     *
     * @param generators Process generators
     * @return filtered list
     */
    private List<TriggerMetaData> filterTriggers(final List<ProcessExecutableModelGenerator> generators) {
        if (generators != null) {
            final List<TriggerMetaData> filteredTriggers = new ArrayList<>();
            generators
                    .stream()
                    .filter(m -> m.generate().getTriggers() != null)
                    .forEach(m -> filteredTriggers.addAll(m.generate().getTriggers().stream()
                                                                  .filter(t -> TriggerMetaData.TriggerType.ConsumeMessage.equals(t.getType()))
                                                                  .collect(Collectors.toList())));
            return filteredTriggers;
        }
        return Collections.emptyList();
    }

    private CompilationUnit parseTemplate() {
        return parse(this.getClass().getResourceAsStream(getResourceTemplate())).setPackageDeclaration(ApplicationGenerator.DEFAULT_PACKAGE_NAME);
    }

    private void addChannels(final ClassOrInterfaceDeclaration template) {
        // adding Emitters to hashmap
        final MethodDeclaration setup = template.findFirst(MethodDeclaration.class, m -> m.getAnnotationByName("PostConstruct").isPresent())
                .orElseThrow(() -> new IllegalArgumentException("No setup method found!"));
        final BlockStmt setupBody = setup.getBody().orElseThrow(() -> new IllegalArgumentException("No body found in setup method!"));
        // first we take the comment block and then filter the content to use only the lines we are interested
        final List<String> linesSetup = Stream.of(setup.getAllContainedComments().stream()
                                                          .filter(c -> c.isBlockComment() && c.getContent().contains("$repeat$"))
                                                          .findFirst().orElseThrow(() -> new IllegalArgumentException("Emitters setup repeat block not found!"))
                                                          .getContent().split("\n"))
                .filter(l -> !l.trim().isEmpty() && !l.contains("repeat"))
                .map(l -> l.replace("*", ""))
                .collect(Collectors.toList());
        // clean up the comments
        setup.getAllContainedComments().forEach(Comment::remove);
        // declaring Emitters
        this.triggers.forEach(t -> {
            final String emitterField = String.join("", EMITTER_PREFIX, t.getName());
            // fields to be injected
            annotator.withOutgoingMessage(template.addField(EMITTER_TYPE, new StringLiteralExpr(emitterField).asString()), t.getName());
            // hashmap setup
            linesSetup.forEach(l -> setupBody.addStatement(l.replace("$channel$", t.getName()).replace("$emitter$", emitterField)));
        });
    }

    private void addInjection(final ClassOrInterfaceDeclaration template) {
        annotator.withApplicationComponent(template);
        template.findAll(FieldDeclaration.class, fd -> fd.getVariables().get(0).getNameAsString().contains(EMITTER_PREFIX))
                .forEach(annotator::withInjection);
    }
}
