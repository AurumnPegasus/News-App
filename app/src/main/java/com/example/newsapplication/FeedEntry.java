package com.example.newsapplication;

import androidx.annotation.NonNull;

public class FeedEntry {
    private String title;
    private String description;
    private String linkToStory;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLinkToStory() {
        return linkToStory;
    }

    public void setLinkToStory(String linkToStory) {
        this.linkToStory = linkToStory;
    }

    @NonNull
    @Override
    public String toString() {
        return ("Title " + title + "\n" + "description = " + description + "link to the story " + linkToStory + "\n");
    }
}
