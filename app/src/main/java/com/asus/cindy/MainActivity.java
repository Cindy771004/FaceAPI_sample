package com.asus.cindy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private String TAG ="MainActivity";

    FaceAPI mFaceAPI;
    Button mFaceDetectBtn;
    Button mCreatePersonGroupIDBtn;
    Button mDeletePersonGroupIDBtn;
    Button mCreatePersonBtn;
    Button mDeletePersonBtn;
    Button mAddPersonFaceBtn;
    Button mTrainPersonGroupIDBtn;
    Button mIdentifyFaceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFaceAPI= new FaceAPI();

        mCreatePersonGroupIDBtn= (Button) findViewById(R.id.PersonGroupIDBtn);
        mCreatePersonGroupIDBtn.setOnClickListener(cteatePersonGroupIDClick);

        mDeletePersonGroupIDBtn = (Button) findViewById(R.id.DeletePersonGroupIDBtn);
        mDeletePersonGroupIDBtn.setOnClickListener(DeletePersonGroupIDClice);

        mCreatePersonBtn =(Button) findViewById(R.id.CreatePersonBtn);
        mCreatePersonBtn.setOnClickListener(createPersonClick);

        mDeletePersonBtn = (Button) findViewById(R.id.DeletePersonBtn);
        mDeletePersonBtn.setOnClickListener(deletePersonClick);

        mAddPersonFaceBtn = (Button) findViewById(R.id.AddPersonFaceBtn);
        mAddPersonFaceBtn.setOnClickListener(addPersonFaceClick);

        mTrainPersonGroupIDBtn = (Button) findViewById(R.id.TrainPersonGroupIDBtn);
        mTrainPersonGroupIDBtn.setOnClickListener(trainPersonGroupIDClick);

        mIdentifyFaceBtn=(Button) findViewById(R.id.IdentifyFaceBtn);
        mIdentifyFaceBtn.setOnClickListener(identifyFaceClick);

        mFaceDetectBtn= (Button) findViewById(R.id.faceDetectBtn);
        mFaceDetectBtn.setOnClickListener(faceDetectClick);
    }

    private View.OnClickListener cteatePersonGroupIDClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG,"cteatePersonGroupClick");
            String personGroupID="cindy1";
            mFaceAPI.startCteatePersonGroupID(personGroupID);
        }
    };

    private View.OnClickListener DeletePersonGroupIDClice =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "DeletePersonGroupIDClick");
            String personGroupID="cindy1";
            mFaceAPI.startDeletePersonGroupID(personGroupID);
        }
    };

    private View.OnClickListener createPersonClick = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.d(TAG, "CreatePersonClick");
            String personGroupID="cindy1";
            String psersonName="person1";
            mFaceAPI.startCreatePerson(personGroupID,psersonName);
        }
    };

    private View.OnClickListener deletePersonClick= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "DeletePersonClick");
            String personGroupID="cindy1";
            String psersonID="51dd748e-f3d8-41a0-9104-1212bbef7aee";
            mFaceAPI.startDeletePerson(personGroupID,psersonID);
        }
    };

    private View.OnClickListener addPersonFaceClick= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "addPersonFaceClick");
            String personGroupID="cindy1";
            String personID="8a99a8cc-553c-4331-964b-cd9e9869d48d";
            byte[] image=getJPEGContent("man.jpg");
            mFaceAPI.startAddPersonFace(personGroupID,personID,image);
        }
    };

    private View.OnClickListener trainPersonGroupIDClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "trainPersonGroupIDClick");
            String personGroupID="cindy1";
            mFaceAPI.startTrainPersonGroupID(personGroupID);
        }
    };

    private View.OnClickListener identifyFaceClick = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.d(TAG, "identifyFaceclick");
            String personGroupID="cindy1";
            byte[] image=getJPEGContent("man.jpg");
            mFaceAPI.startIdentifyFace(personGroupID,image);
        }
    };

    private View.OnClickListener faceDetectClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG,"faceDetectClick");
            byte[] image=getJPEGContent("man.jpg");
            mFaceAPI.startFaceDetect(image);
        }
    };

    private byte[] getJPEGContent(String filename) {
        try {
//            AssetManager mngr=  context.getAssets();
//            InputStream image = mngr.open(filename);
            InputStream image = this.getAssets().open(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, "get JPEG Fail");
            e.printStackTrace();
        }
        return null;
    }


}
