package android.content.pm;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import libcore.util.ArrayUtils;

public class LimitedLengthInputStream extends FilterInputStream {
    private final long mEnd;
    private long mOffset;

    public LimitedLengthInputStream(InputStream in, long offset, long length) throws IOException {
        super(in);
        if (in == null) {
            throw new IOException("in == null");
        } else if (offset < 0) {
            throw new IOException("offset < 0");
        } else if (length < 0) {
            throw new IOException("length < 0");
        } else if (length <= Long.MAX_VALUE - offset) {
            this.mEnd = offset + length;
            skip(offset);
            this.mOffset = offset;
        } else {
            throw new IOException("offset + length > Long.MAX_VALUE");
        }
    }

    public synchronized int read() throws IOException {
        if (this.mOffset >= this.mEnd) {
            return -1;
        }
        this.mOffset++;
        return super.read();
    }

    public int read(byte[] buffer, int offset, int byteCount) throws IOException {
        if (this.mOffset >= this.mEnd) {
            return -1;
        }
        ArrayUtils.throwsIfOutOfBounds(buffer.length, offset, byteCount);
        long j = this.mOffset;
        if (j <= Long.MAX_VALUE - ((long) byteCount)) {
            long j2 = ((long) byteCount) + j;
            long j3 = this.mEnd;
            if (j2 > j3) {
                byteCount = (int) (j3 - j);
            }
            int numRead = super.read(buffer, offset, byteCount);
            this.mOffset += (long) numRead;
            return numRead;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("offset out of bounds: ");
        stringBuilder.append(this.mOffset);
        stringBuilder.append(" + ");
        stringBuilder.append(byteCount);
        throw new IOException(stringBuilder.toString());
    }

    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }
}
