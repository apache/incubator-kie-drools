/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.testscenarios.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.datamodel.imports.Imports;

/**
 * This represents a test scenario. It also encapsulates the result of a
 * scenario run.
 */
public class Scenario implements HasImports {

    /**
     * An arbitrary name to identify this test, used in reports
     */
    private String name = "Unnamed";

    /**
     * The maximum number of rules to fire so we don't recurse for ever.
     */
    private int maxRuleFirings = 100000;

    /**
     * global data which must be setup before hand.
     */
    private List<FactData> globals = new ArrayList<FactData>();

    /**
     * Fixtures are parts of the test. They may be assertions, globals, data,
     * execution runs etc. Anything really.
     */
    private List<Fixture> fixtures = new ArrayList<Fixture>();

    /**
     * This is the date the last time the scenario was run (and what the results
     * apply to).
     */
    private Date lastRunResult;

    /**
     * the rules to include or exclude
     */
    private List<String> rules = new ArrayList<String>();

    /**
     * true if only the rules in the list should be allowed to fire. Otherwise
     * it is exclusive (ie all rules can fire BUT the ones in the list).
     */
    private boolean inclusive = false;

    private String packageName;

    private Imports imports = new Imports();

    private ArrayList<String> ksessions = new ArrayList<String>();

    public Scenario() {
    }

    public Scenario( String packageName,
                     String name ) {
        this.packageName = packageName;
        this.name = name;
    }

    /**
     * Returns true if this was a totally successful scenario, based on the
     * results contained.
     */
    public boolean wasSuccessful() {
        for ( Fixture fixture : fixtures ) {
            if ( fixture instanceof Expectation ) {
                if ( !( (Expectation) fixture ).wasSuccessful() ) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Will slip in a fixture after the specified one, but before the next
     * execution trace.
     */
    public void insertBetween( final Fixture fixtureBeforeTheNewOne,
                               final Fixture newFixture ) {

        boolean inserted = false;
        int start = ( fixtureBeforeTheNewOne == null ) ? 0 : fixtures.indexOf( fixtureBeforeTheNewOne ) + 1;

        for ( int j = start; j < fixtures.size(); j++ ) {
            if ( fixtures.get( j ) instanceof ExecutionTrace ) {
                getFixtures().add( j,
                                   newFixture );
                return;
            }
        }

        if ( !inserted ) {
            fixtures.add( newFixture );
        }
    }

    /**
     * Remove the specified fixture.
     */
    public void removeFixture( final Fixture f ) {
        this.fixtures.remove( f );
        this.globals.remove( f );
    }

    /**
     * Remove fixtures between this ExecutionTrace and the previous one.
     */
    public void removeExecutionTrace( final ExecutionTrace executionTrace ) {
        removeExpected( executionTrace );
        removeGiven( executionTrace );
    }

    private void removeExpected( final ExecutionTrace executionTrace ) {
        boolean remove = false;
        for ( Iterator<Fixture> iterator = getFixtures().iterator(); iterator.hasNext(); ) {
            Fixture fixture = iterator.next();

            if ( fixture.equals( executionTrace ) ) {
                remove = true;
                continue;
            } else if ( remove && fixture instanceof ExecutionTrace ) {
                break;
            }

            if ( remove && fixture instanceof Expectation ) {
                iterator.remove();
                globals.remove( fixture );
            }
        }
    }

    private void removeGiven( final ExecutionTrace executionTrace ) {

        Collections.reverse( getFixtures() );

        boolean remove = false;
        Iterator<Fixture> iterator = getFixtures().iterator();
        while ( iterator.hasNext() ) {
            Fixture fixture = iterator.next();

            // Catch the first or next ExecutionTrace.
            if ( fixture.equals( executionTrace ) ) {
                remove = true;
            } else if ( remove && fixture instanceof ExecutionTrace ) {
                break;
            }

            if ( remove && !( fixture instanceof Expectation ) ) {
                iterator.remove();
                globals.remove( fixture );
            }
        }

        Collections.reverse( getFixtures() );
    }

    /**
     * @return A mapping of variable names to their fact data.
     */
    public Map<String, FactData> getFactTypes() {
        Map<String, FactData> factTypesByName = new HashMap<String, FactData>();
        for ( Fixture fixture : fixtures ) {
            if ( fixture instanceof FactData ) {
                FactData factData = (FactData) fixture;
                factTypesByName.put( factData.getName(),
                                     factData );
            }
        }
        for ( FactData factData : globals ) {
            factTypesByName.put( factData.getName(),
                                 factData );
        }
        return factTypesByName;
    }

    /**
     * @return A mapping of variable names to their fact type.
     */
    public Map<String, String> getVariableTypes() {
        Map<String, String> map = new HashMap<String, String>();
        for ( Fixture fixture : fixtures ) {
            if ( fixture instanceof FactData ) {
                FactData factData = (FactData) fixture;
                map.put( factData.getName(),
                         factData.getType() );
            }
        }
        for ( FactData factData : globals ) {
            map.put( factData.getName(),
                     factData.getType() );
        }
        return map;
    }

    /**
     * @return A mapping of Fact types to their Fact definitions.
     */
    public Map<String, List<FactData>> getFactTypesToFactData() {
        Map<String, List<FactData>> map = new HashMap<String, List<FactData>>();
        for ( Fixture fixture : fixtures ) {
            if ( fixture instanceof FactData ) {
                FactData factData = (FactData) fixture;
                List<FactData> fd = map.get( factData.getType() );
                if ( fd == null ) {
                    fd = new ArrayList<FactData>();
                    map.put( factData.getType(),
                             fd );
                }
                fd.add( factData );
            }
        }
        for ( FactData factData : globals ) {
            List<FactData> fd = map.get( factData.getType() );
            if ( fd == null ) {
                fd = new ArrayList<FactData>();
                map.put( factData.getType(),
                         fd );
            }
            fd.add( factData );
        }
        return map;
    }

    /**
     * This will return a list of fact names that are in scope (including
     * globals).
     * @return List<String>
     */
    public List<String> getFactNamesInScope( final ExecutionTrace executionTrace,
                                             final boolean includeGlobals ) {
        if ( executionTrace == null ) {
            return Collections.emptyList();
        }

        List<String> factDataNames = new ArrayList<String>();
        int p = this.getFixtures().indexOf( executionTrace );
        for ( int i = 0; i < p; i++ ) {
            Fixture fixture = (Fixture) getFixtures().get( i );
            if ( fixture instanceof FactData ) {
                FactData factData = (FactData) fixture;
                factDataNames.add( factData.getName() );
            } else if ( fixture instanceof RetractFact ) {
                RetractFact retractFact = (RetractFact) fixture;
                factDataNames.remove( retractFact.getName() );
            }
        }

        if ( includeGlobals ) {
            for ( FactData factData : getGlobals() ) {
                factDataNames.add( factData.getName() );
            }
        }
        return factDataNames;
    }

    /**
     * @return true if a fact name is already in use.
     */
    public boolean isFactNameReserved( final String factName ) {
        if ( isFactNameUsedInGlobals( factName ) ) {
            return true;
        } else if ( isFactNameUsedInFactDataFixtures( factName ) ) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isFactNameUsedInFactDataFixtures( final String factName ) {
        for ( Fixture fixture : fixtures ) {
            if ( fixture instanceof FactData ) {
                FactData factData = (FactData) fixture;
                if ( factData.getName().equals( factName ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isFactNameUsedInGlobals( final String factName ) {
        for ( FactData factData : globals ) {
            if ( factData.getName().equals( factName ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if a fact is actually used (ie if its not, its safe to
     *         remove it).
     */
    public boolean isFactDataReferenced( final FactData factData ) {
        int start = fixtures.indexOf( factData ) + 1;
        String factName = factData.getName();

        for ( Fixture fixture : fixtures.subList( start,
                                                  fixtures.size() ) ) {
            if ( isFactNameUsedInThisFixture( fixture,
                                              factName ) ) {
                return true;
            }
        }

        return false;
    }

    private boolean isFactNameUsedInThisFixture( final Fixture fixture,
                                                 final String factName ) {
        if ( fixture instanceof FactData ) {
            return ( (FactData) fixture ).getName().equals( factName );
        } else if ( fixture instanceof VerifyFact ) {
            return ( (VerifyFact) fixture ).getName().equals( factName );
        } else if ( fixture instanceof RetractFact ) {
            return ( (RetractFact) fixture ).getName().equals( factName );
        } else {
            return false;
        }
    }

    /**
     * @return the list of failure messages
     */
    public List<String> getFailureMessages() {
        List<String> messages = new ArrayList<String>();
        for ( Fixture fixture : fixtures ) {
            if ( fixture instanceof VerifyRuleFired ) {
                VerifyRuleFired verifyRuleFired = (VerifyRuleFired) fixture;
                if ( ruleFailedToFire( verifyRuleFired ) ) {
                    messages.add( verifyRuleFired.getExplanation() );
                }
            } else if ( fixture instanceof VerifyFact ) {
                VerifyFact verifyFact = (VerifyFact) fixture;
                for ( VerifyField verifyField : verifyFact.getFieldValues() ) {
                    if ( fieldExpectationFailed( verifyField ) ) {
                        messages.add( verifyField.getExplanation() );
                    }
                }
            }
        }
        return messages;
    }

    /**
     * @return int[0] = failures, int[1] = total;
     */
    public int[] countFailuresTotal() {
        int total = 0;
        int failures = 0;
        for ( Fixture fixture : fixtures ) {
            if ( fixture instanceof VerifyRuleFired ) {
                total++;
                VerifyRuleFired verifyRuleFired = (VerifyRuleFired) fixture;
                if ( ruleFailedToFire( verifyRuleFired ) ) {
                    failures++;
                }
            } else if ( fixture instanceof VerifyFact ) {
                VerifyFact verifyFact = (VerifyFact) fixture;
                for ( VerifyField verifyField : verifyFact.getFieldValues() ) {
                    if ( fieldExpectationFailed( verifyField ) ) {
                        failures++;
                    }
                    total++;
                }
            }
        }
        return new int[]{ failures, total };
    }

    protected boolean fieldExpectationFailed( VerifyField verifyField ) {
        return verifyField.getSuccessResult() != null && !verifyField.getSuccessResult();
    }

    protected boolean ruleFailedToFire( VerifyRuleFired verifyRuleFired ) {
        return verifyRuleFired.getSuccessResult() != null && !verifyRuleFired.getSuccessResult();
    }

    public List<Fixture> getFixtures() {
        return fixtures;
    }

    public int getMaxRuleFirings() {
        return maxRuleFirings;
    }

    public List<FactData> getGlobals() {
        return globals;
    }

    public void setLastRunResult( final Date lastRunResult ) {
        this.lastRunResult = lastRunResult;
    }

    public Date getLastRunResult() {
        return lastRunResult;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setInclusive( final boolean inclusive ) {
        this.inclusive = inclusive;
    }

    public boolean isInclusive() {
        return inclusive;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void setImports( final Imports imports ) {
        this.imports = imports;
    }

    @Override
    public Imports getImports() {
        return imports;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<String> getKSessions() {
        return ksessions;
    }

    public void setPackageName( String packageName ) {
        this.packageName = packageName;
    }
}
