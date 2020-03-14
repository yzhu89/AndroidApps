package com.example.yawei_multinote;

public class Note {
    private String title;
    private String content;
    private String lastUpdateTime;
    private String text;

    //initialize the note
    public Note(){
        title = "";
        content = "";
        lastUpdateTime = "";
        text = "";
    }

    //change the data after user added note
    public Note(String inputTitle, String inputContent, String updateTime){
        this.title = inputTitle;
        this.content = inputContent;
        this.lastUpdateTime = updateTime;
        if(inputContent.length() > 80){
            text = content.substring(0, 79) + "...";
        }else{
            text = content;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getText() {
        return text;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
        if(content.length() > 80){
            text = content.substring(0, 79) + "...";
        }else{
            text = content;
        }
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

}
