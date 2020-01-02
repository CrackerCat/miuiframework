package android.telecom;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.telecom.Conference.Listener;
import android.telecom.Connection.RttTextStream;
import android.telecom.Connection.VideoProvider;
import android.telecom.Logging.Runnable;
import android.telecom.Logging.Session;
import android.telecom.Logging.Session.Info;
import android.telephony.ims.ImsCallProfile;
import com.android.internal.os.SomeArgs;
import com.android.internal.telecom.IConnectionService.Stub;
import com.android.internal.telecom.IConnectionServiceAdapter;
import com.android.internal.telecom.IVideoProvider;
import com.android.internal.telecom.RemoteServiceCallback;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import miui.util.HapticFeedbackUtil;

public abstract class ConnectionService extends Service {
    public static final String EXTRA_IS_HANDOVER = "android.telecom.extra.IS_HANDOVER";
    private static final int MSG_ABORT = 3;
    private static final int MSG_ADD_CONNECTION_SERVICE_ADAPTER = 1;
    private static final int MSG_ADD_PARTICIPANT_WITH_CONFERENCE = 40;
    private static final int MSG_ANSWER = 4;
    private static final int MSG_ANSWER_VIDEO = 17;
    private static final int MSG_CONFERENCE = 12;
    private static final int MSG_CONNECTION_SERVICE_FOCUS_GAINED = 31;
    private static final int MSG_CONNECTION_SERVICE_FOCUS_LOST = 30;
    private static final int MSG_CREATE_CONNECTION = 2;
    private static final int MSG_CREATE_CONNECTION_COMPLETE = 29;
    private static final int MSG_CREATE_CONNECTION_FAILED = 25;
    private static final int MSG_DEFLECT = 34;
    private static final int MSG_DISCONNECT = 6;
    private static final int MSG_HANDOVER_COMPLETE = 33;
    private static final int MSG_HANDOVER_FAILED = 32;
    private static final int MSG_HOLD = 7;
    private static final int MSG_MERGE_CONFERENCE = 18;
    private static final int MSG_ON_CALL_AUDIO_STATE_CHANGED = 9;
    private static final int MSG_ON_EXTRAS_CHANGED = 24;
    private static final int MSG_ON_POST_DIAL_CONTINUE = 14;
    private static final int MSG_ON_START_RTT = 26;
    private static final int MSG_ON_STOP_RTT = 27;
    private static final int MSG_PLAY_DTMF_TONE = 10;
    private static final int MSG_PULL_EXTERNAL_CALL = 22;
    private static final int MSG_REJECT = 5;
    private static final int MSG_REJECT_WITH_MESSAGE = 20;
    private static final int MSG_REMOVE_CONNECTION_SERVICE_ADAPTER = 16;
    private static final int MSG_RTT_UPGRADE_RESPONSE = 28;
    private static final int MSG_SEND_CALL_EVENT = 23;
    private static final int MSG_SILENCE = 21;
    private static final int MSG_SPLIT_FROM_CONFERENCE = 13;
    private static final int MSG_STOP_DTMF_TONE = 11;
    private static final int MSG_SWAP_CONFERENCE = 19;
    private static final int MSG_UNHOLD = 8;
    private static final boolean PII_DEBUG = Log.isLoggable(3);
    public static final String SERVICE_INTERFACE = "android.telecom.ConnectionService";
    private static final String SESSION_ABORT = "CS.ab";
    private static final String SESSION_ADD_CS_ADAPTER = "CS.aCSA";
    private static final String SESSION_ANSWER = "CS.an";
    private static final String SESSION_ANSWER_VIDEO = "CS.anV";
    private static final String SESSION_CALL_AUDIO_SC = "CS.cASC";
    private static final String SESSION_CONFERENCE = "CS.c";
    private static final String SESSION_CONNECTION_SERVICE_FOCUS_GAINED = "CS.cSFG";
    private static final String SESSION_CONNECTION_SERVICE_FOCUS_LOST = "CS.cSFL";
    private static final String SESSION_CREATE_CONN = "CS.crCo";
    private static final String SESSION_CREATE_CONN_COMPLETE = "CS.crCoC";
    private static final String SESSION_CREATE_CONN_FAILED = "CS.crCoF";
    private static final String SESSION_DEFLECT = "CS.def";
    private static final String SESSION_DISCONNECT = "CS.d";
    private static final String SESSION_EXTRAS_CHANGED = "CS.oEC";
    private static final String SESSION_HANDLER = "H.";
    private static final String SESSION_HANDOVER_COMPLETE = "CS.hC";
    private static final String SESSION_HANDOVER_FAILED = "CS.haF";
    private static final String SESSION_HOLD = "CS.h";
    private static final String SESSION_MERGE_CONFERENCE = "CS.mC";
    private static final String SESSION_PLAY_DTMF = "CS.pDT";
    private static final String SESSION_POST_DIAL_CONT = "CS.oPDC";
    private static final String SESSION_PULL_EXTERNAL_CALL = "CS.pEC";
    private static final String SESSION_REJECT = "CS.r";
    private static final String SESSION_REJECT_MESSAGE = "CS.rWM";
    private static final String SESSION_REMOVE_CS_ADAPTER = "CS.rCSA";
    private static final String SESSION_RTT_UPGRADE_RESPONSE = "CS.rTRUR";
    private static final String SESSION_SEND_CALL_EVENT = "CS.sCE";
    private static final String SESSION_SILENCE = "CS.s";
    private static final String SESSION_SPLIT_CONFERENCE = "CS.sFC";
    private static final String SESSION_START_RTT = "CS.+RTT";
    private static final String SESSION_STOP_DTMF = "CS.sDT";
    private static final String SESSION_STOP_RTT = "CS.-RTT";
    private static final String SESSION_SWAP_CONFERENCE = "CS.sC";
    private static final String SESSION_UNHOLD = "CS.u";
    private static final String SESSION_UPDATE_RTT_PIPES = "CS.uRTT";
    private static Connection sNullConnection;
    private final ConnectionServiceAdapter mAdapter = new ConnectionServiceAdapter();
    private boolean mAreAccountsInitialized = false;
    private final IBinder mBinder = new Stub() {
        public void addConnectionServiceAdapter(IConnectionServiceAdapter adapter, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_ADD_CS_ADAPTER);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = adapter;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(1, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void removeConnectionServiceAdapter(IConnectionServiceAdapter adapter, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_REMOVE_CS_ADAPTER);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = adapter;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(16, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void createConnection(PhoneAccountHandle connectionManagerPhoneAccount, String id, ConnectionRequest request, boolean isIncoming, boolean isUnknown, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CREATE_CONN);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = connectionManagerPhoneAccount;
                args.arg2 = id;
                args.arg3 = request;
                args.arg4 = Log.createSubsession();
                int i = 1;
                args.argi1 = isIncoming ? 1 : 0;
                if (!isUnknown) {
                    i = 0;
                }
                args.argi2 = i;
                ConnectionService.this.mHandler.obtainMessage(2, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void createConnectionComplete(String id, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CREATE_CONN_COMPLETE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = id;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(29, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void createConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, String callId, ConnectionRequest request, boolean isIncoming, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CREATE_CONN_FAILED);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = request;
                args.arg3 = Log.createSubsession();
                args.arg4 = connectionManagerPhoneAccount;
                args.argi1 = isIncoming ? 1 : 0;
                ConnectionService.this.mHandler.obtainMessage(25, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void handoverFailed(String callId, ConnectionRequest request, int reason, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_HANDOVER_FAILED);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = request;
                args.arg3 = Log.createSubsession();
                args.arg4 = Integer.valueOf(reason);
                ConnectionService.this.mHandler.obtainMessage(32, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void handoverComplete(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_HANDOVER_COMPLETE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(33, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void abort(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_ABORT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(3, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void answerVideo(String callId, int videoState, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_ANSWER_VIDEO);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                args.argi1 = videoState;
                ConnectionService.this.mHandler.obtainMessage(17, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void answer(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_ANSWER);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(4, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void deflect(String callId, Uri address, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_DEFLECT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = address;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(34, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void reject(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_REJECT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(5, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void rejectWithMessage(String callId, String message, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_REJECT_MESSAGE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = message;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(20, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void silence(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_SILENCE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(21, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void disconnect(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_DISCONNECT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(6, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void hold(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_HOLD);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(7, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void unhold(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_UNHOLD);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(8, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void onCallAudioStateChanged(String callId, CallAudioState callAudioState, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CALL_AUDIO_SC);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = callAudioState;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(9, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void playDtmfTone(String callId, char digit, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_PLAY_DTMF);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = Character.valueOf(digit);
                args.arg2 = callId;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(10, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void stopDtmfTone(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_STOP_DTMF);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(11, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void conference(String callId1, String callId2, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CONFERENCE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId1;
                args.arg2 = callId2;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(12, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void splitFromConference(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_SPLIT_CONFERENCE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(13, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void addParticipantWithConference(String callId, String participant) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = callId;
            args.arg2 = participant;
            ConnectionService.this.mHandler.obtainMessage(40, args).sendToTarget();
        }

        public void mergeConference(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_MERGE_CONFERENCE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(18, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void swapConference(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_SWAP_CONFERENCE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(19, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void onPostDialContinue(String callId, boolean proceed, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_POST_DIAL_CONT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                args.argi1 = proceed ? 1 : 0;
                ConnectionService.this.mHandler.obtainMessage(14, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void pullExternalCall(String callId, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_PULL_EXTERNAL_CALL);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(22, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void sendCallEvent(String callId, String event, Bundle extras, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_SEND_CALL_EVENT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = event;
                args.arg3 = extras;
                args.arg4 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(23, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void onExtrasChanged(String callId, Bundle extras, Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_EXTRAS_CHANGED);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = extras;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(24, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void startRtt(String callId, ParcelFileDescriptor fromInCall, ParcelFileDescriptor toInCall, Info sessionInfo) throws RemoteException {
            Log.startSession(sessionInfo, ConnectionService.SESSION_START_RTT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = new RttTextStream(toInCall, fromInCall);
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(26, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void stopRtt(String callId, Info sessionInfo) throws RemoteException {
            Log.startSession(sessionInfo, ConnectionService.SESSION_STOP_RTT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(27, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void respondToRttUpgradeRequest(String callId, ParcelFileDescriptor fromInCall, ParcelFileDescriptor toInCall, Info sessionInfo) throws RemoteException {
            Log.startSession(sessionInfo, ConnectionService.SESSION_RTT_UPGRADE_RESPONSE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                if (toInCall != null) {
                    if (fromInCall != null) {
                        args.arg2 = new RttTextStream(toInCall, fromInCall);
                        args.arg3 = Log.createSubsession();
                        ConnectionService.this.mHandler.obtainMessage(28, args).sendToTarget();
                    }
                }
                args.arg2 = null;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(28, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void connectionServiceFocusLost(Info sessionInfo) throws RemoteException {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CONNECTION_SERVICE_FOCUS_LOST);
            try {
                ConnectionService.this.mHandler.obtainMessage(30).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        public void connectionServiceFocusGained(Info sessionInfo) throws RemoteException {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CONNECTION_SERVICE_FOCUS_GAINED);
            try {
                ConnectionService.this.mHandler.obtainMessage(31).sendToTarget();
            } finally {
                Log.endSession();
            }
        }
    };
    private final Map<String, Conference> mConferenceById = new ConcurrentHashMap();
    private final Listener mConferenceListener = new Listener() {
        public void onStateChanged(Conference conference, int oldState, int newState) {
            String id = (String) ConnectionService.this.mIdByConference.get(conference);
            if (newState == 4) {
                ConnectionService.this.mAdapter.setActive(id);
            } else if (newState == 5) {
                ConnectionService.this.mAdapter.setOnHold(id);
            }
        }

        public void onDisconnected(Conference conference, DisconnectCause disconnectCause) {
            ConnectionService.this.mAdapter.setDisconnected((String) ConnectionService.this.mIdByConference.get(conference), disconnectCause);
        }

        public void onConnectionAdded(Conference conference, Connection connection) {
        }

        public void onConnectionRemoved(Conference conference, Connection connection) {
        }

        public void onConferenceableConnectionsChanged(Conference conference, List<Connection> conferenceableConnections) {
            ConnectionService.this.mAdapter.setConferenceableConnections((String) ConnectionService.this.mIdByConference.get(conference), ConnectionService.this.createConnectionIdList(conferenceableConnections));
        }

        public void onDestroyed(Conference conference) {
            ConnectionService.this.removeConference(conference);
        }

        public void onConnectionCapabilitiesChanged(Conference conference, int connectionCapabilities) {
            String id = (String) ConnectionService.this.mIdByConference.get(conference);
            Log.d((Object) this, "call capabilities: conference: %s", Connection.capabilitiesToString(connectionCapabilities));
            ConnectionService.this.mAdapter.setConnectionCapabilities(id, connectionCapabilities);
        }

        public void onConnectionPropertiesChanged(Conference conference, int connectionProperties) {
            String id = (String) ConnectionService.this.mIdByConference.get(conference);
            Log.d((Object) this, "call capabilities: conference: %s", Connection.propertiesToString(connectionProperties));
            ConnectionService.this.mAdapter.setConnectionProperties(id, connectionProperties);
        }

        public void onVideoStateChanged(Conference c, int videoState) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            Log.d((Object) this, "onVideoStateChanged set video state %d", Integer.valueOf(videoState));
            ConnectionService.this.mAdapter.setVideoState(id, videoState);
        }

        public void onVideoProviderChanged(Conference c, VideoProvider videoProvider) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            Log.d((Object) this, "onVideoProviderChanged: Connection: %s, VideoProvider: %s", c, videoProvider);
            ConnectionService.this.mAdapter.setVideoProvider(id, videoProvider);
        }

        public void onStatusHintsChanged(Conference conference, StatusHints statusHints) {
            String id = (String) ConnectionService.this.mIdByConference.get(conference);
            if (id != null) {
                ConnectionService.this.mAdapter.setStatusHints(id, statusHints);
            }
        }

        public void onExtrasChanged(Conference c, Bundle extras) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.putExtras(id, extras);
            }
        }

        public void onExtrasRemoved(Conference c, List<String> keys) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.removeExtras(id, keys);
            }
        }

        public void onConferenceStateChanged(Conference c, boolean isConference) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.setConferenceState(id, isConference);
            }
        }

        public void onAddressChanged(Conference c, Uri newAddress, int presentation) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.setAddress(id, newAddress, presentation);
            }
        }

        public void onCallerDisplayNameChanged(Conference c, String callerDisplayName, int presentation) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.setCallerDisplayName(id, callerDisplayName, presentation);
            }
        }

        public void onConnectionEvent(Conference c, String event, Bundle extras) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.onConnectionEvent(id, event, extras);
            }
        }
    };
    private final Map<String, Connection> mConnectionById = new ConcurrentHashMap();
    private final Connection.Listener mConnectionListener = new Connection.Listener() {
        public void onStateChanged(Connection c, int state) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.d((Object) this, "Adapter set state %s %s", id, Connection.stateToString(state));
            switch (state) {
                case 2:
                    ConnectionService.this.mAdapter.setRinging(id);
                    return;
                case 3:
                    ConnectionService.this.mAdapter.setDialing(id);
                    return;
                case 4:
                    ConnectionService.this.mAdapter.setActive(id);
                    return;
                case 5:
                    ConnectionService.this.mAdapter.setOnHold(id);
                    return;
                case 7:
                    ConnectionService.this.mAdapter.setPulling(id);
                    return;
                default:
                    return;
            }
        }

        public void onDisconnected(Connection c, DisconnectCause disconnectCause) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.d((Object) this, "Adapter set disconnected %s", disconnectCause);
            ConnectionService.this.mAdapter.setDisconnected(id, disconnectCause);
        }

        public void onVideoStateChanged(Connection c, int videoState) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.d((Object) this, "Adapter set video state %d", Integer.valueOf(videoState));
            ConnectionService.this.mAdapter.setVideoState(id, videoState);
        }

        public void onAddressChanged(Connection c, Uri address, int presentation) {
            ConnectionService.this.mAdapter.setAddress((String) ConnectionService.this.mIdByConnection.get(c), address, presentation);
        }

        public void onCallerDisplayNameChanged(Connection c, String callerDisplayName, int presentation) {
            ConnectionService.this.mAdapter.setCallerDisplayName((String) ConnectionService.this.mIdByConnection.get(c), callerDisplayName, presentation);
        }

        public void onDestroyed(Connection c) {
            ConnectionService.this.removeConnection(c);
        }

        public void onPostDialWait(Connection c, String remaining) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.d((Object) this, "Adapter onPostDialWait %s, %s", c, remaining);
            ConnectionService.this.mAdapter.onPostDialWait(id, remaining);
        }

        public void onPostDialChar(Connection c, char nextChar) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.d((Object) this, "Adapter onPostDialChar %s, %s", c, Character.valueOf(nextChar));
            ConnectionService.this.mAdapter.onPostDialChar(id, nextChar);
        }

        public void onRingbackRequested(Connection c, boolean ringback) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.d((Object) this, "Adapter onRingback %b", Boolean.valueOf(ringback));
            ConnectionService.this.mAdapter.setRingbackRequested(id, ringback);
        }

        public void onConnectionCapabilitiesChanged(Connection c, int capabilities) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.d((Object) this, "capabilities: parcelableconnection: %s", Connection.capabilitiesToString(capabilities));
            ConnectionService.this.mAdapter.setConnectionCapabilities(id, capabilities);
        }

        public void onConnectionPropertiesChanged(Connection c, int properties) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.d((Object) this, "properties: parcelableconnection: %s", Connection.propertiesToString(properties));
            ConnectionService.this.mAdapter.setConnectionProperties(id, properties);
        }

        public void onVideoProviderChanged(Connection c, VideoProvider videoProvider) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.d((Object) this, "onVideoProviderChanged: Connection: %s, VideoProvider: %s", c, videoProvider);
            ConnectionService.this.mAdapter.setVideoProvider(id, videoProvider);
        }

        public void onAudioModeIsVoipChanged(Connection c, boolean isVoip) {
            ConnectionService.this.mAdapter.setIsVoipAudioMode((String) ConnectionService.this.mIdByConnection.get(c), isVoip);
        }

        public void onStatusHintsChanged(Connection c, StatusHints statusHints) {
            ConnectionService.this.mAdapter.setStatusHints((String) ConnectionService.this.mIdByConnection.get(c), statusHints);
        }

        public void onConferenceablesChanged(Connection connection, List<Conferenceable> conferenceables) {
            ConnectionService.this.mAdapter.setConferenceableConnections((String) ConnectionService.this.mIdByConnection.get(connection), ConnectionService.this.createIdList(conferenceables));
        }

        public void onConferenceChanged(Connection connection, Conference conference) {
            String id = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (id != null) {
                String conferenceId = null;
                if (conference != null) {
                    conferenceId = (String) ConnectionService.this.mIdByConference.get(conference);
                }
                ConnectionService.this.mAdapter.setIsConferenced(id, conferenceId);
            }
        }

        public void onConferenceMergeFailed(Connection connection) {
            String id = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (id != null) {
                ConnectionService.this.mAdapter.onConferenceMergeFailed(id);
            }
        }

        public void onExtrasChanged(Connection c, Bundle extras) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.putExtras(id, extras);
            }
        }

        public void onExtrasRemoved(Connection c, List<String> keys) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.removeExtras(id, keys);
            }
        }

        public void onConnectionEvent(Connection connection, String event, Bundle extras) {
            String id = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (id != null) {
                ConnectionService.this.mAdapter.onConnectionEvent(id, event, extras);
            }
        }

        public void onAudioRouteChanged(Connection c, int audioRoute, String bluetoothAddress) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.setAudioRoute(id, audioRoute, bluetoothAddress);
            }
        }

        public void onRttInitiationSuccess(Connection c) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.onRttInitiationSuccess(id);
            }
        }

        public void onRttInitiationFailure(Connection c, int reason) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.onRttInitiationFailure(id, reason);
            }
        }

        public void onRttSessionRemotelyTerminated(Connection c) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.onRttSessionRemotelyTerminated(id);
            }
        }

        public void onRemoteRttRequest(Connection c) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.onRemoteRttRequest(id);
            }
        }

        public void onPhoneAccountChanged(Connection c, PhoneAccountHandle pHandle) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.onPhoneAccountChanged(id, pHandle);
            }
        }

        public void onConnectionTimeReset(Connection c) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.resetConnectionTime(id);
            }
        }
    };
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            Message message = msg;
            int i = message.what;
            SomeArgs args;
            if (i != 40) {
                String str = "H.CS.r";
                String str2 = "Enqueueing pre-init request %s";
                boolean z = false;
                final PhoneAccountHandle connectionManagerPhoneAccount;
                String id;
                final ConnectionRequest request;
                final boolean isIncoming;
                final String str3;
                SomeArgs args2;
                switch (i) {
                    case 1:
                        args = (SomeArgs) message.obj;
                        try {
                            IConnectionServiceAdapter adapter = args.arg1;
                            Log.continueSession((Session) args.arg2, "H.CS.aCSA");
                            ConnectionService.this.mAdapter.addAdapter(adapter);
                            ConnectionService.this.onAdapterAttached();
                            return;
                        } finally {
                            args.recycle();
                            Log.endSession();
                        }
                    case 2:
                        SomeArgs args3 = message.obj;
                        Log.continueSession((Session) args3.arg4, "H.CS.crCo");
                        try {
                            connectionManagerPhoneAccount = args3.arg1;
                            id = args3.arg2;
                            request = args3.arg3;
                            isIncoming = args3.argi1 == 1;
                            final boolean isUnknown = args3.argi2 == 1;
                            if (ConnectionService.this.mAreAccountsInitialized) {
                                ConnectionService.this.createConnection(connectionManagerPhoneAccount, id, request, isIncoming, isUnknown);
                            } else {
                                Log.d(this, str2, id);
                                str3 = id;
                                ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.crCo.pICR", null) {
                                    public void loggedRun() {
                                        ConnectionService.this.createConnection(connectionManagerPhoneAccount, str3, request, isIncoming, isUnknown);
                                    }
                                }.prepare());
                            }
                            args3.recycle();
                            Log.endSession();
                            return;
                        } catch (Throwable th) {
                            args3.recycle();
                            Log.endSession();
                        }
                    case 3:
                        args = (SomeArgs) message.obj;
                        Log.continueSession((Session) args.arg2, "H.CS.ab");
                        try {
                            ConnectionService.this.abort((String) args.arg1);
                            return;
                        } finally {
                            args.recycle();
                            Log.endSession();
                        }
                    case 4:
                        args = (SomeArgs) message.obj;
                        Log.continueSession((Session) args.arg2, "H.CS.an");
                        try {
                            ConnectionService.this.answer((String) args.arg1);
                            return;
                        } finally {
                            args.recycle();
                            Log.endSession();
                        }
                    case 5:
                        args2 = (SomeArgs) message.obj;
                        Log.continueSession((Session) args2.arg2, str);
                        try {
                            ConnectionService.this.reject((String) args2.arg1);
                            return;
                        } finally {
                            args2.recycle();
                            Log.endSession();
                        }
                    case 6:
                        args = (SomeArgs) message.obj;
                        Log.continueSession((Session) args.arg2, "H.CS.d");
                        try {
                            ConnectionService.this.disconnect((String) args.arg1);
                            return;
                        } finally {
                            args.recycle();
                            Log.endSession();
                        }
                    case 7:
                        args2 = message.obj;
                        Log.continueSession((Session) args2.arg2, str);
                        try {
                            ConnectionService.this.hold((String) args2.arg1);
                            return;
                        } finally {
                            args2.recycle();
                            Log.endSession();
                        }
                    case 8:
                        args = (SomeArgs) message.obj;
                        Log.continueSession((Session) args.arg2, "H.CS.u");
                        try {
                            ConnectionService.this.unhold((String) args.arg1);
                            return;
                        } finally {
                            args.recycle();
                            Log.endSession();
                        }
                    case 9:
                        args = (SomeArgs) message.obj;
                        Log.continueSession((Session) args.arg3, "H.CS.cASC");
                        try {
                            ConnectionService.this.onCallAudioStateChanged(args.arg1, new CallAudioState(args.arg2));
                            return;
                        } finally {
                            args.recycle();
                            Log.endSession();
                        }
                    case 10:
                        args = (SomeArgs) message.obj;
                        try {
                            Log.continueSession((Session) args.arg3, "H.CS.pDT");
                            ConnectionService.this.playDtmfTone((String) args.arg2, ((Character) args.arg1).charValue());
                            return;
                        } finally {
                            args.recycle();
                            Log.endSession();
                        }
                    case 11:
                        args = (SomeArgs) message.obj;
                        try {
                            Log.continueSession((Session) args.arg2, "H.CS.sDT");
                            ConnectionService.this.stopDtmfTone((String) args.arg1);
                            return;
                        } finally {
                            args.recycle();
                            Log.endSession();
                        }
                    case 12:
                        args = (SomeArgs) message.obj;
                        try {
                            Log.continueSession((Session) args.arg3, "H.CS.c");
                            ConnectionService.this.conference(args.arg1, args.arg2);
                            return;
                        } finally {
                            args.recycle();
                            Log.endSession();
                        }
                    case 13:
                        args = (SomeArgs) message.obj;
                        try {
                            Log.continueSession((Session) args.arg2, "H.CS.sFC");
                            ConnectionService.this.splitFromConference((String) args.arg1);
                            return;
                        } finally {
                            args.recycle();
                            Log.endSession();
                        }
                    case 14:
                        args = (SomeArgs) message.obj;
                        try {
                            Log.continueSession((Session) args.arg2, "H.CS.oPDC");
                            id = (String) args.arg1;
                            if (args.argi1 == 1) {
                                z = true;
                            }
                            ConnectionService.this.onPostDialContinue(id, z);
                            return;
                        } finally {
                            args.recycle();
                            Log.endSession();
                        }
                    default:
                        str = "createConnectionFailed %s";
                        switch (i) {
                            case 16:
                                args = (SomeArgs) message.obj;
                                try {
                                    Log.continueSession((Session) args.arg2, "H.CS.rCSA");
                                    ConnectionService.this.mAdapter.removeAdapter((IConnectionServiceAdapter) args.arg1);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 17:
                                args = (SomeArgs) message.obj;
                                Log.continueSession((Session) args.arg2, "H.CS.anV");
                                try {
                                    ConnectionService.this.answerVideo((String) args.arg1, args.argi1);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 18:
                                args = (SomeArgs) message.obj;
                                try {
                                    Log.continueSession((Session) args.arg2, "H.CS.mC");
                                    ConnectionService.this.mergeConference((String) args.arg1);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 19:
                                args = (SomeArgs) message.obj;
                                try {
                                    Log.continueSession((Session) args.arg2, "H.CS.sC");
                                    ConnectionService.this.swapConference((String) args.arg1);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 20:
                                args = (SomeArgs) message.obj;
                                Log.continueSession((Session) args.arg3, "H.CS.rWM");
                                try {
                                    ConnectionService.this.reject((String) args.arg1, (String) args.arg2);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 21:
                                args = (SomeArgs) message.obj;
                                Log.continueSession((Session) args.arg2, "H.CS.s");
                                try {
                                    ConnectionService.this.silence((String) args.arg1);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 22:
                                args = (SomeArgs) message.obj;
                                try {
                                    Log.continueSession((Session) args.arg2, "H.CS.pEC");
                                    ConnectionService.this.pullExternalCall((String) args.arg1);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 23:
                                args = (SomeArgs) message.obj;
                                try {
                                    Log.continueSession((Session) args.arg4, "H.CS.sCE");
                                    ConnectionService.this.sendCallEvent((String) args.arg1, args.arg2, args.arg3);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 24:
                                args = (SomeArgs) message.obj;
                                try {
                                    Log.continueSession((Session) args.arg3, "H.CS.oEC");
                                    ConnectionService.this.handleExtrasChanged(args.arg1, args.arg2);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 25:
                                SomeArgs args4 = message.obj;
                                Log.continueSession((Session) args4.arg3, "H.CS.crCoF");
                                try {
                                    id = args4.arg1;
                                    ConnectionRequest request2 = (ConnectionRequest) args4.arg2;
                                    boolean isIncoming2 = args4.argi1 == 1;
                                    PhoneAccountHandle connectionMgrPhoneAccount = (PhoneAccountHandle) args4.arg4;
                                    if (ConnectionService.this.mAreAccountsInitialized) {
                                        Log.i(this, str, id);
                                        ConnectionService.this.createConnectionFailed(connectionMgrPhoneAccount, id, request2, isIncoming2);
                                    } else {
                                        Log.d(this, str2, id);
                                        connectionManagerPhoneAccount = connectionMgrPhoneAccount;
                                        str3 = id;
                                        request = request2;
                                        isIncoming = isIncoming2;
                                        ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.crCoF.pICR", null) {
                                            public void loggedRun() {
                                                ConnectionService.this.createConnectionFailed(connectionManagerPhoneAccount, str3, request, isIncoming);
                                            }
                                        }.prepare());
                                    }
                                    args4.recycle();
                                    Log.endSession();
                                    return;
                                } catch (Throwable th2) {
                                    args4.recycle();
                                    Log.endSession();
                                }
                            case 26:
                                args = (SomeArgs) message.obj;
                                try {
                                    Log.continueSession((Session) args.arg3, "H.CS.+RTT");
                                    ConnectionService.this.startRtt((String) args.arg1, (RttTextStream) args.arg2);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 27:
                                args = (SomeArgs) message.obj;
                                try {
                                    Log.continueSession((Session) args.arg2, "H.CS.-RTT");
                                    ConnectionService.this.stopRtt((String) args.arg1);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 28:
                                args = (SomeArgs) message.obj;
                                try {
                                    Log.continueSession((Session) args.arg3, "H.CS.rTRUR");
                                    ConnectionService.this.handleRttUpgradeResponse(args.arg1, args.arg2);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 29:
                                args = (SomeArgs) message.obj;
                                Log.continueSession((Session) args.arg2, "H.CS.crCoC");
                                try {
                                    id = (String) args.arg1;
                                    if (ConnectionService.this.mAreAccountsInitialized) {
                                        ConnectionService.this.notifyCreateConnectionComplete(id);
                                    } else {
                                        Log.d(this, str2, id);
                                        ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.crCoC.pICR", null) {
                                            public void loggedRun() {
                                                ConnectionService.this.notifyCreateConnectionComplete(id);
                                            }
                                        }.prepare());
                                    }
                                    args.recycle();
                                    Log.endSession();
                                    return;
                                } catch (Throwable th3) {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 30:
                                ConnectionService.this.onConnectionServiceFocusLost();
                                return;
                            case 31:
                                ConnectionService.this.onConnectionServiceFocusGained();
                                return;
                            case 32:
                                SomeArgs args5 = message.obj;
                                Log.continueSession((Session) args5.arg3, "H.CS.haF");
                                try {
                                    id = args5.arg1;
                                    ConnectionRequest request3 = (ConnectionRequest) args5.arg2;
                                    int reason = ((Integer) args5.arg4).intValue();
                                    if (ConnectionService.this.mAreAccountsInitialized) {
                                        Log.i(this, str, id);
                                        ConnectionService.this.handoverFailed(id, request3, reason);
                                    } else {
                                        Log.d(this, str2, id);
                                        final String str4 = id;
                                        final ConnectionRequest connectionRequest = request3;
                                        final int i2 = reason;
                                        ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.haF.pICR", null) {
                                            public void loggedRun() {
                                                ConnectionService.this.handoverFailed(str4, connectionRequest, i2);
                                            }
                                        }.prepare());
                                    }
                                    args5.recycle();
                                    Log.endSession();
                                    return;
                                } catch (Throwable th4) {
                                    args5.recycle();
                                    Log.endSession();
                                }
                            case 33:
                                args = (SomeArgs) message.obj;
                                try {
                                    Log.continueSession((Session) args.arg2, "H.CS.hC");
                                    ConnectionService.this.notifyHandoverComplete(args.arg1);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            case 34:
                                args = message.obj;
                                Log.continueSession((Session) args.arg3, "H.CS.def");
                                try {
                                    ConnectionService.this.deflect((String) args.arg1, (Uri) args.arg2);
                                    return;
                                } finally {
                                    args.recycle();
                                    Log.endSession();
                                }
                            default:
                                return;
                        }
                }
            }
            args = (SomeArgs) message.obj;
            try {
                ConnectionService.this.addParticipantWithConference(args.arg1, args.arg2);
            } finally {
                args.recycle();
            }
        }
    };
    private int mId = 0;
    private final Map<Conference, String> mIdByConference = new ConcurrentHashMap();
    private final Map<Connection, String> mIdByConnection = new ConcurrentHashMap();
    private Object mIdSyncRoot = new Object();
    private final List<Runnable> mPreInitializationConnectionRequests = new ArrayList();
    private final RemoteConnectionManager mRemoteConnectionManager = new RemoteConnectionManager(this);
    private Conference sNullConference;

    public final IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public boolean onUnbind(Intent intent) {
        endAllConnections();
        return super.onUnbind(intent);
    }

    private void createConnection(PhoneAccountHandle callManagerAccount, String callId, ConnectionRequest request, boolean isIncoming, boolean isUnknown) {
        Connection connection;
        PhoneAccountHandle phoneAccountHandle = callManagerAccount;
        String str = callId;
        ConnectionRequest connectionRequest = request;
        boolean isLegacyHandover = request.getExtras() != null && request.getExtras().getBoolean("android.telecom.extra.IS_HANDOVER", false);
        boolean isHandover = request.getExtras() != null && request.getExtras().getBoolean(TelecomManager.EXTRA_IS_HANDOVER_CONNECTION, false);
        Log.d(this, "createConnection, callManagerAccount: %s, callId: %s, request: %s, isIncoming: %b, isUnknown: %b, isLegacyHandover: %b, isHandover: %b", phoneAccountHandle, str, connectionRequest, Boolean.valueOf(isIncoming), Boolean.valueOf(isUnknown), Boolean.valueOf(isLegacyHandover), Boolean.valueOf(isHandover));
        IVideoProvider iVideoProvider = null;
        if (isHandover) {
            PhoneAccountHandle fromPhoneAccountHandle;
            if (request.getExtras() != null) {
                fromPhoneAccountHandle = (PhoneAccountHandle) request.getExtras().getParcelable(TelecomManager.EXTRA_HANDOVER_FROM_PHONE_ACCOUNT);
            } else {
                fromPhoneAccountHandle = null;
            }
            if (isIncoming) {
                connection = onCreateIncomingHandoverConnection(fromPhoneAccountHandle, connectionRequest);
            } else {
                connection = onCreateOutgoingHandoverConnection(fromPhoneAccountHandle, connectionRequest);
            }
        } else {
            Connection onCreateUnknownConnection;
            if (isUnknown) {
                onCreateUnknownConnection = onCreateUnknownConnection(phoneAccountHandle, connectionRequest);
            } else if (isIncoming) {
                onCreateUnknownConnection = onCreateIncomingConnection(phoneAccountHandle, connectionRequest);
            } else {
                onCreateUnknownConnection = onCreateOutgoingConnection(phoneAccountHandle, connectionRequest);
            }
            connection = onCreateUnknownConnection;
        }
        Log.d(this, "createConnection, connection: %s", connection);
        if (connection == null) {
            Log.i(this, "createConnection, implementation returned null connection.", new Object[0]);
            connection = Connection.createFailedConnection(new DisconnectCause(1, "IMPL_RETURNED_NULL_CONNECTION"));
        }
        boolean isSelfManaged = (connection.getConnectionProperties() & 128) == 128;
        if (isSelfManaged) {
            connection.setAudioModeIsVoip(true);
        }
        connection.setTelecomCallId(str);
        if (connection.getState() != 6) {
            addConnection(request.getAccountHandle(), str, connection);
        }
        Uri address = connection.getAddress();
        String number = address == null ? "null" : address.getSchemeSpecificPart();
        Log.v(this, "createConnection, number: %s, state: %s, capabilities: %s, properties: %s", Connection.toLogSafePhoneNumber(number), Connection.stateToString(connection.getState()), Connection.capabilitiesToString(connection.getConnectionCapabilities()), Connection.propertiesToString(connection.getConnectionProperties()));
        Log.d(this, "createConnection, calling handleCreateConnectionSuccessful %s", str);
        ConnectionServiceAdapter connectionServiceAdapter = this.mAdapter;
        PhoneAccountHandle accountHandle = request.getAccountHandle();
        int state = connection.getState();
        int connectionCapabilities = connection.getConnectionCapabilities();
        int connectionProperties = connection.getConnectionProperties();
        int supportedAudioRoutes = connection.getSupportedAudioRoutes();
        Uri address2 = connection.getAddress();
        int addressPresentation = connection.getAddressPresentation();
        String callerDisplayName = connection.getCallerDisplayName();
        int callerDisplayNamePresentation = connection.getCallerDisplayNamePresentation();
        if (connection.getVideoProvider() != null) {
            iVideoProvider = connection.getVideoProvider().getInterface();
        }
        connectionServiceAdapter.handleCreateConnectionComplete(str, connectionRequest, new ParcelableConnection(accountHandle, state, connectionCapabilities, connectionProperties, supportedAudioRoutes, address2, addressPresentation, callerDisplayName, callerDisplayNamePresentation, iVideoProvider, connection.getVideoState(), connection.isRingbackRequested(), connection.getAudioModeIsVoip(), connection.getConnectTimeMillis(), connection.getConnectElapsedTimeMillis(), connection.getStatusHints(), connection.getDisconnectCause(), createIdList(connection.getConferenceables()), connection.getExtras()));
        if (isIncoming && request.shouldShowIncomingCallUi() && isSelfManaged) {
            connection.onShowIncomingCallUi();
        }
        if (isUnknown) {
            triggerConferenceRecalculate();
        }
    }

    private void createConnectionFailed(PhoneAccountHandle callManagerAccount, String callId, ConnectionRequest request, boolean isIncoming) {
        Log.i((Object) this, "createConnectionFailed %s", callId);
        if (isIncoming) {
            onCreateIncomingConnectionFailed(callManagerAccount, request);
        } else {
            onCreateOutgoingConnectionFailed(callManagerAccount, request);
        }
    }

    private void handoverFailed(String callId, ConnectionRequest request, int reason) {
        Log.i((Object) this, "handoverFailed %s", callId);
        onHandoverFailed(request, reason);
    }

    private void notifyCreateConnectionComplete(String callId) {
        Log.i((Object) this, "notifyCreateConnectionComplete %s", callId);
        if (callId == null) {
            Log.w((Object) this, "notifyCreateConnectionComplete: callId is null.", new Object[0]);
            return;
        }
        onCreateConnectionComplete(findConnectionForAction(callId, "notifyCreateConnectionComplete"));
    }

    private void abort(String callId) {
        Log.d((Object) this, "abort %s", callId);
        findConnectionForAction(callId, "abort").onAbort();
    }

    private void answerVideo(String callId, int videoState) {
        Log.d((Object) this, "answerVideo %s", callId);
        findConnectionForAction(callId, "answer").onAnswer(videoState);
    }

    private void answer(String callId) {
        Log.d((Object) this, "answer %s", callId);
        findConnectionForAction(callId, "answer").onAnswer();
    }

    private void deflect(String callId, Uri address) {
        Log.d((Object) this, "deflect %s", callId);
        findConnectionForAction(callId, "deflect").onDeflect(address);
    }

    private void reject(String callId) {
        Log.d((Object) this, "reject %s", callId);
        findConnectionForAction(callId, "reject").onReject();
    }

    private void reject(String callId, String rejectWithMessage) {
        Log.d((Object) this, "reject %s with message", callId);
        findConnectionForAction(callId, "reject").onReject(rejectWithMessage);
    }

    private void silence(String callId) {
        Log.d((Object) this, "silence %s", callId);
        findConnectionForAction(callId, "silence").onSilence();
    }

    private void disconnect(String callId) {
        Log.d((Object) this, "disconnect %s", callId);
        String str = "disconnect";
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, str).onDisconnect();
        } else {
            findConferenceForAction(callId, str).onDisconnect();
        }
    }

    private void hold(String callId) {
        Log.d((Object) this, "hold %s", callId);
        boolean containsKey = this.mConnectionById.containsKey(callId);
        String str = HapticFeedbackUtil.EFFECT_KEY_HOLD;
        if (containsKey) {
            findConnectionForAction(callId, str).onHold();
        } else {
            findConferenceForAction(callId, str).onHold();
        }
    }

    private void unhold(String callId) {
        Log.d((Object) this, "unhold %s", callId);
        String str = "unhold";
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, str).onUnhold();
        } else {
            findConferenceForAction(callId, str).onUnhold();
        }
    }

    private void onCallAudioStateChanged(String callId, CallAudioState callAudioState) {
        Log.d((Object) this, "onAudioStateChanged %s %s", callId, callAudioState);
        String str = "onCallAudioStateChanged";
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, str).setCallAudioState(callAudioState);
        } else {
            findConferenceForAction(callId, str).setCallAudioState(callAudioState);
        }
    }

    private void playDtmfTone(String callId, char digit) {
        Log.d((Object) this, "playDtmfTone %s %c", callId, Character.valueOf(digit));
        String str = "playDtmfTone";
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, str).onPlayDtmfTone(digit);
        } else {
            findConferenceForAction(callId, str).onPlayDtmfTone(digit);
        }
    }

    private void stopDtmfTone(String callId) {
        Log.d((Object) this, "stopDtmfTone %s", callId);
        String str = "stopDtmfTone";
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, str).onStopDtmfTone();
        } else {
            findConferenceForAction(callId, str).onStopDtmfTone();
        }
    }

    private void conference(String callId1, String callId2) {
        Log.d((Object) this, "conference %s, %s", callId1, callId2);
        Connection connection1 = ImsCallProfile.EXTRA_CONFERENCE;
        Connection connection2 = findConnectionForAction(callId2, connection1);
        Conference conference2 = getNullConference();
        if (connection2 == getNullConnection()) {
            conference2 = findConferenceForAction(callId2, connection1);
            if (conference2 == getNullConference()) {
                Log.w((Object) this, "Connection2 or Conference2 missing in conference request %s.", callId2);
                return;
            }
        }
        connection1 = findConnectionForAction(callId1, connection1);
        if (connection1 == getNullConnection()) {
            Conference conference1 = findConferenceForAction(callId1, "addConnection");
            if (conference1 == getNullConference()) {
                Log.w((Object) this, "Connection1 or Conference1 missing in conference request %s.", callId1);
            } else if (connection2 != getNullConnection()) {
                conference1.onMerge(connection2);
            } else {
                Log.wtf((Object) this, "There can only be one conference and an attempt was made to merge two conferences.", new Object[0]);
            }
        } else if (conference2 != getNullConference()) {
            conference2.onMerge(connection1);
        } else {
            onConference(connection1, connection2);
        }
    }

    private void splitFromConference(String callId) {
        Log.d((Object) this, "splitFromConference(%s)", callId);
        Connection connection = findConnectionForAction(callId, "splitFromConference");
        if (connection == getNullConnection()) {
            Log.w((Object) this, "Connection missing in conference request %s.", callId);
            return;
        }
        Conference conference = connection.getConference();
        if (conference != null) {
            conference.onSeparate(connection);
        }
    }

    private void addParticipantWithConference(String callId, String participant) {
        Log.d((Object) this, "ConnectionService addParticipantWithConference(%s, %s)", participant, callId);
        Conference conference = findConferenceForAction(callId, "addParticipantWithConference");
        Connection connection = findConnectionForAction(callId, "addParticipantWithConnection");
        if (connection != getNullConnection()) {
            onAddParticipant(connection, participant);
        } else if (conference != getNullConference()) {
            conference.onAddParticipant(participant);
        }
    }

    private void mergeConference(String callId) {
        Log.d((Object) this, "mergeConference(%s)", callId);
        Conference conference = findConferenceForAction(callId, "mergeConference");
        if (conference != null) {
            conference.onMerge();
        }
    }

    private void swapConference(String callId) {
        Log.d((Object) this, "swapConference(%s)", callId);
        Conference conference = findConferenceForAction(callId, "swapConference");
        if (conference != null) {
            conference.onSwap();
        }
    }

    private void pullExternalCall(String callId) {
        Log.d((Object) this, "pullExternalCall(%s)", callId);
        Connection connection = findConnectionForAction(callId, "pullExternalCall");
        if (connection != null) {
            connection.onPullExternalCall();
        }
    }

    private void sendCallEvent(String callId, String event, Bundle extras) {
        Log.d((Object) this, "sendCallEvent(%s, %s)", callId, event);
        Connection connection = findConnectionForAction(callId, "sendCallEvent");
        if (connection != null) {
            connection.onCallEvent(event, extras);
        }
    }

    private void notifyHandoverComplete(String callId) {
        Log.d((Object) this, "notifyHandoverComplete(%s)", callId);
        Connection connection = findConnectionForAction(callId, "notifyHandoverComplete");
        if (connection != null) {
            connection.onHandoverComplete();
        }
    }

    private void handleExtrasChanged(String callId, Bundle extras) {
        Log.d((Object) this, "handleExtrasChanged(%s, %s)", callId, extras);
        String str = "handleExtrasChanged";
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, str).handleExtrasChanged(extras);
        } else if (this.mConferenceById.containsKey(callId)) {
            findConferenceForAction(callId, str).handleExtrasChanged(extras);
        }
    }

    private void startRtt(String callId, RttTextStream rttTextStream) {
        Log.d((Object) this, "startRtt(%s)", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "startRtt").onStartRtt(rttTextStream);
        } else if (this.mConferenceById.containsKey(callId)) {
            Log.w((Object) this, "startRtt called on a conference.", new Object[0]);
        }
    }

    private void stopRtt(String callId) {
        Log.d((Object) this, "stopRtt(%s)", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "stopRtt").onStopRtt();
        } else if (this.mConferenceById.containsKey(callId)) {
            Log.w((Object) this, "stopRtt called on a conference.", new Object[0]);
        }
    }

    private void handleRttUpgradeResponse(String callId, RttTextStream rttTextStream) {
        Object[] objArr = new Object[2];
        objArr[0] = callId;
        objArr[1] = Boolean.valueOf(rttTextStream == null);
        Log.d((Object) this, "handleRttUpgradeResponse(%s, %s)", objArr);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "handleRttUpgradeResponse").handleRttUpgradeResponse(rttTextStream);
        } else if (this.mConferenceById.containsKey(callId)) {
            Log.w((Object) this, "handleRttUpgradeResponse called on a conference.", new Object[0]);
        }
    }

    private void onPostDialContinue(String callId, boolean proceed) {
        Log.d((Object) this, "onPostDialContinue(%s)", callId);
        findConnectionForAction(callId, "stopDtmfTone").onPostDialContinue(proceed);
    }

    private void onAdapterAttached() {
        if (!this.mAreAccountsInitialized) {
            this.mAdapter.queryRemoteConnectionServices(new RemoteServiceCallback.Stub() {
                public void onResult(List<ComponentName> componentNames, List<IBinder> services) {
                    final List<ComponentName> list = componentNames;
                    final List<IBinder> list2 = services;
                    ConnectionService.this.mHandler.post(new Runnable("oAA.qRCS.oR", null) {
                        public void loggedRun() {
                            int i = 0;
                            while (i < list.size() && i < list2.size()) {
                                ConnectionService.this.mRemoteConnectionManager.addConnectionService((ComponentName) list.get(i), Stub.asInterface((IBinder) list2.get(i)));
                                i++;
                            }
                            ConnectionService.this.onAccountsInitialized();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("remote connection services found: ");
                            stringBuilder.append(list2);
                            Log.d((Object) this, stringBuilder.toString(), new Object[0]);
                        }
                    }.prepare());
                }

                public void onError() {
                    ConnectionService.this.mHandler.post(new Runnable("oAA.qRCS.oE", null) {
                        public void loggedRun() {
                            ConnectionService.this.mAreAccountsInitialized = true;
                        }
                    }.prepare());
                }
            }, getOpPackageName());
        }
    }

    public final RemoteConnection createRemoteIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return this.mRemoteConnectionManager.createRemoteConnection(connectionManagerPhoneAccount, request, true);
    }

    public final RemoteConnection createRemoteOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return this.mRemoteConnectionManager.createRemoteConnection(connectionManagerPhoneAccount, request, false);
    }

    public final void conferenceRemoteConnections(RemoteConnection remoteConnection1, RemoteConnection remoteConnection2) {
        this.mRemoteConnectionManager.conferenceRemoteConnections(remoteConnection1, remoteConnection2);
    }

    public final void addConference(Conference conference) {
        Log.d(this, "addConference: conference=%s", conference);
        String id = addConferenceInternal(conference);
        if (id != null) {
            List<String> connectionIds = new ArrayList(2);
            for (Connection connection : conference.getConnections()) {
                if (this.mIdByConnection.containsKey(connection)) {
                    connectionIds.add((String) this.mIdByConnection.get(connection));
                }
            }
            r1.setTelecomCallId(id);
            this.mAdapter.addConferenceCall(id, new ParcelableConference(conference.getPhoneAccountHandle(), conference.getState(), conference.getConnectionCapabilities(), conference.getConnectionProperties(), connectionIds, conference.getVideoProvider() == null ? null : conference.getVideoProvider().getInterface(), conference.getVideoState(), conference.getConnectTimeMillis(), conference.getConnectionStartElapsedRealTime(), conference.getStatusHints(), conference.getExtras(), conference.getAddress(), conference.getAddressPresentation(), conference.getCallerDisplayName(), conference.getCallerDisplayNamePresentation()));
            this.mAdapter.setVideoProvider(id, conference.getVideoProvider());
            this.mAdapter.setVideoState(id, conference.getVideoState());
            for (Connection connection2 : conference.getConnections()) {
                String connectionId = (String) this.mIdByConnection.get(connection2);
                if (connectionId != null) {
                    this.mAdapter.setIsConferenced(connectionId, id);
                }
            }
            onConferenceAdded(conference);
        }
    }

    public final void addExistingConnection(PhoneAccountHandle phoneAccountHandle, Connection connection) {
        addExistingConnection(phoneAccountHandle, connection, null);
    }

    public final void connectionServiceFocusReleased() {
        this.mAdapter.onConnectionServiceFocusReleased();
    }

    public final void addExistingConnection(PhoneAccountHandle phoneAccountHandle, Connection connection, Conference conference) {
        Conference conference2 = conference;
        String id = addExistingConnectionInternal(phoneAccountHandle, connection);
        if (id != null) {
            String conferenceId;
            ArrayList emptyList = new ArrayList(0);
            if (conference2 != null) {
                conferenceId = (String) this.mIdByConference.get(conference2);
            } else {
                conferenceId = null;
            }
            this.mAdapter.addExistingConnection(id, new ParcelableConnection(phoneAccountHandle, connection.getState(), connection.getConnectionCapabilities(), connection.getConnectionProperties(), connection.getSupportedAudioRoutes(), connection.getAddress(), connection.getAddressPresentation(), connection.getCallerDisplayName(), connection.getCallerDisplayNamePresentation(), connection.getVideoProvider() == null ? null : connection.getVideoProvider().getInterface(), connection.getVideoState(), connection.isRingbackRequested(), connection.getAudioModeIsVoip(), connection.getConnectTimeMillis(), connection.getConnectElapsedTimeMillis(), connection.getStatusHints(), connection.getDisconnectCause(), emptyList, connection.getExtras(), conferenceId, connection.getCallDirection()));
        }
    }

    public final Collection<Connection> getAllConnections() {
        return this.mConnectionById.values();
    }

    public final Collection<Conference> getAllConferences() {
        return this.mConferenceById.values();
    }

    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return null;
    }

    public void onCreateConnectionComplete(Connection connection) {
    }

    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
    }

    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
    }

    public void triggerConferenceRecalculate() {
    }

    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return null;
    }

    public Connection onCreateOutgoingHandoverConnection(PhoneAccountHandle fromPhoneAccountHandle, ConnectionRequest request) {
        return null;
    }

    public Connection onCreateIncomingHandoverConnection(PhoneAccountHandle fromPhoneAccountHandle, ConnectionRequest request) {
        return null;
    }

    public void onHandoverFailed(ConnectionRequest request, int error) {
    }

    public Connection onCreateUnknownConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return null;
    }

    public void onConference(Connection connection1, Connection connection2) {
    }

    public void onConnectionAdded(Connection connection) {
    }

    public void onConnectionRemoved(Connection connection) {
    }

    public void onConferenceAdded(Conference conference) {
    }

    public void onConferenceRemoved(Conference conference) {
    }

    public void onAddParticipant(Connection connection, String participant) {
    }

    public void onRemoteConferenceAdded(RemoteConference conference) {
    }

    public void onRemoteExistingConnectionAdded(RemoteConnection connection) {
    }

    public void onConnectionServiceFocusLost() {
    }

    public void onConnectionServiceFocusGained() {
    }

    public boolean containsConference(Conference conference) {
        return this.mIdByConference.containsKey(conference);
    }

    /* Access modifiers changed, original: 0000 */
    public void addRemoteConference(RemoteConference remoteConference) {
        onRemoteConferenceAdded(remoteConference);
    }

    /* Access modifiers changed, original: 0000 */
    public void addRemoteExistingConnection(RemoteConnection remoteConnection) {
        onRemoteExistingConnectionAdded(remoteConnection);
    }

    private void onAccountsInitialized() {
        this.mAreAccountsInitialized = true;
        for (Runnable r : this.mPreInitializationConnectionRequests) {
            r.run();
        }
        this.mPreInitializationConnectionRequests.clear();
    }

    private String addExistingConnectionInternal(PhoneAccountHandle handle, Connection connection) {
        String id;
        if (connection.getExtras() != null) {
            Bundle extras = connection.getExtras();
            String str = Connection.EXTRA_ORIGINAL_CONNECTION_ID;
            if (extras.containsKey(str)) {
                id = connection.getExtras().getString(str);
                Log.d((Object) this, "addExistingConnectionInternal - conn %s reusing original id %s", connection.getTelecomCallId(), id);
                addConnection(handle, id, connection);
                return id;
            }
        }
        if (handle == null) {
            id = UUID.randomUUID().toString();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(handle.getComponentName().getClassName());
            stringBuilder.append("@");
            stringBuilder.append(getNextCallId());
            id = stringBuilder.toString();
        }
        addConnection(handle, id, connection);
        return id;
    }

    private void addConnection(PhoneAccountHandle handle, String callId, Connection connection) {
        connection.setTelecomCallId(callId);
        this.mConnectionById.put(callId, connection);
        this.mIdByConnection.put(connection, callId);
        connection.addConnectionListener(this.mConnectionListener);
        connection.setConnectionService(this);
        connection.setPhoneAccountHandle(handle);
        onConnectionAdded(connection);
    }

    /* Access modifiers changed, original: protected */
    public void removeConnection(Connection connection) {
        connection.unsetConnectionService(this);
        connection.removeConnectionListener(this.mConnectionListener);
        String id = (String) this.mIdByConnection.get(connection);
        if (id != null) {
            this.mConnectionById.remove(id);
            this.mIdByConnection.remove(connection);
            this.mAdapter.removeCall(id);
            onConnectionRemoved(connection);
        }
    }

    private String addConferenceInternal(Conference conference) {
        String originalId = null;
        if (conference.getExtras() != null) {
            Bundle extras = conference.getExtras();
            String str = Connection.EXTRA_ORIGINAL_CONNECTION_ID;
            if (extras.containsKey(str)) {
                originalId = conference.getExtras().getString(str);
                Log.d((Object) this, "addConferenceInternal: conf %s reusing original id %s", conference.getTelecomCallId(), originalId);
            }
        }
        if (this.mIdByConference.containsKey(conference)) {
            Log.w((Object) this, "Re-adding an existing conference: %s.", conference);
            return null;
        }
        String id = originalId == null ? UUID.randomUUID().toString() : originalId;
        this.mConferenceById.put(id, conference);
        this.mIdByConference.put(conference, id);
        conference.addListener(this.mConferenceListener);
        return id;
    }

    private void removeConference(Conference conference) {
        if (this.mIdByConference.containsKey(conference)) {
            conference.removeListener(this.mConferenceListener);
            String id = (String) this.mIdByConference.get(conference);
            this.mConferenceById.remove(id);
            this.mIdByConference.remove(conference);
            this.mAdapter.removeCall(id);
            onConferenceRemoved(conference);
        }
    }

    private Connection findConnectionForAction(String callId, String action) {
        if (callId != null && this.mConnectionById.containsKey(callId)) {
            return (Connection) this.mConnectionById.get(callId);
        }
        Log.w((Object) this, "%s - Cannot find Connection %s", action, callId);
        return getNullConnection();
    }

    static synchronized Connection getNullConnection() {
        Connection connection;
        synchronized (ConnectionService.class) {
            if (sNullConnection == null) {
                sNullConnection = new Connection() {
                };
            }
            connection = sNullConnection;
        }
        return connection;
    }

    private Conference findConferenceForAction(String conferenceId, String action) {
        if (this.mConferenceById.containsKey(conferenceId)) {
            return (Conference) this.mConferenceById.get(conferenceId);
        }
        Log.w((Object) this, "%s - Cannot find conference %s", action, conferenceId);
        return getNullConference();
    }

    private List<String> createConnectionIdList(List<Connection> connections) {
        List<String> ids = new ArrayList();
        for (Connection c : connections) {
            if (this.mIdByConnection.containsKey(c)) {
                ids.add((String) this.mIdByConnection.get(c));
            }
        }
        Collections.sort(ids);
        return ids;
    }

    private List<String> createIdList(List<Conferenceable> conferenceables) {
        List<String> ids = new ArrayList();
        for (Conferenceable c : conferenceables) {
            if (c instanceof Connection) {
                Connection connection = (Connection) c;
                if (this.mIdByConnection.containsKey(connection)) {
                    ids.add((String) this.mIdByConnection.get(connection));
                }
            } else if (c instanceof Conference) {
                Conference conference = (Conference) c;
                if (this.mIdByConference.containsKey(conference)) {
                    ids.add((String) this.mIdByConference.get(conference));
                }
            }
        }
        Collections.sort(ids);
        return ids;
    }

    private Conference getNullConference() {
        if (this.sNullConference == null) {
            this.sNullConference = new Conference(null) {
            };
        }
        return this.sNullConference;
    }

    private void endAllConnections() {
        for (Connection connection : this.mIdByConnection.keySet()) {
            if (connection.getConference() == null) {
                connection.onDisconnect();
            }
        }
        for (Conference conference : this.mIdByConference.keySet()) {
            conference.onDisconnect();
        }
    }

    private int getNextCallId() {
        int i;
        synchronized (this.mIdSyncRoot) {
            i = this.mId + 1;
            this.mId = i;
        }
        return i;
    }
}
