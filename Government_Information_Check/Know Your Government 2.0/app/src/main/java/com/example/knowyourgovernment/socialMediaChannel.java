package com.example.knowyourgovernment;

/*
  These are the user ids for the related social media channels.
  There will be up to four entries. Possible entries are:
    • GooglePlus
    • Facebook
    • Twitter
    • YouTube
  Some or all of these 4 items might be omitted.
 */

import java.io.Serializable;

public class socialMediaChannel implements Serializable {
    private String googlePlusId;
    private String facebookId;
    private String twitterId;
    private String youtubeId;

    public String getGooglePlusId() {
        return googlePlusId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setGooglePlusId(String googlePlusId) {
        this.googlePlusId = googlePlusId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

}
