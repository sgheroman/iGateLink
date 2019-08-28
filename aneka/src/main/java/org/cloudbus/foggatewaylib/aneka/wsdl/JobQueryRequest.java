package org.cloudbus.foggatewaylib.aneka.wsdl;

import org.ksoap2.deserialization.KSoap2Utils;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class JobQueryRequest extends org.cloudbus.foggatewaylib.aneka.wsdl.Request {

	/** Optional property */
	private String jobId;

	/** Optional property */
	private String applicationId;

	public JobQueryRequest() {
		super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "JobQueryRequest");
	}

	protected JobQueryRequest(String nsUri, String name) {
		super(nsUri, name);
	}

	public void fromSoapResponse(AttributeContainer response) {
		fromSoapResponse(this, response);
	}

	protected void fromSoapResponse(JobQueryRequest object, AttributeContainer response) {
		super.fromSoapResponse(object, response);
		jobId = KSoap2Utils.getString((SoapObject) response, "JobId");
		applicationId = KSoap2Utils.getString((SoapObject) response, "ApplicationId");
	}

	public int getPropertyCount() {
		return 3;
	}

	public Object getProperty(int index) {
		switch (index) {
			case 1:
				return jobId;
			case 2:
				return applicationId;
			default:
				super.getProperty(index);
		}
		return null;
	}

	public void getPropertyInfo(int index, java.util.Hashtable table, PropertyInfo info) {
		switch (index) {
			case 1:
				info.name = "JobId";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			case 2:
				info.name = "ApplicationId";
				info.type = String.class;
				info.namespace = "http://www.manjrasoft.com/Aneka/v2.0/WebServices";
				break;
			default:
				super.getPropertyInfo(index, table, info);
		}
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public void setProperty(int index, Object object) {
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("ApplicationQueryRequest [");
		sb.append("userCredential=").append(getUserCredential());
		sb.append(", ");
		sb.append("jobId=").append(jobId);
		sb.append(", ");
		sb.append("applicationId=").append(applicationId);
		return sb.append(']').toString();
	}
}
