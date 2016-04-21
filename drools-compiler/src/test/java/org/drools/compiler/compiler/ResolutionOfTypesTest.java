/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.drools.compiler.compiler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.commons.jci.compilers.TypeReferenceNameCollector;
import org.drools.compiler.integrationtests.eventgenerator.Event;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.drools.core.ClockType;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.builder.conf.CompilerTypeResolutionOption;
import org.kie.internal.io.ResourceFactory;
import org.reflections.Reflections;
import org.reflections.scanners.MemberUsageScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test attempts to test some of the logic for DROOLS-1109.
 * </p>
 * However, the majority of the tests for this logic will be found in the drools-wb and kie-wb-common modules, where
 * the Indexer implemenations are found which use the logic created in DROOLS-1109.
 *
 * See https://issues.jboss.org/browse/DROOLS-1109
 */
public class ResolutionOfTypesTest extends CommonTestMethodBase {

    protected static final transient Logger logger = LoggerFactory.getLogger(ResolutionOfTypesTest.class);

    private static final Reflections reflections = new Reflections("org.drools", new MemberUsageScanner());

    // HELPER, SETUP METHODS/LOGIC -------------------------------------------------------------------------------------------------------------

    /**
     * Setup for {@link ResolutionOfTypesTest#noNullResources()}
     */
    private static final Set<String> membersToSkip = Collections.newSetFromMap(new IdentityHashMap<String, Boolean>());
    static {
        List<Member>  members = new ArrayList<Member>();
        members.addAll(Arrays.asList(ProcessDescr.class.getConstructors())); // done in jBPM
        members.addAll(Arrays.asList(ReturnValueDescr.class.getConstructors())); // taken care of in jbpm
        members.addAll(Arrays.asList(ActionDescr.class.getConstructors())); // taken care of in jbpm

        for( Member member : members ) {
           membersToSkip.add(member.getName());
        }
    }

    private static final Comparator<Member> memberComp = new Comparator<Member>() {

        @Override
        public int compare( Member o1, Member o2 ) {
            if( o1 == o2 ) {
                return 0;
            }
            if( o1 instanceof Constructor ) {
               if( o2 instanceof Constructor) {
                   return o1.getName().compareTo(o2.getName());
               }
               return -1;
            }
            if( o2 instanceof Constructor) {
                return 1;
            } else {
                return o1.getName().compareTo(o2.getName());
            }
        }
    };

    /**
     * TO DO:
     * - type resolution references
     * - resource stuff? Mvel/Java dialects?
     *
     * CHECKLIST:
     * - mvel/java dialects
     * - traits, extending
     *
     *
     *  GO THROUGH ALL other cases TO MAKE CHECKLIST!
     *
     */
    private Map<String, Set<String>> compileResourceAndGetTypeReferences(String... pathDrlStringPairs) {
        KieServices ks = KieServices.Factory.get();

        // add resources
        KieFileSystem kfs = ks.newKieFileSystem();

        assertTrue( "Expected pairs of drl 'path' with drl content strings", pathDrlStringPairs.length %2 == 0);
        for( int i = 0; i < pathDrlStringPairs.length; ) {
            String drl = pathDrlStringPairs[i+1];
            String drlPath = pathDrlStringPairs[i];
            kfs.write( ResourceFactory.newByteArrayResource(drl.getBytes())
                    .setTargetPath(drlPath) );
            i += 2;
        }

        KieBuilder kBuilder = ks.newKieBuilder(kfs);

        // Configure KieBase
        KieModuleModel kModuleModel = ks.newKieModuleModel();
        KieBaseModel kBaseModel = kModuleModel
                .newKieBaseModel()
                .setDefault(true)
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        // set the type resolution option!
        ((KieBaseModelImpl) kBaseModel).getKModule().getConfigurationProperties()
            .put(CompilerTypeResolutionOption.PROPERTY_NAME, Boolean.TRUE.toString());

        // Configure KieSession
        kBaseModel.addPackage("*")
                  .newKieSessionModel("defaulKieSession")
                  .setDefault(true)
                  .setType(KieSessionModel.KieSessionType.STATEFUL)
                  .setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ));

        // write kModule.xml and pom.xml  so that it will be picked up during the build
        kfs.writeKModuleXML(kModuleModel.toXML());
        // note: "kBuilder.getKieModule()" BUILDS the KieModule!!! (which is why we need a hack to the get releaseId)
        ReleaseId releaseId = ((KieBuilderImpl) kBuilder).getPomModel().getReleaseId();
        kfs.generateAndWritePomXML(releaseId);

        // build!
        List<Message> buildMsgs = kBuilder.buildAll().getResults().getMessages();
        if( ! buildMsgs.isEmpty() ) {
           for( Message msg : buildMsgs ) {
              System.out.println( "! " + msg.toString() );
           }
        }
        assertTrue( "Errors found when compiling rule!", buildMsgs.isEmpty() );

        KieContainer kContainer = ks.newKieContainer(releaseId);
        return ((KieContainerImpl) kContainer).getKieProject().getTypeReferences();
    }

    private void cleanJavaTypeReferences( Set<String> testDrlTypeReferences ) {
        for( String ref : testDrlTypeReferences ) {
           if( ref.startsWith("java.lang.") )  {
               testDrlTypeReferences.remove(ref);
           }
        }

    }


    // TESTS ----------------------------------------------------------------------------------------------------------------------

    /**
     * In short, this test tries to make sure that the {@link BaseDescr}.resource field is always set when a {@link BaseDescr} implementation class
     * is constructed.
     * </p>
     * The longer explanation is the following algorithm implemented in the test:<ol>
     * <li>For each {@link BaseDescr} implemenation:<ol>
     * <li>Finds the constructor uses of the {@link BaseDescr} implementation</li>
     * <li>If the constructor is used in a {@link Resource} implementation,</li>
     * <li>..then it calls the {@link Resource} implemenation constructor</li>
     * <li>..and makes sure that the related {@link BaseDescr}.resource field is set by the {@link Resource} constructor</li>
     * </ol>
     * </ol>
     * Of course, there's additional logic in the test that deals with the complexity implosed by Java inheritance.
     * </p>
     * In order to track which references came from which asset (DRL), it's important to link the {@link BaseDescr} instance
     * (which contains the reference information) to the original asset (the {@link Resource} implemenation instance).
     *
     * @throws Exception When something goes wrong.
     */
    @Test
    public void noNullResources() throws Exception {

        Set<Member> members = new TreeSet<Member>(memberComp);
        Queue<Constructor> constructors = new LinkedList<Constructor>();
        Map<Member, Member> methods = new TreeMap<Member, Member>(memberComp);

        constructors.add(BaseDescr.class.getConstructors()[0]);
        while( ! constructors.isEmpty() ) {
            Constructor fromCnstr = constructors.poll();
            members.addAll(reflections.getConstructorUsage(fromCnstr));

            for( Member descrCnstrUsageMember : members ) {
                if( descrCnstrUsageMember.getDeclaringClass().getSimpleName().endsWith("Test") ) {
                    continue;
                }
                if( membersToSkip.contains(descrCnstrUsageMember.getName()) ) {
                    continue;
                }
                if( descrCnstrUsageMember instanceof Constructor ) {
                    Constructor cnstr = (Constructor) descrCnstrUsageMember;
                    Class [] paramTypes = cnstr.getParameterTypes();
                    boolean resourceParameter = false;
                    for( Class paramClass : paramTypes ) {
                        if( Resource.class.isAssignableFrom(paramClass) ) {
                            resourceParameter = true;
                            break;
                        }
                    }
                    if( resourceParameter ) {
                        Object [] args = new Object[paramTypes.length];
                        for( int i = 0; i < paramTypes.length; ++i ) {
                           args[i] = mock(paramTypes[i]);
                        }
                        Object cnstrObj = cnstr.newInstance(args);
                        Field resField = BaseDescr.class.getDeclaredField("resource");
                        resField.setAccessible(true);
                        Object resFieldObj = resField.get(cnstrObj);
                        assertNotNull( descrCnstrUsageMember.getDeclaringClass().getSimpleName() + " constructor does not set .resource field!",
                              resFieldObj );
                    } else if( BaseDescr.class.isAssignableFrom(descrCnstrUsageMember.getDeclaringClass()) ) {
                        constructors.add((Constructor) descrCnstrUsageMember);
                    } else {
                        methods.put(descrCnstrUsageMember, fromCnstr);
                    }
                } else if( descrCnstrUsageMember instanceof Method ) {
                    methods.put((Method) descrCnstrUsageMember, fromCnstr);
                } else {
                    fail( "Unexpected usage type: " + descrCnstrUsageMember.getClass().getSimpleName() );
                }
            }
            members.clear();
        }

        /**
         * Prints all methods in which a BaseDescr implementation is created,
         * in order for me to make sure (by hand.. :/ ) that the .resource field is set when
         * the BaseDescr implemenation is created by those methods, or later on.
         */
        for( Entry<Member, Member> memberEntry : methods.entrySet() ) {
            Member key = memberEntry.getKey();
            String keyStr = key.getDeclaringClass().getSimpleName();
            if( key instanceof Method ) {
                keyStr += "." + key.getName();
            }
            Member descrCnstrClass = memberEntry.getValue();
            // FORMAT:
            // <method where the BaseDescr is created> : <BaseDescr implementation class that's created>
            logger.debug( keyStr + ": " + descrCnstrClass.getName() );
        }
    }

    @Test
    public void testTypeResolutionJavaClassesWildcardImport() {
        // queries and Events make for interesting reference resolution
        String drl =
                "package test;\n" +
                "import org.drools.compiler.integrationtests.eventgenerator.*;\n" +

                "query 'all inserted events'\n" +
                "    Event()\n" +
                "end\n" +

                "query 'all inserted events with generation time lt 1 min'\n" +
                "    Event(eval(endTime<PseudoSessionClock.timeInMinutes(1)))\n" +
                "end\n" +

                "query 'all inserted events with 2 min lt generation time lt 3 min'\n" +
                "    Event(eval (endTime>PseudoSessionClock.timeInMinutes(2)), eval(endTime<PseudoSessionClock.timeInMinutes(3)))\n" +
                "end\n" +

                "query 'all inserted events with parent resource A'\n" +
                "    Event(parentId=='resA')\n" +
                "end\n" +

                "query 'all inserted events with parent resource B'\n" +
                "    Event(parentId=='resB')\n" +
                "end\n";

        String drlResourcePath = "org/drools/compiler/integrationtests/eventgenerator/test.drl";

        Map<String, Set<String>> typeReferences = compileResourceAndGetTypeReferences(drlResourcePath, drl);

        assertNotNull( "No type references created during compilation!", typeReferences );
        Set<String> testDrlTypeReferences = typeReferences.get(drlResourcePath);
        assertTrue( "Expected type references for " + drlResourcePath,
                testDrlTypeReferences != null && ! testDrlTypeReferences.isEmpty() );
        assertTrue( testDrlTypeReferences.contains(Event.class.getName()) );

        cleanJavaTypeReferences(testDrlTypeReferences);
        assertEquals( "Only expected one reference!", 1, testDrlTypeReferences.size() );
    }

    @Test
    public void testTypeResolutionTraitsAndExtend() {
       String traitDrl =
               "package org.drools.compiler.trait.test;\n" +
               "import org.drools.core.factmodel.traits.*;\n" +
               "global java.util.List list;\n" +

               "declare trait Parent\n" +
               "    child   : Student\n" +
               "end\n" +

               "declare trait Person\n" +
               "    name    : String \n" +
               "    age     : int   @position(0) \n" +
               "end\n" +

               "declare trait Role\n" +
               "\n" +
               "end\n" +

               "declare trait Student extends Person, Role\n" +
               "    school  : String\n" +
               "end \n" +

               "declare java.lang.Object\n" +
               "    @Traitable\n" +
               "end\n" +

               "declare java.lang.String\n" +
               "    @Traitable\n" +
               "end\n";
       String traitDrlPath = "org/drools/compiler/factmodel/traits/traitDef.drl";

       String ruleDrl =
               "package org.drools.compiler.trait.test;\n" +
               "import org.drools.core.factmodel.traits.*;\n" +
               "global java.util.List list;\n" +

               "rule 'Check' \n" +
               "when\n" +
               "    $z: Student( $s : school == \"skl\", fields[ \"name\" ] == \"xx\", $a : age == 88 )\n" +
               "then\n" +
               "    list.add( \"DON\" );\n" +
               "end \n";

       String ruleDrlPath = "org/drools/compiler/factmodel/traits/traitRule.drl";
       Map<String, Set<String>> typeReferences = compileResourceAndGetTypeReferences(
                traitDrlPath, traitDrl,
                ruleDrlPath, ruleDrl );

       assertTrue( typeReferences.get(ruleDrlPath).contains("org.drools.compiler.trait.test.Student"));
    }

    @Test
    public void testWildcardImportOnLHS() {
        String drlSource = "import org.drools.compiler.*;\n" +
                "rule RuleName when\n" +
                "   Person( name == \"mark\", cheese.(price == 10, type.(length == 10) ) )\n" +
                "then\n" +
                "end\n";

        String ruleDrlPath = "org/drools/compiler/rhsImport.drl";
        Map<String, Set<String>> typeReferences = compileResourceAndGetTypeReferences(
                 ruleDrlPath, drlSource);

        assertFalse( "Expected references but found none!", typeReferences.isEmpty() );
        assertTrue( "Expected references for compiled rule", typeReferences.containsKey(ruleDrlPath) );
        Set<String> refs = typeReferences.get(ruleDrlPath);
        assertFalse( "Empty reference set found for compiled rule", refs == null || refs.isEmpty() );
        boolean refFound = false;
        String classname = Person.class.getName();
        for( String ref : refs ) {
            if( ref.equals(classname) ) {
                refFound = true;
            }
        }
        assertTrue( "Reference to " + classname + " not found", refFound);

    }

    /**
     * The problem is that as the Java language evolves (8, 9, 10.. ), there
     * could be new visitor methods that correlate to new langauge features (lambda's, for example).
     * </p>
     * Given that these could be used in the compiled Drools classes (RHS actions, in particular),
     * it's important that we continue to correctly and fully collect all meta information from
     * the compiled classes for use in indexing and change-impact.
     */
    @Test
    public void areAllASTVisitorMethodsBeingUsedTest() {

        Method [] astVisitorMethodsArr = ASTVisitor.class.getMethods();
        // only declared methods, not inherited methods, (because then this test would be pointless..)
        Method [] typeRefVisitorMethodArr = TypeReferenceNameCollector.class.getDeclaredMethods();

        Set<String> typeRefVisitorMethods = new HashSet<>();
        for( Method typeRefMethod : typeRefVisitorMethodArr ) {
            if( ! typeRefMethod.getName().contains("isit") ) {
                // we're only interested in visit and endVisit methods
                continue;
            }
            String signature = getSignature(typeRefMethod);
            typeRefVisitorMethods.add(signature);
            assertFalse( "Should not be overridden, but is: " + signature, methodDoesNotNeedToBeOverridden(typeRefMethod) );
        }

        Set<Method> astVisitorMethods = new TreeSet<Method>(new Comparator<Method>() {

            @Override
            public int compare(Method o1, Method o2) {
                if( o1.getName().equals(o2.getName()) ) {
                    Class [] oneParamTypes = o1.getParameterTypes();
                    Class [] twoParamTypes = o2.getParameterTypes();
                    if( oneParamTypes.length == twoParamTypes.length ) {
                       for( int i = 0; i < oneParamTypes.length; ++i ) {
                          if( oneParamTypes[i].equals(twoParamTypes[i]) )  {
                             continue;
                          } else {
                              return oneParamTypes[i].getName().compareTo(twoParamTypes[i].getName());
                          }
                       }
                       return 0;
                    } else {
                        return oneParamTypes.length < twoParamTypes.length ? 1 : -1;
                    }
                } else {
                    return o1.getName().compareTo(o2.getName());
                }
            }
        });
        astVisitorMethods.addAll(Arrays.asList(astVisitorMethodsArr));
        for( Method astVisitorMethod : astVisitorMethods ) {
            if( ! astVisitorMethod.getName().contains("isit") ) {
                continue;
            }
            if( methodDoesNotNeedToBeOverridden(astVisitorMethod) ) {
               continue;
            }
            String astMethodSignature = getSignature(astVisitorMethod);

            assertTrue( astMethodSignature + " missing from " + TypeReferenceNameCollector.class.getSimpleName(),
                    typeRefVisitorMethods.contains(astMethodSignature)) ;
        }

    }

    private static String getSignature(Method method ) {
        StringBuffer signature = new StringBuffer(method.getName()).append("(");
        Class [] paramTypes = method.getParameterTypes();
        for( int i = 0; i < paramTypes.length -1; ++i ) {
           signature.append(paramTypes[i].getSimpleName()).append(", ");
        }
        signature.append(paramTypes[paramTypes.length-1].getSimpleName()).append(")");
        return signature.toString();
    }

    private static boolean methodDoesNotNeedToBeOverridden(Method method) {
       Class [] paramTypes = method.getParameterTypes();
       String methodName = method.getName();

       String firstParamType = paramTypes[0].getSimpleName();

       if( firstParamType.endsWith("Literal") ) {
           // These literals contain no class information
           switch( firstParamType) {
               case "CharLiteral":
               case "DoubleLiteral":
               case "ExtendedStringLiteral":
               case "FalseLiteral":
               case "FloatLiteral":
               case "IntLiteral":
               case "LongLiteral":
               case "NullLiteral":
               case "StringLiteral":
               case "TrueLiteral":
                   return true;
           }
       }

       if( methodName.equals("visit") ) {
           System.out.println("]  " + firstParamType);
           if( firstParamType.contains("Reference") ) {
               switch(firstParamType) {
                   case "ArrayQualifiedTypeReference":
                      return true;
               }
               // we always endVisit *References and traverse is just visit; endVisit;
               return true;
           }
       } else if( methodName.equals("endVisit") ) {
           System.out.println("]] " + firstParamType);
           if( firstParamType.startsWith("Javadoc") ) {
               // we never visit JavaDoc elements because compiled Rules don't have them
               // (and JavaDoc references also do not affect the code.. )
               return true;
           } else if( firstParamType.equals(BinaryExpression.class.getSimpleName()) ) {
               // No endVisit needed, BinaryExpression is correctly traversed
               return true;
           } else if( firstParamType.equals(ConstructorDeclaration.class.getSimpleName()) ) {
               // No endVisit needed, ConstructorDeclaration is correctly traversed
               return true;
           } else if( firstParamType.equals(ExplicitConstructorCall.class.getSimpleName()) ) {
               // No endVisit needed, ExplicitConstructorCall is correctly traversed
               return true;
           } else if( firstParamType.equals(StringLiteralConcatenation.class.getSimpleName()) ) {
               // No endVisit needed, StringLiteralConcatentation is correctly traversed
               return true;
           }
       }

       return false;
    }

    @Test
    public void testVarious() {
        String traitDrl =
                "package org.drools.compiler.trait.test;\n" +
                        "import org.drools.core.factmodel.traits.*;\n" +
                        "global java.util.List list;\n" +

                    "declare trait Parent\n" +
                    "    child   : Student\n" +
                    "end\n" +

                    "declare trait Person\n" +
                    "    name    : String \n" +
                    "    age     : int   @position(0) \n" +
                    "end\n" +

                    "declare trait Role\n" +
                    "\n" +
                    "end\n" +

                    "declare trait Student extends Person, Role\n" +
                    "    school  : String\n" +
                    "end \n" +

                    "declare java.lang.Object\n" +
                    "    @Traitable\n" +
                    "end\n" +

                    "declare java.lang.String\n" +
                    "    @Traitable\n" +
                    "end\n";
        String traitDrlPath = "org/drools/compiler/factmodel/traits/traitDef.drl";

        String ruleDrl =
                "package org.drools.compiler.trait.test;\n" +
                        "import org.drools.core.factmodel.traits.*;\n" +
                        "global java.util.List list;\n" +

                    "rule 'Check' \n" +
                    "when\n" +
                    "    $z: Student( $s : school == \"skl\", fields[ \"name\" ] == \"xx\", $a : age == 88 )\n" +
                    "then\n" +
                    "    list.add( \"DON\" );\n" +
                    "end \n";

        String ruleDrlPath = "org/drools/compiler/factmodel/traits/traitRule.drl";
        Map<String, Set<String>> typeReferences = compileResourceAndGetTypeReferences(
                traitDrlPath, traitDrl,
                ruleDrlPath, ruleDrl );

        assertTrue( typeReferences.get(ruleDrlPath).contains("org.drools.compiler.trait.test.Student"));
    }
}
