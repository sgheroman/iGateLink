package org.cloudbus.foggatewaylib;

import android.util.Log;

public abstract class ProviderChooser {
    public static final String TAG = "ProviderChooser";

    private ExecutionManager executionManager;

    public ProviderChooser(){ }

    public abstract String chooseProvider(String... providers);

    public void onAttach(){}
    public void onDetach(){}

    public void attach(ExecutionManager executionManager){
        this.executionManager = executionManager;
        if (executionManager == null){
            Log.e(TAG, "executionManager is null");
            return;
        }
        onAttach();
    }

    public void detach(){
        onDetach();
        this.executionManager = null;
    }

    protected ExecutionManager getExecutionManager(){
        if (this.executionManager == null)
            Log.e(TAG, "ExecutionManager is not bound");
        return this.executionManager;
    }
}
