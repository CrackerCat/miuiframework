package android.media;

public final class MediaTimestamp {
    public static final MediaTimestamp TIMESTAMP_UNKNOWN = new MediaTimestamp(-1, -1, 0.0f);
    public final float clockRate;
    public final long mediaTimeUs;
    public final long nanoTime;

    public long getAnchorMediaTimeUs() {
        return this.mediaTimeUs;
    }

    @Deprecated
    public long getAnchorSytemNanoTime() {
        return getAnchorSystemNanoTime();
    }

    public long getAnchorSystemNanoTime() {
        return this.nanoTime;
    }

    public float getMediaClockRate() {
        return this.clockRate;
    }

    public MediaTimestamp(long mediaTimeUs, long nanoTimeNs, float clockRate) {
        this.mediaTimeUs = mediaTimeUs;
        this.nanoTime = nanoTimeNs;
        this.clockRate = clockRate;
    }

    MediaTimestamp() {
        this.mediaTimeUs = 0;
        this.nanoTime = 0;
        this.clockRate = 1.0f;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MediaTimestamp that = (MediaTimestamp) obj;
        if (!(this.mediaTimeUs == that.mediaTimeUs && this.nanoTime == that.nanoTime && this.clockRate == that.clockRate)) {
            z = false;
        }
        return z;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getClass().getName());
        stringBuilder.append("{AnchorMediaTimeUs=");
        stringBuilder.append(this.mediaTimeUs);
        stringBuilder.append(" AnchorSystemNanoTime=");
        stringBuilder.append(this.nanoTime);
        stringBuilder.append(" clockRate=");
        stringBuilder.append(this.clockRate);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
