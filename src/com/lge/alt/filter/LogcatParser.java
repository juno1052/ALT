package com.lge.alt.filter;

import java.util.Date;
import java.util.StringTokenizer;

import com.lge.alt.ALTHelper;

public abstract class LogcatParser implements IParser {

    public LogcatData toLogcatData(String line) {
        // later~ about boolean condition should be think more~
        if (line.contains("/")) {
            String date = null;
            String time = null;
            String tag = null;
            String pid = null;
            String content = null;

            int sp_1 = line.indexOf("/");
            String aboutTimeLog = line.substring(0, sp_1);
            String aboutContentLog = line.substring(sp_1 + 1);

            StringTokenizer st_time = new StringTokenizer(aboutTimeLog, " ");

            if (st_time.countTokens() == 3) {
                // tokens[0] = st_time.nextToken();
                // tokens[1] = st_time.nextToken();
                date = st_time.nextToken();
                time = st_time.nextToken();
            }

            if (aboutContentLog.contains(":")) {
                int sp_2 = aboutContentLog.indexOf(":");
                String tagWithPid = aboutContentLog.substring(0, sp_2);
                StringTokenizer st_tag = new StringTokenizer(tagWithPid, "()");
                if (st_tag.countTokens() == 2) {
                    // tokens[2] = st_tag.nextToken();
                    // tokens[3] = st_tag.nextToken();
                    tag = st_tag.nextToken();
                    pid = st_tag.nextToken();
                }

                // tokens[4] = aboutContentLog.substring(sp_2+1);
                content = aboutContentLog.substring(sp_2 + 1);
            }
            return new LogcatData(date, time, tag, pid, content);
            // return data.setLogcatData(date, time, tag, pid, content);
        }

        return null;
    }

    class LogcatData {

        private Date date;
        private String tag;
        private String pid;
        private String content;

        private boolean isSet = false;

        boolean isSet() {
            return isSet;
        }

        public LogcatData(String date, String time, String tag, String pid,
                String content) {
            // TODO Auto-generated constructor stub
            if (date != null && time != null && tag != null && pid != null
                    && content != null) {
                this.date = ALTHelper.stringToDate(date + " "
                        + time);
                this.tag = tag;
                this.pid = pid;
                this.content = content;

                isSet = true;
            }
        }

        String getContent() {
            return content;
        }

        Date getDate() {
            return date;
        }

        String getPid() {
            return pid;
        }

        String getTag() {
            return tag;
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return content;
        }
    }

}
