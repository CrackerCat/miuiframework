package com.android.internal.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;

public class ProcFileReader implements Closeable {
    private final byte[] mBuffer;
    private boolean mLineFinished;
    private final InputStream mStream;
    private int mTail;

    public ProcFileReader(InputStream stream) throws IOException {
        this(stream, 4096);
    }

    public ProcFileReader(InputStream stream, int bufferSize) throws IOException {
        this.mStream = stream;
        this.mBuffer = new byte[bufferSize];
        fillBuf();
    }

    private int fillBuf() throws IOException {
        int read = this.mBuffer;
        int length = read.length;
        int i = this.mTail;
        length -= i;
        if (length != 0) {
            read = this.mStream.read(read, i, length);
            if (read != -1) {
                this.mTail += read;
            }
            return read;
        }
        throw new IOException("attempting to fill already-full buffer");
    }

    private void consumeBuf(int count) throws IOException {
        byte[] bArr = this.mBuffer;
        System.arraycopy(bArr, count, bArr, 0, this.mTail - count);
        this.mTail -= count;
        if (this.mTail == 0) {
            fillBuf();
        }
    }

    private int nextTokenIndex() throws IOException {
        if (this.mLineFinished) {
            return -1;
        }
        int i = 0;
        while (true) {
            if (i < this.mTail) {
                byte b = this.mBuffer[i];
                if (b == (byte) 10) {
                    this.mLineFinished = true;
                    return i;
                } else if (b == (byte) 32) {
                    return i;
                } else {
                    i++;
                }
            } else if (fillBuf() <= 0) {
                throw new ProtocolException("End of stream while looking for token boundary");
            }
        }
    }

    public boolean hasMoreData() {
        return this.mTail > 0;
    }

    public void finishLine() throws IOException {
        if (this.mLineFinished) {
            this.mLineFinished = false;
            return;
        }
        int i = 0;
        while (true) {
            if (i < this.mTail) {
                if (this.mBuffer[i] == (byte) 10) {
                    consumeBuf(i + 1);
                    return;
                }
                i++;
            } else if (fillBuf() <= 0) {
                throw new ProtocolException("End of stream while looking for line boundary");
            }
        }
    }

    public String nextString() throws IOException {
        int tokenIndex = nextTokenIndex();
        if (tokenIndex != -1) {
            return parseAndConsumeString(tokenIndex);
        }
        throw new ProtocolException("Missing required string");
    }

    public long nextLong() throws IOException {
        int tokenIndex = nextTokenIndex();
        if (tokenIndex != -1) {
            return parseAndConsumeLong(tokenIndex);
        }
        throw new ProtocolException("Missing required long");
    }

    public long nextOptionalLong(long def) throws IOException {
        int tokenIndex = nextTokenIndex();
        if (tokenIndex == -1) {
            return def;
        }
        return parseAndConsumeLong(tokenIndex);
    }

    private String parseAndConsumeString(int tokenIndex) throws IOException {
        String s = new String(this.mBuffer, 0, tokenIndex, StandardCharsets.US_ASCII);
        consumeBuf(tokenIndex + 1);
        return s;
    }

    private long parseAndConsumeLong(int tokenIndex) throws IOException {
        int i = 0;
        boolean negative = this.mBuffer[0] == (byte) 45;
        long result = 0;
        if (negative) {
            i = 1;
        }
        while (i < tokenIndex) {
            int digit = this.mBuffer[i] - 48;
            if (digit < 0 || digit > 9) {
                throw invalidLong(tokenIndex);
            }
            long next = (10 * result) - ((long) digit);
            if (next <= result) {
                result = next;
                i++;
            } else {
                throw invalidLong(tokenIndex);
            }
        }
        consumeBuf(tokenIndex + 1);
        return negative ? result : -result;
    }

    private NumberFormatException invalidLong(int tokenIndex) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("invalid long: ");
        stringBuilder.append(new String(this.mBuffer, 0, tokenIndex, StandardCharsets.US_ASCII));
        return new NumberFormatException(stringBuilder.toString());
    }

    public int nextInt() throws IOException {
        long value = nextLong();
        if (value <= 2147483647L && value >= -2147483648L) {
            return (int) value;
        }
        throw new NumberFormatException("parsed value larger than integer");
    }

    public void close() throws IOException {
        this.mStream.close();
    }
}
