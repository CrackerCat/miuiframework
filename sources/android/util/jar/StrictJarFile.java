package android.util.jar;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import dalvik.system.CloseGuard;
import java.io.FileDescriptor;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import libcore.io.IoBridge;
import libcore.io.IoUtils;
import libcore.io.Streams;

public final class StrictJarFile {
    private boolean closed;
    private final FileDescriptor fd;
    private final CloseGuard guard;
    private final boolean isSigned;
    private final StrictJarManifest manifest;
    private final long nativeHandle;
    private final StrictJarVerifier verifier;

    static final class EntryIterator implements Iterator<ZipEntry> {
        private final long iterationHandle;
        private ZipEntry nextEntry;

        EntryIterator(long nativeHandle, String prefix) throws IOException {
            this.iterationHandle = StrictJarFile.nativeStartIteration(nativeHandle, prefix);
        }

        public ZipEntry next() {
            if (this.nextEntry == null) {
                return StrictJarFile.nativeNextEntry(this.iterationHandle);
            }
            ZipEntry ze = this.nextEntry;
            this.nextEntry = null;
            return ze;
        }

        public boolean hasNext() {
            if (this.nextEntry != null) {
                return true;
            }
            ZipEntry ze = StrictJarFile.nativeNextEntry(this.iterationHandle);
            if (ze == null) {
                return false;
            }
            this.nextEntry = ze;
            return true;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static class FDStream extends InputStream {
        private long endOffset;
        private final FileDescriptor fd;
        private long offset;

        public FDStream(FileDescriptor fd, long initialOffset, long endOffset) {
            this.fd = fd;
            this.offset = initialOffset;
            this.endOffset = endOffset;
        }

        public int available() throws IOException {
            return this.offset < this.endOffset ? 1 : 0;
        }

        public int read() throws IOException {
            return Streams.readSingleByte(this);
        }

        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            synchronized (this.fd) {
                long length = this.endOffset - this.offset;
                if (((long) byteCount) > length) {
                    byteCount = (int) length;
                }
                try {
                    Os.lseek(this.fd, this.offset, OsConstants.SEEK_SET);
                    int count = IoBridge.read(this.fd, buffer, byteOffset, byteCount);
                    if (count > 0) {
                        this.offset += (long) count;
                        return count;
                    }
                    return -1;
                } catch (ErrnoException e) {
                    throw new IOException(e);
                }
            }
        }

        public long skip(long byteCount) throws IOException {
            long j = this.endOffset;
            long j2 = this.offset;
            if (byteCount > j - j2) {
                byteCount = j - j2;
            }
            this.offset += byteCount;
            return byteCount;
        }
    }

    static final class JarFileInputStream extends FilterInputStream {
        private long count;
        private boolean done = false;
        private final VerifierEntry entry;

        JarFileInputStream(InputStream is, long size, VerifierEntry e) {
            super(is);
            this.entry = e;
            this.count = size;
        }

        public int read() throws IOException {
            if (this.done) {
                return -1;
            }
            if (this.count > 0) {
                int r = super.read();
                if (r != -1) {
                    this.entry.write(r);
                    this.count--;
                } else {
                    this.count = 0;
                }
                if (this.count == 0) {
                    this.done = true;
                    this.entry.verify();
                }
                return r;
            }
            this.done = true;
            this.entry.verify();
            return -1;
        }

        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            if (this.done) {
                return -1;
            }
            if (this.count > 0) {
                int r = super.read(buffer, byteOffset, byteCount);
                if (r != -1) {
                    int size = r;
                    long j = this.count;
                    if (j < ((long) size)) {
                        size = (int) j;
                    }
                    this.entry.write(buffer, byteOffset, size);
                    this.count -= (long) size;
                } else {
                    this.count = 0;
                }
                if (this.count == 0) {
                    this.done = true;
                    this.entry.verify();
                }
                return r;
            }
            this.done = true;
            this.entry.verify();
            return -1;
        }

        public int available() throws IOException {
            if (this.done) {
                return 0;
            }
            return super.available();
        }

        public long skip(long byteCount) throws IOException {
            return Streams.skipByReading(this, byteCount);
        }
    }

    public static class ZipInflaterInputStream extends InflaterInputStream {
        private long bytesRead = 0;
        private boolean closed;
        private final ZipEntry entry;

        public ZipInflaterInputStream(InputStream is, Inflater inf, int bsize, ZipEntry entry) {
            super(is, inf, bsize);
            this.entry = entry;
        }

        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            StringBuilder stringBuilder;
            try {
                int i = super.read(buffer, byteOffset, byteCount);
                if (i != -1) {
                    this.bytesRead += (long) i;
                } else if (this.entry.getSize() != this.bytesRead) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Size mismatch on inflated file: ");
                    stringBuilder.append(this.bytesRead);
                    stringBuilder.append(" vs ");
                    stringBuilder.append(this.entry.getSize());
                    throw new IOException(stringBuilder.toString());
                }
                return i;
            } catch (IOException e) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Error reading data for ");
                stringBuilder.append(this.entry.getName());
                stringBuilder.append(" near offset ");
                stringBuilder.append(this.bytesRead);
                throw new IOException(stringBuilder.toString(), e);
            }
        }

        public int available() throws IOException {
            int i = 0;
            if (this.closed) {
                return 0;
            }
            if (super.available() != 0) {
                i = (int) (this.entry.getSize() - this.bytesRead);
            }
            return i;
        }

        public void close() throws IOException {
            super.close();
            this.closed = true;
        }
    }

    private static native void nativeClose(long j);

    private static native ZipEntry nativeFindEntry(long j, String str);

    private static native ZipEntry nativeNextEntry(long j);

    private static native long nativeOpenJarFile(String str, int i) throws IOException;

    private static native long nativeStartIteration(long j, String str);

    public StrictJarFile(String fileName) throws IOException, SecurityException {
        this(fileName, true, true);
    }

    public StrictJarFile(FileDescriptor fd) throws IOException, SecurityException {
        this(fd, true, true);
    }

    public StrictJarFile(FileDescriptor fd, boolean verify, boolean signatureSchemeRollbackProtectionsEnforced) throws IOException, SecurityException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[fd:");
        stringBuilder.append(fd.getInt$());
        stringBuilder.append("]");
        this(stringBuilder.toString(), fd, verify, signatureSchemeRollbackProtectionsEnforced);
    }

    public StrictJarFile(String fileName, boolean verify, boolean signatureSchemeRollbackProtectionsEnforced) throws IOException, SecurityException {
        this(fileName, IoBridge.open(fileName, OsConstants.O_RDONLY), verify, signatureSchemeRollbackProtectionsEnforced);
    }

    private StrictJarFile(String name, FileDescriptor fd, boolean verify, boolean signatureSchemeRollbackProtectionsEnforced) throws IOException, SecurityException {
        this.guard = CloseGuard.get();
        this.nativeHandle = nativeOpenJarFile(name, fd.getInt$());
        this.fd = fd;
        boolean z = false;
        if (verify) {
            try {
                HashMap<String, byte[]> metaEntries = getMetaEntries();
                this.manifest = new StrictJarManifest((byte[]) metaEntries.get("META-INF/MANIFEST.MF"), true);
                this.verifier = new StrictJarVerifier(name, this.manifest, metaEntries, signatureSchemeRollbackProtectionsEnforced);
                for (String file : this.manifest.getEntries().keySet()) {
                    if (findEntry(file) == null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("File ");
                        stringBuilder.append(file);
                        stringBuilder.append(" in manifest does not exist");
                        throw new SecurityException(stringBuilder.toString());
                    }
                }
                if (this.verifier.readCertificates() && this.verifier.isSignedJar()) {
                    z = true;
                }
                this.isSigned = z;
            } catch (IOException | SecurityException e) {
                nativeClose(this.nativeHandle);
                IoUtils.closeQuietly(fd);
                this.closed = true;
                throw e;
            }
        }
        this.isSigned = false;
        this.manifest = null;
        this.verifier = null;
        this.guard.open("close");
    }

    public StrictJarManifest getManifest() {
        return this.manifest;
    }

    public Iterator<ZipEntry> iterator() throws IOException {
        return new EntryIterator(this.nativeHandle, "");
    }

    public ZipEntry findEntry(String name) {
        return nativeFindEntry(this.nativeHandle, name);
    }

    public Certificate[][] getCertificateChains(ZipEntry ze) {
        if (this.isSigned) {
            return this.verifier.getCertificateChains(ze.getName());
        }
        return null;
    }

    @Deprecated
    public Certificate[] getCertificates(ZipEntry ze) {
        if (!this.isSigned) {
            return null;
        }
        Certificate[][] certChains = this.verifier.getCertificateChains(ze.getName());
        int count = 0;
        for (Certificate[] chain : certChains) {
            count += chain.length;
        }
        Certificate[] certs = new Certificate[count];
        int i = 0;
        for (Certificate[] chain2 : certChains) {
            System.arraycopy(chain2, 0, certs, i, chain2.length);
            i += chain2.length;
        }
        return certs;
    }

    public InputStream getInputStream(ZipEntry ze) {
        InputStream is = getZipInputStream(ze);
        if (!this.isSigned) {
            return is;
        }
        VerifierEntry entry = this.verifier.initEntry(ze.getName());
        if (entry == null) {
            return is;
        }
        return new JarFileInputStream(is, ze.getSize(), entry);
    }

    public void close() throws IOException {
        if (!this.closed) {
            CloseGuard closeGuard = this.guard;
            if (closeGuard != null) {
                closeGuard.close();
            }
            nativeClose(this.nativeHandle);
            IoUtils.closeQuietly(this.fd);
            this.closed = true;
        }
    }

    /* Access modifiers changed, original: protected */
    public void finalize() throws Throwable {
        try {
            if (this.guard != null) {
                this.guard.warnIfOpen();
            }
            close();
        } finally {
            super.finalize();
        }
    }

    private InputStream getZipInputStream(ZipEntry ze) {
        if (ze.getMethod() == 0) {
            return new FDStream(this.fd, ze.getDataOffset(), ze.getDataOffset() + ze.getSize());
        }
        return new ZipInflaterInputStream(new FDStream(this.fd, ze.getDataOffset(), ze.getDataOffset() + ze.getCompressedSize()), new Inflater(true), Math.max(1024, (int) Math.min(ze.getSize(), 65535)), ze);
    }

    private HashMap<String, byte[]> getMetaEntries() throws IOException {
        HashMap<String, byte[]> metaEntries = new HashMap();
        Iterator<ZipEntry> entryIterator = new EntryIterator(this.nativeHandle, "META-INF/");
        while (entryIterator.hasNext()) {
            ZipEntry entry = (ZipEntry) entryIterator.next();
            metaEntries.put(entry.getName(), Streams.readFully(getInputStream(entry)));
        }
        return metaEntries;
    }
}
