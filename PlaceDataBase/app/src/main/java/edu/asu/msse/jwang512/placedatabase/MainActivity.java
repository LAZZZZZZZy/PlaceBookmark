package edu.asu.msse.jwang512.placedatabase;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
    Copyright 2018 Jiayan Wang. created at 4/14/2019

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

I agree and grant the University and the instructor of ASU with the right to build and evaluate the software package for the purpose of determining your grade and program assessment.

@author   Jiayan Wang    mailto:jwang512@asu.edu.
@version Apr 14, 2019
 **/

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener, DialogInterface.OnClickListener{
    private String selectedPlace;
    private ListView placesList;
    private String[] placeNames;
    private String[] colLabels;
    private int[] colIds;
    private List<HashMap<String,String>> fillMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placesList =(ListView)findViewById(R.id.place_list);

        updateNames();
        prepareAdapter();

        SimpleAdapter sa = new SimpleAdapter(this, fillMaps, R.layout.place_list_item, colLabels, colIds);
        placesList.setAdapter(sa);
        placesList.setOnItemClickListener(this);

        setTitle("Places");
    }

    private void updateNames(){
        try {
            PlaceDB db = new PlaceDB(this);
            SQLiteDatabase crsDB = db.openDB();
            Cursor cur = crsDB.rawQuery("select name from place;", new String[]{});
            ArrayList<String> al = new ArrayList<String>();
            while(cur.moveToNext()){
                try{
                    al.add(cur.getString(0));
                }catch(Exception ex){
                    android.util.Log.w(this.getClass().getSimpleName(),"exception stepping thru cursor"+ex.getMessage());
                }
            }
            placeNames = al.toArray(new String[al.size()]);
            Arrays.sort(placeNames);
            cur.close();
            db.close();
        }
        catch(Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"unable to setup Names");
        }
    }

    private PlaceDescription getPlaceDescription(String name){
        PlaceDescription newPlace = null;
        android.util.Log.w(this.getClass().getSimpleName(),"getPlace: " + name );
        try {
            PlaceDB db = new PlaceDB(this);
            SQLiteDatabase crsDB = db.openDB();
            Cursor cur = crsDB.rawQuery("select * from place where name = ?;", new String[]{name});
            while(cur.moveToNext()) {
                try {
                    newPlace = new PlaceDescription(cur.getString(0), cur.getString(1), cur.getString(2),
                            cur.getString(3), cur.getString(4), cur.getString(5), cur.getString(6), cur.getString(7));
                } catch (Exception ex) {
                    android.util.Log.w(this.getClass().getSimpleName(), "exception stepping thru cursor" + ex.getMessage());
                }
            }
            cur.close();
            db.close();
        }
        catch(Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"unable to setup Names");
        }
        android.util.Log.w(this.getClass().getSimpleName(),"newPlace: " + name + "   " + newPlace.getCategory());
        return newPlace;
    }

    private void prepareAdapter(){
        colLabels = this.getResources().getStringArray(R.array.col_header);
        colIds = new int[] {R.id.place_name, R.id.place_category};
        fillMaps = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> titles = new HashMap<>();
        // first row contains column headers
        titles.put("Name","Name");
        titles.put("Category","Category");
        fillMaps.add(titles);
        // fill in the remaining rows with first last and student id.
        for (int i = 0; i < placeNames.length; i++) {
            HashMap<String,String> map = new HashMap<>();
            map.put("Name", placeNames[i]);
            String category = getPlaceDescription(placeNames[i]).getCategory();
            map.put("Category", category);
            Log.d("prepareAdapter",category + "   Name: " + placeNames[i]);
            fillMaps.add(map);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                newPlace();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Arrays.sort(placeNames);
        if(position > 0 && position <= placeNames.length) {
            Intent displayStud = new Intent(this, PlaceActivity.class);
            PlaceDescription newPlace = getPlaceDescription(placeNames[position-1]);
            displayStud.putExtra("places", newPlace);
            displayStud.putExtra("placesnames", placeNames);
            this.startActivityForResult(displayStud, 1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        updateNames();
        prepareAdapter();
        SimpleAdapter sa = new SimpleAdapter(this, fillMaps, R.layout.place_list_item, colLabels, colIds);
        placesList.setAdapter(sa);
        placesList.setOnItemClickListener(this);
    }

    public void newPlace(){
        Intent intent = new Intent(this, PlaceActivity.class);
        intent.putExtra("new_place",true);
        this.startActivityForResult(intent, 1);
    }

    @Override
    public void onClick(DialogInterface dialog, int whichButton) {

    }
}
