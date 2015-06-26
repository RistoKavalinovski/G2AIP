/**
 * Get_entry_result_for_reports.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.sugarcrm.www.sugarcrm;

public class Get_entry_result_for_reports  implements java.io.Serializable {
    private com.sugarcrm.www.sugarcrm.Field_list2[] field_list;

    private com.sugarcrm.www.sugarcrm.Entry_list2[] entry_list;

    public Get_entry_result_for_reports() {
    }

    public Get_entry_result_for_reports(
           com.sugarcrm.www.sugarcrm.Field_list2[] field_list,
           com.sugarcrm.www.sugarcrm.Entry_list2[] entry_list) {
           this.field_list = field_list;
           this.entry_list = entry_list;
    }


    /**
     * Gets the field_list value for this Get_entry_result_for_reports.
     * 
     * @return field_list
     */
    public com.sugarcrm.www.sugarcrm.Field_list2[] getField_list() {
        return field_list;
    }


    /**
     * Sets the field_list value for this Get_entry_result_for_reports.
     * 
     * @param field_list
     */
    public void setField_list(com.sugarcrm.www.sugarcrm.Field_list2[] field_list) {
        this.field_list = field_list;
    }


    /**
     * Gets the entry_list value for this Get_entry_result_for_reports.
     * 
     * @return entry_list
     */
    public com.sugarcrm.www.sugarcrm.Entry_list2[] getEntry_list() {
        return entry_list;
    }


    /**
     * Sets the entry_list value for this Get_entry_result_for_reports.
     * 
     * @param entry_list
     */
    public void setEntry_list(com.sugarcrm.www.sugarcrm.Entry_list2[] entry_list) {
        this.entry_list = entry_list;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Get_entry_result_for_reports)) return false;
        Get_entry_result_for_reports other = (Get_entry_result_for_reports) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.field_list==null && other.getField_list()==null) || 
             (this.field_list!=null &&
              java.util.Arrays.equals(this.field_list, other.getField_list()))) &&
            ((this.entry_list==null && other.getEntry_list()==null) || 
             (this.entry_list!=null &&
              java.util.Arrays.equals(this.entry_list, other.getEntry_list())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getField_list() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getField_list());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getField_list(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getEntry_list() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEntry_list());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEntry_list(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Get_entry_result_for_reports.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "get_entry_result_for_reports"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("field_list");
        elemField.setXmlName(new javax.xml.namespace.QName("", "field_list"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "field_list2"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entry_list");
        elemField.setXmlName(new javax.xml.namespace.QName("", "entry_list"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.sugarcrm.com/sugarcrm", "entry_list2"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
