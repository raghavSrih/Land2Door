package com.example.ragha.openfarm;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ragha on 1/27/2018.
 */

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> strMain;
    private final ArrayList<String> strSub;
    private final ArrayList<Bitmap> image;

    public CustomListAdapter(Activity context, ArrayList<String> strMain, ArrayList<String> strSub, ArrayList<Bitmap> image) {
        super(context, R.layout.listview, strMain);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.strMain=strMain;
        this.strSub=strSub;
        this.image=image;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(strMain.get(position));
        imageView.setImageBitmap(image.get(position));
        extratxt.setText("Description "+strSub.get(position));
        return rowView;

    };
}