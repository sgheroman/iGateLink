package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.Deserializable;
import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class QueryApplicationStatusResponse extends SoapObject implements Deserializable {

	/** Optional property */
	private org.cloudbus.foggatewaylib.aneka.wsdl.ApplicationStatusResult queryApplicationStatusResult;

	public QueryApplicationStatusResponse() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "QueryApplicationStatusResponse");
	}

	protected QueryApplicationStatusResponse(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(QueryApplicationStatusResponse object, AttributeContainer response) {
		Object queryApplicationStatusResultValue = KSoap2Utils.getProperty((SoapObject) response, "QueryApplicationStatusResult");
		object.setQueryApplicationStatusResult(queryApplicationStatusResultValue != null ? (org.cloudbus.foggatewaylib.aneka.wsdl.ApplicationStatusResult) KSoap2Utils.getObject(new org.cloudbus.foggatewaylib.aneka.wsdl.ApplicationStatusResult(), (AttributeContainer) queryApplicationStatusResultValue) : null);
	}

	public int getPropertyCount() {
		return 1;
	}

	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return queryApplicationStatusResult;
		default:
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
		case 0:
			info.name = "QueryApplicationStatusResult";
			info.type = org.cloudbus.foggatewaylib.aneka.wsdl.ApplicationStatusResult.class;
			info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
			break;
		default:
		}
	}

	public void setProperty(int index, Object object) {}

	public org.cloudbus.foggatewaylib.aneka.wsdl.ApplicationStatusResult getQueryApplicationStatusResult() {
		return queryApplicationStatusResult;
	}

	public void setQueryApplicationStatusResult(org.cloudbus.foggatewaylib.aneka.wsdl.ApplicationStatusResult newValue) {
		queryApplicationStatusResult = newValue;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("QueryApplicationStatusResponse [");
		sb.append("queryApplicationStatusResult=").append(queryApplicationStatusResult);
		return sb.append(']').toString();
	}
}
