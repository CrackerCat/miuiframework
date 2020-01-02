package com.android.internal.util;

import java.io.IOException;
import java.io.InputStream;
import libcore.io.Streams;

public class SizedInputStream extends InputStream {
    private long mLength;
    private final InputStream mWrapped;

    public SizedInputStream(InputStream wrapped, long length) {
        this.mWrapped = wrapped;
        this.mLength = length;
    }

    public void close() throws IOException {
        super.close();
        this.mWrapped.close();
    }

    public int read() throws IOException {
        return Streams.readSingleByte(this);
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        long j = this.mLength;
        if (j <= 0) {
            return -1;
        }
        if (((long) byteCount) > j) {
            byteCount = (int) j;
        }
        int n = this.mWrapped.read(buffer, byteOffset, byteCount);
        if (n != -1) {
            this.mLength -= (long) n;
        } else if (this.mLength > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected EOF; expected ");
            stringBuilder.append(this.mLength);
            stringBuilder.append(" more bytes");
            throw new IOException(stringBuilder.toString());
        }
        return n;
    }
}
