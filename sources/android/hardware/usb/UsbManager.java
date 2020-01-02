package android.hardware.usb;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class UsbManager {
    public static final String ACTION_USB_ACCESSORY_ATTACHED = "android.hardware.usb.action.USB_ACCESSORY_ATTACHED";
    public static final String ACTION_USB_ACCESSORY_DETACHED = "android.hardware.usb.action.USB_ACCESSORY_DETACHED";
    public static final String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    @SystemApi
    public static final String ACTION_USB_PORT_CHANGED = "android.hardware.usb.action.USB_PORT_CHANGED";
    @UnsupportedAppUsage
    public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
    public static final String EXTRA_ACCESSORY = "accessory";
    public static final String EXTRA_CAN_BE_DEFAULT = "android.hardware.usb.extra.CAN_BE_DEFAULT";
    public static final String EXTRA_DEVICE = "device";
    public static final String EXTRA_PACKAGE = "android.hardware.usb.extra.PACKAGE";
    public static final String EXTRA_PERMISSION_GRANTED = "permission";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_PORT_STATUS = "portStatus";
    public static final long FUNCTION_ACCESSORY = 2;
    public static final long FUNCTION_ADB = 1;
    public static final long FUNCTION_AUDIO_SOURCE = 64;
    public static final long FUNCTION_MIDI = 8;
    public static final long FUNCTION_MTP = 4;
    private static final Map<String, Long> FUNCTION_NAME_TO_CODE = new HashMap();
    public static final long FUNCTION_NONE = 0;
    public static final long FUNCTION_PTP = 16;
    public static final long FUNCTION_RNDIS = 32;
    private static final long SETTABLE_FUNCTIONS = 60;
    private static final String TAG = "UsbManager";
    public static final String USB_CONFIGURED = "configured";
    @UnsupportedAppUsage
    public static final String USB_CONNECTED = "connected";
    @UnsupportedAppUsage
    public static final String USB_DATA_UNLOCKED = "unlocked";
    public static final String USB_FUNCTION_ACCESSORY = "accessory";
    public static final String USB_FUNCTION_ADB = "adb";
    public static final String USB_FUNCTION_AUDIO_SOURCE = "audio_source";
    public static final String USB_FUNCTION_MIDI = "midi";
    public static final String USB_FUNCTION_MTP = "mtp";
    @UnsupportedAppUsage
    public static final String USB_FUNCTION_NONE = "none";
    public static final String USB_FUNCTION_PTP = "ptp";
    public static final String USB_FUNCTION_RNDIS = "rndis";
    public static final String USB_HOST_CONNECTED = "host_connected";
    private final Context mContext;
    private final IUsbManager mService;

    static {
        FUNCTION_NAME_TO_CODE.put(USB_FUNCTION_MTP, Long.valueOf(4));
        FUNCTION_NAME_TO_CODE.put(USB_FUNCTION_PTP, Long.valueOf(16));
        FUNCTION_NAME_TO_CODE.put(USB_FUNCTION_RNDIS, Long.valueOf(32));
        FUNCTION_NAME_TO_CODE.put("midi", Long.valueOf(8));
        FUNCTION_NAME_TO_CODE.put("accessory", Long.valueOf(2));
        FUNCTION_NAME_TO_CODE.put(USB_FUNCTION_AUDIO_SOURCE, Long.valueOf(64));
        FUNCTION_NAME_TO_CODE.put("adb", Long.valueOf(1));
    }

    @UnsupportedAppUsage(maxTargetSdk = 28, trackingBug = 115609023)
    public UsbManager(Context context, IUsbManager service) {
        this.mContext = context;
        this.mService = service;
    }

    public HashMap<String, UsbDevice> getDeviceList() {
        HashMap<String, UsbDevice> result = new HashMap();
        if (this.mService == null) {
            return result;
        }
        Bundle bundle = new Bundle();
        try {
            this.mService.getDeviceList(bundle);
            for (String name : bundle.keySet()) {
                result.put(name, (UsbDevice) bundle.get(name));
            }
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public UsbDeviceConnection openDevice(UsbDevice device) {
        try {
            String deviceName = device.getDeviceName();
            ParcelFileDescriptor pfd = this.mService.openDevice(deviceName, this.mContext.getPackageName());
            if (pfd != null) {
                UsbDeviceConnection connection = new UsbDeviceConnection(device);
                boolean result = connection.open(deviceName, pfd, this.mContext);
                pfd.close();
                if (result) {
                    return connection;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "exception in UsbManager.openDevice", e);
        }
        return null;
    }

    public UsbAccessory[] getAccessoryList() {
        UsbAccessory accessory = this.mService;
        if (accessory == null) {
            return null;
        }
        try {
            if (accessory.getCurrentAccessory() == null) {
                return null;
            }
            return new UsbAccessory[]{accessory.getCurrentAccessory()};
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ParcelFileDescriptor openAccessory(UsbAccessory accessory) {
        try {
            return this.mService.openAccessory(accessory);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ParcelFileDescriptor getControlFd(long function) {
        try {
            return this.mService.getControlFd(function);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean hasPermission(UsbDevice device) {
        IUsbManager iUsbManager = this.mService;
        if (iUsbManager == null) {
            return false;
        }
        try {
            return iUsbManager.hasDevicePermission(device, this.mContext.getPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean hasPermission(UsbAccessory accessory) {
        IUsbManager iUsbManager = this.mService;
        if (iUsbManager == null) {
            return false;
        }
        try {
            return iUsbManager.hasAccessoryPermission(accessory);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void requestPermission(UsbDevice device, PendingIntent pi) {
        try {
            this.mService.requestDevicePermission(device, this.mContext.getPackageName(), pi);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void requestPermission(UsbAccessory accessory, PendingIntent pi) {
        try {
            this.mService.requestAccessoryPermission(accessory, this.mContext.getPackageName(), pi);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void grantPermission(UsbDevice device) {
        grantPermission(device, Process.myUid());
    }

    public void grantPermission(UsbDevice device, int uid) {
        try {
            this.mService.grantDevicePermission(device, uid);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void grantPermission(UsbDevice device, String packageName) {
        try {
            grantPermission(device, this.mContext.getPackageManager().getPackageUidAsUser(packageName, this.mContext.getUserId()));
        } catch (NameNotFoundException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Package ");
            stringBuilder.append(packageName);
            stringBuilder.append(" not found.");
            Log.e(TAG, stringBuilder.toString(), e);
        }
    }

    @Deprecated
    @UnsupportedAppUsage
    public boolean isFunctionEnabled(String function) {
        try {
            return this.mService.isFunctionEnabled(function);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setCurrentFunctions(long functions) {
        try {
            this.mService.setCurrentFunctions(functions);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    @UnsupportedAppUsage
    public void setCurrentFunction(String functions, boolean usbDataUnlocked) {
        try {
            this.mService.setCurrentFunction(functions, usbDataUnlocked);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public long getCurrentFunctions() {
        try {
            return this.mService.getCurrentFunctions();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setScreenUnlockedFunctions(long functions) {
        try {
            this.mService.setScreenUnlockedFunctions(functions);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public long getScreenUnlockedFunctions() {
        try {
            return this.mService.getScreenUnlockedFunctions();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public List<UsbPort> getPorts() {
        List<ParcelableUsbPort> parcelablePorts = this.mService;
        if (parcelablePorts == null) {
            return Collections.emptyList();
        }
        try {
            parcelablePorts = parcelablePorts.getPorts();
            if (parcelablePorts == null) {
                return Collections.emptyList();
            }
            int numPorts = parcelablePorts.size();
            ArrayList<UsbPort> ports = new ArrayList(numPorts);
            for (int i = 0; i < numPorts; i++) {
                ports.add(((ParcelableUsbPort) parcelablePorts.get(i)).getUsbPort(this));
            }
            return ports;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public UsbPortStatus getPortStatus(UsbPort port) {
        try {
            return this.mService.getPortStatus(port.getId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void setPortRoles(UsbPort port, int powerRole, int dataRole) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setPortRoles Package:");
        stringBuilder.append(this.mContext.getPackageName());
        Log.d(TAG, stringBuilder.toString());
        try {
            this.mService.setPortRoles(port.getId(), powerRole, dataRole);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void enableContaminantDetection(UsbPort port, boolean enable) {
        try {
            this.mService.enableContaminantDetection(port.getId(), enable);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setUsbDeviceConnectionHandler(ComponentName usbDeviceConnectionHandler) {
        try {
            this.mService.setUsbDeviceConnectionHandler(usbDeviceConnectionHandler);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static boolean areSettableFunctions(long functions) {
        if (functions == 0) {
            return true;
        }
        if ((-61 & functions) == 0 && Long.bitCount(functions) == 1) {
            return true;
        }
        return false;
    }

    public static String usbFunctionsToString(long functions) {
        StringJoiner joiner = new StringJoiner(",");
        if ((4 & functions) != 0) {
            joiner.add(USB_FUNCTION_MTP);
        }
        if ((16 & functions) != 0) {
            joiner.add(USB_FUNCTION_PTP);
        }
        if ((32 & functions) != 0) {
            joiner.add(USB_FUNCTION_RNDIS);
        }
        if ((8 & functions) != 0) {
            joiner.add("midi");
        }
        if ((2 & functions) != 0) {
            joiner.add("accessory");
        }
        if ((64 & functions) != 0) {
            joiner.add(USB_FUNCTION_AUDIO_SOURCE);
        }
        if ((1 & functions) != 0) {
            joiner.add("adb");
        }
        return joiner.toString();
    }

    public static long usbFunctionsFromString(String functions) {
        if (functions == null || functions.equals("none")) {
            return 0;
        }
        long ret = 0;
        for (String function : functions.split(",")) {
            if (FUNCTION_NAME_TO_CODE.containsKey(function)) {
                ret |= ((Long) FUNCTION_NAME_TO_CODE.get(function)).longValue();
            } else if (function.length() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid usb function ");
                stringBuilder.append(functions);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        return ret;
    }
}
