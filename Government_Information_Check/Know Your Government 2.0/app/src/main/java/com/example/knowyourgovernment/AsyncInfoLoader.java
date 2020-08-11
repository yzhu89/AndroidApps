package com.example.knowyourgovernment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncInfoLoader extends AsyncTask<String, Integer, String> {
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    final private String URL_PREFIX =
            "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    final private String API_KEY = "AIzaSyARlLsGhl2EhsawduQnmdjS9WkYHeotsyY";
    final private String URL_SUFFIX = "&address=";

    private String request = "GET";
    private String address = "";

    private static final String TAG = "AsyncInfoLoader";
    AsyncInfoLoader(MainActivity ma){
        mainActivity = ma;
    }


    @Override
    protected String doInBackground(String... Params) {
        address = Params[0];
        String infoURL = URL_PREFIX + API_KEY + URL_SUFFIX + address;
        Uri infoURI = Uri.parse(infoURL);

        String urlToUse = infoURI.toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod(request);

            InputStream inputStream = c.getInputStream();
            BufferedReader bufferedReader =
                    new BufferedReader((new InputStreamReader(inputStream)));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        ArrayList<Official> officialsList = parseJson(s);
        if (officialsList != null)
            Toast.makeText
                    (mainActivity, "Loaded "
                            + officialsList.size()
                            + " officials.", Toast.LENGTH_SHORT).show();
        mainActivity.updateData(officialsList);
    }

    private ArrayList<Official> parseJson(String s) {
        ArrayList<Official> officialsList = new ArrayList<>();

        try{
            JSONObject jObjMain = new JSONObject(s);

            //The “normalizedInput” JSONObject contains: city, state, zip
            JSONObject jNormalInput = (JSONObject)jObjMain.getJSONObject("normalizedInput");

            //Set the Location display
            String locationText = jNormalInput.getString("city") + ", "
                    +jNormalInput.getString("state") + " "
                    + jNormalInput.getString("zip");
            mainActivity.setLocationText(locationText);

            //The “offices” JSONArray contains: name, officialIndices
            JSONArray jArrayOffices = jObjMain.getJSONArray("offices");
            JSONArray jArrayOfficials = jObjMain.getJSONArray("officials");
            int length = jArrayOffices.length();
            mainActivity.clearOfficialList();

            for (int i = 0; i < length; i++) { //The right Length is very important
                JSONObject jsonObject = (JSONObject)jArrayOffices.get(i);
                String officeName = jsonObject.getString("name");

                //Converting JSONArray to ArrayList
                JSONArray jsonIndices = jsonObject.getJSONArray("officialIndices");
                ArrayList<Integer> indices = new ArrayList<>();
                if (jsonIndices != null) {
                    for (int k=0; k<jsonIndices.length(); k++){
                        //Converting String to Int
                        indices.add(Integer.parseInt(jsonIndices.getString(k)));
                    }
                }

                //Match
                for (int j = 0; j<indices.size(); j++){
                    int pos = indices.get(j);
                    JSONObject jOfficial = jArrayOfficials.getJSONObject(pos);

                    //official name
                    String name = jOfficial.getString("name");

                    //official address
                    String address = "";
                    if(jOfficial.has("address")){
                        JSONArray jAddressInfo = jOfficial.getJSONArray("address");
                        JSONObject jAddress = jAddressInfo.getJSONObject(0);

                        if (jAddress.has("line1")){
                            address += jAddress.getString("line1")+'\n';
                        }
                        if (jAddress.has("line2")){
                            address += jAddress.getString("line2")+'\n';
                        }
                        if (jAddress.has("line3")){
                            address += jAddress.getString("line3")+'\n';
                        }
                        if (jAddress.has("city")){
                            address += jAddress.getString("city")+", ";
                        }
                        if (jAddress.has("state")){
                            address += jAddress.getString("state")+' ';
                        }
                        if (jAddress.has("zip")){
                            address += jAddress.getString("zip");
                        }

                    }
                    //official party
                    String party = null;
                    if (jOfficial.has("party")){
                        party = jOfficial.getString("party");
                    }

                    //official phones
                    String phones = null;
                    if (jOfficial.has("phones")){
                        phones = jOfficial.getJSONArray("phones").getString(0);
                    }

                    //official urls
                    String urls = null;
                    if (jOfficial.has("urls")){
                        urls = jOfficial.getJSONArray("urls").getString(0);
                    }

                    //official emails
                    String emails = null;
                    if (jOfficial.has("emails")) {
                        emails = jOfficial.getJSONArray("emails").getString(0);
                    }

                    //official photoUrl
                    String photoUrl = null;
                    if (jOfficial.has("photoUrl")) {
                        photoUrl = jOfficial.getString("photoUrl");
                    }

                    //official channel
                    socialMediaChannel channel = new socialMediaChannel();
                    if (jOfficial.has("channels")){
                        JSONArray jChannels = jOfficial.getJSONArray("channels");
                        for (int m = 0; m<jChannels.length(); m++){
                            JSONObject jChannel = jChannels.getJSONObject(m);
                            if (jChannel.getString("type").equals("GooglePlus")){
                                channel.setGooglePlusId(jChannel.getString("id"));
                            }
                            if (jChannel.getString("type").equals("Facebook")){
                                channel.setFacebookId(jChannel.getString("id"));
                            }
                            if (jChannel.getString("type").equals("Twitter")){
                                channel.setTwitterId(jChannel.getString("id"));
                            }
                            if (jChannel.getString("type").equals("YouTube")){
                                channel.setYoutubeId(jChannel.getString("id"));
                            }
                        }
                    }
                    //add new Official object into OfficialList
                    officialsList.add(new Official(officeName,
                                                    name,
                                                    address,
                                                    party,
                                                    phones,
                                                    urls,
                                                    emails,
                                                    photoUrl,
                                                    channel));
                }

            }
            return officialsList;
        }catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveTheAddress(String address){
        this.address = address;
        return true;
    }
}
