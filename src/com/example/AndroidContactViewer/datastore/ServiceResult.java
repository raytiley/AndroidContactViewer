package com.example.AndroidContactViewer.datastore;

import java.util.List;

public class ServiceResult {
	private String status;
	private String message;
	private Group group;
	private List<ContactDTO> contacts;
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}
	/**
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}
	/**
	 * @return the contact
	 */
	public List<ContactDTO> getContacts() {
		return contacts;
	}
	/**
	 * @param contact the contact to set
	 */
	public void setContacts(List<ContactDTO> contacts) {
		this.contacts = contacts;
	}
}
