package com.example.uploaddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button uploadBtn, chooseBtn;
    private EditText editText;
    private ImageView imageView;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    private Files file;
    private String UploadUrl = "http://198.199.74.228/api/v1/users/profile/image/2/";
//    private String UploadUrl = "http://198.199.74.228/auth/login/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadBtn = findViewById(R.id.uploadbtn);
        chooseBtn = findViewById(R.id.choosebtn);
        editText = findViewById(R.id.editText);
        imageView = findViewById(R.id.imageView);

        chooseBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.choosebtn:
                selectImage();
                break;

            case R.id.uploadbtn:
                uploadImage();
                break;
        }

    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {

            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                //file = MediaStore.Files.FileColumn;
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void uploadImage() {

//        StringRequest stringRequest = new StringRequest(Request.Method.POST, UploadUrl
//                , new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                try {
//
//                    JSONObject jsonObject = new JSONObject(response);
//                    String Response = jsonObject.getString("data");
//                    Toast.makeText(MainActivity.this, Response, Toast.LENGTH_LONG).show();
//                    imageView.setImageResource(0);
//                    imageView.setVisibility(View.GONE);
//                    editText.setText("");
//                    editText.setVisibility(View.GONE);
//                    System.out.println("Response" + Response);
//                } catch (JSONException e) {
//                    JSONObject jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(response);
//                        String Response = jsonObject.getString("data");
//                        System.out.println("Response" + Response);
//                    } catch (JSONException ex) {
//                        ex.printStackTrace();
//                    }
//                    e.printStackTrace();
//                }
//
//            }

        JSONObject loginBody = new JSONObject();

        try {
            loginBody.put("file",  imageToString(bitmap));
//            loginBody.put("user",  2);
//            loginBody.put("id",  9);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, UploadUrl, loginBody,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("status") == 1) {

                                System.out.println("onReponseProfile: " + response);
//                                Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
//                                Log.d("Login", "onResponse: id " + response.getString("id"));
//                                System.out.println("Token: " + response.getString("user"));
//                                startActivity(myIntent);
                            } else {
                                System.out.println("onReponseERRORProfile: " + response);
                                Context context = getApplicationContext();
                                CharSequence messages = response.getString("message");
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, messages, Toast.LENGTH_LONG);
                                toast.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("file", imageToString(bitmap));
//
//                return params;
//            }

            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Token 22796d2fd0d5c3397d0a9cb4e151f1b7067c364771cbfe041f461df1c4482b80");
                return headers;
            }
        };

        MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

}
