package android.print;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class PrintDocumentInfo implements Parcelable {
    public static final int CONTENT_TYPE_DOCUMENT = 0;
    public static final int CONTENT_TYPE_PHOTO = 1;
    public static final int CONTENT_TYPE_UNKNOWN = -1;
    public static final Creator<PrintDocumentInfo> CREATOR = new Creator<PrintDocumentInfo>() {
        public PrintDocumentInfo createFromParcel(Parcel parcel) {
            return new PrintDocumentInfo(parcel, null);
        }

        public PrintDocumentInfo[] newArray(int size) {
            return new PrintDocumentInfo[size];
        }
    };
    public static final int PAGE_COUNT_UNKNOWN = -1;
    private int mContentType;
    private long mDataSize;
    private String mName;
    private int mPageCount;

    public static final class Builder {
        private final PrintDocumentInfo mPrototype;

        public Builder(String name) {
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("name cannot be empty");
            }
            this.mPrototype = new PrintDocumentInfo();
            this.mPrototype.mName = name;
        }

        public Builder setPageCount(int pageCount) {
            if (pageCount >= 0 || pageCount == -1) {
                this.mPrototype.mPageCount = pageCount;
                return this;
            }
            throw new IllegalArgumentException("pageCount must be greater than or equal to zero or DocumentInfo#PAGE_COUNT_UNKNOWN");
        }

        public Builder setContentType(int type) {
            this.mPrototype.mContentType = type;
            return this;
        }

        public PrintDocumentInfo build() {
            if (this.mPrototype.mPageCount == 0) {
                this.mPrototype.mPageCount = -1;
            }
            return new PrintDocumentInfo(this.mPrototype, null);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ContentType {
    }

    private PrintDocumentInfo() {
    }

    private PrintDocumentInfo(PrintDocumentInfo prototype) {
        this.mName = prototype.mName;
        this.mPageCount = prototype.mPageCount;
        this.mContentType = prototype.mContentType;
        this.mDataSize = prototype.mDataSize;
    }

    private PrintDocumentInfo(Parcel parcel) {
        this.mName = (String) Preconditions.checkStringNotEmpty(parcel.readString());
        this.mPageCount = parcel.readInt();
        int i = this.mPageCount;
        boolean z = i == -1 || i > 0;
        Preconditions.checkArgument(z);
        this.mContentType = parcel.readInt();
        this.mDataSize = Preconditions.checkArgumentNonnegative(parcel.readLong());
    }

    public String getName() {
        return this.mName;
    }

    public int getPageCount() {
        return this.mPageCount;
    }

    public int getContentType() {
        return this.mContentType;
    }

    public long getDataSize() {
        return this.mDataSize;
    }

    public void setDataSize(long dataSize) {
        this.mDataSize = dataSize;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mName);
        parcel.writeInt(this.mPageCount);
        parcel.writeInt(this.mContentType);
        parcel.writeLong(this.mDataSize);
    }

    public int hashCode() {
        int result = 1 * 31;
        String str = this.mName;
        int hashCode = (((((result + (str != null ? str.hashCode() : 0)) * 31) + this.mContentType) * 31) + this.mPageCount) * 31;
        long j = this.mDataSize;
        return ((hashCode + ((int) j)) * 31) + ((int) (j >> 32));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrintDocumentInfo other = (PrintDocumentInfo) obj;
        if (TextUtils.equals(this.mName, other.mName) && this.mContentType == other.mContentType && this.mPageCount == other.mPageCount && this.mDataSize == other.mDataSize) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PrintDocumentInfo{");
        builder.append("name=");
        builder.append(this.mName);
        builder.append(", pageCount=");
        builder.append(this.mPageCount);
        builder.append(", contentType=");
        builder.append(contentTypeToString(this.mContentType));
        builder.append(", dataSize=");
        builder.append(this.mDataSize);
        builder.append("}");
        return builder.toString();
    }

    private String contentTypeToString(int contentType) {
        if (contentType == 0) {
            return "CONTENT_TYPE_DOCUMENT";
        }
        if (contentType != 1) {
            return "CONTENT_TYPE_UNKNOWN";
        }
        return "CONTENT_TYPE_PHOTO";
    }
}
