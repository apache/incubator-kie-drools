/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.base.evaluators;

import org.drools.core.base.BaseEvaluator;
import org.drools.core.base.ValueType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.factmodel.traits.CoreWrapper;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.factmodel.traits.TraitType;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.rule.VariableRestriction;
import org.drools.core.rule.VariableRestriction.VariableContextEntry;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.CodedHierarchy;
import org.drools.core.util.HierarchyEncoderImpl;
import org.kie.api.runtime.ObjectFilter;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;

/**
 * <p>The implementation of the 'str' evaluator definition.</p>
 *
 * <p>The <b><code>str</code></b> compares two string values.</p> 
 *
 * <p>Lets look at some examples:</p>
 *
 * <pre>$m : Message( routingValue str[startsWith] "R1" )</pre>
 * <pre>$m : Message( routingValue str[endsWith] "R2" )</pre>
 * <pre>$m : Message( routingValue str[length] 17 )</pre>
 */
public class IsAEvaluatorDefinition implements EvaluatorDefinition {

    protected static final String   isAOp = "isA";

    public static Operator          ISA;
    public static Operator          NOT_ISA;

    private static String[]         SUPPORTED_IDS;

    { init(); }

    static void init() {
        if ( Operator.determineOperator( isAOp, false ) == null ) {
            ISA = Operator.addOperatorToRegistry( isAOp, false );
            NOT_ISA = Operator.addOperatorToRegistry( isAOp, true );
            SUPPORTED_IDS = new String[] { isAOp };
        }
    }

    private Evaluator[] evaluator;

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, Operator operator) {
        return this.getEvaluator(type, operator.getOperatorString(), operator
                .isNegated(), null);
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, Operator operator,
            String parameterText) {
        return this.getEvaluator(type, operator.getOperatorString(), operator
                .isNegated(), parameterText);
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, String operatorId,
            boolean isNegated, String parameterText) {
        return getEvaluator(type, operatorId, isNegated, parameterText,
                Target.FACT, Target.FACT);
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, String operatorId,
            boolean isNegated, String parameterText, Target leftTarget,
            Target rightTarget) {
        IsAEvaluator evaluator = new IsAEvaluator(type, isNegated);
        evaluator.setParameterText(parameterText);
        return evaluator;
    }

    /**
     * @inheridDoc
     */
    public String[] getEvaluatorIds() {
        return SUPPORTED_IDS;
    }

    /**
     * @inheridDoc
     */
    public Target getTarget() {
        return Target.FACT;
    }

    /**
     * @inheridDoc
     */
    public boolean isNegatable() {
        return true;
    }

    /**
     * @inheridDoc
     */
    public boolean supportsType(ValueType type) {
        return (type.equals( ValueType.TRAIT_TYPE ) );
    }

    /**
     * @inheridDoc
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        evaluator = (Evaluator[]) in.readObject();
    }

    /**
     * @inheridDoc
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(evaluator);
    }

    public static class IsAEvaluator extends BaseEvaluator {

        private BitSet cachedLiteral;
        private Object cachedValue;

        {
            IsAEvaluatorDefinition.init();
        }

        public IsAEvaluator() { }

        public void setParameterText(String parameterText) {

        }

        public IsAEvaluator(final ValueType type, final boolean isNegated) {
            super( type, isNegated ? NOT_ISA : ISA );
        }

        /**
         * @inheridDoc
         */
        public boolean evaluate( InternalWorkingMemory workingMemory,
                                 InternalReadAccessor extractor, InternalFactHandle handle, FieldValue value ) {
            final Object objectValue = extractor.getValue( workingMemory, handle.getObject() );
            final Object literal = value.getValue();
            if ( cachedValue != literal) {
                cachedValue = literal;
                cacheLiteral( literal, workingMemory );
            }

            TraitableBean core;
            if ( objectValue == null ) {
                return this.getOperator().isNegated();
            } if ( objectValue instanceof Thing ) {
                Thing thing = (Thing) objectValue;
                core = (TraitableBean) thing.getCore();
                BitSet code = core.getCurrentTypeCode();
                if ( code != null ) {
                    return this.getOperator().isNegated() ^ isA( code, cachedLiteral );
                } else {
                    return this.getOperator().isNegated() ^ hasTrait( core, literal );
                }
            } else if ( objectValue instanceof TraitableBean ) {
                core = (TraitableBean) objectValue;
                BitSet code = core.getCurrentTypeCode();
                if ( code != null ) {
                    return this.getOperator().isNegated() ^ isA( code, cachedLiteral );
                } else {
                    return this.getOperator().isNegated() ^ hasTrait( core, literal );
                }
            } else {
                core = lookForWrapper( objectValue, workingMemory );
                if ( core == null ) {
                    return this.getOperator().isNegated();
                }
                BitSet code = core.getCurrentTypeCode();
                if ( code != null ) {
                    return this.getOperator().isNegated() ^ isA( code, cachedLiteral );
                } else {
                    return this.getOperator().isNegated() ^ hasTrait( core, literal );
                }
            }
        }

        private boolean hasTrait( TraitableBean core, Object value ) {
            if ( value instanceof Class ) {
                return core.hasTrait( ( (Class) value ).getName() );
            } else if ( value instanceof String ) {
                return core.hasTrait( (String) value );
            } else if ( value instanceof Collection ) {
                for ( Object o : (Collection) value ) {
                    if ( ! hasTrait( core, o ) ) {
                        return false;
                    }
                }
                return true;
            }
            throw new UnsupportedOperationException( " IsA Operator : Unsupported literal " + value );
        }

        private void cacheLiteral( Object value, InternalWorkingMemory workingMemory ) {
            CodedHierarchy x = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy();
            cachedLiteral = getCode( value, x );
        }

        private BitSet getCode( Object value, CodedHierarchy x ) {
            if ( value instanceof Class ) {
                String typeName = ((Class) value).getName();
                return x.getCode( typeName );
            } else if ( value instanceof String ) {
                return x.getCode( value );
            } else if ( value instanceof Collection ) {
                BitSet code = null;
                for ( Object o : ( (Collection) value ) ) {
                    if ( code == null ) {
                        code = (BitSet) getCode( o, x ).clone();
                    } else {
                        code.and( getCode( o, x ) );
                    }
                }
                return code;
            }
            throw new UnsupportedOperationException( " IsA Operator : Unsupported literal " + value );
        }


        protected TraitableBean lookForWrapper( final Object objectValue, InternalWorkingMemory workingMemory) {
            Iterator iter = workingMemory.getObjectStore().iterateObjects( new ObjectFilter() {
                public boolean accept(Object object) {
                    if ( object instanceof TraitProxy ) {
                        Object core = ((TraitProxy) object).getObject();
                        if ( core instanceof CoreWrapper ) {
                            core = ((CoreWrapper) core).getCore();
                        }
                        return core == objectValue;
                    } else {
                        return false;
                    }
                }
            });
            if ( iter.hasNext() ) {
                return (TraitableBean) ((TraitProxy) iter.next()).getObject();
            } else {
                return null;
// throw new RuntimeException(" Error : the isA operator must be used on a trait-type, was applied to " + objectValue );
            }
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                InternalReadAccessor leftExtractor, InternalFactHandle left,
                                InternalReadAccessor rightExtractor, InternalFactHandle right) {
            final Object value1 = leftExtractor.getValue( workingMemory, left != null ? left.getObject() : left );
            final Object value2 = rightExtractor.getValue( workingMemory, right != null ? right.getObject() : right );

            Object target = value1;
            Object source = value2;

            return compare( source, target, workingMemory );
        }


        public boolean evaluateCachedLeft( InternalWorkingMemory workingMemory,
                                           VariableContextEntry context, InternalFactHandle right ) {

            Object target = ((VariableRestriction.ObjectVariableContextEntry) context).left;
            Object source = context.getFieldExtractor().getValue( workingMemory, right.getObject() );

            return compare( source, target, workingMemory );
        }

        public boolean evaluateCachedRight( InternalWorkingMemory workingMemory,
                                            VariableContextEntry context, InternalFactHandle left ) {

            Object target = context.getFieldExtractor().getValue( workingMemory, left.getObject() );
            Object source = ((VariableRestriction.ObjectVariableContextEntry) context).right;

            return compare( source, target, workingMemory );
        }



        private boolean compare( Object source, Object target, InternalWorkingMemory workingMemory ) {
            BitSet sourceTraits = null;
            BitSet targetTraits = null;
            if ( source instanceof Thing ) {
                sourceTraits = ((TraitableBean) ((Thing) source).getCore()).getCurrentTypeCode();
                if ( sourceTraits == null && source instanceof TraitType ) {
                    CodedHierarchy x = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy();
                    sourceTraits = x.getCode( ((TraitType)source)._getTraitName() );
                }
            } else if ( source instanceof TraitableBean ) {
                sourceTraits = ((TraitableBean) source).getCurrentTypeCode();
            } else if ( source instanceof String ) {
                CodedHierarchy x = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy();
                sourceTraits = x.getCode( source );
            } else {
                TraitableBean tbean = lookForWrapper( source, workingMemory);
                if ( tbean != null ) {
                    sourceTraits = tbean.getCurrentTypeCode();
                }
            }

            if ( target instanceof Class ) {
                CodedHierarchy x = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy();
                targetTraits = x.getCode( ((Class) target).getName() );
            } else if ( target instanceof String ) {
                CodedHierarchy x = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy();
                targetTraits = x.getCode( target );
            } else if ( target instanceof Thing ) {
                targetTraits = ((TraitableBean) ((Thing) target).getCore()).getCurrentTypeCode();
                if ( targetTraits == null && target instanceof TraitType ) {
                    CodedHierarchy x = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy();
                    targetTraits = x.getCode( ((TraitType)target)._getTraitName() );
                }
            } else if ( target instanceof TraitableBean ) {
                targetTraits = ((TraitableBean) target).getCurrentTypeCode();
            } else {
                TraitableBean tbean = lookForWrapper( target, workingMemory );
                if ( tbean != null ) {
                    targetTraits = tbean.getCurrentTypeCode();
                }
            }


            if (sourceTraits == null || targetTraits == null) {
                return getOperator().isNegated();
            }

            return isA(sourceTraits, targetTraits) ^ getOperator().isNegated();
        }

        private boolean isA( BitSet sourceTraits, BitSet targetTraits ) {
            if ( sourceTraits == null ) {
                return false;
            }
            if ( targetTraits == null ) {
                return true;
            }
            return HierarchyEncoderImpl.supersetOrEqualset( sourceTraits, targetTraits );
        }

        @Override
        public String toString() {
            return "IsAEvaluatorDefinition isA";

        }

    }
}
