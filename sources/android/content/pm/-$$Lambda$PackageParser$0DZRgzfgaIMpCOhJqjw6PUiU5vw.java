package android.content.pm;

import android.content.pm.PackageParser.Activity;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PackageParser$0DZRgzfgaIMpCOhJqjw6PUiU5vw implements Comparator {
    public static final /* synthetic */ -$$Lambda$PackageParser$0DZRgzfgaIMpCOhJqjw6PUiU5vw INSTANCE = new -$$Lambda$PackageParser$0DZRgzfgaIMpCOhJqjw6PUiU5vw();

    private /* synthetic */ -$$Lambda$PackageParser$0DZRgzfgaIMpCOhJqjw6PUiU5vw() {
    }

    public final int compare(Object obj, Object obj2) {
        return Integer.compare(((Activity) obj2).order, ((Activity) obj).order);
    }
}
