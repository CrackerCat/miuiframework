package android.graphics.fonts;

import android.graphics.FontListParser;
import android.security.KeyChain;
import android.text.FontConfig.Alias;
import android.text.FontConfig.Family;
import android.util.Xml;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class FontCustomizationParser {

    public static class Result {
        ArrayList<Alias> mAdditionalAliases = new ArrayList();
        ArrayList<Family> mAdditionalNamedFamilies = new ArrayList();
    }

    public static Result parse(InputStream in, String fontDir) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, null);
        parser.nextTag();
        return readFamilies(parser, fontDir);
    }

    private static void validate(Result result) {
        HashSet<String> familyNames = new HashSet();
        int i = 0;
        while (i < result.mAdditionalNamedFamilies.size()) {
            String name = ((Family) result.mAdditionalNamedFamilies.get(i)).getName();
            if (name == null) {
                throw new IllegalArgumentException("new-named-family requires name attribute");
            } else if (familyNames.add(name)) {
                i++;
            } else {
                throw new IllegalArgumentException("new-named-family requires unique name attribute");
            }
        }
    }

    private static Result readFamilies(XmlPullParser parser, String fontDir) throws XmlPullParserException, IOException {
        Result out = new Result();
        parser.require(2, null, "fonts-modification");
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                String tag = parser.getName();
                if (tag.equals("family")) {
                    readFamily(parser, fontDir, out);
                } else if (tag.equals(KeyChain.EXTRA_ALIAS)) {
                    out.mAdditionalAliases.add(FontListParser.readAlias(parser));
                } else {
                    FontListParser.skip(parser);
                }
            }
        }
        validate(out);
        return out;
    }

    private static void readFamily(XmlPullParser parser, String fontDir, Result out) throws XmlPullParserException, IOException {
        String customizationType = parser.getAttributeValue(null, "customizationType");
        if (customizationType == null) {
            throw new IllegalArgumentException("customizationType must be specified");
        } else if (customizationType.equals("new-named-family")) {
            out.mAdditionalNamedFamilies.add(FontListParser.readFamily(parser, fontDir));
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unknown customizationType=");
            stringBuilder.append(customizationType);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }
}
