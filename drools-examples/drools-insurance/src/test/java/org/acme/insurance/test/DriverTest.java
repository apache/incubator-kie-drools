package org.acme.insurance.test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.acme.insurance.base.Driver;
import org.acme.insurance.base.Policy;
import org.acme.insurance.web.InsuranceSessionHelper;
import org.drools.StatefulSession;

public class DriverTest extends TestCase {
    private StatefulSession session;
    private Date            defaultBirthday;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        session = InsuranceSessionHelper.getSession();
        
        SimpleDateFormat df = new java.text.SimpleDateFormat( "dd/MM/yyyy" );
        defaultBirthday = df.parse( "18/09/1983" );
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //session.dispose();
    }

    public void testDriverGenreFactor() throws
                                       IOException,
                                       Exception {
        Driver driverMale = new Driver();
        driverMale.setGenre( Driver.MALE );
        driverMale.setBirhDate( defaultBirthday );
        driverMale.setMaritalState( Driver.SINGLE );
        driverMale.setLicenceYears( 2 );

        Driver driverFemale = new Driver();
        driverFemale.setGenre( Driver.FEMALE );
        driverFemale.setBirhDate( defaultBirthday );
        driverFemale.setMaritalState( Driver.MARRIED );

        Policy policy = new Policy();
        policy.setApproved( false );
        session.insert( policy );

        session.insert( driverMale );
        session.fireAllRules();

        assertEquals( 1.92,
                      driverMale.getInsuranceFactor() );

        policy = new Policy();
        policy.setApproved( false );
        session.insert( policy );

        session.insert( driverFemale );
        session.fireAllRules();

        assertEquals( 1.0,
                      driverFemale.getInsuranceFactor() );

    }

    public void testMatureDriverWithChildFactor() throws
                                                 IOException,
                                                 Exception {

        SimpleDateFormat df = new java.text.SimpleDateFormat( "dd/MM/yyyy" );
        defaultBirthday = df.parse( "18/09/1959" );

        Driver driverMale = new Driver();
        driverMale.setGenre( Driver.MALE );
        driverMale.setBirhDate( defaultBirthday );
        driverMale.setMaritalState( Driver.MARRIED );
        driverMale.setHasChildren( true );
        driverMale.setLicenceYears(10);

        Policy policy = new Policy();
        policy.setApproved( false );
        session.insert( policy );

        session.insert( driverMale );
        session.fireAllRules();

        assertEquals( 1.5,
                      driverMale.getInsuranceFactor() );

    }

    public void testDriverUnderAgeRejection() throws ParseException {
        Driver driver = new Driver();

        SimpleDateFormat df = new java.text.SimpleDateFormat( "dd/MM/yyyy" );
        Date birhDate = df.parse( "18/09/1996" );

        driver.setGenre( Driver.MALE );
        driver.setMaritalState( Driver.SINGLE );
        driver.setHasChildren( false );
        driver.setBirhDate( birhDate );

        Policy policy = new Policy();
        policy.setApproved( false );

        session.insert( driver );
        session.insert( policy );

        session.fireAllRules();

        assertEquals( 0.0,
                      driver.getInsuranceFactor() );

        assertFalse( policy.isApproved() );
    }

    public void testYoungProblematicDriver() {
        Driver driverMale = new Driver();
        driverMale.setGenre( Driver.MALE );
        driverMale.setBirhDate( defaultBirthday );
        driverMale.setMaritalState( Driver.SINGLE );
        driverMale.setLicenceYears( 2 );
        driverMale.setPriorClaims( 5 );

        Policy policy = new Policy();
        policy.setApproved( false );
        session.insert( policy );

        session.insert( driverMale );
        session.fireAllRules();

        assertEquals( 3.84,
                      driverMale.getInsuranceFactor() );

    }

    public void testEmptyDriver() {
        Driver driver = new Driver();

        Policy policy = new Policy();
        policy.setApproved( false );

        try {
            session.insert( driver );
            session.insert( policy );
            session.fireAllRules();
            fail("should throw exception");
        } catch ( Exception e ) {
            // OK 
        }
    }

    // rewrite test or add rule in drl
    
    public void testMarginalWithPriorDriver() throws ParseException {
        Driver driver = new Driver();

        SimpleDateFormat df = new java.text.SimpleDateFormat( "dd/MM/yyyy" );
        Date birhDate = df.parse( "18/09/2005" );
        
        
        driver.setGenre( Driver.MALE );
        driver.setMaritalState( Driver.MARRIED );
        driver.setHasChildren( false );
        driver.setBirhDate( birhDate );
        driver.setPriorClaims( 9 );

        Policy policy = new Policy();
        policy.setApproved( false );

        session.insert( driver );
        session.insert( policy );

        session.fireAllRules();
        
        assertFalse( policy.isApproved() );
    }    

    public void testProblematicDriver() throws ParseException {
        Driver driver = new Driver();

        SimpleDateFormat df = new java.text.SimpleDateFormat( "dd/MM/yyyy" );
        Date birhDate = df.parse( "18/09/1960" );

        driver.setGenre( Driver.MALE );
        driver.setMaritalState( Driver.MARRIED );
        driver.setHasChildren( false );
        driver.setBirhDate( birhDate );
        driver.setPriorClaims( 5 );

        Policy policy = new Policy();
        policy.setApproved( false );

        session.insert( driver );
        session.insert( policy );

        session.fireAllRules();

        assertEquals( 2.4,
                      driver.getInsuranceFactor() );

        assertTrue( policy.isApproved() );
    }

}
