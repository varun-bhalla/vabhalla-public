package com.kira.doublebooked;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Event {

	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	Event() { 
		this.startDateTime = null;
		this.endDateTime = null;
	}
	Event(String startDateTime,String endDateTime) {
		try {
		this.startDateTime = LocalDateTime.parse(startDateTime, formatter);
		this.endDateTime = LocalDateTime.parse(endDateTime, formatter);
		} catch(DateTimeParseException ex) {
			System.err.println("Invalid Date Format");
			System.exit(1);
		}
		
	}
	
	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}
	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}
	public boolean isOverlapping(Event e) {
		if(((this.getEndDateTime()).isAfter(e.getStartDateTime()))) {
			return true;
		}
		return false;
	}
	@Override
    public String toString() { 
        return String.format("("+this.getStartDateTime().format(formatter) +" - "+this.getEndDateTime().format(formatter) +")"); 
    } 
	
}
