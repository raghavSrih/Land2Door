package com.example.ragha.openfarm;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapActivity.class.getSimpleName();

    private static final float ZOOM_DELTA = 2.0f;
    private static final float DEFAULT_MIN_ZOOM = 2.0f;
    private static final float DEFAULT_MAX_ZOOM = 22.0f;

    private GoogleMap mMap;

    /**
     * Internal min zoom level that can be toggled via the demo.
     */
    private float mMinZoom;

    /**
     * Internal max zoom level that can be toggled via the demo.
     */
    private float mMaxZoom;

    private TextView mCameraTextView;
    private ListView produceList;
    private JSONArray farmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        produceList = findViewById(R.id.produceList);
        mMap = null;
        resetMinMaxZoom();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Open Farms");
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("farmdb.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        enableMyLocation();
        try {
            String temp = loadJSONFromAsset();
            farmList = new JSONArray(temp);
            ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;
            boolean bTemp = true;
            for (int i = 0; i < farmList.length(); i++) {
                final JSONObject jsonObj = farmList.getJSONObject(i);
                if (bTemp) {
                    bTemp = false;
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(jsonObj.getJSONObject("meta").getDouble("latitude"), jsonObj.getJSONObject("meta").getDouble("longitude")), 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target((new LatLng(jsonObj.getJSONObject("meta").getDouble("latitude"), jsonObj.getJSONObject("meta").getDouble("longitude"))))      // Sets the center of the map to location user
                            .zoom(8)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                Picasso.with(this).load("http://f.datalets.ch"+jsonObj.getJSONObject("image_thumb").getString("url")).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try {
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(jsonObj.getJSONObject("meta").getDouble("latitude"), jsonObj.getJSONObject("meta").getDouble("longitude")))
                                    .title(jsonObj.getString("title"))
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Log.d("MapActivity", "onBitmapFailed");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Log.d("MapActivity", "onPrepareLoad");
                    }
                });


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraIdle() {
        //mCameraTextView.setText(mMap.getCameraPosition().toString());
    }

    /**
     * Before the map is ready many calls will fail.
     * This should be called on all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (mMap == null) {
            //Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void toast(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void resetMinMaxZoom() {
        mMinZoom = DEFAULT_MIN_ZOOM;
        mMaxZoom = DEFAULT_MAX_ZOOM;
    }


    public void onLatLngClampReset(View view) {
        if (!checkReady()) {
            return;
        }
        // Setting bounds to null removes any previously set bounds.
        mMap.setLatLngBoundsForCameraTarget(null);
        toast("LatLngBounds clamp reset.");
    }

    public void onSetMinZoomClamp(View view) {
        if (!checkReady()) {
            return;
        }
        mMinZoom += ZOOM_DELTA;
        // Constrains the minimum zoom level.
        mMap.setMinZoomPreference(mMinZoom);
        toast("Min zoom preference set to: " + mMinZoom);
    }

    public void onSetMaxZoomClamp(View view) {
        if (!checkReady()) {
            return;
        }
        mMaxZoom -= ZOOM_DELTA;
        // Constrains the maximum zoom level.
        mMap.setMaxZoomPreference(mMaxZoom);
        toast("Max zoom preference set to: " + mMaxZoom);
    }

    public void onMinMaxZoomClampReset(View view) {
        if (!checkReady()) {
            return;
        }
        resetMinMaxZoom();
        mMap.resetMinMaxZoomPreference();
        toast("Min/Max zoom preferences reset.");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String title = marker.getTitle();
        final ArrayList<String> strMain = new ArrayList<String>();
        final ArrayList<String> strSub = new ArrayList<String>();
        final ArrayList<Bitmap> bitmap = new ArrayList<Bitmap>();
        for(int i=0;i<farmList.length();i++) {
            try {
                if(marker.getTitle().compareToIgnoreCase(farmList.getJSONObject(i).getString("title")) == 0){
                    final JSONArray produceArray = farmList.getJSONObject(i).getJSONArray("produce");
                    for(int j=0;j<produceArray.length();j++) {
                        final int k = j;
                        final Context context = this;
                        Picasso.with(this).load("http://f.datalets.ch"+produceArray.getJSONObject(k).getJSONObject("thumb").getString("url")).into(new Target() {

                            @Override
                            public void onBitmapLoaded(Bitmap bitmap1, Picasso.LoadedFrom from) {
                                try {
                                    strMain.add(produceArray.getJSONObject(k).getString("name"));
                                    strSub.add(produceArray.getJSONObject(k).getString("about"));
                                    bitmap.add(bitmap1);
                                    CustomListAdapter adapter=new CustomListAdapter((Activity)context, strMain, strSub, bitmap);
                                    produceList.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    produceList.invalidateViews();
                                    ((BaseAdapter) produceList.getAdapter()).notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });


                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    private void enableMyLocation() {
        if (mMap != null) {
            // Access to the location has been granted to the app.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }
}
