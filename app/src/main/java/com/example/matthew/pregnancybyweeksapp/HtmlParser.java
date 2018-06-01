package com.example.matthew.pregnancybyweeksapp;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HtmlParser {

    private String youTubeSignature;
    private ArrayList<String> paragraphs = new ArrayList<>();
    private ArrayList<String> helpfulLinkTitles = new ArrayList<>();
    private ArrayList<String> helpfulLinks = new ArrayList<>();
    private String pageTitle;
    public Boolean parseFailed = false;

    HtmlParser(int weekNum) {
        long millis = System.currentTimeMillis();
        String link = "http://medtwice.com/pregnancy-by-weeks-week-" +  weekNum + "/";
        parseData(link);
        Log.i("parse time", System.currentTimeMillis() - millis + "");
    }

    HtmlParser(String link) {
        if (link.equals("http://medtwice.com/labs-during-first-prenatal-visit/"))
            Log.i("broken link", "link is ok");
        parseData(link);
    }

    private void parseData(final String link) {
        try {
            Document document = Jsoup.connect(link).get();
            Log.i("parse start", "parse started " + link);
            Elements rawParagraphs = document.select("p");
            boolean isPBW = false;
            extractPageTitle(document.title());
            for(Element p : rawParagraphs) {
                String paragraph = p.html();
                if (paragraph.contains("Helpful Links"))
                    isPBW = true;
                else if (paragraph.contains("<iframe"))
                    extractYouTubeSignature(paragraph);
                else if (paragraph.contains("<a title=") && isPBW)
                    extractHelpfulLink(paragraph);
                else if (paragraph.contains(">Continue<"))
                    extractListPageLink(paragraph);
                //Get rid of unwanted paragraphs from link
                else if (!paragraph.contains("\"http://medtwice.com/\"")
                        && !paragraph.contains("Medical Videos By Real Doctors")
                        && !paragraph.equals("")
                        && !paragraph.contains("Helpful Links")
                        && !paragraph.contains("medtwice@gmail.com")
                        && !paragraph.contains("\"footer-text\"")) {
                    extractTextParagraph(paragraph);
                }
            }
            Log.i("parse", "parse finished " + link);
        } catch (IOException e) {
            e.printStackTrace();
            parseFailed = true;
        }
    }

    private void extractYouTubeSignature(String paragraph) {
        youTubeSignature = StringUtils.substringBetween(paragraph, "embed/", "?rel=");
    }

    private void extractHelpfulLink(String paragraph) {
        helpfulLinkTitles.add(StringUtils.substringBetween(paragraph, "title=\"","\""));
        helpfulLinks.add(StringUtils.substringBetween(paragraph, "href=\"", "\""));
    }

    private void extractListPageLink(String paragraph) throws IOException {
        String link = StringUtils.substringBetween(paragraph, "href=\"", "/\"");
        String title = StringUtils.substringAfter(link, "http://medtwice.com/");
        title = title.replace("-", " ");
        title = StringUtils.capitalize(title);
        helpfulLinkTitles.add(title);
        helpfulLinks.add(link);
    }

    private void extractTextParagraph(String paragraph) {
        String paragraphText;
        //extract paragraph with embedded link with title tag
        if (paragraph.contains("<a title=")) {
            StringBuilder text = new StringBuilder();
            String link;
            String linkTitle;
            while (paragraph.contains("<a title=")) {
                text.append(StringUtils.substringBefore(paragraph, "<a title="));
                linkTitle = StringUtils.substringBetween(paragraph, "<a title=\"", "\"");
                link = StringUtils.substringBetween(paragraph, "href=\"", "\"");
                helpfulLinks.add(link);
                helpfulLinkTitles.add(linkTitle);
                text.append(linkTitle).append("*");
                paragraph = StringUtils.substringAfter(paragraph, "</a>");
            }
            text.append(paragraph);
            paragraphText = text.toString();
        }
        //extract paragraph with bold category and info text
        else if (paragraph.contains("<strong>")) {
            String text = StringUtils.substringBetween(paragraph, "<strong>", "</");
            text += "\n";
            text += StringUtils.substringAfter(paragraph, "<br> ");
            paragraphText = text;
        }
        //extract paragraph with multiple embedded links no title tag
        else if (paragraph.contains("href=\"")) {
            StringBuilder text = new StringBuilder();
            String link;
            String linkTitle;
            while (paragraph.contains("<a href=\"")) {
                text.append(StringUtils.substringBefore(paragraph, "<a href=\""));
                link = StringUtils.substringBetween(paragraph, "href=\"", "\">");
                linkTitle = StringUtils.substringBetween(paragraph, "\">", "</a>");
                helpfulLinks.add(link);
                helpfulLinkTitles.add(linkTitle);
                text.append(linkTitle).append("*");
                paragraph = StringUtils.substringAfter(paragraph, "</a>");
            }
            text.append(paragraph);
            paragraphText = text.toString();
        } else
            paragraphText = paragraph;
        paragraphText = removeTextJunk(paragraphText);
        if (!paragraphText.equals(""))
            paragraphs.add(paragraphText);
    }

    private void extractPageTitle(String title) {
        pageTitle = StringUtils.substringBefore(title, " | MedTwice");
    }

    private String removeTextJunk(String text) {
        text = StringUtils.remove(text, "&nbsp;");
        text = StringUtils.remove(text, "<em>");
        text = StringUtils.remove(text, "</em>");
        text = StringUtils.remove(text, "amp;");
        text = StringUtils.remove(text, "\n");
        return text;
    }

    public String getYouTubeSignature() {
        return youTubeSignature;
    }

    public ArrayList<String> getParagraphs() {
        return paragraphs;
    }

    public ArrayList<String> getHelpfulLinkTitles() {
        return helpfulLinkTitles;
    }

    public ArrayList<String> getHelpfulLinks() {
        return helpfulLinks;
    }

    public String getPageTitle() {
        return pageTitle;
    }
}
