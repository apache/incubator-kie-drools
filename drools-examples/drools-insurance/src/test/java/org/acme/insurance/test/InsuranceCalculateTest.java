package org.acme.insurance.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.acme.insurance.base.AccessoriesCoverage;
import org.acme.insurance.base.Driver;
import org.acme.insurance.base.DriverAdditionalInfo;
import org.acme.insurance.base.Policy;
import org.acme.insurance.base.SupplementalInfo;
import org.acme.insurance.web.InsuranceSessionHelper;
import org.drools.StatefulSession;

public class InsuranceCalculateTest extends TestCase {
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

    public void testHasExtraCar() throws 
                                 IOException,
                                 Exception {

        Driver driver = new Driver();

        driver.setGenre( Driver.MALE );
        driver.setBirhDate( defaultBirthday );
        driver.setMaritalState( Driver.SINGLE );
        driver.setLicenceYears( 2 );
        driver.setId( 400 );
        driver.setPriorClaims( 3 );

        SupplementalInfo suppinfo = new SupplementalInfo();

        suppinfo.setExtraCar( true );
        suppinfo.setExtraAssistence( true );
        suppinfo.setGlassCoverage( true );
        suppinfo.setNonRelatedExpenses( true );
        suppinfo.setDriverId( driver.getId() );

        Policy policy = new Policy();
        policy.setApproved( false );

        policy.setBasePrice( 1000.00 );

        session.insert( policy );
        session.insert( driver );
        session.insert( suppinfo );

        session.fireAllRules();
        
        assertEquals( 3.7340352000000006,
                      driver.getInsuranceFactor() );

        assertFalse( policy.isApproved() );

    }

    public void testDriveVehiclePlace() throws 
                                       IOException,
                                       Exception {

        Driver driver = new Driver();

        driver.setGenre( Driver.MALE );
        driver.setBirhDate( defaultBirthday );
        driver.setMaritalState( Driver.MARRIED );
        driver.setLicenceYears( 10 );
        driver.setId( 400 );

        DriverAdditionalInfo driverAdditional = new DriverAdditionalInfo();
        driverAdditional.setDayVehiclePlace( DriverAdditionalInfo.STREET );
        driverAdditional.setNightVehiclePlace( DriverAdditionalInfo.STREET );
        driverAdditional.setDriverId( driver.getId() );

        Policy policy = new Policy();
        policy.setApproved( false );
        policy.setBasePrice( 500.00 );

        session.insert( policy );
        session.insert( driver );
        session.insert( driverAdditional );

        session.fireAllRules();
        
        assertEquals( 1.656,
                      driver.getInsuranceFactor() );

        assertTrue( policy.isApproved() );
        assertEquals( 828.00,
                      policy.getInsurancePrice() );
    }

    public void testAccessoriesValue() throws 
                                      IOException,
                                      Exception {

        Driver driver = new Driver();

        driver.setGenre( Driver.MALE );
        driver.setBirhDate( defaultBirthday );
        driver.setMaritalState( Driver.MARRIED );
        driver.setLicenceYears( 10 );
        driver.setId( 400 );

        DriverAdditionalInfo driverAdditional = new DriverAdditionalInfo();
        driverAdditional.setDayVehiclePlace( DriverAdditionalInfo.STREET );
        driverAdditional.setNightVehiclePlace( DriverAdditionalInfo.STREET );
        driverAdditional.setDriverId( driver.getId() );
        
        AccessoriesCoverage accessories = new AccessoriesCoverage();
        
        accessories.setAlarmSystemValue( 350.00 );
        accessories.setArmorValue( 1500.00 );
        accessories.setSoundSystemValue( 700.00 );
        accessories.setDriverId( driver.getId() );

        Policy policy = new Policy();
        policy.setApproved( false );
        policy.setBasePrice( 500.00 );

        session.insert( policy );
        session.insert( driver );
        session.insert( driverAdditional );
        session.insert( accessories );

        session.fireAllRules();

        assertEquals( 1.656,
                      driver.getInsuranceFactor() );

        assertTrue( policy.isApproved() );
        assertEquals( 1373.00,
                      policy.getInsurancePrice() );
    }

}
