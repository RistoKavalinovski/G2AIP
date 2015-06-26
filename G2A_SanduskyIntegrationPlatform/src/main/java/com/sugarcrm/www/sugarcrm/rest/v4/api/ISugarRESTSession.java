package com.sugarcrm.www.sugarcrm.rest.v4.api;

/**
 * Sugar Session API
 * @author mmarum
 *
 */
public interface ISugarRESTSession {
	
  public String getSessionID();
  public User getUser();

}