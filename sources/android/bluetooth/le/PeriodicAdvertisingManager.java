package android.bluetooth.le;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetoothGatt;
import android.bluetooth.IBluetoothManager;
import android.bluetooth.le.IPeriodicAdvertisingCallback.Stub;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import java.util.IdentityHashMap;
import java.util.Map;

public final class PeriodicAdvertisingManager {
    private static final int SKIP_MAX = 499;
    private static final int SKIP_MIN = 0;
    private static final int SYNC_STARTING = -1;
    private static final String TAG = "PeriodicAdvertisingManager";
    private static final int TIMEOUT_MAX = 16384;
    private static final int TIMEOUT_MIN = 10;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final IBluetoothManager mBluetoothManager;
    Map<PeriodicAdvertisingCallback, IPeriodicAdvertisingCallback> mCallbackWrappers = new IdentityHashMap();

    public PeriodicAdvertisingManager(IBluetoothManager bluetoothManager) {
        this.mBluetoothManager = bluetoothManager;
    }

    public void registerSync(ScanResult scanResult, int skip, int timeout, PeriodicAdvertisingCallback callback) {
        registerSync(scanResult, skip, timeout, callback, null);
    }

    public void registerSync(ScanResult scanResult, int skip, int timeout, PeriodicAdvertisingCallback callback, Handler handler) {
        String str = TAG;
        if (callback == null) {
            throw new IllegalArgumentException("callback can't be null");
        } else if (scanResult == null) {
            throw new IllegalArgumentException("scanResult can't be null");
        } else if (scanResult.getAdvertisingSid() != 255) {
            String str2 = "timeout must be between 10 and 16384";
            if (skip < 0 || skip > SKIP_MAX) {
                throw new IllegalArgumentException(str2);
            } else if (timeout < 10 || timeout > 16384) {
                throw new IllegalArgumentException(str2);
            } else {
                try {
                    IBluetoothGatt gatt = this.mBluetoothManager.getBluetoothGatt();
                    if (handler == null) {
                        handler = new Handler(Looper.getMainLooper());
                    }
                    IPeriodicAdvertisingCallback wrapped = wrap(callback, handler);
                    this.mCallbackWrappers.put(callback, wrapped);
                    try {
                        gatt.registerSync(scanResult, skip, timeout, wrapped);
                    } catch (RemoteException e) {
                        Log.e(str, "Failed to register sync - ", e);
                    }
                } catch (RemoteException e2) {
                    Log.e(str, "Failed to get Bluetooth gatt - ", e2);
                    callback.onSyncEstablished(0, scanResult.getDevice(), scanResult.getAdvertisingSid(), skip, timeout, 2);
                }
            }
        } else {
            throw new IllegalArgumentException("scanResult must contain a valid sid");
        }
    }

    public void unregisterSync(PeriodicAdvertisingCallback callback) {
        String str = TAG;
        if (callback != null) {
            try {
                IBluetoothGatt gatt = this.mBluetoothManager.getBluetoothGatt();
                IPeriodicAdvertisingCallback wrapper = (IPeriodicAdvertisingCallback) this.mCallbackWrappers.remove(callback);
                if (wrapper != null) {
                    try {
                        gatt.unregisterSync(wrapper);
                        return;
                    } catch (RemoteException e) {
                        Log.e(str, "Failed to cancel sync creation - ", e);
                        return;
                    }
                }
                throw new IllegalArgumentException("callback was not properly registered");
            } catch (RemoteException e2) {
                Log.e(str, "Failed to get Bluetooth gatt - ", e2);
                return;
            }
        }
        throw new IllegalArgumentException("callback can't be null");
    }

    private IPeriodicAdvertisingCallback wrap(final PeriodicAdvertisingCallback callback, final Handler handler) {
        return new Stub() {
            public void onSyncEstablished(int syncHandle, BluetoothDevice device, int advertisingSid, int skip, int timeout, int status) {
                final int i = syncHandle;
                final BluetoothDevice bluetoothDevice = device;
                final int i2 = advertisingSid;
                final int i3 = skip;
                final int i4 = timeout;
                final int i5 = status;
                handler.post(new Runnable() {
                    public void run() {
                        callback.onSyncEstablished(i, bluetoothDevice, i2, i3, i4, i5);
                        if (i5 != 0) {
                            PeriodicAdvertisingManager.this.mCallbackWrappers.remove(callback);
                        }
                    }
                });
            }

            public void onPeriodicAdvertisingReport(final PeriodicAdvertisingReport report) {
                handler.post(new Runnable() {
                    public void run() {
                        callback.onPeriodicAdvertisingReport(report);
                    }
                });
            }

            public void onSyncLost(final int syncHandle) {
                handler.post(new Runnable() {
                    public void run() {
                        callback.onSyncLost(syncHandle);
                        PeriodicAdvertisingManager.this.mCallbackWrappers.remove(callback);
                    }
                });
            }
        };
    }
}
