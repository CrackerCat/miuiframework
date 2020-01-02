package com.android.framework.protobuf.nano.android;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

public final class ParcelableMessageNanoCreator<T extends MessageNano> implements Creator<T> {
    private static final String TAG = "PMNCreator";
    private final Class<T> mClazz;

    public ParcelableMessageNanoCreator(Class<T> clazz) {
        this.mClazz = clazz;
    }

    public T createFromParcel(Parcel in) {
        String str = "Exception trying to create proto from parcel";
        String str2 = TAG;
        String className = in.readString();
        T proto = null;
        try {
            proto = (MessageNano) Class.forName(className, false, getClass().getClassLoader()).asSubclass(MessageNano.class).getConstructor(new Class[0]).newInstance(new Object[0]);
            MessageNano.mergeFrom(proto, in.createByteArray());
            return proto;
        } catch (ClassNotFoundException e) {
            Log.e(str2, str, e);
            return proto;
        } catch (NoSuchMethodException e2) {
            Log.e(str2, str, e2);
            return proto;
        } catch (InvocationTargetException e3) {
            Log.e(str2, str, e3);
            return proto;
        } catch (IllegalAccessException e4) {
            Log.e(str2, str, e4);
            return proto;
        } catch (InstantiationException e5) {
            Log.e(str2, str, e5);
            return proto;
        } catch (InvalidProtocolBufferNanoException e6) {
            Log.e(str2, str, e6);
            return proto;
        }
    }

    public T[] newArray(int i) {
        return (MessageNano[]) Array.newInstance(this.mClazz, i);
    }

    static <T extends MessageNano> void writeToParcel(Class<T> clazz, MessageNano message, Parcel out) {
        out.writeString(clazz.getName());
        out.writeByteArray(MessageNano.toByteArray(message));
    }
}
