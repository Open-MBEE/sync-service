package org.openmbee.syncservice.core.queue.dto;

import java.io.Serializable;

/**
 * A POJO class for holding ResponseEntity responses
 * @author Anil Kumar Polamarasetty
 *
 */
public class QueueDetailsResponseTO implements Serializable {
	
	private static final long serialVersionUID = -2786946685633118343L;
	
	private String status;
	private String message;
	private QueueDetailsTO data = null;
	
	public QueueDetailsResponseTO() {
		super();
	}

	public QueueDetailsResponseTO(QueueDetailsTO data) {
		super();
		this.data = data;
	}

	public QueueDetailsResponseTO(String status, String message, QueueDetailsTO data) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
	}
	
	public QueueDetailsResponseTO(String status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public QueueDetailsTO getData() {
		return data;
	}
	
	public void setData(QueueDetailsTO data) {
		this.data = data;
	}
}
