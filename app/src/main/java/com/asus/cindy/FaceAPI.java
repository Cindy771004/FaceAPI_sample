package com.asus.cindy;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Cindyyh_Chou on 2017/4/5.
 */

public class FaceAPI {
    private String TAG = "FaceAPI";

    private final String CreatePersonGroupID="create_person_group_id";
    private final String DeletePersonGroupID="delete_person_group_id";
    private final String CreatePerson="create_person";
    private final String DeletePerson="delete_person";
    private final String AddPersonFace="add_person_face";
    private final String TrainPersonGroupID="train_person_group_id";
    private final String FaceDetect ="face_detect";
    private final String IdentifyFace="identify_face";

    private String mSubscriptionKey = "0b2d5ad5201e4d44ba1a4dc650a76d49";

    public void startFaceDetect(byte[] image){
        new HttpAsyncTask(image).execute(FaceDetect);
    }

    public void startCteatePersonGroupID(String PersonGroupID){
        new HttpAsyncTask(null).execute(CreatePersonGroupID,PersonGroupID);
    }

    public void startDeletePersonGroupID(String PersonGroupID){
        new HttpAsyncTask(null).execute(DeletePersonGroupID,PersonGroupID);
    }

    public void startCreatePerson(String PersonGroupID,String PersonName){
        new HttpAsyncTask(null).execute(CreatePerson,PersonGroupID,PersonName);
    }

    public void startDeletePerson(String personGroupID, String personID ){
        new HttpAsyncTask(null).execute(DeletePerson,personGroupID,personID);
    }

    public void startAddPersonFace(String personGroupID, String personID,byte[] image){
        new HttpAsyncTask(image).execute(AddPersonFace,personGroupID,personID);
    }

    public void startTrainPersonGroupID(String personGroupID){
        new HttpAsyncTask(null).execute(TrainPersonGroupID,personGroupID);
    }

    public void startIdentifyFace(String personGroupID, byte[] image){
        new HttpAsyncTask(image).execute(FaceDetect,personGroupID);
    }

    private class HttpAsyncTask extends AsyncTask<String,Void,String[]>{

        byte[] mImage;
        HttpAsyncTask(byte[] image){
            mImage=image;
        }

        @Override
        protected String[] doInBackground(String... strings) {
            switch (strings[0]){

                case CreatePersonGroupID:
                    CteatePersonGroupID(strings[1]);
                    break;

                case DeletePersonGroupID:
                    DeletePersonGroupID(strings[1]);
                    break;

                case CreatePerson:
                    CreatePerson(strings[1],strings[2]);
                    break;

                case DeletePerson:
                    DeletePerson(strings[1],strings[2]);
                    break;

                case AddPersonFace:
                    AddPersonFace(strings[1],strings[2],mImage);
                    break;

                case TrainPersonGroupID:
                    TrainPersonGroupID(strings[1]);
                    break;

                case FaceDetect:
                    String faceID=FaceDetect(mImage);
                    if(faceID!=null){
                        return new String[]{strings[0],strings[1],faceID};
                    }
                    break;
                case IdentifyFace:
                    IdentifyFace(strings[1],strings[2]);
                    break;
            }
            return new String[]{strings[0]};
        }

        @Override
        protected void onPostExecute(String[] strings) {
            switch (strings[0]){
                case FaceDetect:
                    new HttpAsyncTask(null).execute(IdentifyFace,strings[1],strings[2]);
            }
        }
    }

    private void CteatePersonGroupID(String personGroupID){
        Log.d(TAG, "CreatePersonGroupID / personGroupID="+personGroupID);

        String urltext ="https://westus.api.cognitive.microsoft.com/face/v1.0/persongroups/"+personGroupID;

        String responseData="";
        URL url;
        HttpURLConnection urlConnection=null;
        try {
            url= new URL(urltext);
            urlConnection= (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("PUT");
            //header
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key",mSubscriptionKey);

            //request body
            String userData = "";
            try {
                JSONObject json= new JSONObject();
                json.put("name",personGroupID);
                userData=json.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            urlConnection.setDoOutput(true);
            OutputStream out = urlConnection.getOutputStream();
            out.write(userData.getBytes());

            int responseCode = urlConnection.getResponseCode();
            if(responseCode==200){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in = urlConnection.getInputStream();
                responseData = readStream(in);
                Log.d(TAG, "responseData: "+responseData);
            }else if(responseCode>=400 && responseCode<=429){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in= urlConnection.getErrorStream();
                responseData = readStream(in);
                Log.d(TAG, "responseData: "+responseData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }
    }

    private void DeletePersonGroupID(String personGroupID){
        Log.d(TAG, "DeletePersonGroupID / personGroupID="+personGroupID);

        String urlText= "https://westus.api.cognitive.microsoft.com/face/v1.0/persongroups/"+personGroupID;

        String responseData="";
        URL url = null;
        HttpURLConnection urlConnection;
        try {
            url= new URL(urlText);
            urlConnection= (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key",mSubscriptionKey);
            int responseCode = urlConnection.getResponseCode();

            if(responseCode==200){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in = urlConnection.getInputStream();
                responseData = readStream(in);
                Log.i(TAG, "response=" + responseData);
            }else if(responseCode>=400 && responseCode<=429){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in =urlConnection.getErrorStream();
                responseData = readStream(in);
                Log.i(TAG, "error=" + responseData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CreatePerson(String personGroupID, String personName){
        Log.d(TAG, "CreatePerson / personGroupID="+personGroupID+"/personName="+personName);

        String urlText="https://westus.api.cognitive.microsoft.com/face/v1.0/persongroups/"+personGroupID+"/persons";

        String responseDate="";
        URL url;
        HttpURLConnection urlConnection=null;

        try {
            url=new URL(urlText);
            urlConnection=(HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key",mSubscriptionKey);

            String userData="";
            try{
                JSONObject json = new JSONObject();
                json.put("name",personName);
                userData=json.toString();
            }catch (JSONException e) {
                e.printStackTrace();
            }

            urlConnection.setDoOutput(true);
            OutputStream out = urlConnection.getOutputStream();
            out.write(userData.getBytes());

            int responseCode = urlConnection.getResponseCode();
            if(responseCode==200){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in = urlConnection.getInputStream();
                String responseData = readStream(in);
                Log.d(TAG, "responseData: "+responseData);

            }else if(responseCode>=400 && responseCode<=429){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in= urlConnection.getErrorStream();
                String responseData= readStream(in);
                Log.d(TAG, "responseData: "+responseData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
    }

    private void DeletePerson(String personGroupID, String personID){
        Log.d(TAG, "DeletePerson / personGroupID="+personGroupID+"/personID="+personID);

        String urlText="https://westus.api.cognitive.microsoft.com/face/v1.0/persongroups/"+personGroupID+"/persons/"+personID;

        URL url;
        HttpURLConnection urlConnection = null;
        try{
            url= new URL(urlText);
            urlConnection= (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key",mSubscriptionKey);

            int responseCode= urlConnection.getResponseCode();
            if(responseCode==200){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in = urlConnection.getInputStream();
                String responseData= readStream(in);
                Log.d(TAG,"responseData :"+responseData);
            }else if(responseCode>=400 && responseCode<=429){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in = urlConnection.getErrorStream();
                String responseData = readStream(in);
                Log.d(TAG,"error :"+responseData);
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }

    }

    private void AddPersonFace(String personGroupID, String personID,byte[] image){
        Log.d(TAG, "AddPersonFace / personGroupID="+personGroupID+" /personID="+personID);

        String urlText= "https://westus.api.cognitive.microsoft.com/face/v1.0/persongroups/"+personGroupID+"/persons/"+personID+"/persistedFaces";

        String responseDate="";
        URL url;
        HttpURLConnection urlConnection=null;

        try {
            url = new URL(urlText);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/octet-stream");
            urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key",mSubscriptionKey);

            urlConnection.setDoOutput(true);
            OutputStream out = urlConnection.getOutputStream();
            out.write(image);

            int responseCode = urlConnection.getResponseCode();
            if(responseCode==200){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in= urlConnection.getInputStream();
                String responseData= readStream(in);
                Log.d(TAG, "responseData: "+responseData);
            }else if(responseCode>=400 && responseCode<=429){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in=urlConnection.getInputStream();
                String responseData= readStream(in);
                Log.d(TAG, "error: "+responseData);
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }

    }

    private void TrainPersonGroupID(String personGroupID){
        Log.d(TAG, "TrainPersonGroupID / personGroupID="+personGroupID);

        String urlText= "https://westus.api.cognitive.microsoft.com/face/v1.0/persongroups/"+personGroupID+"/train";

        String responseDate="";
        URL url;
        HttpURLConnection urlConnection=null;

        try {
            url = new URL(urlText);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key",mSubscriptionKey);

            int responseCode = urlConnection.getResponseCode();
            if(responseCode==200 || responseCode==202){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in= urlConnection.getInputStream();
                String responseData= readStream(in);
                Log.d(TAG, "responseData: "+responseData);
            }else if(responseCode>=400 && responseCode<=429){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in=urlConnection.getInputStream();
                String responseData= readStream(in);
                Log.d(TAG, "error: "+responseData);
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }
    }

    private String FaceDetect(byte[] image){
        Log.d(TAG, "FaceDetect ");

        String urlText ="https://westus.api.cognitive.microsoft.com/face/v1.0/detect";

        String responseData = "";
        URL url=null;
        HttpURLConnection urlConnection;
        try {
            url= new URL(urlText);
            urlConnection= (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            //set header
            urlConnection.setRequestProperty("Content-Type","application/octet-stream");
            urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key",mSubscriptionKey);

            urlConnection.setDoOutput(true);
            OutputStream out = urlConnection.getOutputStream();
            out.write(image);

            int responseCode = urlConnection.getResponseCode();
            String faceId="";
            if(responseCode==200){
                try {
                    Log.d(TAG, "responseCode: "+responseCode);
                    InputStream in = urlConnection.getInputStream();
                    responseData = readStream(in);

                    JSONArray json = new JSONArray(responseData);
                    JSONObject face = json.getJSONObject(0);
                    faceId = (String) face.get("faceId");

                    return faceId;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(responseCode>=400 && responseCode<=429){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in =urlConnection.getErrorStream();
                responseData = readStream(in);
                Log.i(TAG, "error=" + responseData);
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void IdentifyFace(String personGroupID, String faceID){
        Log.d(TAG, "IdentifyFace /personGroupID="+personGroupID+" /faceID="+faceID);

        String urlText ="https://westus.api.cognitive.microsoft.com/face/v1.0/identify";

        String responseData = "";
        URL url=null;
        HttpURLConnection urlConnection;

        try{
            url= new URL(urlText);
            urlConnection= (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key",mSubscriptionKey);

            String userData="";
            try {
                JSONObject json = new JSONObject();
                json.put("personGroupId",personGroupID);
                json.put("maxNumOfCandidatesReturned",1);
                json.put("confidenceThreshold",0.5);

                JSONArray jsonArray= new JSONArray();
                jsonArray.put(faceID);
                json.put("faceIds",jsonArray);

                userData=json.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            urlConnection.setDoOutput(true);
            OutputStream out = urlConnection.getOutputStream();
            out.write(userData.getBytes());

            int responseCode = urlConnection.getResponseCode();
            if(responseCode==200 ){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in = urlConnection.getInputStream();
                responseData = readStream(in);
                Log.d(TAG, "responseData: "+responseData);
            }else if(responseCode>=400 && responseCode<=429){
                Log.d(TAG, "responseCode: "+responseCode);
                InputStream in = urlConnection.getErrorStream();
                responseData = readStream(in);
                Log.d(TAG, "responseData: "+responseData);
            }

        }catch (IOException e){
            e.getStackTrace();
        }
    }
    public static String readStream(InputStream in) {
        char[] buf = new char[2048];
        Reader r = null;
        try {
            r = new InputStreamReader(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder s = new StringBuilder();
        while (true) {
            int n = 0;
            try {
                n = r.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (n < 0)
                break;
            s.append(buf, 0, n);
        }
        return s.toString();
    }
}
