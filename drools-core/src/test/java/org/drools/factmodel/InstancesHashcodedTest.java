/*
 * Copyright (c) 2004-2006 Auster Solutions. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 26/08/2006
 */
package org.drools.factmodel;

import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

/**
 * @author framos
 * @version $Id$
 *
 */
public class InstancesHashcodedTest extends TestCase {
	
	
/*
		<dimension name="bck_cycle_dm"  item-expression="cycleDimension" 
			       mode="ignore" cache="10" export-to="cycle_id"> 
			<id    name="objid"            type="long"             generator-sql="select bck_dimensions_uid.nextval from dual"/> 
			<field name="cut_date"         type="java.util.Date"   value="cutDate"     key="true"/> 
			<field name="due_date"         type="java.util.Date"   value="dueDate"     key="true"/> 
			<field name="cycle_code"       type="java.lang.String" value="cycleCode"/> 
			<field name="issue_date"       type="java.util.Date"   value="issueDate"/> 
		</dimension> 
 */	

	
	public void testInstanceHashcodes() {
		
		ClassDefinition cd = new ClassDefinition( "br.com.auster.TestClass2", null, new String[]{} );
		cd.addField(new FieldDefinition("cutDate", "java.util.Date", true));
		cd.addField(new FieldDefinition("dueDate", "java.util.Date", true));
		cd.addField(new FieldDefinition("issueDate", "java.util.Date", false));
		cd.addField(new FieldDefinition("cycleCode", "java.lang.String", false));
		
		ClassBuilder cb = new ClassBuilder();
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2006);
		cal.set(Calendar.MONTH, Calendar.JUNE);
		cal.set(Calendar.DAY_OF_MONTH, 01);
		Date cut = cal.getTime();
		
		cal.set(Calendar.MONTH, Calendar.JULY);
		cal.set(Calendar.DAY_OF_MONTH, 13);
		Date d1 = cal.getTime();
		
		cal.set(Calendar.DAY_OF_MONTH, 15);
		Date d2 = cal.getTime();
			
		try {
			Class klass = cb.buildAndLoadClass(cd);
			Object o1 = klass.newInstance();
			cd.getField("cutDate").getFieldAccessor().setValue(o1, cut);
			cd.getField("dueDate").getFieldAccessor().setValue(o1, d1);
			
			Object o2 = klass.newInstance();
			cd.getField("cutDate").getFieldAccessor().setValue(o2, cut);
			cd.getField("dueDate").getFieldAccessor().setValue(o2, d2);
			
			System.out.println(o1);
			System.out.println(o1.hashCode());
			System.out.println(o2);
			System.out.println(o2.hashCode());
			
			System.out.println(o1.equals(o2));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
