package com.sugarcrm.www.sugarcrm.rest.v4.api;

import java.util.Collection;

/**
 * Generic Bean API (mostly for accessing custom fields not defined within an existing API)
 * 
 * @author mmarum
 *
 */

public interface ISugarBean {
  String getId();
  String get(String fieldName);
  Collection<String> getFieldNames();
  String getModuleName();
}