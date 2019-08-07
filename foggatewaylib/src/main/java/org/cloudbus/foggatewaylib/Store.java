package org.cloudbus.foggatewaylib;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public abstract class Store<T extends Data> {
    private Map<String, StoreObserver<T>> observers;
    Class<T> dataType;

    public Store(@NonNull Class<T> dataType){
        observers = new HashMap<>();
        this.dataType = dataType;
    }

    @NonNull
    public Class<T> getDataType() {
        return dataType;
    }

    public abstract T retrieveLast();
    public abstract T retrieveLast(long requestID);

    public abstract T[] retrieveLastN(int N);
    public abstract T[] retrieveLastN(int N, long requestID);

    public abstract T[] retrieveInterval(long from, long to);
    public abstract T[] retrieveInterval(long from, long to, long requestID);

    protected abstract void __store(T... data);

    public abstract int size();

    protected void notifyObservers(T... data){
        if (data.length == 0)
            return;

        for (StoreObserver<T> observer: observers.values()){
            observer.onDataStored(this, data);
        }
    }

    public void store(T... data){
        __store(data);
        notifyObservers(data);
    }

    public void addObserver(String key, StoreObserver<T> observer){
        observers.put(key, observer);
    }

    public StoreObserver<T> removeObserver(String key){
        return observers.remove(key);
    }

    public T[] retrieveIntervalFrom(long from){
        return retrieveInterval(from, Long.MAX_VALUE);
    }
    public T[] retrieveIntervalFrom(long from, long requestID){
        return retrieveInterval(from, Long.MAX_VALUE, requestID);
    }

}