package com.kira.doublebooked;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DoubleBooked {

	public static void main(String[] args) {
		
		DoubleBooked db = new DoubleBooked();
		
		List<Event> events = new ArrayList<Event> (); 
		//DateTime format should be of format "yyyy-MM-dd HH:mm" in 24hr time format.
		
		Event e1 = new Event("2020-11-02 09:00","2020-11-02 11:00");
		Event e2 = new Event("2020-11-02 14:00","2020-11-02 18:00");
		Event e3 = new Event("2020-11-02 09:00","2020-11-02 10:30");
		Event e4 = new Event("2020-11-02 10:30","2020-11-02 12:00");
		Event e5 = new Event("2020-11-02 15:00","2020-11-02 17:00");
		Event e6 = new Event("2020-11-03 09:00","2020-11-03 10:00");
		Event e7 = new Event("2020-11-03 09:30","2020-11-03 12:00");
		Event e8 = new Event("2020-11-03 11:00","2020-11-03 15:00");
		Event e9 = new Event("2020-11-03 10:00","2020-11-03 11:30");
		Event e10 = new Event("2020-11-02 10:30","2020-11-02 12:30");
		
		events.add(e1);
		events.add(e2);
		events.add(e3);
		events.add(e4);
		events.add(e5);
		events.add(e6);
		events.add(e7);
		events.add(e8);
		events.add(e9);
		events.add(e10);
		
		events.sort(eventComparator);
		db.printAllEvents(events);
		db.printOverlappingEvents(events);
		
	}
	
	
	public void printOverlappingEvents(List<Event> events) {
		System.out.println("\nList of Overlapping Events: \n");
		for(int i=0;i<events.size();i++) {
			for(int j=i+1;j<events.size();j++) {
				if(events.get(i).isOverlapping(events.get(j))) {
					System.out.println(events.get(i)+","+events.get(j));
				}
				else {
					break;
				}
			}
		}
	}
	
	public void printAllEvents(List<Event> events) {
		System.out.println("\nList of All Events: \n");
		for (Event event : events) {
			System.out.println(event);
		}
	}
	
	 public static Comparator<Event> eventComparator = new Comparator<Event>() {

			public int compare(Event e1, Event e2) {
				if(e1.getStartDateTime().isBefore(e2.getStartDateTime())) {
					return -1;
				}
				else if(e1.getStartDateTime().isAfter(e2.getStartDateTime())) {
					return 1;
				}
				else if(e1.getStartDateTime().isEqual(e2.getStartDateTime())) {
					if(e1.getEndDateTime().isBefore(e2.getEndDateTime())) {
						return -1;
					}
					else if(e1.getEndDateTime().isAfter(e2.getEndDateTime())) {
						return 1;
					}
					else if(e1.getEndDateTime().isEqual(e2.getEndDateTime())) {
					return 0;
					}
				}
				return 0;
				
			  }
	 		};
}
