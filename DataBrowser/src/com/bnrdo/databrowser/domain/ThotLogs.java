package com.bnrdo.databrowser.domain;

import java.util.Date;

public class ThotLogs {
	private Long id;	
	private Date date_val;
	private String entity;
	private String environment;
	private String message_val;
	private Double number_val;
    private String probe_id;
    private Long process_id;
    private String server_ip;
    private Long timestamp;
    private String transaction_id;
    private String unknown;
    private String user_id;
	public void setId(Long id) {
		this.id = (id == null) ? 0 : id;
	}
	public void setDate_val(Date date_val) {
		this.date_val = date_val;
	}
	public void setEntity(String entity) {
		this.entity = (entity == null) ? "" : entity;
	}
	public void setEnvironment(String environment) {
		this.environment = (environment == null) ? "" : environment;
	}
	public void setMessage_val(String message_val) {
		this.message_val = (message_val == null) ? "" : message_val;
	}
	public void setNumber_val(Double number_val) {
		this.number_val = (number_val == null) ? 0 : number_val;
	}
	public void setProbe_id(String probe_id) {
		this.probe_id = (probe_id == null) ? "" : probe_id;
	}
	public void setProcess_id(Long process_id) {
		this.process_id = (process_id == null) ? 0 : process_id;
	}
	public void setServer_ip(String server_ip) {
		this.server_ip = (server_ip == null) ? "" : server_ip;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = (timestamp == null) ? 0 : timestamp;
	}
	public void setTransaction_id(String transaction_id) {
		this.transaction_id = (transaction_id == null) ? "" : transaction_id;
	}
	public void setUnknown(String unknown) {
		this.unknown = (unknown == null) ? "" : unknown;
	}
	public void setUser_id(String user_id) {
		this.user_id = (user_id == null) ? "" : user_id;
	}
	public long getId() {
		return id;
	}
	public Date getDate_val() {
		return date_val;
	}
	public String getEntity() {
		return entity;
	}
	public String getEnvironment() {
		return environment;
	}
	public String getMessage_val() {
		return message_val;
	}
	public Double getNumber_val() {
		return number_val;
	}
	public String getProbe_id() {
		return probe_id;
	}
	public Long getProcess_id() {
		return process_id;
	}
	public String getServer_ip() {
		return server_ip;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public String getTransaction_id() {
		return transaction_id;
	}
	public String getUnknown() {
		return unknown;
	}
	public String getUser_id() {
		return user_id;
	}
    
    
}
