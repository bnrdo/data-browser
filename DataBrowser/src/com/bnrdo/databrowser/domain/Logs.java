package com.bnrdo.databrowser.domain;

import java.util.Date;

public class Logs {
	private Integer event_id;

    private String probe_id;

    private String entity;

    private String environment;

    private String user_id;

    private String transaction_id;

    private String message;

    private Float number_val;

    private Date date_value;
    
    private Date send_time;
    
    private Date absolute_time;

    private Integer thread_id;
    
    private String ip_address;

	public Integer getEvent_id() {
		return event_id;
	}

	public void setEvent_id(Integer id) {
		event_id = (id == null) ? 0 : id;
	}

	public String getProbe_id() {
		return probe_id;
	}

	public void setProbe_id(String id) {
		probe_id = (id == null) ? "" : id;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entt) {
		entity = (entt == null) ? "" : entt;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String envrt) {
		environment = (envrt == null) ? "" : envrt;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String id) {
		user_id = (id == null) ? "" : id;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String id) {
		transaction_id = (id == null) ? "" : id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		message = (msg == null) ? "" : msg;
	}

	public Float getNumber_val() {
		return number_val;
	}

	public void setNumber_val(Float val) {
		number_val = (val == null) ? 0 : val;
	}

	public Date getDate_value() {
		return date_value;
	}

	public void setDate_value(Date val) {
		date_value = (val == null) ? new Date() : val;
	}

	public Date getSend_time() {
		return send_time;
	}

	public void setSend_time(Date val) {
		send_time = (val == null) ? new Date() : val;
	}

	public Date getAbsolute_time() {
		return absolute_time;
	}

	public void setAbsolute_time(Date val) {
		absolute_time = (val == null) ? new Date() : val;
	}

	public Integer getThread_id() {
		return thread_id;
	}

	public void setThread_id(Integer id) {
		thread_id = (id == null) ? 0 : id;
	}

	public String getIp_address() {
		return ip_address;
	}

	public void setIp_address(String addr) {
		ip_address = (addr == null) ? "" : addr;
	}
    
    
}
