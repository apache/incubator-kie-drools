package org.acme.insurance.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.acme.insurance.base.Driver;
import org.acme.insurance.base.Policy;
import org.acme.insurance.base.SupplementalInfo;
import org.acme.insurance.web.InsuranceSessionHelper;
import org.drools.StatefulSession;

public class SupplementalInfoTest extends TestCase {
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
        session.dispose();
    }

    public void testHasExtraCar() throws IOException,
                                 Exception {

        Driver driver = new Driver();

        driver.setGenre( Driver.MALE );
        driver.setBirhDate( defaultBirthday );
        driver.setMaritalState( Driver.MARRIED );
        driver.setLicenceYears( 10 );
        driver.setId( 400 );

        SupplementalInfo suppinfo = new SupplementalInfo();

        suppinfo.setExtraCar( true );
        suppinfo.setDriverId( driver.getId() );

        Policy policy = new Policy();
        policy.setApproved( false );

        session.insert( policy );
        session.insert( driver );
        session.insert( suppinfo );

        session.fireAllRules();

        assertEquals( 1.05,
                      driver.getInsuranceFactor() );
        assertTrue( policy.isApproved() );
    }

    public void testeHasExtraAssistence() throws IOException,
                                         Exception {

        Driver driver = new Driver();

        driver.setGenre( Driver.MALE );
        driver.setBirhDate( defaultBirthday );
        driver.setMaritalState( Driver.MARRIED );
        driver.setLicenceYears( 10 );
        driver.setId( 400 );

        SupplementalInfo suppinfo = new SupplementalInfo();

        suppinfo.setExtraAssistence( true );
        suppinfo.setDriverId( driver.getId() );

        Policy policy = new Policy();
        policy.setApproved( false );

        session.insert( policy );
        session.insert( driver );
        session.insert( suppinfo );

        session.fireAllRules();

        assertEquals( 1.05,
                      driver.getInsuranceFactor() );
        assertTrue( policy.isApproved() );
    }

    public void testeGlassCoverage() throws IOException,
                                    Exception {

        Driver driver = new Driver();

        driver.setGenre( Driver.MALE );
        driver.setBirhDate( defaultBirthday );
        driver.setMaritalState( Driver.MARRIED );
        driver.setLicenceYears( 10 );
        driver.setId( 400 );

        SupplementalInfo suppinfo = new SupplementalInfo();

        suppinfo.setGlassCoverage( true );
        suppinfo.setDriverId( driver.getId() );

        Policy policy = new Policy();
        policy.setApproved( false );

        session.insert( policy );
        session.insert( driver );
        session.insert( suppinfo );

        session.fireAllRules();

        assertEquals( 1.05,
                      driver.getInsuranceFactor() );
        assertTrue( policy.isApproved() );
    }

    public void testeNonRelatedExpenses() throws IOException,
                                         Exception {

        Driver driver = new Driver();

        driver.setGenre( Driver.MALE );
        driver.setBirhDate( defaultBirthday );
        driver.setMaritalState( Driver.MARRIED );
        driver.setLicenceYears( 10 );
        driver.setId( 400 );

        SupplementalInfo suppinfo = new SupplementalInfo();

        suppinfo.setNonRelatedExpenses( true );
        suppinfo.setDriverId( driver.getId() );

        Policy policy = new Policy();
        policy.setApproved( false );

        session.insert( policy );
        session.insert( driver );
        session.insert( suppinfo );

        session.fireAllRules();

        assertEquals( 1.05,
                      driver.getInsuranceFactor() );
        assertTrue( policy.isApproved() );
    }

    public void testeSupplementalInfoMix() throws IOException,
                                          Exception {

        Driver driver = new Driver();

        driver.setGenre( Driver.MALE );
        driver.setBirhDate( defaultBirthday );
        driver.setMaritalState( Driver.MARRIED );
        driver.setLicenceYears( 10 );
        driver.setId( 400 );

        SupplementalInfo suppinfo = new SupplementalInfo();

        suppinfo.setNonRelatedExpenses( true );
        suppinfo.setGlassCoverage( true );
        suppinfo.setExtraAssistence( true );
        suppinfo.setExtraCar( true );

        suppinfo.setDriverId( driver.getId() );

        Policy policy = new Policy();
        policy.setApproved( false );

        session.insert( policy );
        session.insert( driver );
        session.insert( suppinfo );

        session.fireAllRules();

        assertEquals( 1.2155062500000002,
                      driver.getInsuranceFactor() );

        assertTrue( policy.isApproved() );
    }

}
