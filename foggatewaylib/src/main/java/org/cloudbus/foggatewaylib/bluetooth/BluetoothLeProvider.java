package org.cloudbus.foggatewaylib.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.os.Build;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import org.cloudbus.foggatewaylib.Data;
import org.cloudbus.foggatewaylib.Provider;

import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BluetoothLeProvider<T extends Data, S extends Data> extends Provider<T, S> {

    private BluetoothLeHandler bluetoothLeHandler;

    private UUID service;
    private UUID characteristic;
    private BluetoothGatt gatt;

    private static int attachedBluetoothProviders = 0;

    /**
     * @param inputType  type of the input data.
     * @param outputType type of the output data.
     */
    public BluetoothLeProvider(BluetoothGatt gatt, UUID service, UUID characteristic,
                               Class<T> inputType, Class<S> outputType) {
        super(inputType, outputType);
        this.service = service;
        this.characteristic = characteristic;
        this.gatt = gatt;
    }

    public BluetoothLeHandler getBluetoothLeHandler() {
        if (bluetoothLeHandler == null)
            bluetoothLeHandler = BluetoothLeHandler.getInstance();
        return bluetoothLeHandler;
    }

    public UUID getService() {
        return service;
    }

    public UUID getCharacteristic() {
        return characteristic;
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }

    private static synchronized int incrementAttachedBluetoothProviders(){
        attachedBluetoothProviders++;
        return attachedBluetoothProviders;
    }

    private static synchronized int decrementAttachedBluetoothProviders(){
        attachedBluetoothProviders--;
        return attachedBluetoothProviders;
    }

    @Override
    public void onAttach() {
        if (incrementAttachedBluetoothProviders() == 1)
            getBluetoothLeHandler().setContext(getExecutionManager().getContext());
    }

    @Override
    public void onDetach() {
        if (decrementAttachedBluetoothProviders() == 0)
            getBluetoothLeHandler().setContext(null);
    }

    public Pair<UUID, UUID> getHandledCharacteristic(){
        return new Pair<>(service, characteristic);
    }
}
