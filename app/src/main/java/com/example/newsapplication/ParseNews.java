package com.example.newsapplication;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseNews {
    private static final String TAG = "ParseNews";
    private ArrayList<FeedEntry> news;

    public ParseNews()
    {
        news = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getNews() {
        return news;
    }

    public boolean parse(String XMLData)
    {
        boolean status = true;
        boolean inItem = false;
        FeedEntry currentRecord = null;
        String textValue  = "";
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(XMLData));
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT)
            {
                String tagName = xpp.getName();
                //Log.d(TAG, "parse: tag name is " + tagName);
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        if("item".equals(tagName))
                        {
                            inItem = true;
                            currentRecord = new FeedEntry();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case  XmlPullParser.END_TAG:
                        if(inItem)
                        {
             //               Log.d(TAG, "parse: Ending tag for " + tagName);
                            if("item".equals(tagName))
                            {
                                news.add(currentRecord);
                                inItem = false;
                            }
                            else if("title".equalsIgnoreCase(tagName))
                            {
                                currentRecord.setTitle(textValue);
                            }
                            else if("link".equalsIgnoreCase(tagName))
                            {
                                currentRecord.setLinkToStory(textValue);
                            }
                            else if("description".equalsIgnoreCase(tagName))
                            {
                                int l = textValue.length();
                      //          Log.d(TAG, "parse:  text Value is " + textValue);
                                String actualTextValue = "";
                                if(textValue.charAt(0) == '<') {
                                    for (int i = 0; i < l; i++) {
                                        if (textValue.charAt(i) == 'a' && textValue.charAt(i + 1) == '>') {
                                            actualTextValue = textValue.substring(i+2);
                                            break;
                                        }
                                    }
                                    if(textValue.endsWith("</a>"))
                                    {
                                        actualTextValue = "No description provided";
                                    }
                                }
                                else
                                    actualTextValue = textValue;
               //                 Log.d(TAG, "parse: actual text value is " + actualTextValue);
                                currentRecord.setDescription(actualTextValue);
                            }
                        }
                        break;
                }
                eventType = xpp.next();
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "parse: Error occured " + e.getMessage());
            status = false;
        }
        return status;
    }

}
