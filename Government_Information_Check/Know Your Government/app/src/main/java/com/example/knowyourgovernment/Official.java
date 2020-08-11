package com.example.knowyourgovernment;

import java.io.Serializable;

public class Official implements Serializable {
    private String officialName = "PPPPP"; //Test
    private String name = "DDDDD"; //Test
    private String address;
    private String party = "AAAAAA"; //Test
    private String phones;
    private String urls;
    private String emails;
    private String photoUrl;
    private socialMediaChannel channel;

    public Official(String oName) {
        this.officialName = oName;
    }
    //TEST
    public Official() {

    }
    public Official(String officeName, String name, String party) {
        this.name = name;
        this.officialName = officeName;
        this.party = party;
    }

    public Official(String officeName,
                    String name,
                    String address,
                    String party,
                    String phones,
                    String urls,
                    String emails,
                    String photoUrl,
                    socialMediaChannel c) {
        this.name = name;
        this.officialName = officeName;
        this.address = address;
        this.party = party;
        this.phones = phones;
        this.urls = urls;
        this.emails = emails;
        this.photoUrl = photoUrl;
        this.channel = c;
    }


    public String getOfficialName() {
        return officialName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getParty() {
        return party;
    }

    public socialMediaChannel getChannel() {
        return channel;
    }

    public String getPhones() {
        return phones;
    }

    public String getUrls() {
        return urls;
    }

    public String getEmails() {
        return emails;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

}
