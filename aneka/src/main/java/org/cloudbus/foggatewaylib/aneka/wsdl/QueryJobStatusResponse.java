package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class QueryJobStatusResponse extends SoapObject implements Deserializable {

	/** Optional property */
	private org.cloudbus.foggatewaylib.aneka.wsdl.JobStatusResult queryJobStatusResult;

	public QueryJobStatusResponse() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "QueryJobStatusResponse");
	}

	protected QueryJobStatusResponse(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(QueryJobStatusResponse object, AttributeContainer response) {
		Object queryJobStatusResultValue = KSoap2Utils.getProperty((SoapObject) response, "QueryJobStatusResult");
		object.setQueryJobStatusResult(queryJobStatusResultValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.JobStatusResult) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.JobStatusResult(), (AttributeContainer) queryJobStatusResultValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return queryJobStatusResult;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "QueryJobStatusResult";
			info.type = org.cloudbus.foggatewaylib.aneka.wsdl.JobStatusResult.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public org.cloudbus.foggatewaylib.aneka.wsdl.JobStatusResult getQueryJobStatusResult() {
		return queryJobStatusResult;
	}

	public void setQueryJobStatusResult(org.cloudbus.foggatewaylib.aneka.wsdl.JobStatusResult newValue) {
		queryJobStatusResult = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("QueryJobStatusResponse [");
		sb.append("queryJobStatusResult=").append(queryJobStatusResult);
		return sb.append(']').toString();
	}
}
