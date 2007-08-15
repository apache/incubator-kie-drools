package org.acme.insurance.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.acme.insurance.base.Driver;
import org.acme.insurance.base.DriverAdditionalInfo;
import org.acme.insurance.base.Policy;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;

public class DriverAdditionalInfoTest extends TestCase {
    private StatefulSession session;
    private Date            defaultBirthday;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        session = (new InsuranceTestHelper()).getSession();
        
        SimpleDateFormat df = new java.text.SimpleDateFormat( "dd/MM/yyyy" );
        defaultBirthday = df.parse( "18/09/1983" );
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        session.dispose();
    }

    public void testDriveVehiclePlace() throws DroolsParserException,
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

        session.insert( policy );
        session.insert( driver );
        session.insert( driverAdditional );
        
        session.fireAllRules();

        assertEquals( 1.656 , driver.getInsuranceFactor() );
        assertTrue( policy.isApproved() );
    }

}
