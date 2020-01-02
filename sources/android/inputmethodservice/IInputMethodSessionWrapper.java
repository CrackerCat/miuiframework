package android.inputmethodservice;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputMethodSession;
import android.view.inputmethod.InputMethodSession.EventCallback;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.HandlerCaller.Callback;
import com.android.internal.os.SomeArgs;
import com.android.internal.view.IInputMethodSession.Stub;

class IInputMethodSessionWrapper extends Stub implements Callback {
    private static final int DO_APP_PRIVATE_COMMAND = 100;
    private static final int DO_DISPLAY_COMPLETIONS = 65;
    private static final int DO_FINISH_SESSION = 110;
    private static final int DO_NOTIFY_IME_HIDDEN = 120;
    private static final int DO_TOGGLE_SOFT_INPUT = 105;
    private static final int DO_UPDATE_CURSOR = 95;
    private static final int DO_UPDATE_CURSOR_ANCHOR_INFO = 99;
    private static final int DO_UPDATE_EXTRACTED_TEXT = 67;
    private static final int DO_UPDATE_SELECTION = 90;
    private static final int DO_VIEW_CLICKED = 115;
    private static final String TAG = "InputMethodWrapper";
    @UnsupportedAppUsage
    HandlerCaller mCaller;
    InputChannel mChannel;
    InputMethodSession mInputMethodSession;
    ImeInputEventReceiver mReceiver;

    private final class ImeInputEventReceiver extends InputEventReceiver implements EventCallback {
        private final SparseArray<InputEvent> mPendingEvents = new SparseArray();

        public ImeInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        public void onInputEvent(InputEvent event) {
            if (IInputMethodSessionWrapper.this.mInputMethodSession == null) {
                finishInputEvent(event, false);
                return;
            }
            int seq = event.getSequenceNumber();
            this.mPendingEvents.put(seq, event);
            if (event instanceof KeyEvent) {
                IInputMethodSessionWrapper.this.mInputMethodSession.dispatchKeyEvent(seq, (KeyEvent) event, this);
            } else {
                MotionEvent motionEvent = (MotionEvent) event;
                if (motionEvent.isFromSource(4)) {
                    IInputMethodSessionWrapper.this.mInputMethodSession.dispatchTrackballEvent(seq, motionEvent, this);
                } else {
                    IInputMethodSessionWrapper.this.mInputMethodSession.dispatchGenericMotionEvent(seq, motionEvent, this);
                }
            }
        }

        public void finishedEvent(int seq, boolean handled) {
            int index = this.mPendingEvents.indexOfKey(seq);
            if (index >= 0) {
                InputEvent event = (InputEvent) this.mPendingEvents.valueAt(index);
                this.mPendingEvents.removeAt(index);
                finishInputEvent(event, handled);
            }
        }
    }

    public IInputMethodSessionWrapper(Context context, InputMethodSession inputMethodSession, InputChannel channel) {
        this.mCaller = new HandlerCaller(context, null, this, true);
        this.mInputMethodSession = inputMethodSession;
        this.mChannel = channel;
        if (channel != null) {
            this.mReceiver = new ImeInputEventReceiver(channel, context.getMainLooper());
        }
    }

    public InputMethodSession getInternalInputMethodSession() {
        return this.mInputMethodSession;
    }

    public void executeMessage(Message msg) {
        int i;
        if (this.mInputMethodSession == null) {
            i = msg.what;
            if (i == 90 || i == 100) {
                msg.obj.recycle();
            }
            return;
        }
        i = msg.what;
        SomeArgs args;
        if (i == 65) {
            this.mInputMethodSession.displayCompletions((CompletionInfo[]) msg.obj);
        } else if (i == 67) {
            this.mInputMethodSession.updateExtractedText(msg.arg1, (ExtractedText) msg.obj);
        } else if (i == 90) {
            args = (SomeArgs) msg.obj;
            this.mInputMethodSession.updateSelection(args.argi1, args.argi2, args.argi3, args.argi4, args.argi5, args.argi6);
            args.recycle();
        } else if (i == 95) {
            this.mInputMethodSession.updateCursor((Rect) msg.obj);
        } else if (i == 105) {
            this.mInputMethodSession.toggleSoftInput(msg.arg1, msg.arg2);
        } else if (i == 110) {
            doFinishSession();
        } else if (i == 115) {
            InputMethodSession inputMethodSession = this.mInputMethodSession;
            boolean z = true;
            if (msg.arg1 != 1) {
                z = false;
            }
            inputMethodSession.viewClicked(z);
        } else if (i == 120) {
            this.mInputMethodSession.notifyImeHidden();
        } else if (i == 99) {
            this.mInputMethodSession.updateCursorAnchorInfo((CursorAnchorInfo) msg.obj);
        } else if (i != 100) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unhandled message code: ");
            stringBuilder.append(msg.what);
            Log.w(TAG, stringBuilder.toString());
        } else {
            args = (SomeArgs) msg.obj;
            this.mInputMethodSession.appPrivateCommand((String) args.arg1, (Bundle) args.arg2);
            args.recycle();
        }
    }

    private void doFinishSession() {
        this.mInputMethodSession = null;
        ImeInputEventReceiver imeInputEventReceiver = this.mReceiver;
        if (imeInputEventReceiver != null) {
            imeInputEventReceiver.dispose();
            this.mReceiver = null;
        }
        InputChannel inputChannel = this.mChannel;
        if (inputChannel != null) {
            inputChannel.dispose();
            this.mChannel = null;
        }
    }

    public void displayCompletions(CompletionInfo[] completions) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(65, completions));
    }

    public void updateExtractedText(int token, ExtractedText text) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageIO(67, token, text));
    }

    public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageIIIIII(90, oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd));
    }

    public void viewClicked(boolean focusChanged) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageI(115, focusChanged));
    }

    public void notifyImeHidden() {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessage(120));
    }

    public void updateCursor(Rect newCursor) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(95, newCursor));
    }

    public void updateCursorAnchorInfo(CursorAnchorInfo cursorAnchorInfo) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageO(99, cursorAnchorInfo));
    }

    public void appPrivateCommand(String action, Bundle data) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageOO(100, action, data));
    }

    public void toggleSoftInput(int showFlags, int hideFlags) {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessageII(105, showFlags, hideFlags));
    }

    public void finishSession() {
        HandlerCaller handlerCaller = this.mCaller;
        handlerCaller.executeOrSendMessage(handlerCaller.obtainMessage(110));
    }
}
