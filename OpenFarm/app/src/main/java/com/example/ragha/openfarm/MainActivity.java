package com.example.ragha.openfarm;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String shoppingList = "shoppingList.json";
    private String suggestionList = "suggestionlist.json";
    private ListView shoppingListView;
    private JSONArray jsonShoppingList = new JSONArray();
    private EditText editText;
    private ListView suggestionListView;
    private LinearLayout suggestionLayout;
    private LinearLayout listLayout;
    private JSONArray SuggestionListArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        shoppingListView = findViewById(R.id.shoppingList);
        suggestionListView = findViewById(R.id.suggestionList);
        editText = findViewById(R.id.editText);
        suggestionLayout = findViewById(R.id.suggestionLayout);
        listLayout = findViewById(R.id.listLayout);

        boolean isFilePresent = isFilePresent(this, shoppingList);
        if(isFilePresent) {
            String jsonString = read(this, shoppingList);
            try {
                if(jsonString != "") {
                    jsonShoppingList = new JSONArray((jsonString));
                    jsonShoppingList = new JSONArray();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        populateSuggestionList();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0){
                    listLayout.setVisibility(View.GONE);
                    suggestionLayout.setVisibility(View.VISIBLE);
                    shoppingListView.setVisibility(View.GONE);
                    populateSuggestionList();

                }
                else {
                    suggestionLayout.setVisibility(View.GONE);
                    listLayout.setVisibility(View.VISIBLE);
                    shoppingListView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        listLayout.setVisibility(View.VISIBLE);
        suggestionLayout.setVisibility(View.GONE);


        suggestionLayout.setClickable(true);
        suggestionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                suggestionLayout.setVisibility(View.GONE);
                listLayout.setVisibility(View.VISIBLE);
                shoppingListView.setVisibility(View.VISIBLE);
                String mainStr = ((TextView)view.findViewById(R.id.item)).getText().toString();
                jsonShoppingList.put(mainStr);
                editText.setText("");
                updateShoppingList();
            }
        });

        updateShoppingList();
        shoppingListView.invalidateViews();

        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        setTitle("Shopping List");
    }

    private void updateShoppingList(){
        try {
            final ArrayList<String> strMain = new ArrayList<String>();
            final ArrayList<String> strSub = new ArrayList<String>();
            final ArrayList<Bitmap> bitmap = new ArrayList<Bitmap>();
            for (int i = 0; i < jsonShoppingList.length(); i++) {
                for(int j=0;j<SuggestionListArray.length();j++){
                    if(jsonShoppingList.get(i).toString().compareToIgnoreCase(SuggestionListArray.getJSONObject(j).getString("name")) == 0){
                        final JSONObject jsonObj = SuggestionListArray.getJSONObject(j);
                        final Context context = this;
                        Picasso.with(this).load(jsonObj.getString("imageurl")).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap1, Picasso.LoadedFrom from) {
                                try {
                                    strMain.add(jsonObj.getString("name"));
                                    strSub.add(jsonObj.getString("name"));
                                    bitmap.add(bitmap1);
                                    CustomListAdapter adapter=new CustomListAdapter((Activity)context, strMain, strSub, bitmap);
                                    shoppingListView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    shoppingListView.invalidateViews();
                                    ((BaseAdapter) shoppingListView.getAdapter()).notifyDataSetChanged();
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
                }
                write(this, shoppingList, jsonShoppingList.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open(suggestionList);
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

    private void populateSuggestionList(){
        try {
            final ArrayList<String> strMain = new ArrayList<String>();
            final ArrayList<String> strSub = new ArrayList<String>();
            final ArrayList<Bitmap> bitmap = new ArrayList<Bitmap>();
            String temp = loadJSONFromAsset();
            SuggestionListArray = new JSONArray(temp);
            for (int i = 0; i < SuggestionListArray.length(); i++) {
                final JSONObject jsonObj = SuggestionListArray.getJSONObject(i);
                final Context context = this;
                Picasso.with(this).load(jsonObj.getString("imageurl")).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap1, Picasso.LoadedFrom from) {
                        try {
                            strMain.add(jsonObj.getString("name"));
                            strSub.add(jsonObj.getString("name"));
                            bitmap.add(bitmap1);
                            CustomListAdapter adapter=new CustomListAdapter((Activity)context, strMain, strSub, bitmap);
                            suggestionListView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            suggestionListView.invalidateViews();
                            ((BaseAdapter) suggestionListView.getAdapter()).notifyDataSetChanged();
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String read(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    private boolean write(Context context, String fileName, String jsonString){
        try {
            FileOutputStream fos = openFileOutput(fileName,Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }

    public boolean isFilePresent(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }
}
