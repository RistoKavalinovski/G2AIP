package com.sugarcrm.www.sugarcrm.rest.v4.api;

/**
 * Sugar User API
 * @author mmarum
 *
 */
public interface User extends ISugarBean {
  
  public String getUserId();
  public String getUserName();
  public String getUserLanguage();

}