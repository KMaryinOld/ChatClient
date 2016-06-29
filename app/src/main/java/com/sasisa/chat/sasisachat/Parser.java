package com.sasisa.chat.sasisachat;

import android.text.Html;
import android.util.Log;

import com.sasisa.chat.sasisachat.dialog.UserDialog;
import com.sasisa.chat.sasisachat.dialog.UserDialogMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cherry on 03.09.2015.
 */
public class Parser {
    public static List<String> parseMessages(String pageFragment) {
        List<String> returnMessages = new ArrayList<>();

        MessagePosition position = findNextStartMessagePos(pageFragment, 0);
        while(position.messageStartPosition != -1) {
            MessagePosition currentMessageStart = position;
            position = findNextStartMessagePos(pageFragment, position.textStartPosition);

            if (position.messageStartPosition == -1) {
                returnMessages.add(pageFragment.substring(currentMessageStart.messageStartPosition));
            } else {
                returnMessages.add(pageFragment.substring(currentMessageStart.messageStartPosition,
                        position.messageStartPosition));
            }
        }

        return returnMessages;
    }

    private static MessagePosition findNextStartMessagePos(String page, int startPos) {
        MessagePosition pos = new MessagePosition();

        int startMessagePos = page.indexOf("&gt;", startPos);
        if (startMessagePos != -1) {
            pos.textStartPosition = startMessagePos + 4;
            if (page.charAt(startMessagePos - 1) == ')') {
                while(true) {
                    startMessagePos--;
                    if (page.charAt(startMessagePos) == '(') {
                        break;
                    }
                }
                if (page.charAt(startMessagePos - 1) == '>' && page.charAt(startMessagePos - 2) == 'b' &&
                        page.charAt(startMessagePos - 3) == '/' && page.charAt(startMessagePos - 4) == '<') {
                    while(true) {
                        startMessagePos--;
                        if (startMessagePos == 1) {
                            startMessagePos = 0;
                            break;
                        }
                        if (page.charAt(startMessagePos) == '>' && page.charAt(startMessagePos - 1) == 'b' &&
                                page.charAt(startMessagePos - 2) == '<') {
                            startMessagePos -= 2;
                            break;
                        }
                    }
                }
            }
            pos.messageStartPosition = startMessagePos;
        }

        return pos;
    }

    public static List<URLParameters> parseUsersPage(String page) {
        List<URLParameters> returnList = new ArrayList<>();
        Pattern p = Pattern.compile("<a href=\"/chat/inside\\.php\\?nk=([\\d]+)&amp;rm=([\\d]+)\">(.+?)</a>", Pattern.DOTALL);
        Matcher m = p.matcher(page);
        while (m.find()) {
            returnList.add(new URLParameters("/chat/inside.php", m.group(1), m.group(3)));
        }

        return returnList;
    }

    public static String replaceImageLinks(String text) {
        Pattern p = Pattern.compile("<img src=\"", Pattern.DOTALL);
        String[] parts = p.split(text);

        StringBuilder replacedText = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; ++i) {
            if (parts[i].charAt(0) == 'h' && parts[i].charAt(1) == 't' && parts[i].charAt(2) == 't' &&
                    parts[i].charAt(3) == 'p') {
                replacedText.append(String.format("<img src=\"%s", parts[i]));
            } else if (parts[i].charAt(0) == '/') {
                replacedText.append(String.format("<img src=\"http://wap.sasisa.ru%s", parts[i]));
            } else {
                replacedText.append(String.format("<img src=\"http://wap.sasisa.ru/chat/%s", parts[i]));
            }
        }
        return replacedText.toString();
    }

    public static String replaceALinks(String text) {
        Pattern p = Pattern.compile("<a href=\"", Pattern.DOTALL);
        text = text.replaceAll("\n|\t", "");
        String[] parts = p.split(text);

        StringBuilder replacedText = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; ++i) {
            if (parts[i].charAt(0) == 'h' && parts[i].charAt(1) == 't' && parts[i].charAt(2) == 't' &&
                    parts[i].charAt(3) == 'p') {
                replacedText.append(String.format("<a href=\"%s", parts[i]));
            } else if (parts[i].charAt(0) == '/') {
                replacedText.append(String.format("<a href=\"http://wap.sasisa.ru%s", parts[i]));
            } else {
                replacedText.append(String.format("<a href=\"http://wap.sasisa.ru/chat/%s", parts[i]));
            }
        }
        return replacedText.toString();
    }

    public static String parseUserInfoPage(String page) {
        Pattern p = Pattern.compile("Закладки</a><br/>---<br/>(.+?)<br/>---<br/>Статистика по обменнику(.+?)" +
                "<br/>---<br/>Дата регистрации: <b>(.+?)</b>", Pattern.DOTALL);
        page = page.replaceAll("\n|\t", "");
        Matcher m = p.matcher(page);
        if (m.find()) {
            page = String.format("%s<br />Дата регистрации: <b>%s</b>", m.group(1), m.group(3));
        }
        return page;
    }

    public static String parsePageTitle(String page) {
        Pattern p = Pattern.compile("<title>(.+?)</title>", Pattern.DOTALL);
        page = page.replaceAll("\n|\t", "");
        Matcher m = p.matcher(page);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    private static class MessagePosition {
        public int messageStartPosition = -1;
        public int textStartPosition = 0;
    }

    public static class URLParameters {
        public String link = "";
        public String name = "";
        public String value = "";

        public URLParameters(String link, String value, String name) {
            this.link = link;
            this.value = value;
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public static boolean isHaveUnderlineUsername(String s, String username) {
        Pattern p = Pattern.compile("<u>(.+?)</u>", Pattern.DOTALL);
        s = s.replaceAll("\n|\t", "");
        Matcher m = p.matcher(s);
        while (m.find()) {
            if (m.group(1).equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static Vector<RoomListElement> parseNewsList(String page) {
        Vector<RoomListElement> v = new Vector<>();

        Pattern p = Pattern.compile("<a href=\"view_obiav\\.php\\?mid=([\\d]+)\">(.+?)</a>", Pattern.DOTALL);
        page = page.replaceAll("\n|\t", "");
        Matcher m = p.matcher(page);
        while (m.find()) {
            v.add(new RoomListElement(Html.fromHtml(m.group(2)).toString(),
                    String.format("http://wap.sasisa.ru/chat/view_obiav.php?mid=%s", m.group(1))));
        }

        return v;
    }

    public static String parseNewsContent(String page) {
        Pattern p = Pattern.compile("<div class=\"c1\">(.+?)</div>(.+?)<div class=\"c1\">", Pattern.DOTALL);
        page = page.replaceAll("\n", "");
        Matcher m = p.matcher(page);

        if (m.find()) {
            return m.group(2);
        }
        return "";
    }

    public static int parseMessagesCount(String page) {
        Pattern p = Pattern.compile("<a href=\"/chat/im.php\"><img src=\"/images/msg_ico.gif\" alt=\" \" /> Новых писем: ([\\d+])</a>", Pattern.DOTALL);
        page = page.replaceAll("\n|\t", "");
        Matcher m = p.matcher(page);

        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }

        return 0;
    }

    public static Vector<UserDialog> parseUserDialogs(String page) {
        Vector<UserDialog> dialogs = new Vector<>();
        Pattern p = Pattern.compile("<a class=\"block\" href=\"\\?sel=(.+?)\">(.+?)</a>", Pattern.DOTALL);
        page = page.replaceAll("\n|\t", "");
        Matcher m = p.matcher(page);
        while(m.find()) {
            dialogs.add(new UserDialog(m.group(1), m.group(2)));
        }

        return dialogs;
    }

    public static Vector<UserDialogMessage> parseDialogMessages(String page) {
        Vector<UserDialogMessage> userDialogMessages = new Vector<>();

        Pattern p = Pattern.compile("<table class=\"im\">(.+?)</table>", Pattern.DOTALL);
        Pattern pimgURL = Pattern.compile("<img class=\"im_avatar_small\" src=\"(.+?)\"", Pattern.DOTALL);
        Pattern pusername = Pattern.compile("<a href=\"/chat/inside.php\\?nk=(.+?)\">(.+?)</a>", Pattern.DOTALL);
        Pattern pmessageText = Pattern.compile("<td class=\"im_text\" colspan=\"2\">(.+?)</td>", Pattern.DOTALL);
        page = page.replaceAll("\n|\t", "");
        Matcher m = p.matcher(page);

        while (m.find()) {
            UserDialogMessage message = new UserDialogMessage();
            Matcher mimgURL = pimgURL.matcher(m.group(1));
            if (mimgURL.find()) {
                message.imgURL = mimgURL.group(1);
            }

            Matcher musername = pusername.matcher(m.group(1));
            if (musername.find()) {
                message.userID = musername.group(1);
                message.username = musername.group(2);
            }

            Matcher mmessageText = pmessageText.matcher(m.group(1));
            if (mmessageText.find()) {
                message.messageText = mmessageText.group(1).replaceAll("[\\s]{2,}", " ");
            }

            userDialogMessages.add(message);
        }

        return userDialogMessages;
    }

    public static String getBannedMessage(String page) {
        Pattern p = Pattern.compile("<div>(.+?) выпнул вас из чата. Разбан через (.+?).<br/>Причина: (.+?)<br/>", Pattern.DOTALL);
        page = page.replaceAll("\n|\t", "");
        Matcher m = p.matcher(page);

        if (m.find()) {
            return String.format("%s выпнул вас из чата. Разбан через %s. Причина: %s", m.group(1), m.group(2), m.group(3));
        }

        return null;
    }

    public static List<String> getUsernamesFromText(String text) {
        List<String> usernames = new ArrayList<>();
        Pattern p = Pattern.compile("<a(.+?)inside\\.php(.+?)>(.+?)</a>", Pattern.DOTALL);
        Matcher m = p.matcher(text);
        while(m.find()) {
            usernames.add(m.group(3));
        }

        return usernames;
    }
}
