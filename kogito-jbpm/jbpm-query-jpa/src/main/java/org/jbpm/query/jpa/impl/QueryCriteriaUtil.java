/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.query.jpa.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.query.jpa.data.QueryWhere.QueryCriteriaType;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.internal.query.QueryParameterIdentifiers;

public abstract class QueryCriteriaUtil {

    private Map<Class, Map<String, Attribute>> criteriaAttributes;
    private final AtomicBoolean criteriaAttributesInitialized = new AtomicBoolean(false);

    public QueryCriteriaUtil(Map<Class, Map<String, Attribute>> criteriaAttributes) {
        initialize(criteriaAttributes);
    }

    protected QueryCriteriaUtil() {
        // for the AbstractTaskQueryCriteriaUtil
    }

    protected void initialize(Map<Class, Map<String, Attribute>> criteriaAttributes) {
        this.criteriaAttributes = criteriaAttributes;
    }

    protected Map<Class, Map<String, Attribute>> getCriteriaAttributes() {
        if( ! criteriaAttributesInitialized.get() ) {
           if( initializeCriteriaAttributes() ) {
               criteriaAttributesInitialized.set(true);
           }  else {
               throw new IllegalStateException("Queries can not be performed if no persistence unit has been initalized!");
           }
        }
        return criteriaAttributes;
    }

    // List cast conversion methods -----------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static <C,I> List<I> convertListToInterfaceList( List<C>internalResult, Class<I> interfaceType ) {
        List<I> result = new ArrayList<I>(internalResult.size());
        for( C element : internalResult ) {
           result.add((I) element);
        }
        return result;
    }

    // constructor helper methods -------------------------------------------------------------------------------------------------

    public static void addCriteria( Map<Class, Map<String, Attribute>> criteriaAttributes, String listId, Attribute attr ) {
        Class table = attr.getJavaMember().getDeclaringClass();
        addCriteria(criteriaAttributes, listId, table, attr);
    }

    public static void addCriteria( Map<Class, Map<String, Attribute>> criteriaAttributes, String listId, Class table, Attribute attr ) {
        Map<String, Attribute> tableAttrs = criteriaAttributes.get(table);
        if( tableAttrs == null ) {
            tableAttrs = new ConcurrentHashMap<String, Attribute>(1);
            criteriaAttributes.put(table, tableAttrs);
        }
        Attribute previousMapping = tableAttrs.put(listId, attr);
        assert previousMapping == null : "Previous mapping existed for [" + listId + "]!";
    }

    // abstract methods -----------------------------------------------------------------------------------------------------------

    /**
     * The implementation of this method should be synchronized!
     */
    protected abstract boolean initializeCriteriaAttributes();

    protected abstract CriteriaBuilder getCriteriaBuilder();

    // @formatter:on

    // query logic ----------------------------------------------------------------------------------------------------------------

    /**
     * This method takes the high-level steps needed in order to create a JPA {@link CriteriaQuery}.
     * <ol>
     * <li>A {@link CriteriaBuilder} and {@link CriteriaQuery} instance are created.</li>
     * <li>The tables being selected from are defined in the query.</li>
     * <li>The {@link CriteriaQuery} instance is filled using the criteria in the {@link QueryWhere} instance</li>
     * <li>A JPA {@link Query} instance is created</li>
     * <li>The meta criteria (max results, offset) are applied to the query</li>
     * <li>The results are retrieved and returned</li>
     * </ol>
     * @param queryWhere a {@link QueryWhere} instance containing the query criteria
     * @param queryType The type ({@link Class}) of the result
     * @return The result of the query, a {@link List}.
     */
    public <T> List<T> doCriteriaQuery( QueryWhere queryWhere, Class<T> queryType ) {
        // 1. create builder and query instances
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(queryType);

        // query base;
        criteriaQuery.select(criteriaQuery.from(queryType));

        fillCriteriaQuery(criteriaQuery, queryWhere, builder, queryType);

        List<T> result = createQueryAndCallApplyMetaCriteriaAndGetResult(queryWhere, criteriaQuery, builder);

        return result;
    }

    // query logic ----------------------------------------------------------------------------------------------------------------

    /**
     * This is the main ("highest"? "most abstract"?) method that is used to create a {@link CriteriaQuery} from a {@link QueryWhere} instance.
     *
     * @param query The (empty) {@link CriteriaQuery} that will be filled using the {@link QueryCriteria} and other information in the {@link QueryWhere} instance
     * @param queryWhere The {@link QueryWhere} instance, with abstract information that should be added to the {@link CriteriaQuery}
     * @param builder The {@link CriteriaBuilder}, helpful when creating {@link Predicate}s to add to the {@link CriteriaQuery}
     * @param queryType The {@link Class} indicating the main {@link Root} of the {@link CriteriaQuery}
     */
    protected <R,T> void fillCriteriaQuery( CriteriaQuery<R> query, QueryWhere queryWhere, CriteriaBuilder builder, Class<T> queryType ) {

        Predicate queryPredicate = createPredicateFromCriteriaList(query, builder, queryType, queryWhere.getCriteria(), queryWhere );

        if( queryPredicate != null ) {
            query.where(queryPredicate);
        }

        if( queryWhere.getAscOrDesc() != null ) {
            String orderByListId = queryWhere.getOrderByListId();
            assert orderByListId != null : "Ascending boolean is set but no order by list Id has been specified!";
            Expression orderByPath = getOrderByExpression(query, queryType, orderByListId);
            Order order;
            if( queryWhere.getAscOrDesc() ) {
               order = builder.asc(orderByPath);
            } else {
               order = builder.desc(orderByPath);
            }
            query.orderBy(order);
        }
    }

    /**
     * This method is contains the setup steps for creating and assembling {@link Predicate} instances
     * from the information in a {@link List} of {@link QueryCriteria} instances.
     * </p>
     * The steps taken when assembling a {@link Predicate} are the following:
     * <ol>
     * <li>Separate the given {@link List} of {@link QueryCriteria} into an intersection and disjunction (union) list.</li>
     * <li>Combine separate "range" {@link QueryCriteria} that apply to the same listId</li>
     * <li>Call the {@link #createPredicateFromCriteriaList(CriteriaQuery, List, CriteriaBuilder, Class, boolean)}
     * method on disjunction criteria list and on the intersection criteria list</li>
     * <li>Take the result of the previous step and appropriately combine the returned {@link Predicate} instances into a
     * final {@link Predicate} instance that is then returned.</li>
     * </ol>
     * @param query The {@link CriteriaQuery} instance that we're assembling {@link Predicate} instances for
     * @param inputCriteriaList The list of {@link QueryCriteria} instances that will be processed
     * @param builder A {@link CriteriaBuilder} instance to help us build {@link Predicate} instances
     * @param resultType The {@link Class} (type) of the result, given so that later methods can use it
     * @return A {@link Predicate} instance based on the given {@link QueryCriteria} list
     */
    private <R,T> Predicate createPredicateFromCriteriaList(
            CriteriaQuery<R> query, CriteriaBuilder builder,
            Class<T> resultType,
            List<QueryCriteria> inputCriteriaList,  QueryWhere queryWhere ) {
        Predicate queryPredicate = null;
        if( inputCriteriaList.size() > 1 ) {

            List<Predicate> predicateList = new LinkedList<Predicate>();
            QueryCriteria previousCriteria = null;
            QueryCriteria firstCriteria = null;
            List<QueryCriteria> currentIntersectingCriteriaList = new LinkedList<QueryCriteria>();

            int i = 0;
            for( QueryCriteria criteria : inputCriteriaList ) {
                assert i++ != 0 || criteria.isFirst() : "First criteria is not flagged as first!";

                if( criteria.isFirst() ) {
                   firstCriteria = previousCriteria = criteria;
                   continue;
                } else if( firstCriteria != null ) {
                    if( criteria.isUnion() ) {
                       Predicate predicate = createPredicateFromSingleOrGroupCriteria(query, builder, resultType, previousCriteria, queryWhere);
                       predicateList.add(predicate);
                    } else {
                        currentIntersectingCriteriaList.add(firstCriteria);
                    }
                    firstCriteria = null;
                }

                if( criteria.isUnion() ) {
                    // AND has precedence over OR:
                    // If 'criteria' is now OR and there was a list (currentIntersectingCriteriaList) of AND criteria before 'criteria'
                    // - create a predicate from the AND criteria
                    if( previousCriteria != null && ! previousCriteria.isUnion() && ! currentIntersectingCriteriaList.isEmpty() ) {
                        Predicate predicate
                            = createPredicateFromIntersectingCriteriaList(query, builder, resultType, currentIntersectingCriteriaList, queryWhere );
                        assert predicate != null : "Null predicate when evaluating intersecting criteria [" + criteria.toString() + "]";
                        predicateList.add(predicate);

                        // - new (empty) current intersecting criteria list
                        currentIntersectingCriteriaList = new LinkedList<QueryCriteria>();
                    }

                    // Process the current union criteria
                    Predicate predicate = createPredicateFromSingleOrGroupCriteria(query, builder, resultType, criteria, queryWhere);
                    assert predicate != null : "Null predicate when evaluating union criteria [" + criteria.toString() + "]";
                    predicateList.add(predicate);
                } else {
                    currentIntersectingCriteriaList.add(criteria);
                }

                previousCriteria = criteria;
            }

            if( ! currentIntersectingCriteriaList.isEmpty() ) {
                Predicate predicate
                    = createPredicateFromIntersectingCriteriaList(query, builder, resultType, currentIntersectingCriteriaList, queryWhere );
                predicateList.add(predicate);
            }

            assert ! predicateList.isEmpty() : "The predicate list should not (can not?) be empty here!";
            if( predicateList.size() == 1 ) {
                queryPredicate = predicateList.get(0);
            } else {
                Predicate [] predicates = predicateList.toArray(new Predicate[predicateList.size()]);
                queryPredicate = builder.or(predicates);
            }
        } else if( inputCriteriaList.size() == 1 ) {
            QueryCriteria singleCriteria = inputCriteriaList.get(0);
            queryPredicate = createPredicateFromSingleOrGroupCriteria(query, builder, resultType, singleCriteria, queryWhere);
        }

        return queryPredicate;
    }

    /**
     * This method is necessary because the AND operator in SQL has precedence over the OR operator.
     * </p>
     * That means that intersecting criteria should always be grouped together (and processed first, basically), which is essentially
     * what this method does.
     *
     * @param query The {@link CriteriaQuery} that is being built
     * @param intersectingCriteriaList The list of intersecting (ANDed) {@link QueryCriteria}
     * @param builder The {@link CriteriaBuilder} builder instance
     * @param queryType The (persistent {@link Entity}) {@link Class} that we are querying on
     * @return A {@link Predicate} created on the basis of the given {@link List} of {@link QueryCriteria}
     */
    private <R,T> Predicate createPredicateFromIntersectingCriteriaList(CriteriaQuery<R> query, CriteriaBuilder builder, Class<T> queryType, List<QueryCriteria> intersectingCriteriaList, QueryWhere queryWhere  ) {
        combineIntersectingRangeCriteria(intersectingCriteriaList);
        assert intersectingCriteriaList.size() > 0 : "Empty list of currently intersecting criteria!";
        Predicate [] intersectingPredicates = new Predicate[intersectingCriteriaList.size()];
        int i = 0;
        for( QueryCriteria intersectingCriteria : intersectingCriteriaList ) {
            Predicate predicate = createPredicateFromSingleOrGroupCriteria(query, builder, queryType, intersectingCriteria, queryWhere );
            assert predicate != null : "Null predicate when evaluating individual intersecting criteria [" + intersectingCriteria.toString() + "]";
            intersectingPredicates[i++] = predicate;
        }

        Predicate predicate;
        if( intersectingPredicates.length > 1 ) {
            predicate = builder.and(intersectingPredicates);
        } else {
           predicate = intersectingPredicates[0];
        }

        return predicate;
    }

    /**
     * When there are multiple range criteria in a query (in the same group), it is more efficient to
     * submit a JPA "between" criteria than 2 different criteria.
     *
     * @param intersectionCriteria A {@link List} of {@link QueryCriteria} instances that are range criteria
     */
    @SuppressWarnings("unchecked")
    private void combineIntersectingRangeCriteria(List<QueryCriteria> intersectionCriteria) {
        Map<String, QueryCriteria> intersectingRangeCriteria = new HashMap<String, QueryCriteria>();
        Iterator<QueryCriteria> iter = intersectionCriteria.iterator();
        while( iter.hasNext() ) {
            QueryCriteria criteria = iter.next();
            if( QueryCriteriaType.RANGE.equals(criteria.getType()) ) {
                QueryCriteria previousCriteria = intersectingRangeCriteria.put(criteria.getListId(), criteria);
                if( previousCriteria != null ) {
                    Object [] prevCritValues, thisCritValues;
                    assert previousCriteria.hasValues() || previousCriteria.hasDateValues() :
                        "Previous criteria has neither values nor date values!";
                    assert !(previousCriteria.hasValues() && previousCriteria.hasDateValues()) :
                        "Previous criteria has BOTH values and date values!";
                    assert (previousCriteria.hasValues() && criteria.hasValues())
                    || (previousCriteria.hasDateValues() && criteria.hasDateValues()) :
                        "Previous and current criteria should have either both have values or both have date values!";

                    boolean dateValues = false;
                    if( previousCriteria.hasValues() ) {
                        prevCritValues = previousCriteria.getValues().toArray();
                        thisCritValues = criteria.getValues().toArray();
                    } else {
                        dateValues = true;
                        prevCritValues = previousCriteria.getDateValues().toArray();
                        thisCritValues = criteria.getDateValues().toArray();
                    }

                    List values = dateValues ? previousCriteria.getDateValues() : previousCriteria.getValues();
                    if( prevCritValues[0] == null && thisCritValues[1] == null ) {
                        values.set(0, thisCritValues[0]);
                        intersectingRangeCriteria.put(previousCriteria.getListId(), previousCriteria);
                        iter.remove();
                    } else if( prevCritValues[1] == null && thisCritValues[0] == null ) {
                        values.set(1, thisCritValues[1]);
                        intersectingRangeCriteria.put(previousCriteria.getListId(), previousCriteria);
                        iter.remove();
                    }
                }
            }
        }
    }

    /**
     * Depending on whether or not  the given {@link QueryCriteria} is a group criteria (which then contains a {@link List}<{@link QueryCriteria}>)
     * or a single {@link QueryCriteria}, the correct method to process the given {@link QueryCriteria} is called.
     *
     * @param query The {@link CriteriaQuery} that is being built
     * @param criteria The {@link QueryCriteria} instance
     * @param builder The {@link CriteriaBuilder} builder instance
     * @param queryType The (persistent {@link Entity}) {@link Class} that we are querying on
     * @return A {@link Predicate} created on the basis of the given {@link QueryCriteria} instance
     */
    private <R,T> Predicate createPredicateFromSingleOrGroupCriteria(CriteriaQuery<R> query, CriteriaBuilder builder, Class<T> queryType, QueryCriteria criteria, QueryWhere queryWhere ) {
        Predicate predicate;
        if( criteria.isGroupCriteria() ) {
            assert ! criteria.hasValues() : "Criteria has both subcriteria (group criteria) and values! [" + criteria.toString() + "]";
            predicate = createPredicateFromCriteriaList(query, builder, queryType, criteria.getCriteria(), queryWhere );
        } else {
            assert ! criteria.hasCriteria() || Integer.parseInt(criteria.getListId()) < 0 : "Criteria has both values and subcriteria (group criteria)! [" + criteria.toString() + "]";
            predicate = createPredicateFromSingleCriteria(query, builder, queryType, criteria, queryWhere);
        }
        return predicate;
    }

    /**
     * This method is the main method for creating a {@link Predicate} from a (non-group)  {@link QueryCriteria} instance.
     * </p>
     * If it can not figure out how to create a {@link Predicate} from the given {@link QueryCriteria} instance,
     * then the (abstract) {@link #implSpecificCreatePredicateFromSingleCriteria(CriteriaQuery, QueryCriteria, CriteriaBuilder, Root, Class)}
     * method is called.
     *
     * @param query The {@link CriteriaQuery} that is being built
     * @param criteria The given {@link QueryCriteria} instance
     * @param builder The {@link CriteriaBuilder} builder instance
     * @param queryType The (persistent {@link Entity}) {@link Class} that we are querying on
     * @return A {@link Predicate} created on the basis of the given {@link QueryCriteria}
     */
    private <R,T> Predicate createPredicateFromSingleCriteria(
            CriteriaQuery<R> query, CriteriaBuilder builder,
            Class<T> queryType,
            QueryCriteria criteria, QueryWhere queryWhere) {

        Predicate predicate = null;
        assert criteria.hasValues() || criteria.hasDateValues() || Integer.parseInt(criteria.getListId()) < 0
            : "No values present for criteria with list id: [" + criteria.getListId() + "]";

        String listId = criteria.getListId();
        Attribute attr = getCriteriaAttributes().get(queryType).get(listId);

        if( attr != null ) {
            Expression entityField = getEntityField(query, listId, attr);
            predicate = basicCreatePredicateFromSingleCriteria(builder, entityField, criteria);
        } else {
           predicate = implSpecificCreatePredicateFromSingleCriteria(query, builder, queryType, criteria, queryWhere );
        }

        return predicate;
    }

    /**
     * This is a helper method to retrieve a particular {@link Root} from a {@link CriteriaQuery} instance
     *
     * @param query The {@link CriteriaQuery} instance that we're building
     * @param queryType The {@link Class} matching the {@link Root} we want
     * @return The {@link Root} matching the given {@link Class} or null if it's not in the query
     */
    public static <T> Root getRoot(AbstractQuery<T> query, Class queryType) {
        Root<?> table = null;
        for( Root<?> root : query.getRoots() ) {
           if( root.getJavaType().equals(queryType) )  {
               table = root;
               break;
           }
        }
        return table;
    }

    /**
     * This method retrieves the entity "field" that can be used as the LHS of a {@link Predicate}
     * </p>
     * This method is overridden in some extended {@link QueryCriteriaUtil} implementations
     *
     * @param query The {@link CriteriaQuery} that we're building
     * @param listId The list id of the given {@link QueryCriteria}
     * @return An {@link Expression} with the {@link Path} to the field represented by the {@link QueryCriteria#getListId()} value
     */
    protected <T> Expression getEntityField(CriteriaQuery<T> query, String listId, Attribute attr) {
        return defaultGetEntityField(query, listId, attr);
    }

    @SuppressWarnings("unchecked")
    public static <T> Expression defaultGetEntityField(CriteriaQuery<T> query, String listId, Attribute attr) {
        Expression entityField = null;
        if( attr != null ) {
            Class attrType = attr.getDeclaringType().getJavaType();
            for( From from : query.getRoots() ) {
                if( from.getJavaType().equals(attrType) ) {
                    if( attr != null ) {
                        if( attr instanceof SingularAttribute ) {
                            entityField = from.get((SingularAttribute) attr);
                        } else if( attr instanceof PluralAttribute ) {
                            entityField = from.get((PluralAttribute) attr);
                        } else {
                            throw new IllegalStateException("Unexpected attribute type when processing criteria with list id " + listId + ": " + attr.getClass().getName() );
                        }
                        break;
                    }
                }
            }
        }
        // if entityField  == null, this is because this QueryCriteria is a implementation specific criteria, such as "LAST_VARIABLE_LIST"

        return entityField;
    }

    /**
     * This method creates the basic types of {@link Predicate} from trivial {@link QueryCriteria} (NORMAL/REGEXP/RANGE).
     *
     * @param builder The {@link CriteriaBuilder}, helpful when creating {@link Predicate}s to add to the {@link CriteriaQuery}
     * @param entityField The {@link Expression} representing a field/column in an entity/table.
     * @param criteria The {@link QueryCriteria} with the values to use as the RHS of a {@link Predicate}
     * @return The created {@link Predicate}
     */
    @SuppressWarnings("unchecked")
    public static Predicate basicCreatePredicateFromSingleCriteria(CriteriaBuilder builder, Expression entityField, QueryCriteria criteria) {
        Predicate predicate = null;
        List<Object> parameters = criteria.getParameters();
        int numParameters = parameters.size();
        assert ! parameters.isEmpty() : "Empty parameters for criteria [" + criteria.toString() + "]";
        switch ( criteria.getType() ) {
        case NORMAL:
            if( numParameters == 1 ) {
                Object parameter = parameters.get(0);
                assert parameter != null : "Null parameter for criteria [" + criteria.toString() + "]";
                predicate = builder.equal(entityField, parameter);
            } else {
                assert parameters.get(0) != null : "Null 1rst parameter for criteria [" + criteria.toString() + "]";
                assert parameters.get(parameters.size()-1) != null : "Null last parameter for criteria [" + criteria.toString() + "]";
                predicate = entityField.in(parameters);
            }
            break;
        case REGEXP:
            List<Predicate> predicateList = new ArrayList<Predicate>();
            for( Object param : parameters ) {
                assert param != null : "Null regular expression parameter for criteria [" + criteria.toString() + "]";
                String likeRegex = convertRegexToJPALikeExpression((String) param );
                Predicate regexPredicate = builder.like((Expression<String>) entityField, likeRegex);
                predicateList.add(regexPredicate);
            }
            if( predicateList.size() == 1 ) {
                predicate = predicateList.get(0);
            } else {
                Predicate [] predicates = predicateList.toArray(new Predicate[predicateList.size()]);
                if( criteria.isUnion() ) {
                    predicate = builder.or(predicates);
                } else {
                    predicate = builder.and(predicates);
                }
            }
            break;
        case RANGE:
            assert numParameters > 0 && numParameters < 3: "Range expressions may only contain between 1 and 2 parameters, not " + numParameters + " [" + criteria.toString() + "]";
            Object [] rangeObjArr = parameters.toArray();
            Class rangeType = rangeObjArr[0] != null ? rangeObjArr[0].getClass() : rangeObjArr[1].getClass();
            predicate = createRangePredicate( builder, entityField, rangeObjArr[0], rangeObjArr[1], rangeType);
            break;
        default:
            throw new IllegalStateException("Unknown criteria type: " + criteria.getType());
        }
        assert predicate != null : "No predicate created "
                + "when evaluating " + criteria.getType().toString().toLowerCase() + " criteria "
                + "[" + criteria.toString() + "]";

        return predicate;
    }

    /**
     * Conver the regex (parameter) string to the JPA like syntax
     * @param regexInput The parameter string
     * @return The String in JPA syntax for a regular expressions
     */
    protected static String convertRegexToJPALikeExpression(String regexInput) {
        return regexInput.replace('*', '%').replace('.', '_');
    }

    /**
     * Helper method for creating a ranged (between, open-ended) {@link Predicate}
     *
     * @param builder The {@link CriteriaBuilder}, helpful when creating {@link Predicate}s to add to the {@link CriteriaQuery}
     * @param entityField The {@link Expression} representing a field/column in an entity/table.
     * @param start The start value of the range, or null if open-ended (on the lower end)
     * @param end The end value of the range, or null if open-ended (on the upper end)
     * @param rangeType The {@link Class} or the parameter values
     * @return The created {@link Predicate}
     */
    @SuppressWarnings("unchecked")
    private static <Y extends Comparable<? super Y>> Predicate createRangePredicate( CriteriaBuilder builder, Expression field, Object start, Object end, Class<Y> rangeType ) {
        if( start != null && end != null ) {
            // TODO :asserts!
            return builder.between(field, (Y) start, (Y) end);
        } else if ( start != null ) {
            return builder.greaterThanOrEqualTo(field, (Y) start);
        } else {
            return builder.lessThanOrEqualTo(field, (Y) end);
        }
    }

    /**
     * Some criteria do not directly refer to a field, such as those stored
     * in the criteria attributes {@link Map<Class, Map<String, Attribute>>} passed
     * as an argument to the constructor.
     * </p>
     * For example, the {@link QueryParameterIdentifiers#LAST_VARIABLE_LIST} criteria specifies
     * that only the most recent {@link VariableInstanceLog} should be retrieved.
     * </p>
     * This method is called from the {@link #createPredicateFromSingleCriteria(CriteriaQuery, QueryCriteria, CriteriaBuilder, Class)}
     * method when no {@link Attribute} instance can be found in the
     * instances criteria attributes {@link Map<Class, Map<String, Attribute>>}.
     * </p>
     * @param criteriaQuery The {@link CriteriaQuery} instance.
     * @param criteria The {@link QueryCriteria} instance with the criteria information.
     * @param criteriaBuilder The {@link CriteriaBuilder} instance used to help create the query predicate.
     * @param queryType The {@link Class} of the query, used to identify the type of query
     * @param resultType The {@link Class} of the result being request.
     * @return A {@link Predicate} representin the information in the {@link QueryCriteria} instance.
     */
    // @formatter:off
    protected abstract <R,T> Predicate implSpecificCreatePredicateFromSingleCriteria(
            CriteriaQuery<R> query,
            CriteriaBuilder builder,
            Class queryType,
            QueryCriteria criteria,
            QueryWhere queryWhere);

    /**
     * This method does the persistence-related logic related to executing a query.
     * </p>
     * All implementations of this method should do the following, in approximately the following order:
     * <ol>
     * <li>Get an {@link EntityManager} instance</li>
     * <li>Join a transaction using the entity manager</li>
     * <li>Create a {@link Query} from the given {@link CriteriaQuery} instance.</li>
     * <li>Call the {@link #applyMetaCriteriaToQuery(Query, QueryWhere)} method</li>
     * <li>Retrieve the result from the {@link Query} instance.</li>
     * <li>Close the transaction created, and the created {@link EntityManager} instance.
     * <li>Return the query result</li>
     * </ol>
     *
     * @param criteriaQuery The created and filled {@link CriteriaQuery} instance
     * @param builder The {@link CriteriaBuilder}, helpful when creating {@link Predicate}s to add to the {@link CriteriaQuery}
     * @param queryWhere The {@link QueryWhere} instance containing the meta criteria information.
     * @return A {@link List} of instances, representing the query result.
     */
    // @formatter:off
    protected abstract <T> List<T> createQueryAndCallApplyMetaCriteriaAndGetResult(
            QueryWhere queryWhere,
            CriteriaQuery<T> criteriaQuery,
            CriteriaBuilder builder);

    /**
     * Small method to apply the meta criteria from the {@link QueryWhere} instance to the {@link Query} instance
     * @param query The {@link Query} instance
     * @param queryWhere The {@link QueryWhere} instance, with the abstract information about the query
     */
    public static void applyMetaCriteriaToQuery(Query query, QueryWhere queryWhere) {
        if( queryWhere.getCount() != null ) {
           query.setMaxResults(queryWhere.getCount());
        }
        if( queryWhere.getOffset() != null ) {
           query.setFirstResult(queryWhere.getOffset());
        }
    }

    /**
     *
     *
     * @param orderByListId
     * @param queryType
     * @param query
     * @return
     */
    protected <T,R> Expression getOrderByExpression(CriteriaQuery<R> query, Class<T> queryType, String orderByListId) {
        Attribute field = getCriteriaAttributes().get(queryType).get(orderByListId);
        assert field != null : "No Attribute found for order-by listId " + orderByListId
                + " for result type " + queryType.getSimpleName();
        Root table = getRoot(query, queryType);
        assert table != null : "Unable to find proper table (Root) instance in query for result type " + queryType.getSimpleName();

        Path orderByPath;
        if( field instanceof SingularAttribute ) {
            orderByPath = table.get((SingularAttribute) field);
        } else {
            throw new UnsupportedOperationException("Ordering by a join field is not supported!");
        }
        return orderByPath;
    }

}
