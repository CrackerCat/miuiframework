package com.google.android.util;

import android.app.backup.FullBackup;
import android.provider.Telephony.BaseMmsColumns;
import android.text.format.DateFormat;
import android.view.ThreadedRenderer;
import com.miui.mishare.RemoteDevice;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.miui.commons.lang3.ClassUtils;

public abstract class AbstractMessageParser {
    public static final String musicNote = "♫ ";
    private HashMap<Character, Format> formatStart;
    private int nextChar;
    private int nextClass;
    private boolean parseAcronyms;
    private boolean parseFormatting;
    private boolean parseMeText;
    private boolean parseMusic;
    private boolean parseSmilies;
    private boolean parseUrls;
    private ArrayList<Part> parts;
    private String text;
    private ArrayList<Token> tokens;

    public static abstract class Token {
        protected String text;
        protected Type type;

        public enum Type {
            HTML("html"),
            FORMAT("format"),
            LINK("l"),
            SMILEY("e"),
            ACRONYM(FullBackup.APK_TREE_TOKEN),
            MUSIC("m"),
            GOOGLE_VIDEO(BaseMmsColumns.MMS_VERSION),
            YOUTUBE_VIDEO("yt"),
            PHOTO("p"),
            FLICKR(FullBackup.FILES_TREE_TOKEN);
            
            private String stringRep;

            private Type(String stringRep) {
                this.stringRep = stringRep;
            }

            public String toString() {
                return this.stringRep;
            }
        }

        public abstract boolean isHtml();

        protected Token(Type type, String text) {
            this.type = type;
            this.text = text;
        }

        public Type getType() {
            return this.type;
        }

        public List<String> getInfo() {
            List<String> info = new ArrayList();
            info.add(getType().toString());
            return info;
        }

        public String getRawText() {
            return this.text;
        }

        public boolean isMedia() {
            return false;
        }

        public boolean isArray() {
            return isHtml() ^ 1;
        }

        public String toHtml(boolean caps) {
            throw new AssertionError("not html");
        }

        public boolean controlCaps() {
            return false;
        }

        public boolean setCaps() {
            return false;
        }
    }

    public static class Acronym extends Token {
        private String value;

        public Acronym(String text, String value) {
            super(Type.ACRONYM, text);
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public boolean isHtml() {
            return false;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRawText());
            info.add(getValue());
            return info;
        }
    }

    public static class FlickrPhoto extends Token {
        private static final Pattern GROUPING_PATTERN = Pattern.compile("http://(?:www.)?flickr.com/photos/([^/?#&]+)/(tags|sets)/([^/?#&]+)/?");
        private static final String SETS = "sets";
        private static final String TAGS = "tags";
        private static final Pattern URL_PATTERN = Pattern.compile("http://(?:www.)?flickr.com/photos/([^/?#&]+)/?([^/?#&]+)?/?.*");
        private String grouping;
        private String groupingId;
        private String photo;
        private String user;

        public FlickrPhoto(String user, String photo, String grouping, String groupingId, String text) {
            super(Type.FLICKR, text);
            String str = "tags";
            String str2 = null;
            if (str.equals(user)) {
                this.user = null;
                this.photo = null;
                this.grouping = str;
                this.groupingId = photo;
                return;
            }
            this.user = user;
            if (!ThreadedRenderer.OVERDRAW_PROPERTY_SHOW.equals(photo)) {
                str2 = photo;
            }
            this.photo = str2;
            this.grouping = grouping;
            this.groupingId = groupingId;
        }

        public String getUser() {
            return this.user;
        }

        public String getPhoto() {
            return this.photo;
        }

        public String getGrouping() {
            return this.grouping;
        }

        public String getGroupingId() {
            return this.groupingId;
        }

        public boolean isHtml() {
            return false;
        }

        public boolean isMedia() {
            return true;
        }

        public static FlickrPhoto matchURL(String url, String text) {
            Matcher m = GROUPING_PATTERN.matcher(url);
            if (m.matches()) {
                return new FlickrPhoto(m.group(1), null, m.group(2), m.group(3), text);
            }
            m = URL_PATTERN.matcher(url);
            if (m.matches()) {
                return new FlickrPhoto(m.group(1), m.group(2), null, null, text);
            }
            return null;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getUrl());
            Object obj = "";
            info.add(getUser() != null ? getUser() : obj);
            info.add(getPhoto() != null ? getPhoto() : obj);
            info.add(getGrouping() != null ? getGrouping() : obj);
            if (getGroupingId() != null) {
                obj = getGroupingId();
            }
            info.add(obj);
            return info;
        }

        public String getUrl() {
            if (SETS.equals(this.grouping)) {
                return getUserSetsURL(this.user, this.groupingId);
            }
            String str;
            if ("tags".equals(this.grouping)) {
                str = this.user;
                if (str != null) {
                    return getUserTagsURL(str, this.groupingId);
                }
                return getTagsURL(this.groupingId);
            }
            str = this.photo;
            if (str != null) {
                return getPhotoURL(this.user, str);
            }
            return getUserURL(this.user);
        }

        public static String getRssUrl(String user) {
            return null;
        }

        public static String getTagsURL(String tag) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://flickr.com/photos/tags/");
            stringBuilder.append(tag);
            return stringBuilder.toString();
        }

        public static String getUserURL(String user) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://flickr.com/photos/");
            stringBuilder.append(user);
            return stringBuilder.toString();
        }

        public static String getPhotoURL(String user, String photo) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://flickr.com/photos/");
            stringBuilder.append(user);
            stringBuilder.append("/");
            stringBuilder.append(photo);
            return stringBuilder.toString();
        }

        public static String getUserTagsURL(String user, String tagId) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://flickr.com/photos/");
            stringBuilder.append(user);
            stringBuilder.append("/tags/");
            stringBuilder.append(tagId);
            return stringBuilder.toString();
        }

        public static String getUserSetsURL(String user, String setId) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://flickr.com/photos/");
            stringBuilder.append(user);
            stringBuilder.append("/sets/");
            stringBuilder.append(setId);
            return stringBuilder.toString();
        }
    }

    public static class Format extends Token {
        private char ch;
        private boolean matched;
        private boolean start;

        public Format(char ch, boolean start) {
            super(Type.FORMAT, String.valueOf(ch));
            this.ch = ch;
            this.start = start;
        }

        public void setMatched(boolean matched) {
            this.matched = matched;
        }

        public boolean isHtml() {
            return true;
        }

        public String toHtml(boolean caps) {
            if (this.matched) {
                return this.start ? getFormatStart(this.ch) : getFormatEnd(this.ch);
            }
            char c = this.ch;
            return c == '\"' ? "&quot;" : String.valueOf(c);
        }

        public List<String> getInfo() {
            throw new UnsupportedOperationException();
        }

        public boolean controlCaps() {
            return this.ch == '^';
        }

        public boolean setCaps() {
            return this.start;
        }

        private String getFormatStart(char ch) {
            if (ch == '\"') {
                return "<font color=\"#999999\">“";
            }
            if (ch == '*') {
                return "<b>";
            }
            if (ch == '^') {
                return "<b><font color=\"#005FFF\">";
            }
            if (ch == '_') {
                return "<i>";
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("unknown format '");
            stringBuilder.append(ch);
            stringBuilder.append("'");
            throw new AssertionError(stringBuilder.toString());
        }

        private String getFormatEnd(char ch) {
            if (ch == '\"') {
                return "”</font>";
            }
            if (ch == '*') {
                return "</b>";
            }
            if (ch == '^') {
                return "</font></b>";
            }
            if (ch == '_') {
                return "</i>";
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("unknown format '");
            stringBuilder.append(ch);
            stringBuilder.append("'");
            throw new AssertionError(stringBuilder.toString());
        }
    }

    public static class Html extends Token {
        private String html;

        public Html(String text, String html) {
            super(Type.HTML, text);
            this.html = html;
        }

        public boolean isHtml() {
            return true;
        }

        public String toHtml(boolean caps) {
            String str = this.html;
            return caps ? str.toUpperCase() : str;
        }

        public List<String> getInfo() {
            throw new UnsupportedOperationException();
        }

        public void trimLeadingWhitespace() {
            this.text = trimLeadingWhitespace(this.text);
            this.html = trimLeadingWhitespace(this.html);
        }

        public void trimTrailingWhitespace() {
            this.text = trimTrailingWhitespace(this.text);
            this.html = trimTrailingWhitespace(this.html);
        }

        private static String trimLeadingWhitespace(String text) {
            int index = 0;
            while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
                index++;
            }
            return text.substring(index);
        }

        public static String trimTrailingWhitespace(String text) {
            int index = text.length();
            while (index > 0 && Character.isWhitespace(text.charAt(index - 1))) {
                index--;
            }
            return text.substring(0, index);
        }
    }

    public static class Link extends Token {
        private String url;

        public Link(String url, String text) {
            super(Type.LINK, text);
            this.url = url;
        }

        public String getURL() {
            return this.url;
        }

        public boolean isHtml() {
            return false;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getURL());
            info.add(getRawText());
            return info;
        }
    }

    public static class MusicTrack extends Token {
        private String track;

        public MusicTrack(String track) {
            super(Type.MUSIC, track);
            this.track = track;
        }

        public String getTrack() {
            return this.track;
        }

        public boolean isHtml() {
            return false;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getTrack());
            return info;
        }
    }

    public static class Part {
        private String meText;
        private ArrayList<Token> tokens = new ArrayList();

        public String getType(boolean isSend) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(isSend ? RemoteDevice.KEY_STATUS : "r");
            stringBuilder.append(getPartType());
            return stringBuilder.toString();
        }

        private String getPartType() {
            if (isMedia()) {
                return "d";
            }
            if (this.meText != null) {
                return "m";
            }
            return "";
        }

        public boolean isMedia() {
            return this.tokens.size() == 1 && ((Token) this.tokens.get(0)).isMedia();
        }

        public Token getMediaToken() {
            if (isMedia()) {
                return (Token) this.tokens.get(0);
            }
            return null;
        }

        public void add(Token token) {
            if (isMedia()) {
                throw new AssertionError("media ");
            }
            this.tokens.add(token);
        }

        public void setMeText(String meText) {
            this.meText = meText;
        }

        public String getRawText() {
            StringBuilder buf = new StringBuilder();
            String str = this.meText;
            if (str != null) {
                buf.append(str);
            }
            for (int i = 0; i < this.tokens.size(); i++) {
                buf.append(((Token) this.tokens.get(i)).getRawText());
            }
            return buf.toString();
        }

        public ArrayList<Token> getTokens() {
            return this.tokens;
        }
    }

    public static class Photo extends Token {
        private static final Pattern URL_PATTERN = Pattern.compile("http://picasaweb.google.com/([^/?#&]+)/+((?!searchbrowse)[^/?#&]+)(?:/|/photo)?(?:\\?[^#]*)?(?:#(.*))?");
        private String album;
        private String photo;
        private String user;

        public Photo(String user, String album, String photo, String text) {
            super(Type.PHOTO, text);
            this.user = user;
            this.album = album;
            this.photo = photo;
        }

        public String getUser() {
            return this.user;
        }

        public String getAlbum() {
            return this.album;
        }

        public String getPhoto() {
            return this.photo;
        }

        public boolean isHtml() {
            return false;
        }

        public boolean isMedia() {
            return true;
        }

        public static Photo matchURL(String url, String text) {
            Matcher m = URL_PATTERN.matcher(url);
            if (m.matches()) {
                return new Photo(m.group(1), m.group(2), m.group(3), text);
            }
            return null;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRssUrl(getUser()));
            info.add(getAlbumURL(getUser(), getAlbum()));
            if (getPhoto() != null) {
                info.add(getPhotoURL(getUser(), getAlbum(), getPhoto()));
            } else {
                info.add((String) null);
            }
            return info;
        }

        public static String getRssUrl(String user) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://picasaweb.google.com/data/feed/api/user/");
            stringBuilder.append(user);
            stringBuilder.append("?category=album&alt=rss");
            return stringBuilder.toString();
        }

        public static String getAlbumURL(String user, String album) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://picasaweb.google.com/");
            stringBuilder.append(user);
            stringBuilder.append("/");
            stringBuilder.append(album);
            return stringBuilder.toString();
        }

        public static String getPhotoURL(String user, String album, String photo) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://picasaweb.google.com/");
            stringBuilder.append(user);
            stringBuilder.append("/");
            stringBuilder.append(album);
            stringBuilder.append("/photo#");
            stringBuilder.append(photo);
            return stringBuilder.toString();
        }
    }

    public interface Resources {
        TrieNode getAcronyms();

        TrieNode getDomainSuffixes();

        Set<String> getSchemes();

        TrieNode getSmileys();
    }

    public static class Smiley extends Token {
        public Smiley(String text) {
            super(Type.SMILEY, text);
        }

        public boolean isHtml() {
            return false;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRawText());
            return info;
        }
    }

    public static class TrieNode {
        private final HashMap<Character, TrieNode> children;
        private String text;
        private String value;

        public TrieNode() {
            this("");
        }

        public TrieNode(String text) {
            this.children = new HashMap();
            this.text = text;
        }

        public final boolean exists() {
            return this.value != null;
        }

        public final String getText() {
            return this.text;
        }

        public final String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public TrieNode getChild(char ch) {
            return (TrieNode) this.children.get(Character.valueOf(ch));
        }

        public TrieNode getOrCreateChild(char ch) {
            Character key = Character.valueOf(ch);
            TrieNode node = (TrieNode) this.children.get(key);
            if (node != null) {
                return node;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.text);
            stringBuilder.append(String.valueOf(ch));
            node = new TrieNode(stringBuilder.toString());
            this.children.put(key, node);
            return node;
        }

        public static void addToTrie(TrieNode root, String str, String value) {
            int index = 0;
            while (index < str.length()) {
                int index2 = index + 1;
                root = root.getOrCreateChild(str.charAt(index));
                index = index2;
            }
            root.setValue(value);
        }
    }

    public static class Video extends Token {
        private static final Pattern URL_PATTERN = Pattern.compile("(?i)http://video\\.google\\.[a-z0-9]+(?:\\.[a-z0-9]+)?/videoplay\\?.*?\\bdocid=(-?\\d+).*");
        private String docid;

        public Video(String docid, String text) {
            super(Type.GOOGLE_VIDEO, text);
            this.docid = docid;
        }

        public String getDocID() {
            return this.docid;
        }

        public boolean isHtml() {
            return false;
        }

        public boolean isMedia() {
            return true;
        }

        public static Video matchURL(String url, String text) {
            Matcher m = URL_PATTERN.matcher(url);
            if (m.matches()) {
                return new Video(m.group(1), text);
            }
            return null;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRssUrl(this.docid));
            info.add(getURL(this.docid));
            return info;
        }

        public static String getRssUrl(String docid) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://video.google.com/videofeed?type=docid&output=rss&sourceid=gtalk&docid=");
            stringBuilder.append(docid);
            return stringBuilder.toString();
        }

        public static String getURL(String docid) {
            return getURL(docid, null);
        }

        public static String getURL(String docid, String extraParams) {
            StringBuilder stringBuilder;
            if (extraParams == null) {
                extraParams = "";
            } else if (extraParams.length() > 0) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(extraParams);
                stringBuilder.append("&");
                extraParams = stringBuilder.toString();
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("http://video.google.com/videoplay?");
            stringBuilder.append(extraParams);
            stringBuilder.append("docid=");
            stringBuilder.append(docid);
            return stringBuilder.toString();
        }
    }

    public static class YouTubeVideo extends Token {
        private static final Pattern URL_PATTERN = Pattern.compile("(?i)http://(?:[a-z0-9]+\\.)?youtube\\.[a-z0-9]+(?:\\.[a-z0-9]+)?/watch\\?.*\\bv=([-_a-zA-Z0-9=]+).*");
        private String docid;

        public YouTubeVideo(String docid, String text) {
            super(Type.YOUTUBE_VIDEO, text);
            this.docid = docid;
        }

        public String getDocID() {
            return this.docid;
        }

        public boolean isHtml() {
            return false;
        }

        public boolean isMedia() {
            return true;
        }

        public static YouTubeVideo matchURL(String url, String text) {
            Matcher m = URL_PATTERN.matcher(url);
            if (m.matches()) {
                return new YouTubeVideo(m.group(1), text);
            }
            return null;
        }

        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRssUrl(this.docid));
            info.add(getURL(this.docid));
            return info;
        }

        public static String getRssUrl(String docid) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://youtube.com/watch?v=");
            stringBuilder.append(docid);
            return stringBuilder.toString();
        }

        public static String getURL(String docid) {
            return getURL(docid, null);
        }

        public static String getURL(String docid, String extraParams) {
            StringBuilder stringBuilder;
            if (extraParams == null) {
                extraParams = "";
            } else if (extraParams.length() > 0) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(extraParams);
                stringBuilder.append("&");
                extraParams = stringBuilder.toString();
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("http://youtube.com/watch?");
            stringBuilder.append(extraParams);
            stringBuilder.append("v=");
            stringBuilder.append(docid);
            return stringBuilder.toString();
        }

        public static String getPrefixedURL(boolean http, String prefix, String docid, String extraParams) {
            StringBuilder stringBuilder;
            String protocol = "";
            if (http) {
                protocol = "http://";
            }
            if (prefix == null) {
                prefix = "";
            }
            if (extraParams == null) {
                extraParams = "";
            } else if (extraParams.length() > 0) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(extraParams);
                stringBuilder.append("&");
                extraParams = stringBuilder.toString();
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append(protocol);
            stringBuilder.append(prefix);
            stringBuilder.append("youtube.com/watch?");
            stringBuilder.append(extraParams);
            stringBuilder.append("v=");
            stringBuilder.append(docid);
            return stringBuilder.toString();
        }
    }

    public abstract Resources getResources();

    public AbstractMessageParser(String text) {
        this(text, true, true, true, true, true, true);
    }

    public AbstractMessageParser(String text, boolean parseSmilies, boolean parseAcronyms, boolean parseFormatting, boolean parseUrls, boolean parseMusic, boolean parseMeText) {
        this.text = text;
        this.nextChar = 0;
        this.nextClass = 10;
        this.parts = new ArrayList();
        this.tokens = new ArrayList();
        this.formatStart = new HashMap();
        this.parseSmilies = parseSmilies;
        this.parseAcronyms = parseAcronyms;
        this.parseFormatting = parseFormatting;
        this.parseUrls = parseUrls;
        this.parseMusic = parseMusic;
        this.parseMeText = parseMeText;
    }

    public final String getRawText() {
        return this.text;
    }

    public final int getPartCount() {
        return this.parts.size();
    }

    public final Part getPart(int index) {
        return (Part) this.parts.get(index);
    }

    public final List<Part> getParts() {
        return this.parts;
    }

    public void parse() {
        if (parseMusicTrack()) {
            buildParts(null);
            return;
        }
        String meText = null;
        if (this.parseMeText && this.text.startsWith("/me") && this.text.length() > 3 && Character.isWhitespace(this.text.charAt(3))) {
            meText = this.text.substring(0, 4);
            this.text = this.text.substring(4);
        }
        boolean wasSmiley = false;
        while (this.nextChar < this.text.length()) {
            if (!isWordBreak(this.nextChar) && (!wasSmiley || !isSmileyBreak(this.nextChar))) {
                throw new AssertionError("last chunk did not end at word break");
            } else if (parseSmiley()) {
                wasSmiley = true;
            } else {
                wasSmiley = false;
                if (!(parseAcronym() || parseURL() || parseFormatting())) {
                    parseText();
                }
            }
        }
        int i = 0;
        while (i < this.tokens.size()) {
            if (((Token) this.tokens.get(i)).isMedia()) {
                if (i > 0 && (this.tokens.get(i - 1) instanceof Html)) {
                    ((Html) this.tokens.get(i - 1)).trimLeadingWhitespace();
                }
                if (i + 1 < this.tokens.size() && (this.tokens.get(i + 1) instanceof Html)) {
                    ((Html) this.tokens.get(i + 1)).trimTrailingWhitespace();
                }
            }
            i++;
        }
        i = 0;
        while (i < this.tokens.size()) {
            if (((Token) this.tokens.get(i)).isHtml() && ((Token) this.tokens.get(i)).toHtml(true).length() == 0) {
                this.tokens.remove(i);
                i--;
            }
            i++;
        }
        buildParts(meText);
    }

    public static Token tokenForUrl(String url, String text) {
        if (url == null) {
            return null;
        }
        Video video = Video.matchURL(url, text);
        if (video != null) {
            return video;
        }
        YouTubeVideo ytVideo = YouTubeVideo.matchURL(url, text);
        if (ytVideo != null) {
            return ytVideo;
        }
        Photo photo = Photo.matchURL(url, text);
        if (photo != null) {
            return photo;
        }
        FlickrPhoto flickrPhoto = FlickrPhoto.matchURL(url, text);
        if (flickrPhoto != null) {
            return flickrPhoto;
        }
        return new Link(url, text);
    }

    private void buildParts(String meText) {
        for (int i = 0; i < this.tokens.size(); i++) {
            Token token = (Token) this.tokens.get(i);
            if (token.isMedia() || this.parts.size() == 0 || lastPart().isMedia()) {
                this.parts.add(new Part());
            }
            lastPart().add(token);
        }
        if (this.parts.size() > 0) {
            ((Part) this.parts.get(0)).setMeText(meText);
        }
    }

    private Part lastPart() {
        ArrayList arrayList = this.parts;
        return (Part) arrayList.get(arrayList.size() - 1);
    }

    private boolean parseMusicTrack() {
        if (this.parseMusic) {
            String str = this.text;
            String str2 = musicNote;
            if (str.startsWith(str2)) {
                addToken(new MusicTrack(this.text.substring(str2.length())));
                this.nextChar = this.text.length();
                return true;
            }
        }
        return false;
    }

    private void parseText() {
        StringBuilder buf = new StringBuilder();
        int start = this.nextChar;
        while (true) {
            char ch = this.text;
            int i = this.nextChar;
            this.nextChar = i + 1;
            ch = ch.charAt(i);
            if (ch == 10) {
                buf.append("<br>");
            } else if (ch == '\"') {
                buf.append("&quot;");
            } else if (ch == '<') {
                buf.append("&lt;");
            } else if (ch == '>') {
                buf.append("&gt;");
            } else if (ch == '&') {
                buf.append("&amp;");
            } else if (ch != DateFormat.QUOTE) {
                buf.append(ch);
            } else {
                buf.append("&apos;");
            }
            if (isWordBreak(this.nextChar)) {
                addToken(new Html(this.text.substring(start, this.nextChar), buf.toString()));
                return;
            }
        }
    }

    private boolean parseSmiley() {
        if (!this.parseSmilies) {
            return false;
        }
        TrieNode match = longestMatch(getResources().getSmileys(), this, this.nextChar, true);
        if (match == null) {
            return false;
        }
        int previousCharClass = getCharClass(this.nextChar - 1);
        int nextCharClass = getCharClass(this.nextChar + match.getText().length());
        if ((previousCharClass == 2 || previousCharClass == 3) && (nextCharClass == 2 || nextCharClass == 3)) {
            return false;
        }
        addToken(new Smiley(match.getText()));
        this.nextChar += match.getText().length();
        return true;
    }

    private boolean parseAcronym() {
        if (!this.parseAcronyms) {
            return false;
        }
        TrieNode match = longestMatch(getResources().getAcronyms(), this, this.nextChar);
        if (match == null) {
            return false;
        }
        addToken(new Acronym(match.getText(), match.getValue()));
        this.nextChar += match.getText().length();
        return true;
    }

    private boolean isDomainChar(char c) {
        return c == '-' || Character.isLetter(c) || Character.isDigit(c);
    }

    private boolean isValidDomain(String domain) {
        if (matches(getResources().getDomainSuffixes(), reverse(domain))) {
            return true;
        }
        return false;
    }

    private boolean parseURL() {
        if (!this.parseUrls || !isURLBreak(this.nextChar)) {
            return false;
        }
        int start = this.nextChar;
        int index = start;
        while (index < this.text.length() && isDomainChar(this.text.charAt(index))) {
            index++;
        }
        String url = "";
        boolean done = false;
        if (index == this.text.length()) {
            return false;
        }
        if (this.text.charAt(index) == ':') {
            if (!getResources().getSchemes().contains(this.text.substring(this.nextChar, index))) {
                return false;
            }
        } else if (this.text.charAt(index) != ClassUtils.PACKAGE_SEPARATOR_CHAR) {
            return false;
        } else {
            while (index < this.text.length()) {
                char ch = this.text.charAt(index);
                if (ch != ClassUtils.PACKAGE_SEPARATOR_CHAR && !isDomainChar(ch)) {
                    break;
                }
                index++;
            }
            if (!isValidDomain(this.text.substring(this.nextChar, index))) {
                return false;
            }
            if (index + 1 < this.text.length() && this.text.charAt(index) == ':' && Character.isDigit(this.text.charAt(index + 1))) {
                while (true) {
                    index++;
                    if (index >= this.text.length() || !Character.isDigit(this.text.charAt(index))) {
                        break;
                    }
                }
            }
            if (index == this.text.length()) {
                done = true;
            } else {
                char ch2 = this.text.charAt(index);
                if (ch2 == '?') {
                    if (index + 1 == this.text.length()) {
                        done = true;
                    } else {
                        char ch22 = this.text.charAt(index + 1);
                        if (Character.isWhitespace(ch22) || isPunctuation(ch22)) {
                            done = true;
                        }
                    }
                } else if (isPunctuation(ch2)) {
                    done = true;
                } else if (Character.isWhitespace(ch2)) {
                    done = true;
                } else if (!(ch2 == '/' || ch2 == '#')) {
                    return false;
                }
            }
            url = "http://";
        }
        if (!done) {
            while (index < this.text.length() && !Character.isWhitespace(this.text.charAt(index))) {
                index++;
            }
        }
        String urlText = this.text.substring(start, index);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);
        stringBuilder.append(urlText);
        addURLToken(stringBuilder.toString(), urlText);
        this.nextChar = index;
        return true;
    }

    private void addURLToken(String url, String text) {
        addToken(tokenForUrl(url, text));
    }

    private boolean parseFormatting() {
        if (!this.parseFormatting) {
            return false;
        }
        int endChar = this.nextChar;
        while (endChar < this.text.length() && isFormatChar(this.text.charAt(endChar))) {
            endChar++;
        }
        if (endChar == this.nextChar || !isWordBreak(endChar)) {
            return false;
        }
        LinkedHashMap<Character, Boolean> seenCharacters = new LinkedHashMap();
        for (int index = this.nextChar; index < endChar; index++) {
            char ch = this.text.charAt(index);
            Character key = Character.valueOf(ch);
            if (seenCharacters.containsKey(key)) {
                addToken(new Format(ch, false));
            } else {
                Format start = (Format) this.formatStart.get(key);
                if (start != null) {
                    start.setMatched(true);
                    this.formatStart.remove(key);
                    seenCharacters.put(key, Boolean.TRUE);
                } else {
                    Format start2 = new Format(ch, true);
                    this.formatStart.put(key, start2);
                    addToken(start2);
                    seenCharacters.put(key, Boolean.FALSE);
                }
            }
        }
        for (Character key2 : seenCharacters.keySet()) {
            if (seenCharacters.get(key2) == Boolean.TRUE) {
                Format end = new Format(key2.charValue(), false);
                end.setMatched(true);
                addToken(end);
            }
        }
        this.nextChar = endChar;
        return true;
    }

    private boolean isWordBreak(int index) {
        return getCharClass(index + -1) != getCharClass(index);
    }

    private boolean isSmileyBreak(int index) {
        if (index <= 0 || index >= this.text.length() || !isSmileyBreak(this.text.charAt(index - 1), this.text.charAt(index))) {
            return false;
        }
        return true;
    }

    private boolean isURLBreak(int index) {
        int charClass = getCharClass(index - 1);
        if (charClass == 2 || charClass == 3 || charClass == 4) {
            return false;
        }
        return true;
    }

    private int getCharClass(int index) {
        if (index < 0 || this.text.length() <= index) {
            return 0;
        }
        char ch = this.text.charAt(index);
        if (Character.isWhitespace(ch)) {
            return 1;
        }
        if (Character.isLetter(ch)) {
            return 2;
        }
        if (Character.isDigit(ch)) {
            return 3;
        }
        if (!isPunctuation(ch)) {
            return 4;
        }
        int i = this.nextClass + 1;
        this.nextClass = i;
        return i;
    }

    /* JADX WARNING: Missing block: B:28:0x0043, code skipped:
            return false;
     */
    private static boolean isSmileyBreak(char r4, char r5) {
        /*
        r0 = 36;
        r1 = 42;
        r2 = 64;
        r3 = 47;
        if (r4 == r0) goto L_0x0026;
    L_0x000a:
        r0 = 38;
        if (r4 == r0) goto L_0x0026;
    L_0x000e:
        r0 = 45;
        if (r4 == r0) goto L_0x0026;
    L_0x0012:
        if (r4 == r3) goto L_0x0026;
    L_0x0014:
        if (r4 == r2) goto L_0x0026;
    L_0x0016:
        if (r4 == r1) goto L_0x0026;
    L_0x0018:
        r0 = 43;
        if (r4 == r0) goto L_0x0026;
    L_0x001c:
        switch(r4) {
            case 60: goto L_0x0026;
            case 61: goto L_0x0026;
            case 62: goto L_0x0026;
            default: goto L_0x001f;
        };
    L_0x001f:
        switch(r4) {
            case 91: goto L_0x0026;
            case 92: goto L_0x0026;
            case 93: goto L_0x0026;
            case 94: goto L_0x0026;
            default: goto L_0x0022;
        };
    L_0x0022:
        switch(r4) {
            case 124: goto L_0x0026;
            case 125: goto L_0x0026;
            case 126: goto L_0x0026;
            default: goto L_0x0025;
        };
    L_0x0025:
        goto L_0x0042;
    L_0x0026:
        if (r5 == r1) goto L_0x0044;
    L_0x0028:
        if (r5 == r3) goto L_0x0044;
    L_0x002a:
        if (r5 == r2) goto L_0x0044;
    L_0x002c:
        r0 = 94;
        if (r5 == r0) goto L_0x0044;
    L_0x0030:
        r0 = 126; // 0x7e float:1.77E-43 double:6.23E-322;
        if (r5 == r0) goto L_0x0044;
    L_0x0034:
        r0 = 91;
        if (r5 == r0) goto L_0x0044;
    L_0x0038:
        r0 = 92;
        if (r5 == r0) goto L_0x0044;
    L_0x003c:
        switch(r5) {
            case 35: goto L_0x0044;
            case 36: goto L_0x0044;
            case 37: goto L_0x0044;
            default: goto L_0x003f;
        };
    L_0x003f:
        switch(r5) {
            case 60: goto L_0x0044;
            case 61: goto L_0x0044;
            case 62: goto L_0x0044;
            default: goto L_0x0042;
        };
    L_0x0042:
        r0 = 0;
        return r0;
    L_0x0044:
        r0 = 1;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.util.AbstractMessageParser.isSmileyBreak(char, char):boolean");
    }

    private static boolean isPunctuation(char ch) {
        if (ch == '!' || ch == '\"' || ch == '(' || ch == ')' || ch == ',' || ch == ClassUtils.PACKAGE_SEPARATOR_CHAR || ch == '?' || ch == ':' || ch == ';') {
            return true;
        }
        return false;
    }

    private static boolean isFormatChar(char ch) {
        if (ch == '*' || ch == '^' || ch == '_') {
            return true;
        }
        return false;
    }

    private void addToken(Token token) {
        this.tokens.add(token);
    }

    public String toHtml() {
        StringBuilder html = new StringBuilder();
        Iterator it = this.parts.iterator();
        while (it.hasNext()) {
            Part part = (Part) it.next();
            boolean caps = false;
            html.append("<p>");
            Iterator it2 = part.getTokens().iterator();
            while (it2.hasNext()) {
                Token token = (Token) it2.next();
                if (token.isHtml()) {
                    html.append(token.toHtml(caps));
                } else {
                    String str = "</a>";
                    String str2 = "\">";
                    String str3 = "<a href=\"";
                    switch (token.getType()) {
                        case LINK:
                            html.append(str3);
                            html.append(((Link) token).getURL());
                            html.append(str2);
                            html.append(token.getRawText());
                            html.append(str);
                            break;
                        case SMILEY:
                            html.append(token.getRawText());
                            break;
                        case ACRONYM:
                            html.append(token.getRawText());
                            break;
                        case MUSIC:
                            html.append(((MusicTrack) token).getTrack());
                            break;
                        case GOOGLE_VIDEO:
                            html.append(str3);
                            Video video = (Video) token;
                            html.append(Video.getURL(((Video) token).getDocID()));
                            html.append(str2);
                            html.append(token.getRawText());
                            html.append(str);
                            break;
                        case YOUTUBE_VIDEO:
                            html.append(str3);
                            YouTubeVideo youTubeVideo = (YouTubeVideo) token;
                            html.append(YouTubeVideo.getURL(((YouTubeVideo) token).getDocID()));
                            html.append(str2);
                            html.append(token.getRawText());
                            html.append(str);
                            break;
                        case PHOTO:
                            html.append(str3);
                            html.append(Photo.getAlbumURL(((Photo) token).getUser(), ((Photo) token).getAlbum()));
                            html.append(str2);
                            html.append(token.getRawText());
                            html.append(str);
                            break;
                        case FLICKR:
                            Photo p = (Photo) token;
                            html.append(str3);
                            html.append(((FlickrPhoto) token).getUrl());
                            html.append(str2);
                            html.append(token.getRawText());
                            html.append(str);
                            break;
                        default:
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("unknown token type: ");
                            stringBuilder.append(token.getType());
                            throw new AssertionError(stringBuilder.toString());
                    }
                }
                if (token.controlCaps()) {
                    caps = token.setCaps();
                }
            }
            html.append("</p>\n");
        }
        return html.toString();
    }

    protected static String reverse(String str) {
        StringBuilder buf = new StringBuilder();
        for (int i = str.length() - 1; i >= 0; i--) {
            buf.append(str.charAt(i));
        }
        return buf.toString();
    }

    private static boolean matches(TrieNode root, String str) {
        int index = 0;
        while (index < str.length()) {
            int index2 = index + 1;
            root = root.getChild(str.charAt(index));
            if (root == null) {
                index = index2;
                break;
            } else if (root.exists()) {
                return true;
            } else {
                index = index2;
            }
        }
        return false;
    }

    private static TrieNode longestMatch(TrieNode root, AbstractMessageParser p, int start) {
        return longestMatch(root, p, start, false);
    }

    private static TrieNode longestMatch(TrieNode root, AbstractMessageParser p, int start, boolean smiley) {
        int index = start;
        TrieNode bestMatch = null;
        while (index < p.getRawText().length()) {
            int index2 = index + 1;
            root = root.getChild(p.getRawText().charAt(index));
            if (root == null) {
                index = index2;
                break;
            }
            if (root.exists()) {
                if (p.isWordBreak(index2)) {
                    bestMatch = root;
                    index = index2;
                } else if (smiley && p.isSmileyBreak(index2)) {
                    bestMatch = root;
                    index = index2;
                }
            }
            index = index2;
        }
        return bestMatch;
    }
}
