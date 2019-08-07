package org.cloudbus.foggatewaylib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FogGatewayService extends ForegroundService{
    public static final String TAG = "FogGatewayService";
    public static final String KEY_PROGRESS = "progressStore";

    private Map<String, Store> dataStores;
    private Map<String, Provider> providers;
    private MultiMap<String, String> providersOfData;
    private Map<String, String> triggers;
    private Map<String, ProviderChooser> choosers;
    private Map<String, String> UITriggers;

    private static long NEXT_REQUEST_ID = 1;

    public FogGatewayService(){
        super();
        dataStores = new HashMap<>();
        providers = new HashMap<>();
        providersOfData = new MultiMap<>();
        triggers = new HashMap<>();
        choosers = new HashMap<>();
        UITriggers = new HashMap<>();

        addDataStore(KEY_PROGRESS, new InMemoryStore<>(ProgressData.class));
    }

    @Override
    protected boolean init(Bundle extras) {
        return true;
    }

    @Override
    protected int execute() {
        return 0;
    }

    public static synchronized long nextRequestID(){
        return NEXT_REQUEST_ID++;
    }

    public FogGatewayService addDataStore(String key, Store store){

        if (dataStores.containsKey(key)){
            Log.w(TAG, "Store " + key + " already exists");
            return this;
        }
        dataStores.put(key, store);

        return this;
    }

    public boolean removeDataStore(String key){
        return dataStores.remove(key) != null;
    }

    @SuppressWarnings("unchecked")
    private FogGatewayService addTriggerGeneric(String dataKey, String triggerKey,
                                                BulkTrigger trigger,
                                                Map<String, String> map){
        if (map.containsKey(triggerKey)){
            Log.w(TAG, "Trigger " + triggerKey + " already exists");
            return this;
        }


        Store store = dataStores.get(dataKey);
        if (store == null)
            throw new StoreNotDefinedException(dataKey);

        if (store.getDataType().equals(trigger.getDataType())){
            trigger.bindService(this);
            store.addObserver(triggerKey, trigger);
            map.put(triggerKey, dataKey);
        } else
            throw new TypeMismatchException(dataKey, store.getDataType(),
                    trigger.getDataType());

        return this;
    }

    public FogGatewayService addTrigger(String dataKey, String triggerKey,
                                        BulkTrigger trigger){

        return addTriggerGeneric(dataKey, triggerKey, trigger, triggers);
    }

    public FogGatewayService addUITrigger(String dataKey, String triggerKey,
                                          BulkTrigger trigger){

        return addTriggerGeneric(dataKey, triggerKey, trigger, UITriggers);
    }

    @SuppressWarnings("unchecked")
    public FogGatewayService addUITrigger(String dataKey, String triggerKey, long requestID,
                                          BulkTrigger trigger){
        return addTriggerGeneric(dataKey, triggerKey,
                new FilteredBulkTrigger(requestID, trigger),
                UITriggers);
    }

    public boolean removeTriggerGeneric(String triggerKey, Map<String, String> map){
        String dataKey = map.get(triggerKey);
        if (dataKey == null)
            return false;

        Store store = dataStores.get(dataKey);
        if (store == null)
            return false;

        map.remove(triggerKey);

        BulkTrigger trigger = (BulkTrigger) store.removeObserver(triggerKey);
        if (trigger == null)
            return false;

        trigger.unbindService();
        return true;
    }

    public boolean removeTrigger(String triggerKey){
        return removeTriggerGeneric(triggerKey, triggers);
    }

    public boolean removeUITrigger(String triggerKey){
        return removeTriggerGeneric(triggerKey, UITriggers);
    }

    private void checkDataStore(String key, @Nullable Class type){
        Store store = dataStores.get(key);
        if (store == null)
            throw new StoreNotDefinedException(key);
        if (type != null && !store.getDataType().equals(type))
                throw new TypeMismatchException(key,
                        store.getDataType(),
                        type);
    }

    @SuppressWarnings("unchecked")
    public FogGatewayService addChooser(String outputKey,
                                        ProviderChooser chooser){
        if (choosers.containsKey(outputKey)){
            Log.w(TAG, "Chooser for data " + outputKey + " already exists");
            return this;
        }

        checkDataStore(outputKey, null);
        choosers.put(outputKey, chooser);
        chooser.attach(this);

        return this;
    }

    public boolean removeChooser(String outputKey){
        ProviderChooser chooser = choosers.get(outputKey);

        if (chooser != null){
            chooser.detach();
            return choosers.remove(outputKey) != null;
        } else
            return false;
    }

    @SuppressWarnings("unchecked")
    public FogGatewayService addProvider(String providerKey, String outputKey,
                                         Provider provider){
        if (providers.containsKey(providerKey)){
            Log.w(TAG, "Provider " + providerKey + " already exists");
            return this;
        }

        checkDataStore(outputKey, provider.getOutputType());

        providers.put(providerKey, provider);
        providersOfData.put(outputKey, providerKey);

        provider.attach(this,
                dataStores.get(outputKey),
                dataStores.get(KEY_PROGRESS));

        return this;
    }

    public boolean removeProvider(String providerKey){
        Provider provider = providers.get(providerKey);

        if (provider != null){
            provider.detach();
            providersOfData.removeValue(providerKey);
            return providers.remove(providerKey) != null;
        } else
            return false;
    }

    @SuppressWarnings("unchecked")
    public void runProvider(String providerKey, long request_id, Data... input){
        Provider provider = providers.get(providerKey);
        if (provider != null){
            if (input.length == 0 || input[0].getClass().equals(provider.getInputType())){
                provider.executeCast(request_id, input);
            } else
                throw new IllegalArgumentException("Expected "
                        + provider.getInputType()
                        + " but "
                        + input[0].getClass()
                        + " was given");
        } else
            throw new ProviderNotDefinedException(providerKey);

    }

    @SuppressWarnings("unchecked")
    public void produceData(String dataKey, long request_id, Data... input){
        List<String> providers = new ArrayList<>(providersOfData.getAll(dataKey));
        if (!providers.isEmpty()){
            if (providers.size() == 1){
                runProvider(providers.get(0), request_id, input);
                return;
            }

            ProviderChooser chooser = choosers.get(dataKey);
            if (chooser == null)
                throw new ChooserNotDefinedException(dataKey);

            runProvider(chooser.chooseProvider(providers.toArray(new String[providers.size()])),
                    request_id, input);
        } else
            throw new ProviderForDataNotDefinedException(dataKey);
    }

    public Store getDataStore(String key){
        return dataStores.get(key);
    }

    public Provider getProvider(String key){
        return providers.get(key);
    }

    public ProviderChooser getChooser(String key){
        return choosers.get(key);
    }

    public class StoreNotDefinedException extends RuntimeException{
        StoreNotDefinedException(String key){
            super("No data store was defined for data with key " + key);
        }
    }

    public class ProviderNotDefinedException extends RuntimeException{
        ProviderNotDefinedException(String key){
            super("No provider was defined for key " + key);
        }
    }

    public class ProviderForDataNotDefinedException extends RuntimeException{
        ProviderForDataNotDefinedException(String key){
            super("No provider produces data with key " + key);
        }
    }

    public class ChooserNotDefinedException extends RuntimeException{
        ChooserNotDefinedException(String key){
            super("Multiple providers for data with key " + key + " but no chooser was defined!");
        }
    }

    public class TypeMismatchException extends RuntimeException{
        TypeMismatchException(String key, Class storeType, Class otherType){
            super("Data store for key " + key + " is of type " + storeType.getName() +
                    " but type " + otherType.getName() + " was given.");
        }
    }

    public static void start(Context context, Class<? extends Activity> cls){
        startForegroundService(context, FogGatewayService.class, cls);
    }

    public static void stop(Context context){
        stopForegroundService(context, FogGatewayService.class);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        for (String trigger:UITriggers.keySet())
            removeUITrigger(trigger);
        return false;
    }
}
