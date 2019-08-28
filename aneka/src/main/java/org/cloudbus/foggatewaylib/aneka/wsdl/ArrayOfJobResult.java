package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class ArrayOfJobResult extends SoapObject implements Deserializable {

	/** Optional property */
	private org.cloudbus.foggatewaylib.aneka.wsdl.JobResult[] jobResult;

	public ArrayOfJobResult() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ArrayOfJobResult");
	}

	protected ArrayOfJobResult(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(ArrayOfJobResult object, AttributeContainer response) {
		java.util.List jobResultList = KSoap2Utils.getObjectList((SoapObject) response, "JobResult");
		org.cloudbus.foggatewaylib.aneka.wsdl.JobResult[] jobResultArray = new org.cloudbus.foggatewaylib.aneka.wsdl.JobResult[jobResultList.size()];
		for (int i = 0; i < jobResultArray.length; ++i) {
			jobResultArray[i] = (org.cloudbus.foggatewaylib.aneka.wsdl.JobResult) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.JobResult(), (AttributeContainer) jobResultList.get(i));
		}
		object.setJobResult(jobResultArray);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return jobResult;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "JobResult";
			info.type = org.cloudbus.foggatewaylib.aneka.wsdl.JobResult[].class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public org.cloudbus.foggatewaylib.aneka.wsdl.JobResult[] getJobResult() {
		return jobResult;
	}

	public void setJobResult(org.cloudbus.foggatewaylib.aneka.wsdl.JobResult[] newValue) {
		jobResult = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("ArrayOfJobResult [");
		sb.append("jobResult=").append(java.util.Arrays.toString(jobResult));
		return sb.append(']').toString();
	}
}
