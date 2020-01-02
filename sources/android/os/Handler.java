package android.os;

import android.annotation.UnsupportedAppUsage;
import android.os.IMessenger.Stub;
import android.util.Log;
import android.util.Printer;

public class Handler {
    private static final boolean FIND_POTENTIAL_LEAKS = false;
    private static Handler MAIN_THREAD_HANDLER = null;
    private static final String TAG = "Handler";
    final boolean mAsynchronous;
    @UnsupportedAppUsage
    final Callback mCallback;
    @UnsupportedAppUsage
    final Looper mLooper;
    @UnsupportedAppUsage
    IMessenger mMessenger;
    final MessageQueue mQueue;

    public interface Callback {
        boolean handleMessage(Message message);
    }

    private static final class BlockingRunnable implements Runnable {
        private boolean mDone;
        private final Runnable mTask;

        public BlockingRunnable(Runnable task) {
            this.mTask = task;
        }

        public void run() {
            try {
                this.mTask.run();
                synchronized (this) {
                    this.mDone = true;
                    notifyAll();
                }
            } catch (Throwable th) {
                synchronized (this) {
                    this.mDone = true;
                    notifyAll();
                }
            }
        }

        public boolean postAndWait(Handler handler, long timeout) {
            if (!handler.post(this)) {
                return false;
            }
            synchronized (this) {
                if (timeout > 0) {
                    long expirationTime = SystemClock.uptimeMillis() + timeout;
                    while (!this.mDone) {
                        long delay = expirationTime - SystemClock.uptimeMillis();
                        if (delay <= 0) {
                            return false;
                        }
                        try {
                            wait(delay);
                        } catch (InterruptedException e) {
                        }
                    }
                } else {
                    while (!this.mDone) {
                        try {
                            wait();
                        } catch (InterruptedException e2) {
                        }
                    }
                }
                return true;
            }
        }
    }

    private final class MessengerImpl extends Stub {
        private MessengerImpl() {
        }

        public void send(Message msg) {
            msg.sendingUid = Binder.getCallingUid();
            Handler.this.sendMessage(msg);
        }
    }

    public void handleMessage(Message msg) {
    }

    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            Callback callback = this.mCallback;
            if (callback == null || !callback.handleMessage(msg)) {
                handleMessage(msg);
            }
        }
    }

    public Handler() {
        this(null, false);
    }

    public Handler(Callback callback) {
        this(callback, false);
    }

    public Handler(Looper looper) {
        this(looper, null, false);
    }

    public Handler(Looper looper, Callback callback) {
        this(looper, callback, false);
    }

    @UnsupportedAppUsage
    public Handler(boolean async) {
        this(null, async);
    }

    public Handler(Callback callback, boolean async) {
        this.mLooper = Looper.myLooper();
        Looper looper = this.mLooper;
        if (looper != null) {
            this.mQueue = looper.mQueue;
            this.mCallback = callback;
            this.mAsynchronous = async;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Can't create handler inside thread ");
        stringBuilder.append(Thread.currentThread());
        stringBuilder.append(" that has not called Looper.prepare()");
        throw new RuntimeException(stringBuilder.toString());
    }

    @UnsupportedAppUsage
    public Handler(Looper looper, Callback callback, boolean async) {
        this.mLooper = looper;
        this.mQueue = looper.mQueue;
        this.mCallback = callback;
        this.mAsynchronous = async;
    }

    public static Handler createAsync(Looper looper) {
        if (looper != null) {
            return new Handler(looper, null, true);
        }
        throw new NullPointerException("looper must not be null");
    }

    public static Handler createAsync(Looper looper, Callback callback) {
        if (looper == null) {
            throw new NullPointerException("looper must not be null");
        } else if (callback != null) {
            return new Handler(looper, callback, true);
        } else {
            throw new NullPointerException("callback must not be null");
        }
    }

    @UnsupportedAppUsage
    public static Handler getMain() {
        if (MAIN_THREAD_HANDLER == null) {
            MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper());
        }
        return MAIN_THREAD_HANDLER;
    }

    public static Handler mainIfNull(Handler handler) {
        return handler == null ? getMain() : handler;
    }

    public String getTraceName(Message message) {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(": ");
        if (message.callback != null) {
            sb.append(message.callback.getClass().getName());
        } else {
            sb.append("#");
            sb.append(message.what);
        }
        return sb.toString();
    }

    public String getMessageName(Message message) {
        if (message.callback != null) {
            return message.callback.getClass().getName();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("0x");
        stringBuilder.append(Integer.toHexString(message.what));
        return stringBuilder.toString();
    }

    public final Message obtainMessage() {
        return Message.obtain(this);
    }

    public final Message obtainMessage(int what) {
        return Message.obtain(this, what);
    }

    public final Message obtainMessage(int what, Object obj) {
        return Message.obtain(this, what, obj);
    }

    public final Message obtainMessage(int what, int arg1, int arg2) {
        return Message.obtain(this, what, arg1, arg2);
    }

    public final Message obtainMessage(int what, int arg1, int arg2, Object obj) {
        return Message.obtain(this, what, arg1, arg2, obj);
    }

    public final boolean post(Runnable r) {
        return sendMessageDelayed(getPostMessage(r), 0);
    }

    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return sendMessageAtTime(getPostMessage(r), uptimeMillis);
    }

    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return sendMessageAtTime(getPostMessage(r, token), uptimeMillis);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return sendMessageDelayed(getPostMessage(r), delayMillis);
    }

    public final boolean postDelayed(Runnable r, int what, long delayMillis) {
        return sendMessageDelayed(getPostMessage(r).setWhat(what), delayMillis);
    }

    public final boolean postDelayed(Runnable r, Object token, long delayMillis) {
        return sendMessageDelayed(getPostMessage(r, token), delayMillis);
    }

    public final boolean postAtFrontOfQueue(Runnable r) {
        return sendMessageAtFrontOfQueue(getPostMessage(r));
    }

    public final boolean runWithScissors(Runnable r, long timeout) {
        if (r == null) {
            throw new IllegalArgumentException("runnable must not be null");
        } else if (timeout < 0) {
            throw new IllegalArgumentException("timeout must be non-negative");
        } else if (Looper.myLooper() != this.mLooper) {
            return new BlockingRunnable(r).postAndWait(this, timeout);
        } else {
            r.run();
            return true;
        }
    }

    public final void removeCallbacks(Runnable r) {
        this.mQueue.removeMessages(this, r, null);
    }

    public final void removeCallbacks(Runnable r, Object token) {
        this.mQueue.removeMessages(this, r, token);
    }

    public final boolean sendMessage(Message msg) {
        return sendMessageDelayed(msg, 0);
    }

    public final boolean sendEmptyMessage(int what) {
        return sendEmptyMessageDelayed(what, 0);
    }

    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageDelayed(msg, delayMillis);
    }

    public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageAtTime(msg, uptimeMillis);
    }

    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        MessageQueue queue = this.mQueue;
        if (queue != null) {
            return enqueueMessage(queue, msg, uptimeMillis);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this);
        stringBuilder.append(" sendMessageAtTime() called with no mQueue");
        RuntimeException e = new RuntimeException(stringBuilder.toString());
        Log.w("Looper", e.getMessage(), e);
        return false;
    }

    public final boolean sendMessageAtFrontOfQueue(Message msg) {
        MessageQueue queue = this.mQueue;
        if (queue != null) {
            return enqueueMessage(queue, msg, 0);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this);
        stringBuilder.append(" sendMessageAtTime() called with no mQueue");
        RuntimeException e = new RuntimeException(stringBuilder.toString());
        Log.w("Looper", e.getMessage(), e);
        return false;
    }

    public final boolean executeOrSendMessage(Message msg) {
        if (this.mLooper != Looper.myLooper()) {
            return sendMessage(msg);
        }
        dispatchMessage(msg);
        return true;
    }

    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;
        msg.workSourceUid = ThreadLocalWorkSource.getUid();
        if (this.mAsynchronous) {
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }

    public final void removeMessages(int what) {
        this.mQueue.removeMessages(this, what, null);
    }

    public final void removeMessages(int what, Object object) {
        this.mQueue.removeMessages(this, what, object);
    }

    public final void removeCallbacksAndMessages(Object token) {
        this.mQueue.removeCallbacksAndMessages(this, token);
    }

    public final boolean hasMessages(int what) {
        return this.mQueue.hasMessages(this, what, null);
    }

    public final boolean hasMessagesOrCallbacks() {
        return this.mQueue.hasMessages(this);
    }

    public final boolean hasMessages(int what, Object object) {
        return this.mQueue.hasMessages(this, what, object);
    }

    public final boolean hasCallbacks(Runnable r) {
        return this.mQueue.hasMessages(this, r, null);
    }

    public final Looper getLooper() {
        return this.mLooper;
    }

    public final void dump(Printer pw, String prefix) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prefix);
        stringBuilder.append(this);
        stringBuilder.append(" @ ");
        stringBuilder.append(SystemClock.uptimeMillis());
        pw.println(stringBuilder.toString());
        Looper looper = this.mLooper;
        if (looper == null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append("looper uninitialized");
            pw.println(stringBuilder.toString());
            return;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(prefix);
        stringBuilder2.append("  ");
        looper.dump(pw, stringBuilder2.toString());
    }

    public final void dumpMine(Printer pw, String prefix) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prefix);
        stringBuilder.append(this);
        stringBuilder.append(" @ ");
        stringBuilder.append(SystemClock.uptimeMillis());
        pw.println(stringBuilder.toString());
        Looper looper = this.mLooper;
        if (looper == null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append("looper uninitialized");
            pw.println(stringBuilder.toString());
            return;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(prefix);
        stringBuilder2.append("  ");
        looper.dump(pw, stringBuilder2.toString(), this);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Handler (");
        stringBuilder.append(getClass().getName());
        stringBuilder.append(") {");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    /* Access modifiers changed, original: final */
    @UnsupportedAppUsage
    public final IMessenger getIMessenger() {
        synchronized (this.mQueue) {
            IMessenger iMessenger;
            if (this.mMessenger != null) {
                iMessenger = this.mMessenger;
                return iMessenger;
            }
            this.mMessenger = new MessengerImpl();
            iMessenger = this.mMessenger;
            return iMessenger;
        }
    }

    private static Message getPostMessage(Runnable r) {
        Message m = Message.obtain();
        m.callback = r;
        return m;
    }

    @UnsupportedAppUsage
    private static Message getPostMessage(Runnable r, Object token) {
        Message m = Message.obtain();
        m.obj = token;
        m.callback = r;
        return m;
    }

    private static void handleCallback(Message message) {
        message.callback.run();
    }
}
