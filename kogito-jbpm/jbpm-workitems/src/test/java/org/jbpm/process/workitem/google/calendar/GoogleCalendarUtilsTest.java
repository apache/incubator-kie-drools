package org.jbpm.process.workitem.google.calendar;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleCalendarUtilsTest extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarUtilsTest.class);
    
	private static final String USERNAME = "drools.demo@gmail.com";
	private static final String PASSWORD = "pa$$word";
	
	@Test
	@Ignore
	public void testCalendar() throws Exception {
//		List<String> calendars =
//			GoogleCalendarUtils.getCalendars(USERNAME, PASSWORD);
//		for (String calendar: calendars) {
//			logger.info("{}", calendar);
//		}
//		assertEquals(1, calendars.size());
//		assertEquals("drools.demo@gmail.com", calendars.get(0));
	}
	
	@Test
	@Ignore
	public void TODOtestCreateCalendarEntry() throws Exception {
//		GoogleCalendarUtils.insertEntry(
//			USERNAME, PASSWORD, 
//			"New Drools Meeting",
//			"Showing the new features of Drools",
//			DateTime.now().toString(),
//			DateTime.now().toString());
//		logger.info("{}", DateTime.now().toString());
//		List<CalendarEventEntry> entries = 
//			GoogleCalendarUtils.getEntries(USERNAME, PASSWORD, "Drools");
//		for (CalendarEventEntry entry: entries) {
//			logger.info("{}", entry.getTitle().getPlainText());
//		}
//		assertEquals(1, entries.size());
//		entries.get(0).delete();
	}
	
}
