package android.hardware.hdmi;

import android.annotation.SystemApi;
import android.hardware.hdmi.HdmiRecordSources.AnalogueServiceSource;
import android.hardware.hdmi.HdmiRecordSources.DigitalServiceSource;
import android.hardware.hdmi.HdmiRecordSources.ExternalPhysicalAddress;
import android.hardware.hdmi.HdmiRecordSources.ExternalPlugData;
import android.hardware.hdmi.HdmiRecordSources.RecordSource;
import android.util.Log;

@SystemApi
public class HdmiTimerRecordSources {
    private static final int EXTERNAL_SOURCE_SPECIFIER_EXTERNAL_PHYSICAL_ADDRESS = 5;
    private static final int EXTERNAL_SOURCE_SPECIFIER_EXTERNAL_PLUG = 4;
    public static final int RECORDING_SEQUENCE_REPEAT_FRIDAY = 32;
    private static final int RECORDING_SEQUENCE_REPEAT_MASK = 127;
    public static final int RECORDING_SEQUENCE_REPEAT_MONDAY = 2;
    public static final int RECORDING_SEQUENCE_REPEAT_ONCE_ONLY = 0;
    public static final int RECORDING_SEQUENCE_REPEAT_SATUREDAY = 64;
    public static final int RECORDING_SEQUENCE_REPEAT_SUNDAY = 1;
    public static final int RECORDING_SEQUENCE_REPEAT_THURSDAY = 16;
    public static final int RECORDING_SEQUENCE_REPEAT_TUESDAY = 4;
    public static final int RECORDING_SEQUENCE_REPEAT_WEDNESDAY = 8;
    private static final String TAG = "HdmiTimerRecordingSources";

    static class TimeUnit {
        final int mHour;
        final int mMinute;

        TimeUnit(int hour, int minute) {
            this.mHour = hour;
            this.mMinute = minute;
        }

        /* Access modifiers changed, original: 0000 */
        public int toByteArray(byte[] data, int index) {
            data[index] = toBcdByte(this.mHour);
            data[index + 1] = toBcdByte(this.mMinute);
            return 2;
        }

        static byte toBcdByte(int value) {
            return (byte) ((((value / 10) % 10) << 4) | (value % 10));
        }
    }

    @SystemApi
    public static final class Duration extends TimeUnit {
        private Duration(int hour, int minute) {
            super(hour, minute);
        }
    }

    private static class ExternalSourceDecorator extends RecordSource {
        private final int mExternalSourceSpecifier;
        private final RecordSource mRecordSource;

        private ExternalSourceDecorator(RecordSource recordSource, int externalSourceSpecifier) {
            super(recordSource.mSourceType, recordSource.getDataSize(false) + 1);
            this.mRecordSource = recordSource;
            this.mExternalSourceSpecifier = externalSourceSpecifier;
        }

        /* Access modifiers changed, original: 0000 */
        public int extraParamToByteArray(byte[] data, int index) {
            data[index] = (byte) this.mExternalSourceSpecifier;
            this.mRecordSource.toByteArray(false, data, index + 1);
            return getDataSize(false);
        }
    }

    @SystemApi
    public static final class Time extends TimeUnit {
        private Time(int hour, int minute) {
            super(hour, minute);
        }
    }

    @SystemApi
    public static final class TimerInfo {
        private static final int BASIC_INFO_SIZE = 7;
        private static final int DAY_OF_MONTH_SIZE = 1;
        private static final int DURATION_SIZE = 2;
        private static final int MONTH_OF_YEAR_SIZE = 1;
        private static final int RECORDING_SEQUENCE_SIZE = 1;
        private static final int START_TIME_SIZE = 2;
        private final int mDayOfMonth;
        private final Duration mDuration;
        private final int mMonthOfYear;
        private final int mRecordingSequence;
        private final Time mStartTime;

        private TimerInfo(int dayOfMonth, int monthOfYear, Time startTime, Duration duration, int recordingSequence) {
            this.mDayOfMonth = dayOfMonth;
            this.mMonthOfYear = monthOfYear;
            this.mStartTime = startTime;
            this.mDuration = duration;
            this.mRecordingSequence = recordingSequence;
        }

        /* Access modifiers changed, original: 0000 */
        public int toByteArray(byte[] data, int index) {
            data[index] = (byte) this.mDayOfMonth;
            index++;
            data[index] = (byte) this.mMonthOfYear;
            index++;
            index += this.mStartTime.toByteArray(data, index);
            data[index + this.mDuration.toByteArray(data, index)] = (byte) this.mRecordingSequence;
            return getDataSize();
        }

        /* Access modifiers changed, original: 0000 */
        public int getDataSize() {
            return 7;
        }
    }

    @SystemApi
    public static final class TimerRecordSource {
        private final RecordSource mRecordSource;
        private final TimerInfo mTimerInfo;

        private TimerRecordSource(TimerInfo timerInfo, RecordSource recordSource) {
            this.mTimerInfo = timerInfo;
            this.mRecordSource = recordSource;
        }

        /* Access modifiers changed, original: 0000 */
        public int getDataSize() {
            return this.mTimerInfo.getDataSize() + this.mRecordSource.getDataSize(false);
        }

        /* Access modifiers changed, original: 0000 */
        public int toByteArray(byte[] data, int index) {
            this.mRecordSource.toByteArray(false, data, index + this.mTimerInfo.toByteArray(data, index));
            return getDataSize();
        }
    }

    private HdmiTimerRecordSources() {
    }

    public static TimerRecordSource ofDigitalSource(TimerInfo timerInfo, DigitalServiceSource source) {
        checkTimerRecordSourceInputs(timerInfo, source);
        return new TimerRecordSource(timerInfo, source);
    }

    public static TimerRecordSource ofAnalogueSource(TimerInfo timerInfo, AnalogueServiceSource source) {
        checkTimerRecordSourceInputs(timerInfo, source);
        return new TimerRecordSource(timerInfo, source);
    }

    public static TimerRecordSource ofExternalPlug(TimerInfo timerInfo, ExternalPlugData source) {
        checkTimerRecordSourceInputs(timerInfo, source);
        return new TimerRecordSource(timerInfo, new ExternalSourceDecorator(source, 4));
    }

    public static TimerRecordSource ofExternalPhysicalAddress(TimerInfo timerInfo, ExternalPhysicalAddress source) {
        checkTimerRecordSourceInputs(timerInfo, source);
        return new TimerRecordSource(timerInfo, new ExternalSourceDecorator(source, 5));
    }

    private static void checkTimerRecordSourceInputs(TimerInfo timerInfo, RecordSource source) {
        String str = TAG;
        String str2;
        if (timerInfo == null) {
            str2 = "TimerInfo should not be null.";
            Log.w(str, str2);
            throw new IllegalArgumentException(str2);
        } else if (source == null) {
            str2 = "source should not be null.";
            Log.w(str, str2);
            throw new IllegalArgumentException(str2);
        }
    }

    public static Time timeOf(int hour, int minute) {
        checkTimeValue(hour, minute);
        return new Time(hour, minute);
    }

    private static void checkTimeValue(int hour, int minute) {
        StringBuilder stringBuilder;
        if (hour < 0 || hour > 23) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Hour should be in rage of [0, 23]:");
            stringBuilder.append(hour);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (minute < 0 || minute > 59) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Minute should be in rage of [0, 59]:");
            stringBuilder.append(minute);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public static Duration durationOf(int hour, int minute) {
        checkDurationValue(hour, minute);
        return new Duration(hour, minute);
    }

    private static void checkDurationValue(int hour, int minute) {
        StringBuilder stringBuilder;
        if (hour < 0 || hour > 99) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Hour should be in rage of [0, 99]:");
            stringBuilder.append(hour);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (minute < 0 || minute > 59) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("minute should be in rage of [0, 59]:");
            stringBuilder.append(minute);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public static TimerInfo timerInfoOf(int dayOfMonth, int monthOfYear, Time startTime, Duration duration, int recordingSequence) {
        StringBuilder stringBuilder;
        if (dayOfMonth < 0 || dayOfMonth > 31) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Day of month should be in range of [0, 31]:");
            stringBuilder.append(dayOfMonth);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (monthOfYear < 1 || monthOfYear > 12) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Month of year should be in range of [1, 12]:");
            stringBuilder.append(monthOfYear);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else {
            checkTimeValue(startTime.mHour, startTime.mMinute);
            checkDurationValue(duration.mHour, duration.mMinute);
            if (recordingSequence == 0 || (recordingSequence & -128) == 0) {
                return new TimerInfo(dayOfMonth, monthOfYear, startTime, duration, recordingSequence);
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid reecording sequence value:");
            stringBuilder.append(recordingSequence);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    @SystemApi
    public static boolean checkTimerRecordSource(int sourcetype, byte[] recordSource) {
        int recordSourceSize = recordSource.length - 7;
        boolean z = true;
        if (sourcetype == 1) {
            if (7 != recordSourceSize) {
                z = false;
            }
            return z;
        } else if (sourcetype == 2) {
            if (4 != recordSourceSize) {
                z = false;
            }
            return z;
        } else if (sourcetype != 3) {
            return false;
        } else {
            int specifier = recordSource[7];
            if (specifier == 4) {
                if (2 != recordSourceSize) {
                    z = false;
                }
                return z;
            } else if (specifier != 5) {
                return false;
            } else {
                if (3 != recordSourceSize) {
                    z = false;
                }
                return z;
            }
        }
    }
}
