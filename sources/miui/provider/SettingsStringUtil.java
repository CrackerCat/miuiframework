package miui.provider;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import com.android.internal.util.ArrayUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class SettingsStringUtil {
    public static final String DELIMITER = ":";

    public static abstract class ColonDelimitedSet<T> extends HashSet<T> {

        public static class OfStrings extends ColonDelimitedSet<String> {
            public OfStrings(String colonSeparatedItems) {
                super(colonSeparatedItems);
            }

            /* Access modifiers changed, original: protected */
            public String itemFromString(String s) {
                return s;
            }

            public static String addAll(String delimitedElements, Collection<String> elements) {
                ColonDelimitedSet<String> set = new OfStrings(delimitedElements);
                return set.addAll(elements) ? set.toString() : delimitedElements;
            }

            public static String add(String delimitedElements, String element) {
                ColonDelimitedSet<String> set = new OfStrings(delimitedElements);
                if (set.contains(element)) {
                    return delimitedElements;
                }
                set.add(element);
                return set.toString();
            }

            public static String remove(String delimitedElements, String element) {
                ColonDelimitedSet<String> set = new OfStrings(delimitedElements);
                if (!set.contains(element)) {
                    return delimitedElements;
                }
                set.remove(element);
                return set.toString();
            }

            public static boolean contains(String delimitedElements, String element) {
                return ArrayUtils.indexOf(TextUtils.split(delimitedElements, ":"), element) != -1;
            }
        }

        public abstract T itemFromString(String str);

        public ColonDelimitedSet(String colonSeparatedItems) {
            for (String cn : TextUtils.split(emptyIfNull(colonSeparatedItems), ":")) {
                add(itemFromString(cn));
            }
        }

        /* Access modifiers changed, original: protected */
        public String itemToString(T item) {
            return String.valueOf(item);
        }

        public String emptyIfNull(String str) {
            return str == null ? "" : str;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Iterator<T> it = iterator();
            if (it.hasNext()) {
                sb.append(itemToString(it.next()));
                while (it.hasNext()) {
                    sb.append(":");
                    sb.append(itemToString(it.next()));
                }
            }
            return sb.toString();
        }
    }

    public static class ComponentNameSet extends ColonDelimitedSet<ComponentName> {
        public ComponentNameSet(String colonSeparatedPackageNames) {
            super(colonSeparatedPackageNames);
        }

        /* Access modifiers changed, original: protected */
        public ComponentName itemFromString(String s) {
            return ComponentName.unflattenFromString(s);
        }

        /* Access modifiers changed, original: protected */
        public String itemToString(ComponentName item) {
            return item.flattenToString();
        }

        public static String add(String delimitedElements, ComponentName element) {
            ComponentNameSet set = new ComponentNameSet(delimitedElements);
            if (set.contains(element)) {
                return delimitedElements;
            }
            set.add(element);
            return set.toString();
        }

        public static String remove(String delimitedElements, ComponentName element) {
            ComponentNameSet set = new ComponentNameSet(delimitedElements);
            if (!set.contains(element)) {
                return delimitedElements;
            }
            set.remove(element);
            return set.toString();
        }

        public static boolean contains(String delimitedElements, ComponentName element) {
            return OfStrings.contains(delimitedElements, element.flattenToString());
        }
    }

    public static class SettingStringHelper {
        private final ContentResolver mContentResolver;
        private final String mSettingName;
        private int mUserId;

        public SettingStringHelper(ContentResolver contentResolver, String name, int userId) {
            this.mContentResolver = contentResolver;
            this.mUserId = userId;
            this.mSettingName = name;
        }

        public String read() {
            return Secure.getStringForUser(this.mContentResolver, this.mSettingName, this.mUserId);
        }

        public boolean write(String value) {
            return Secure.putStringForUser(this.mContentResolver, this.mSettingName, value, this.mUserId);
        }

        public void setUserId(int userId) {
            this.mUserId = userId;
        }
    }

    private SettingsStringUtil() {
    }
}
