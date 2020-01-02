package android.hardware.hdmi;

import android.annotation.SystemApi;
import android.util.Log;

@SystemApi
public final class HdmiRecordSources {
    public static final int ANALOGUE_BROADCAST_TYPE_CABLE = 0;
    public static final int ANALOGUE_BROADCAST_TYPE_SATELLITE = 1;
    public static final int ANALOGUE_BROADCAST_TYPE_TERRESTRIAL = 2;
    public static final int BROADCAST_SYSTEM_NTSC_M = 3;
    public static final int BROADCAST_SYSTEM_PAL_BG = 0;
    public static final int BROADCAST_SYSTEM_PAL_DK = 8;
    public static final int BROADCAST_SYSTEM_PAL_I = 4;
    public static final int BROADCAST_SYSTEM_PAL_M = 2;
    public static final int BROADCAST_SYSTEM_PAL_OTHER_SYSTEM = 31;
    public static final int BROADCAST_SYSTEM_SECAM_BG = 6;
    public static final int BROADCAST_SYSTEM_SECAM_DK = 5;
    public static final int BROADCAST_SYSTEM_SECAM_L = 7;
    public static final int BROADCAST_SYSTEM_SECAM_LP = 1;
    private static final int CHANNEL_NUMBER_FORMAT_1_PART = 1;
    private static final int CHANNEL_NUMBER_FORMAT_2_PART = 2;
    public static final int DIGITAL_BROADCAST_TYPE_ARIB = 0;
    public static final int DIGITAL_BROADCAST_TYPE_ARIB_BS = 8;
    public static final int DIGITAL_BROADCAST_TYPE_ARIB_CS = 9;
    public static final int DIGITAL_BROADCAST_TYPE_ARIB_T = 10;
    public static final int DIGITAL_BROADCAST_TYPE_ATSC = 1;
    public static final int DIGITAL_BROADCAST_TYPE_ATSC_CABLE = 16;
    public static final int DIGITAL_BROADCAST_TYPE_ATSC_SATELLITE = 17;
    public static final int DIGITAL_BROADCAST_TYPE_ATSC_TERRESTRIAL = 18;
    public static final int DIGITAL_BROADCAST_TYPE_DVB = 2;
    public static final int DIGITAL_BROADCAST_TYPE_DVB_C = 24;
    public static final int DIGITAL_BROADCAST_TYPE_DVB_S = 25;
    public static final int DIGITAL_BROADCAST_TYPE_DVB_S2 = 26;
    public static final int DIGITAL_BROADCAST_TYPE_DVB_T = 27;
    private static final int RECORD_SOURCE_TYPE_ANALOGUE_SERVICE = 3;
    private static final int RECORD_SOURCE_TYPE_DIGITAL_SERVICE = 2;
    private static final int RECORD_SOURCE_TYPE_EXTERNAL_PHYSICAL_ADDRESS = 5;
    private static final int RECORD_SOURCE_TYPE_EXTERNAL_PLUG = 4;
    private static final int RECORD_SOURCE_TYPE_OWN_SOURCE = 1;
    private static final String TAG = "HdmiRecordSources";

    @SystemApi
    public static abstract class RecordSource {
        final int mExtraDataSize;
        final int mSourceType;

        public abstract int extraParamToByteArray(byte[] bArr, int i);

        RecordSource(int sourceType, int extraDataSize) {
            this.mSourceType = sourceType;
            this.mExtraDataSize = extraDataSize;
        }

        /* Access modifiers changed, original: final */
        public final int getDataSize(boolean includeType) {
            int i = this.mExtraDataSize;
            return includeType ? i + 1 : i;
        }

        /* Access modifiers changed, original: final */
        public final int toByteArray(boolean includeType, byte[] data, int index) {
            if (includeType) {
                int index2 = index + 1;
                data[index] = (byte) this.mSourceType;
                index = index2;
            }
            extraParamToByteArray(data, index);
            return getDataSize(includeType);
        }
    }

    @SystemApi
    public static final class AnalogueServiceSource extends RecordSource {
        static final int EXTRA_DATA_SIZE = 4;
        private final int mBroadcastSystem;
        private final int mBroadcastType;
        private final int mFrequency;

        private AnalogueServiceSource(int broadcastType, int frequency, int broadcastSystem) {
            super(3, 4);
            this.mBroadcastType = broadcastType;
            this.mFrequency = frequency;
            this.mBroadcastSystem = broadcastSystem;
        }

        /* Access modifiers changed, original: 0000 */
        public int extraParamToByteArray(byte[] data, int index) {
            data[index] = (byte) this.mBroadcastType;
            HdmiRecordSources.shortToByteArray((short) this.mFrequency, data, index + 1);
            data[index + 3] = (byte) this.mBroadcastSystem;
            return 4;
        }
    }

    private interface DigitalServiceIdentification {
        int toByteArray(byte[] bArr, int i);
    }

    public static final class AribData implements DigitalServiceIdentification {
        private final int mOriginalNetworkId;
        private final int mServiceId;
        private final int mTransportStreamId;

        public AribData(int transportStreamId, int serviceId, int originalNetworkId) {
            this.mTransportStreamId = transportStreamId;
            this.mServiceId = serviceId;
            this.mOriginalNetworkId = originalNetworkId;
        }

        public int toByteArray(byte[] data, int index) {
            return HdmiRecordSources.threeFieldsToSixBytes(this.mTransportStreamId, this.mServiceId, this.mOriginalNetworkId, data, index);
        }
    }

    public static final class AtscData implements DigitalServiceIdentification {
        private final int mProgramNumber;
        private final int mTransportStreamId;

        public AtscData(int transportStreamId, int programNumber) {
            this.mTransportStreamId = transportStreamId;
            this.mProgramNumber = programNumber;
        }

        public int toByteArray(byte[] data, int index) {
            return HdmiRecordSources.threeFieldsToSixBytes(this.mTransportStreamId, this.mProgramNumber, 0, data, index);
        }
    }

    private static final class ChannelIdentifier {
        private final int mChannelNumberFormat;
        private final int mMajorChannelNumber;
        private final int mMinorChannelNumber;

        private ChannelIdentifier(int format, int majorNumber, int minorNumer) {
            this.mChannelNumberFormat = format;
            this.mMajorChannelNumber = majorNumber;
            this.mMinorChannelNumber = minorNumer;
        }

        private int toByteArray(byte[] data, int index) {
            int i = this.mChannelNumberFormat << 2;
            int i2 = this.mMajorChannelNumber;
            data[index] = (byte) (i | ((i2 >>> 8) & 3));
            data[index + 1] = (byte) (i2 & 255);
            HdmiRecordSources.shortToByteArray((short) this.mMinorChannelNumber, data, index + 2);
            return 4;
        }
    }

    public static final class DigitalChannelData implements DigitalServiceIdentification {
        private final ChannelIdentifier mChannelIdentifier;

        public static DigitalChannelData ofTwoNumbers(int majorNumber, int minorNumber) {
            return new DigitalChannelData(new ChannelIdentifier(2, majorNumber, minorNumber));
        }

        public static DigitalChannelData ofOneNumber(int number) {
            return new DigitalChannelData(new ChannelIdentifier(1, 0, number));
        }

        private DigitalChannelData(ChannelIdentifier id) {
            this.mChannelIdentifier = id;
        }

        public int toByteArray(byte[] data, int index) {
            this.mChannelIdentifier.toByteArray(data, index);
            data[index + 4] = (byte) 0;
            data[index + 5] = (byte) 0;
            return 6;
        }
    }

    @SystemApi
    public static final class DigitalServiceSource extends RecordSource {
        private static final int DIGITAL_SERVICE_IDENTIFIED_BY_CHANNEL = 1;
        private static final int DIGITAL_SERVICE_IDENTIFIED_BY_DIGITAL_ID = 0;
        static final int EXTRA_DATA_SIZE = 7;
        private final int mBroadcastSystem;
        private final DigitalServiceIdentification mIdentification;
        private final int mIdentificationMethod;

        private DigitalServiceSource(int identificatinoMethod, int broadcastSystem, DigitalServiceIdentification identification) {
            super(2, 7);
            this.mIdentificationMethod = identificatinoMethod;
            this.mBroadcastSystem = broadcastSystem;
            this.mIdentification = identification;
        }

        /* Access modifiers changed, original: 0000 */
        public int extraParamToByteArray(byte[] data, int index) {
            data[index] = (byte) ((this.mIdentificationMethod << 7) | (this.mBroadcastSystem & 127));
            this.mIdentification.toByteArray(data, index + 1);
            return 7;
        }
    }

    public static final class DvbData implements DigitalServiceIdentification {
        private final int mOriginalNetworkId;
        private final int mServiceId;
        private final int mTransportStreamId;

        public DvbData(int transportStreamId, int serviceId, int originalNetworkId) {
            this.mTransportStreamId = transportStreamId;
            this.mServiceId = serviceId;
            this.mOriginalNetworkId = originalNetworkId;
        }

        public int toByteArray(byte[] data, int index) {
            return HdmiRecordSources.threeFieldsToSixBytes(this.mTransportStreamId, this.mServiceId, this.mOriginalNetworkId, data, index);
        }
    }

    @SystemApi
    public static final class ExternalPhysicalAddress extends RecordSource {
        static final int EXTRA_DATA_SIZE = 2;
        private final int mPhysicalAddress;

        private ExternalPhysicalAddress(int physicalAddress) {
            super(5, 2);
            this.mPhysicalAddress = physicalAddress;
        }

        /* Access modifiers changed, original: 0000 */
        public int extraParamToByteArray(byte[] data, int index) {
            HdmiRecordSources.shortToByteArray((short) this.mPhysicalAddress, data, index);
            return 2;
        }
    }

    @SystemApi
    public static final class ExternalPlugData extends RecordSource {
        static final int EXTRA_DATA_SIZE = 1;
        private final int mPlugNumber;

        private ExternalPlugData(int plugNumber) {
            super(4, 1);
            this.mPlugNumber = plugNumber;
        }

        /* Access modifiers changed, original: 0000 */
        public int extraParamToByteArray(byte[] data, int index) {
            data[index] = (byte) this.mPlugNumber;
            return 1;
        }
    }

    @SystemApi
    public static final class OwnSource extends RecordSource {
        private static final int EXTRA_DATA_SIZE = 0;

        private OwnSource() {
            super(1, 0);
        }

        /* Access modifiers changed, original: 0000 */
        public int extraParamToByteArray(byte[] data, int index) {
            return 0;
        }
    }

    private HdmiRecordSources() {
    }

    public static OwnSource ofOwnSource() {
        return new OwnSource();
    }

    public static DigitalServiceSource ofDigitalChannelId(int broadcastSystem, DigitalChannelData data) {
        if (data != null) {
            if (!(broadcastSystem == 0 || broadcastSystem == 1 || broadcastSystem == 2)) {
                switch (broadcastSystem) {
                    case 8:
                    case 9:
                    case 10:
                        break;
                    default:
                        switch (broadcastSystem) {
                            case 16:
                            case 17:
                            case 18:
                                break;
                            default:
                                switch (broadcastSystem) {
                                    case 24:
                                    case 25:
                                    case 26:
                                    case 27:
                                        break;
                                    default:
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("Invalid broadcast type:");
                                        stringBuilder.append(broadcastSystem);
                                        Log.w(TAG, stringBuilder.toString());
                                        StringBuilder stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append("Invalid broadcast system value:");
                                        stringBuilder2.append(broadcastSystem);
                                        throw new IllegalArgumentException(stringBuilder2.toString());
                                }
                        }
                }
            }
            return new DigitalServiceSource(1, broadcastSystem, data);
        }
        throw new IllegalArgumentException("data should not be null.");
    }

    public static DigitalServiceSource ofArib(int aribType, AribData data) {
        if (data != null) {
            if (aribType != 0) {
                switch (aribType) {
                    case 8:
                    case 9:
                    case 10:
                        break;
                    default:
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Invalid ARIB type:");
                        stringBuilder.append(aribType);
                        Log.w(TAG, stringBuilder.toString());
                        throw new IllegalArgumentException("type should not be null.");
                }
            }
            return new DigitalServiceSource(0, aribType, data);
        }
        throw new IllegalArgumentException("data should not be null.");
    }

    public static DigitalServiceSource ofAtsc(int atscType, AtscData data) {
        if (data != null) {
            if (atscType != 1) {
                switch (atscType) {
                    case 16:
                    case 17:
                    case 18:
                        break;
                    default:
                        StringBuilder stringBuilder = new StringBuilder();
                        String str = "Invalid ATSC type:";
                        stringBuilder.append(str);
                        stringBuilder.append(atscType);
                        Log.w(TAG, stringBuilder.toString());
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(str);
                        stringBuilder2.append(atscType);
                        throw new IllegalArgumentException(stringBuilder2.toString());
                }
            }
            return new DigitalServiceSource(0, atscType, data);
        }
        throw new IllegalArgumentException("data should not be null.");
    }

    public static DigitalServiceSource ofDvb(int dvbType, DvbData data) {
        if (data != null) {
            if (dvbType != 2) {
                switch (dvbType) {
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                        break;
                    default:
                        StringBuilder stringBuilder = new StringBuilder();
                        String str = "Invalid DVB type:";
                        stringBuilder.append(str);
                        stringBuilder.append(dvbType);
                        Log.w(TAG, stringBuilder.toString());
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(str);
                        stringBuilder2.append(dvbType);
                        throw new IllegalArgumentException(stringBuilder2.toString());
                }
            }
            return new DigitalServiceSource(0, dvbType, data);
        }
        throw new IllegalArgumentException("data should not be null.");
    }

    public static AnalogueServiceSource ofAnalogue(int broadcastType, int frequency, int broadcastSystem) {
        String str = TAG;
        StringBuilder stringBuilder;
        String str2;
        if (broadcastType < 0 || broadcastType > 2) {
            stringBuilder = new StringBuilder();
            str2 = "Invalid Broadcast type:";
            stringBuilder.append(str2);
            stringBuilder.append(broadcastType);
            Log.w(str, stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(broadcastType);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (frequency < 0 || frequency > 65535) {
            stringBuilder = new StringBuilder();
            str2 = "Invalid frequency value[0x0000-0xFFFF]:";
            stringBuilder.append(str2);
            stringBuilder.append(frequency);
            Log.w(str, stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(frequency);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (broadcastSystem >= 0 && broadcastSystem <= 31) {
            return new AnalogueServiceSource(broadcastType, frequency, broadcastSystem);
        } else {
            stringBuilder = new StringBuilder();
            str2 = "Invalid Broadcast system:";
            stringBuilder.append(str2);
            stringBuilder.append(broadcastSystem);
            Log.w(str, stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(broadcastSystem);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public static ExternalPlugData ofExternalPlug(int plugNumber) {
        if (plugNumber >= 1 && plugNumber <= 255) {
            return new ExternalPlugData(plugNumber);
        }
        StringBuilder stringBuilder = new StringBuilder();
        String str = "Invalid plug number[1-255]";
        stringBuilder.append(str);
        stringBuilder.append(plugNumber);
        Log.w(TAG, stringBuilder.toString());
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(plugNumber);
        throw new IllegalArgumentException(stringBuilder2.toString());
    }

    public static ExternalPhysicalAddress ofExternalPhysicalAddress(int physicalAddress) {
        if ((-65536 & physicalAddress) == 0) {
            return new ExternalPhysicalAddress(physicalAddress);
        }
        StringBuilder stringBuilder = new StringBuilder();
        String str = "Invalid physical address:";
        stringBuilder.append(str);
        stringBuilder.append(physicalAddress);
        Log.w(TAG, stringBuilder.toString());
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(physicalAddress);
        throw new IllegalArgumentException(stringBuilder2.toString());
    }

    private static int threeFieldsToSixBytes(int first, int second, int third, byte[] data, int index) {
        shortToByteArray((short) first, data, index);
        shortToByteArray((short) second, data, index + 2);
        shortToByteArray((short) third, data, index + 4);
        return 6;
    }

    private static int shortToByteArray(short value, byte[] byteArray, int index) {
        byteArray[index] = (byte) ((value >>> 8) & 255);
        byteArray[index + 1] = (byte) (value & 255);
        return 2;
    }

    @SystemApi
    public static boolean checkRecordSource(byte[] recordSource) {
        boolean z = false;
        if (recordSource == null || recordSource.length == 0) {
            return false;
        }
        int recordSourceType = recordSource[0];
        int extraDataSize = recordSource.length - 1;
        if (recordSourceType == 1) {
            if (extraDataSize == 0) {
                z = true;
            }
            return z;
        } else if (recordSourceType == 2) {
            if (extraDataSize == 7) {
                z = true;
            }
            return z;
        } else if (recordSourceType == 3) {
            if (extraDataSize == 4) {
                z = true;
            }
            return z;
        } else if (recordSourceType == 4) {
            if (extraDataSize == 1) {
                z = true;
            }
            return z;
        } else if (recordSourceType != 5) {
            return false;
        } else {
            if (extraDataSize == 2) {
                z = true;
            }
            return z;
        }
    }
}
