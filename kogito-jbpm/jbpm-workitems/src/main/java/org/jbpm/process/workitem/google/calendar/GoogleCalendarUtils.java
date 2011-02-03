package org.jbpm.process.workitem.google.calendar;


public final class GoogleCalendarUtils {

	private GoogleCalendarUtils() {
	}
	
//	private static CalendarService getCalendarService(String userName, String password) throws AuthenticationException {
//		CalendarService service = new CalendarService("Drools-Flow-WorkItem-1.0");
//		service.setUserCredentials(userName, password);
//		return service;
//	}
//	
//	public static List<String> getCalendars(String userName, String password) throws ServiceException {
//		CalendarService service = getCalendarService(userName, password);
//	    
//		try {
//			URL feedUrl = new URL(
//				"http://www.google.com/calendar/feeds/default/allcalendars/full");
//			CalendarFeed resultFeed = service.getFeed(feedUrl, CalendarFeed.class);
//			List<String> result = new ArrayList<String>();
//			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
//				CalendarEntry entry = resultFeed.getEntries().get(i);
//				result.add(entry.getTitle().getPlainText());
//			}
//			return result;
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
//
//	public static void insertEntry(
//			String userName, String password,
//			String title, String content,
//			String start, String end)
//			throws AuthenticationException, ServiceException {
//		CalendarService service = getCalendarService(userName, password);
//		try {
//			URL postURL = new URL(
//				"http://www.google.com/calendar/feeds/" + userName + "/private/full");
//			CalendarEventEntry myEvent = new CalendarEventEntry();
//			myEvent.setTitle(new PlainTextConstruct(title));
//			myEvent.setContent(new PlainTextConstruct(content));
//			DateTime startTime = DateTime.parseDateTime(start);
//			DateTime endTime = DateTime.parseDateTime(end);
//			When eventTimes = new When();
//			eventTimes.setStartTime(startTime);
//			eventTimes.setEndTime(endTime);
//			myEvent.addTime(eventTimes);
//			service.insert(postURL, myEvent);
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
//	
//	public static List<CalendarEventEntry> getEntries(String userName, String password, String text) throws AuthenticationException, ServiceException {
//		CalendarService service = getCalendarService(userName, password);
//		try {
//			URL feedUrl = new URL(
//				"http://www.google.com/calendar/feeds/" + userName + "/private/full");
//			Query myQuery = new Query(feedUrl);
//			myQuery.setFullTextQuery(text);
//			CalendarEventFeed myResultsFeed = service.query(myQuery, CalendarEventFeed.class);
//			return myResultsFeed.getEntries(); 
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
	
}
