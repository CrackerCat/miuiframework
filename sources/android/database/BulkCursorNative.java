package android.database;

import android.database.IContentObserver.Stub;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

public abstract class BulkCursorNative extends Binder implements IBulkCursor {
    public BulkCursorNative() {
        attachInterface(this, IBulkCursor.descriptor);
    }

    public static IBulkCursor asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IBulkCursor in = (IBulkCursor) obj.queryLocalInterface(IBulkCursor.descriptor);
        if (in != null) {
            return in;
        }
        return new BulkCursorProxy(obj);
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        String str = IBulkCursor.descriptor;
        switch (code) {
            case 1:
                data.enforceInterface(str);
                CursorWindow window = getWindow(data.readInt());
                reply.writeNoException();
                if (window == null) {
                    reply.writeInt(0);
                } else {
                    reply.writeInt(1);
                    window.writeToParcel(reply, 1);
                }
                return true;
            case 2:
                data.enforceInterface(str);
                deactivate();
                reply.writeNoException();
                return true;
            case 3:
                data.enforceInterface(str);
                int count = requery(Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                reply.writeInt(count);
                reply.writeBundle(getExtras());
                return true;
            case 4:
                data.enforceInterface(str);
                onMove(data.readInt());
                reply.writeNoException();
                return true;
            case 5:
                data.enforceInterface(str);
                Bundle extras = getExtras();
                reply.writeNoException();
                reply.writeBundle(extras);
                return true;
            case 6:
                data.enforceInterface(str);
                Bundle returnExtras = respond(data.readBundle());
                reply.writeNoException();
                reply.writeBundle(returnExtras);
                return true;
            case 7:
                try {
                    data.enforceInterface(str);
                    close();
                    reply.writeNoException();
                    return true;
                } catch (Exception e) {
                    DatabaseUtils.writeExceptionToParcel(reply, e);
                    return true;
                }
            default:
                return super.onTransact(code, data, reply, flags);
        }
    }

    public IBinder asBinder() {
        return this;
    }
}
