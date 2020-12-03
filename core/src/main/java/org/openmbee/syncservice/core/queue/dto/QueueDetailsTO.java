package org.openmbee.syncservice.core.queue.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A POJO class for holding the JSON data
 * 
 */
public class QueueDetailsTO implements Serializable {

	private static final long serialVersionUID = -8555442357044642366L;

	@JsonProperty("consumers")
	private int consumers;

	@JsonProperty("idle_since")
	private String idleSince;
	
	@JsonProperty("message_bytes")
	private int message_bytes;
	
	@JsonProperty("message_bytes_ready")
	private int message_bytes_ready;
	
	@JsonProperty("message_bytes_unacknowledged")
	private int message_bytes_unacknowledged;
	
	@JsonProperty("message_stats")
	private MessageStatsTO messageStats;
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("messages")
	private int messages;
	
	@JsonProperty("messages_unacknowledged")
	private int messages_unacknowledged;
	
	@JsonProperty("name")
	private String queueName;
	
	@JsonProperty("node")
	private String nodeName;
	
	@JsonProperty("state")
	private String state;
	
	@JsonProperty("vhost")
	private String vhost;

	public int getConsumers() {
		return consumers;
	}

	public String getIdleSince() {
		return idleSince;
	}

	public int getMessage_bytes() {
		return message_bytes;
	}

	public int getMessage_bytes_ready() {
		return message_bytes_ready;
	}

	public int getMessage_bytes_unacknowledged() {
		return message_bytes_unacknowledged;
	}

	public MessageStatsTO getMessageStats() {
		return messageStats;
	}
	
	public void setMessageStats(MessageStatsTO messageStats) {
		this.messageStats = messageStats;
	}

	public int getMessages() {
		return messages;
	}

	public int getMessages_unacknowledged() {
		return messages_unacknowledged;
	}

	public String getQueueName() {
		return queueName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getState() {
		return state;
	}

	public String getVhost() {
		return vhost;
	}

}

