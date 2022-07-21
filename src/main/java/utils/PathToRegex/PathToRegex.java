package utils.PathToRegex;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathToRegex {
    public static class Config {
        private boolean strict;
        private boolean end;
        private boolean sensitive;
        private boolean start;

        public Config() {
            strict = false;
            sensitive = false;
            end = true;
            start = true;
        }

        public Config(boolean strict, boolean end, boolean sensitive, boolean start) {
            this.strict = strict;
            this.end = end;
            this.sensitive = sensitive;
            this.start = start;
        }

        public boolean isStart() {
            return start;
        }

        public void setStart(boolean start) {
            this.start = start;
        }

        public boolean isStrict() {
            return strict;
        }

        public void setStrict(boolean strict) {
            this.strict = strict;
        }

        public boolean isEnd() {
            return end;
        }

        public void setEnd(boolean end) {
            this.end = end;
        }

        public boolean isSensitive() {
            return sensitive;
        }

        public void setSensitive(boolean sensitive) {
            this.sensitive = sensitive;
        }
    }


    public static Pattern parse(String path, List<Key> keyList, Config options) {
        if (options == null) {
            options = new Config();
        }

        List<Key> keys = Objects.requireNonNullElseGet(keyList, ArrayList::new);

        boolean strict = options.isStrict();
        boolean end = options.end;
        String flags = options.sensitive ? "" : "i";
        int extraOffset = 0;
        String escapeRegex = "^" + path + (strict ? "" : (path.charAt(path.length() - 1) == '/' ? "?" : "/?")).replaceAll("/\\(", "(?:");
        String replacingSlashPeriod = escapeRegex.replaceAll("([.])", "\\\\$1");
        Pattern keyPattern = Pattern.compile("(/)?(\\\\.)?:(\\w+)(\\(.*/\\))?(\\*)?(\\?)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = keyPattern.matcher(replacingSlashPeriod);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            final int offset = matcher.start();
            String slash = matcher.group(1) == null ? "" : matcher.group(1);
            String format = matcher.group(2) == null ? "" : matcher.group(2);
            String key = matcher.group(3);
            String capture = matcher.group(4) == null ? "([^\\\\\\\\\\\\\\\\/" + format + "]+?)" : "";
            String star = matcher.group(5);
            String optional = matcher.group(6) == null ? "" : matcher.group(6);
            keys.add(new Key(key, (optional != null && !optional.isEmpty()), extraOffset + offset));

            String result = ""
                    + (optional == null ? slash : "")
                    + "(?:"
                    + format + (optional != null ? slash : "") + capture
                    + (star != null ? "((?[\\\\/" + format + "].+?)?)" : "")
                    + ")"
                    + optional;
            extraOffset += result.length() + matcher.group(0).length();
            matcher.appendReplacement(sb, result);
        }

        matcher.appendTail(sb);
        if (flags.equals("i"))
            return Pattern.compile(sb.toString() + (end ? "$" : sb.charAt(sb.length() - 1) == '/' ? "" : "(?=/|$)"), Pattern.CASE_INSENSITIVE);
        return Pattern.compile(sb.toString() + (end ? "$" : sb.charAt(sb.length() - 1) == '/' ? "" : "(?=/|$)"));
    }
}
