package android.media;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.media.AudioAttributes.Builder;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import java.util.LinkedList;

public class AsyncPlayer {
    private static final int PLAY = 1;
    private static final int STOP = 2;
    private static final boolean mDebug = false;
    private final LinkedList<Command> mCmdQueue = new LinkedList();
    private MediaPlayer mPlayer;
    private int mState = 2;
    private String mTag;
    private Thread mThread;
    private WakeLock mWakeLock;

    private static final class Command {
        AudioAttributes attributes;
        int code;
        Context context;
        boolean looping;
        long requestTime;
        Uri uri;

        private Command() {
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{ code=");
            stringBuilder.append(this.code);
            stringBuilder.append(" looping=");
            stringBuilder.append(this.looping);
            stringBuilder.append(" attr=");
            stringBuilder.append(this.attributes);
            stringBuilder.append(" uri=");
            stringBuilder.append(this.uri);
            stringBuilder.append(" }");
            return stringBuilder.toString();
        }
    }

    private final class Thread extends Thread {
        Thread() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("AsyncPlayer-");
            stringBuilder.append(AsyncPlayer.this.mTag);
            super(stringBuilder.toString());
        }

        public void run() {
            while (true) {
                Command cmd;
                synchronized (AsyncPlayer.this.mCmdQueue) {
                    cmd = (Command) AsyncPlayer.this.mCmdQueue.removeFirst();
                }
                int i = cmd.code;
                if (i == 1) {
                    AsyncPlayer.this.startSound(cmd);
                } else if (i == 2) {
                    if (AsyncPlayer.this.mPlayer != null) {
                        long delay = SystemClock.uptimeMillis() - cmd.requestTime;
                        if (delay > 1000) {
                            String access$000 = AsyncPlayer.this.mTag;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Notification stop delayed by ");
                            stringBuilder.append(delay);
                            stringBuilder.append("msecs");
                            Log.w(access$000, stringBuilder.toString());
                        }
                        AsyncPlayer.this.mPlayer.stop();
                        AsyncPlayer.this.mPlayer.release();
                        AsyncPlayer.this.mPlayer = null;
                    } else {
                        Log.w(AsyncPlayer.this.mTag, "STOP command without a player");
                    }
                }
                synchronized (AsyncPlayer.this.mCmdQueue) {
                    if (AsyncPlayer.this.mCmdQueue.size() == 0) {
                        AsyncPlayer.this.mThread = null;
                        AsyncPlayer.this.releaseWakeLock();
                        return;
                    }
                }
            }
            while (true) {
            }
        }
    }

    private void startSound(Command cmd) {
        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioAttributes(cmd.attributes);
            player.setDataSource(cmd.context, cmd.uri);
            player.setLooping(cmd.looping);
            player.prepare();
            player.start();
            if (this.mPlayer != null) {
                this.mPlayer.release();
            }
            this.mPlayer = player;
            long delay = SystemClock.uptimeMillis() - cmd.requestTime;
            if (delay > 1000) {
                String str = this.mTag;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Notification sound delayed by ");
                stringBuilder.append(delay);
                stringBuilder.append("msecs");
                Log.w(str, stringBuilder.toString());
            }
        } catch (Exception e) {
            String str2 = this.mTag;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("error loading sound for ");
            stringBuilder2.append(cmd.uri);
            Log.w(str2, stringBuilder2.toString(), e);
        }
    }

    public AsyncPlayer(String tag) {
        if (tag != null) {
            this.mTag = tag;
        } else {
            this.mTag = "AsyncPlayer";
        }
    }

    public void play(Context context, Uri uri, boolean looping, int stream) {
        PlayerBase.deprecateStreamTypeForPlayback(stream, "AsyncPlayer", "play()");
        if (context != null && uri != null) {
            try {
                play(context, uri, looping, new Builder().setInternalLegacyStreamType(stream).build());
            } catch (IllegalArgumentException e) {
                Log.e(this.mTag, "Call to deprecated AsyncPlayer.play() method caused:", e);
            }
        }
    }

    public void play(Context context, Uri uri, boolean looping, AudioAttributes attributes) throws IllegalArgumentException {
        if (context == null || uri == null || attributes == null) {
            throw new IllegalArgumentException("Illegal null AsyncPlayer.play() argument");
        }
        Command cmd = new Command();
        cmd.requestTime = SystemClock.uptimeMillis();
        cmd.code = 1;
        cmd.context = context;
        cmd.uri = uri;
        cmd.looping = looping;
        cmd.attributes = attributes;
        synchronized (this.mCmdQueue) {
            enqueueLocked(cmd);
            this.mState = 1;
        }
    }

    public void stop() {
        synchronized (this.mCmdQueue) {
            if (this.mState != 2) {
                Command cmd = new Command();
                cmd.requestTime = SystemClock.uptimeMillis();
                cmd.code = 2;
                enqueueLocked(cmd);
                this.mState = 2;
            }
        }
    }

    private void enqueueLocked(Command cmd) {
        this.mCmdQueue.add(cmd);
        if (this.mThread == null) {
            acquireWakeLock();
            this.mThread = new Thread();
            this.mThread.start();
        }
    }

    @UnsupportedAppUsage
    public void setUsesWakeLock(Context context) {
        if (this.mWakeLock == null && this.mThread == null) {
            this.mWakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(1, this.mTag);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("assertion failed mWakeLock=");
        stringBuilder.append(this.mWakeLock);
        stringBuilder.append(" mThread=");
        stringBuilder.append(this.mThread);
        throw new RuntimeException(stringBuilder.toString());
    }

    private void acquireWakeLock() {
        WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            wakeLock.release();
        }
    }
}
