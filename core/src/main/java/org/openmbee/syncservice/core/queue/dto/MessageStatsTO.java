package org.openmbee.syncservice.core.queue.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MessageStatsTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6523071419953908177L;

	@JsonProperty("ack")
	private Integer ack;
	@JsonProperty("deliver")
	private Integer deliver;
	@JsonProperty("deliver_get")
	private Integer deliverGet;
	@JsonProperty("deliver_no_ack")
	private Integer deliverNoAck;
	@JsonProperty("get")
	private Integer get;
	@JsonProperty("get_no_ack")
	private Integer getNoAck;
	@JsonProperty("publish")
	private Integer publish;
	@JsonProperty("redeliver")
	private Integer redeliver;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("ack")
	public Integer getAck() {
		return ack;
	}

	@JsonProperty("ack")
	public void setAck(Integer ack) {
		this.ack = ack;
	}

	@JsonProperty("deliver")
	public Integer getDeliver() {
		return deliver;
	}

	@JsonProperty("deliver")
	public void setDeliver(Integer deliver) {
		this.deliver = deliver;
	}

	@JsonProperty("deliver_get")
	public Integer getDeliverGet() {
		return deliverGet;
	}

	@JsonProperty("deliver_get")
	public void setDeliverGet(Integer deliverGet) {
		this.deliverGet = deliverGet;
	}

	@JsonProperty("deliver_no_ack")
	public Integer getDeliverNoAck() {
		return deliverNoAck;
	}

	@JsonProperty("deliver_no_ack")
	public void setDeliverNoAck(Integer deliverNoAck) {
		this.deliverNoAck = deliverNoAck;
	}

	@JsonProperty("get")
	public Integer getGet() {
		return get;
	}

	@JsonProperty("get")
	public void setGet(Integer get) {
		this.get = get;
	}

	@JsonProperty("get_no_ack")
	public Integer getGetNoAck() {
		return getNoAck;
	}

	@JsonProperty("get_no_ack")
	public void setGetNoAck(Integer getNoAck) {
		this.getNoAck = getNoAck;
	}

	@JsonProperty("publish")
	public Integer getPublish() {
		return publish;
	}

	@JsonProperty("publish")
	public void setPublish(Integer publish) {
		this.publish = publish;
	}

	@JsonProperty("redeliver")
	public Integer getRedeliver() {
		return redeliver;
	}

	@JsonProperty("redeliver")
	public void setRedeliver(Integer redeliver) {
		this.redeliver = redeliver;
	}

}
