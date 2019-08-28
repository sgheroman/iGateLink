package org.cloudbus.foggatewaylib.aneka;

import androidx.annotation.Nullable;

import org.cloudbus.foggatewaylib.aneka.wsdl.AbortApplication;
import org.cloudbus.foggatewaylib.aneka.wsdl.AbortApplicationResponse;
import org.cloudbus.foggatewaylib.aneka.wsdl.AbortJob;
import org.cloudbus.foggatewaylib.aneka.wsdl.AbortJobResponse;
import org.cloudbus.foggatewaylib.aneka.wsdl.ApplicationAbortRequest;
import org.cloudbus.foggatewaylib.aneka.wsdl.ApplicationQueryRequest;
import org.cloudbus.foggatewaylib.aneka.wsdl.ApplicationResult;
import org.cloudbus.foggatewaylib.aneka.wsdl.ApplicationStatus;
import org.cloudbus.foggatewaylib.aneka.wsdl.ApplicationSubmissionRequest;
import org.cloudbus.foggatewaylib.aneka.wsdl.ArrayOfFile;
import org.cloudbus.foggatewaylib.aneka.wsdl.ArrayOfJob;
import org.cloudbus.foggatewaylib.aneka.wsdl.ArrayOfPropertyGroup;
import org.cloudbus.foggatewaylib.aneka.wsdl.AuthenticateUser;
import org.cloudbus.foggatewaylib.aneka.wsdl.AuthenticateUserResponse;
import org.cloudbus.foggatewaylib.aneka.wsdl.CreateApplication;
import org.cloudbus.foggatewaylib.aneka.wsdl.CreateApplicationResponse;
import org.cloudbus.foggatewaylib.aneka.wsdl.Job;
import org.cloudbus.foggatewaylib.aneka.wsdl.JobAbortRequest;
import org.cloudbus.foggatewaylib.aneka.wsdl.JobQueryRequest;
import org.cloudbus.foggatewaylib.aneka.wsdl.JobResult;
import org.cloudbus.foggatewaylib.aneka.wsdl.JobStatus;
import org.cloudbus.foggatewaylib.aneka.wsdl.JobSubmissionRequest;
import org.cloudbus.foggatewaylib.aneka.wsdl.PropertyGroup;
import org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplication;
import org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationResponse;
import org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationStatus;
import org.cloudbus.foggatewaylib.aneka.wsdl.QueryApplicationStatusResponse;
import org.cloudbus.foggatewaylib.aneka.wsdl.QueryJob;
import org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobResponse;
import org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobStatus;
import org.cloudbus.foggatewaylib.aneka.wsdl.QueryJobStatusResponse;
import org.cloudbus.foggatewaylib.aneka.wsdl.Result;
import org.cloudbus.foggatewaylib.aneka.wsdl.SubmitJobs;
import org.cloudbus.foggatewaylib.aneka.wsdl.SubmitJobsResponse;
import org.cloudbus.foggatewaylib.aneka.wsdl.TaskService;
import org.cloudbus.foggatewaylib.aneka.wsdl.UserCredential;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;

public class AnekaWebServices {
    private String url;
    private TaskService service;
    private UserCredential mUserCredential;
    private String error;

    private int timeout = 5000;
    private int jobTimeout = 60000;
    private int pollingPeriod = 500;

    public AnekaWebServices(String url){
        this(url, false);
    }

    public AnekaWebServices(String url, boolean debug){
        this.url = url;
        service = new TaskService(url, 2, debug);
    }

    public boolean authenticateUser(String username, String password){
        AuthenticateUserResponse response;

        AuthenticateUser authenticateUserParams = new AuthenticateUser();
        authenticateUserParams.setUsername(username);
        authenticateUserParams.setPassword(password);

        try {
             response = service.authenticateUser(authenticateUserParams);
             mUserCredential = response.getAuthenticateUserResult().getUserCredential();
             dumpError(response.getAuthenticateUserResult());
             return response.getAuthenticateUserResult().isSuccess();
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return false;
        }
    }

    @Nullable
    public String createApplication(String name, ArrayOfFile sharedFiles,
                                    StorageBucket... storageBuckets){
        CreateApplicationResponse response;

        CreateApplication createApplicationParams = new CreateApplication();
        ApplicationSubmissionRequest applicationSubmissionRequest = new ApplicationSubmissionRequest();
        applicationSubmissionRequest.setDisplayName(name);
        applicationSubmissionRequest.setSharedFiles(sharedFiles);
        applicationSubmissionRequest.setUserCredential(mUserCredential);
        createApplicationParams.setRequest(applicationSubmissionRequest);

        if (storageBuckets.length > 0){
            PropertyGroup metadata = new PropertyGroup();
            metadata.setNameProperty("Metadata");
            ArrayOfPropertyGroup metadataArray = new ArrayOfPropertyGroup();

            PropertyGroup buckets = new PropertyGroup();
            buckets.setNameProperty("StorageBuckets");
            PropertyGroup[] bucketGroups = new PropertyGroup[storageBuckets.length];

            for (int i = 0; i< storageBuckets.length; i++){
                bucketGroups[i] = storageBuckets[i].asPropertyGroup();
            }
            ArrayOfPropertyGroup arrayOfPropertyGroup = new ArrayOfPropertyGroup();
            arrayOfPropertyGroup.setPropertyGroup(bucketGroups);
            buckets.setGroups(arrayOfPropertyGroup);

            metadataArray.setPropertyGroup(new PropertyGroup[]{buckets});
            metadata.setGroups(metadataArray);

            applicationSubmissionRequest.setMetadata(metadata);
        }

        try {
             response = service.createApplication(createApplicationParams);
             dumpError(response.getCreateApplicationResult());
             if (response.getCreateApplicationResult().isSuccess())
                 return response.getCreateApplicationResult().getApplicationId();
             else
                 return null;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    @Nullable
    public ApplicationResult queryApplication(String applicationId){
        QueryApplicationResponse response;

        QueryApplication queryApplicationParams = new QueryApplication();
        ApplicationQueryRequest applicationQueryRequest = new ApplicationQueryRequest();
        applicationQueryRequest.setApplicationId(applicationId);
        applicationQueryRequest.setUserCredential(mUserCredential);
        queryApplicationParams.setRequest(applicationQueryRequest);

        try {
             response = service.queryApplication(queryApplicationParams);
            dumpError(response.getQueryApplicationResult());
             return response.getQueryApplicationResult();
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    @Nullable
    public String queryApplicationStatus(String applicationId){
        QueryApplicationStatusResponse response;

        QueryApplicationStatus queryApplicationStatusParams = new QueryApplicationStatus();
        ApplicationQueryRequest applicationQueryRequest = new ApplicationQueryRequest();
        applicationQueryRequest.setApplicationId(applicationId);
        applicationQueryRequest.setUserCredential(mUserCredential);
        queryApplicationStatusParams.setRequest(applicationQueryRequest);

        try {
            response = service.queryApplicationStatus(queryApplicationStatusParams);
            dumpError(response.getQueryApplicationStatusResult());
            if(response.getQueryApplicationStatusResult().getStatus() != null)
                return response.getQueryApplicationStatusResult().getStatus().getValue();
             else
                 return null;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    @Nullable
    public boolean abortApplication(String applicationId){
        AbortApplicationResponse response;

        AbortApplication abortApplicationParams = new AbortApplication();
        ApplicationAbortRequest applicationAbortRequest = new ApplicationAbortRequest();
        applicationAbortRequest.setApplicationId(applicationId);
        applicationAbortRequest.setUserCredential(mUserCredential);
        abortApplicationParams.setRequest(applicationAbortRequest);

        try {
            response = service.abortApplication(abortApplicationParams);
            dumpError(response.getAbortApplicationResult());
            return response.getAbortApplicationResult().isSuccess();
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return false;
        }
    }

    public boolean waitApplicationCreation(String applicationId){
        long stopTime = new Date().getTime() + timeout;
        while(true){
            String status = queryApplicationStatus(applicationId);
            if (status == null)
                status = "NULL";

            switch (status){
                case ApplicationStatus.STATUS_SUBMITTED:
                case ApplicationStatus.STATUS_RUNNING:
                    return true;

                case "NULL":
                case ApplicationStatus.STATUS_UNSUBMITTED:
                    if (timeout > 0 && new Date(stopTime).before(new Date())){
                        error = "Timeout";
                        return false;
                    } else
                        break;

                case ApplicationStatus.STATUS_FINISHED:
                case ApplicationStatus.STATUS_PAUSED:
                case ApplicationStatus.STATUS_ERROR:
                case ApplicationStatus.STATUS_UNKNOWN:
                default:
                    return false;
            }

            if (pollingPeriod > 0){
                try {
                    Thread.sleep(pollingPeriod);
                } catch (InterruptedException e){
                    e.printStackTrace();
                    dumpError(e);
                    return false;
                }
            }
        }
    }

    @Nullable
    public String createApplicationWait(String name, ArrayOfFile sharedFiles,
                                        StorageBucket... storageBuckets){
        String id = createApplication(name, sharedFiles, storageBuckets);

        if (id == null)
            return null;

        if (waitApplicationCreation(id)){
            return id;
        } else {
            return null;
        }
    }

    @Nullable
    public String createApplicationWait(String name, StorageBucket... storageBuckets){
        return createApplicationWait(name, null, storageBuckets);
    }

    @Nullable
    private String[] __submitJobs(String applicationId, Job... jobs)
            throws IOException, XmlPullParserException{
        if (jobs.length == 0)
            return null;

        SubmitJobsResponse response;

        SubmitJobs submitJobsParams = new SubmitJobs();
        JobSubmissionRequest jobSubmissionRequest = new JobSubmissionRequest();
        jobSubmissionRequest.setApplicationId(applicationId);
        jobSubmissionRequest.setUserCredential(mUserCredential);
        ArrayOfJob arrayOfJob = new ArrayOfJob();
        arrayOfJob.setJob(jobs);
        jobSubmissionRequest.setJobs(arrayOfJob);
        submitJobsParams.setRequest(jobSubmissionRequest);

        response = service.submitJobs(submitJobsParams);
        dumpError(response.getSubmitJobsResult());
        if(response.getSubmitJobsResult().isSuccess())
            return response.getSubmitJobsResult().getIds().getString();
        else
            return null;
    }

    @Nullable
    public String[] submitJobs(String applicationId, Job... jobs) {

        try{
            return __submitJobs(applicationId, jobs);
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    @Nullable
    public String[] submitJobsWait(String applicationId, Job... jobs) {
        long stopTime = new Date().getTime() + timeout;
        try{
            String [] result;
            while ((result = __submitJobs(applicationId, jobs)) == null) {
                if (timeout > 0 && new Date(stopTime).before(new Date())){
                    error = "Timeout";
                    return null;
                }
                if (pollingPeriod > 0){
                    try {
                        Thread.sleep(pollingPeriod);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                        dumpError(e);
                        return null;
                    }
                }
            }
            return result;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    @Nullable
    public JobResult queryJob(String applicationId, String jobId){
        QueryJobResponse response;

        QueryJob queryJobParams = new QueryJob();
        JobQueryRequest jobQueryRequest = new JobQueryRequest();
        jobQueryRequest.setJobId(jobId);
        jobQueryRequest.setApplicationId(applicationId);
        jobQueryRequest.setUserCredential(mUserCredential);
        queryJobParams.setRequest(jobQueryRequest);

        try {
            response = service.queryJob(queryJobParams);
            dumpError(response.getQueryJobResult());
            if(response.getQueryJobResult().isSuccess())
                return response.getQueryJobResult();
            else
                return null;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    @Nullable
    public String queryJobStatus(String applicationId, String jobId){
        QueryJobStatusResponse response;

        QueryJobStatus queryJobStatusParams = new QueryJobStatus();
        JobQueryRequest jobQueryRequest = new JobQueryRequest();
        jobQueryRequest.setJobId(jobId);
        jobQueryRequest.setApplicationId(applicationId);
        jobQueryRequest.setUserCredential(mUserCredential);
        queryJobStatusParams.setRequest(jobQueryRequest);

        try {
            response = service.queryJobStatus(queryJobStatusParams);
            dumpError(response.getQueryJobStatusResult());
            if(response.getQueryJobStatusResult().getStatus() != null)
                return response.getQueryJobStatusResult().getStatus().getValue();
            else
                return null;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    public boolean abortJob(String applicationId, String jobId){
        AbortJobResponse response;

        AbortJob abortJobParams = new AbortJob();
        JobAbortRequest abortRequest = new JobAbortRequest();
        abortRequest.setJobId(jobId);
        abortRequest.setApplicationId(applicationId);
        abortRequest.setUserCredential(mUserCredential);

        try {
            response = service.abortJob(abortJobParams);
            dumpError(response.getAbortJobResult());
            return response.getAbortJobResult().isSuccess();
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return false;
        }
    }

    @Nullable
    public String submitJob(String applicationId, Job job){
        String[] ids = submitJobs(applicationId, job);
        if (ids != null && ids.length > 0)
            return ids[0];
        else
            return null;
    }

    @Nullable
    public String submitJobWait(String applicationId, Job job){
        String[] ids = submitJobsWait(applicationId, job);
        if (ids != null && ids.length > 0)
            return ids[0];
        else
            return null;
    }


    public String waitJobTermination(String applicationId, String jobId){
        long stopTime = new Date().getTime() + jobTimeout;
        while(true){
            String status = queryJobStatus(applicationId, jobId);
            if (status == null)
                status = "NULL";

            switch (status){
                case JobStatus.STATUS_QUEUED:
                case JobStatus.STATUS_RUNNING:
                case JobStatus.STATUS_STAGINGIN:
                case JobStatus.STATUS_STAGINGOUT:
                    if (jobTimeout > 0 && new Date(stopTime).before(new Date())){
                        return "Timeout";
                    } else
                        break;

                case JobStatus.STATUS_COMPLETED:
                case JobStatus.STATUS_ABORTED:
                case JobStatus.STATUS_FAILED:
                case JobStatus.STATUS_REJECTED:
                case JobStatus.STATUS_UNKNOWN:
                default:
                    return status;

            }

            if (pollingPeriod > 0){
                try {
                    Thread.sleep(pollingPeriod);
                } catch (InterruptedException e){
                    e.printStackTrace();
                    dumpError(e);
                    return "ERROR";
                }
            }
        }
    }

    private void dumpError(Result result){
        if (result != null && result.getError() != null){
            error = result.getError().getMessage();
        } else{
            error = null;
        }
    }

    private void dumpError(Exception e){
        error = e.getMessage();
    }

    public String getError() {
        return error;
    }
}
