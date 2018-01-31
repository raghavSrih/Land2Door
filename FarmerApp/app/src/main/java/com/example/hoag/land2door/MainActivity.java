package com.example.hoag.land2door;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_FARM = "com.example.hoag.land2door.FARM";
    public static final String EXTRA_ABOUT = "com.example.hoag.land2door.ABOUT";
    public static final String EXTRA_NAME = "com.example.hoag.land2door.NAME";
    public static final String EXTRA_PHONE = "com.example.hoag.land2door.PHONE";
    public static final String EXTRA_MOBILE = "com.example.hoag.land2door.MOBILE";
    public static final String EXTRA_EMAIL = "com.example.hoag.land2door.EMAIL";
    public static final String EXTRA_ADDRESS = "com.example.hoag.land2door.ADDRESS";

    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        // Do something in response to button

        // Creating Bundle object
       // Bundle b = new Bundle();

        // Storing data into bundle
        //b.putString("farm", fullname);
        //b.putLong("phoneNumber", phone);
        //b.putDouble("age", ageDouble);
        //b.putBoolean("married", isMarried);




        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editFarm = (EditText) findViewById(R.id.editFarmName);
        String msgFarm = editFarm.getText().toString();
        EditText editAbout = (EditText) findViewById(R.id.editAbout);
        String msgAbout = editAbout.getText().toString();
        EditText editName = (EditText) findViewById(R.id.editFarmerName);
        String msgName = editName.getText().toString();
        EditText editPhone = (EditText) findViewById(R.id.editPhone);
        String msgPhone = editPhone.getText().toString();
        EditText editMobile = (EditText) findViewById(R.id.editMobile);
        String msgMobile = editMobile.getText().toString();
        EditText editEmail = (EditText) findViewById(R.id.editEmail);
        String msgEmail = editEmail.getText().toString();
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        String msgAddress = editAddress.getText().toString();
        intent.putExtra(EXTRA_FARM, msgFarm);
        intent.putExtra(EXTRA_ABOUT, msgAbout);
        intent.putExtra(EXTRA_NAME, msgName);
        intent.putExtra(EXTRA_PHONE, msgPhone);
        intent.putExtra(EXTRA_MOBILE, msgMobile);
        intent.putExtra(EXTRA_EMAIL, msgEmail);
        intent.putExtra(EXTRA_ADDRESS, msgAddress);
        startActivity(intent);

        Intent intentImg = new Intent();
        // Show only images, no videos or anything else
        intentImg.setType("image/*");
        intentImg.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intentImg, "Select Picture"), PICK_IMAGE_REQUEST);


    }
}
