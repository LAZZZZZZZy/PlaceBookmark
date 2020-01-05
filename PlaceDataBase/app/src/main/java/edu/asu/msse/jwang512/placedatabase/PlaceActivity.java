package edu.asu.msse.jwang512.placedatabase;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Arrays;

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

public class PlaceActivity extends AppCompatActivity implements DialogInterface.OnClickListener, AdapterView.OnItemSelectedListener{
    private EditText name,title,category,description,address,longitude,latitude,elevation;
    private TextView distance;
    private Spinner placeSpinner;
    private Button distanceBtn,addbtn;
    private String toPlace;
    private boolean isAdd;
    private PlaceDescription pd;
    private String[] placeNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        name = (EditText)findViewById(R.id.name);
        title = (EditText)findViewById(R.id.title);
        category = (EditText)findViewById(R.id.category);
        description = (EditText)findViewById(R.id.description);
        address = (EditText)findViewById(R.id.address);
        longitude = (EditText)findViewById(R.id.longitude);
        latitude = (EditText)findViewById(R.id.latitude);
        elevation = (EditText)findViewById(R.id.elevation);
        //set keyboard
        description.setHorizontallyScrolling(false);
        description.setMaxLines(Integer.MAX_VALUE);
        address.setHorizontallyScrolling(false);
        address.setMaxLines(Integer.MAX_VALUE);
        placeSpinner = (Spinner) findViewById(R.id.placeSpinner);
        distanceBtn = (Button)findViewById(R.id.distancebtn);
        addbtn = (Button)findViewById(R.id.Addbtn);
        distance = (TextView)findViewById(R.id.distance);
        distance.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        isAdd = false;
        android.util.Log.d(this.getClass().getSimpleName(),"CreatePlaceActivity");

        Intent intent = getIntent();
        // add new place
        if( intent.getBooleanExtra("new_place",false) != false){
            placeSpinner.setVisibility(View.INVISIBLE);
            distanceBtn.setVisibility(View.INVISIBLE);
            distance.setVisibility(View.INVISIBLE);
            addbtn.setVisibility(View.VISIBLE);
            isAdd = true;
            name.setEnabled(true);
        }
        // else normally show the place description information
        else {
            pd  = intent.getSerializableExtra("places") != null ? (PlaceDescription) intent.getSerializableExtra("places") : null;
            placeNames  = intent.getStringArrayExtra("placesnames");
            addbtn.setVisibility(View.INVISIBLE);
            name.setEnabled(false);
            name.setText(pd.getName());
            setContent();

            Arrays.sort(placeNames);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, placeNames);
            placeSpinner.setAdapter(adapter);
            placeSpinner.setOnItemSelectedListener(this);
            setTitle(pd.getName());
        }

        try {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception ex){
            android.util.Log.d(this.getClass().getSimpleName(),"exception action bar: "+ex.getLocalizedMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(isAdd)
            return true;
        android.util.Log.d(this.getClass().getSimpleName(), "called onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.place_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_remove:
                removePlace();
                return true;
            case R.id.action_edit:
                editPlace();
                return true;
            case R.id.action_map:
                showMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent();
        this.setResult(RESULT_OK, intent);
        finish();
        return true;
    }

    // add new place to the place library
    // name, latitude, longitude and elevation can be empty
    public void addClicked(View view){
        if(!TextUtils.isEmpty(name.getText())&&!TextUtils.isEmpty(latitude.getText())&&!TextUtils.isEmpty(longitude.getText())&&!TextUtils.isEmpty(elevation.getText())){
            android.util.Log.d("addNewPlace","add");
            addDBPlace();
            Intent intent = new Intent();
            this.setResult(RESULT_OK, intent);
            finish();
        } else {
            android.util.Log.d("addNewPlace","cancel");
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Elevation,Longitude,Latitude and Place Name Can not be Empty!");
            dialog.setNegativeButton("OK", this);
            dialog.show();
        }
    }

    //remove alert
    public void removePlace(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Remove this Place?");
        dialog.setNegativeButton("Cancel", this);
        dialog.setPositiveButton("Remove", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeDBPlace();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        } );
        dialog.show();
    }

    public void showMap()
    {
        android.util.Log.d("click map","click");
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("Longitude",pd.longitude);
        intent.putExtra("Latitude",pd.latitude);
        intent.putExtra("Name",pd.name);
        startActivity(intent);
    }

    public void removeDBPlace(){
        try{
            PlaceDB db = new PlaceDB((Context)this);
            SQLiteDatabase crsDB = db.openDB();
            crsDB.execSQL("delete from place where place.name=?;",new String[]{pd.getName()});
            crsDB.close();
            db.close();
        }catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"Exception adding place: "+
                    ex.getMessage());
        }
    }

    public void editDBPlace(){
        try{
            PlaceDB db = new PlaceDB((Context)this);
            SQLiteDatabase crsDB = db.openDB();
            android.util.Log.w(this.getClass().getSimpleName(),"update" + pd.getName());
            crsDB.update("place",getDBContent(),"name=?",new String[]{pd.getName()});
            android.util.Log.w(this.getClass().getSimpleName(),"updated");
            crsDB.close();
            db.close();
        }catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"Exception adding place: "+
                    ex.getMessage());
        }
    }

    public void addDBPlace(){
        try{
            PlaceDB db = new PlaceDB((Context)this);
            SQLiteDatabase crsDB = db.openDB();
            crsDB.insert("place",null,getDBContent());
            crsDB.close();
            db.close();
        }catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"Exception adding place: "+
                    ex.getMessage());
        }
    }
    //edit alert
    public void editPlace(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Save the Change?");
        dialog.setNegativeButton("Cancel", this);
        dialog.setPositiveButton("Save",  this);
        dialog.show();
    }

    // DialogInterface.OnClickListener method. Get the result of the Alert View.
    @Override
    public void onClick(DialogInterface dialog, int which) {
        android.util.Log.d(this.getClass().getSimpleName(), "onClick positive button? " +
                (which == DialogInterface.BUTTON_POSITIVE));

        if (which == DialogInterface.BUTTON_POSITIVE) {
            editDBPlace();
            Intent intent = new Intent();
            this.setResult(RESULT_OK, intent);
            finish();
        }
    }

    // set the components in the activity_place
    public void setContent(){
        title.setText(pd.getTitle());
        category.setText(pd.getCategory());
        description.setText(pd.getDescription());
        longitude.setText(pd.getLongitude());
        latitude.setText(pd.getLatitude());
        elevation.setText(pd.getElevation());
        address.setText(pd.getStreet());
    }

    //get the placeDescription instance from component information
    public PlaceDescription getContent(){
        PlaceDescription newPd = new PlaceDescription();
        newPd.setName(name.getText().toString());
        newPd.setTitle(title.getText().toString());
        newPd.setDescription(description.getText().toString());
        newPd.setCategory(category.getText().toString());
        newPd.setStreet(address.getText().toString());
        newPd.setElevation(elevation.getText().toString());
        newPd.setLatitude(latitude.getText().toString());
        newPd.setLongitude(longitude.getText().toString());

        return newPd;
    }

    public ContentValues getDBContent(){
        ContentValues newPd = new ContentValues();
        newPd.put("name",name.getText().toString());
        newPd.put("addressTitle",title.getText().toString());
        newPd.put("description",description.getText().toString());
        newPd.put("category",category.getText().toString());
        newPd.put("addressStreet",address.getText().toString());
        newPd.put("elevation",elevation.getText().toString());
        newPd.put("latitude",latitude.getText().toString());
        newPd.put("longitude",longitude.getText().toString());

        return newPd;
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

    public void locatePlace(View view)
    {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("Locate", true);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1&&  resultCode == 1)
        {
            elevation.setText(String.valueOf(data.getDoubleExtra("Elevation",0)));
            longitude.setText(String.valueOf(data.getDoubleExtra("Longitude",0)));
            latitude.setText(String.valueOf(data.getDoubleExtra("Latitude",0)));
        }
    }

    // calculate distance and bearing
    public void calDistance(View view){
        PlaceDescription toP = getPlaceDescription(toPlace);
        double R = 6371e3;
        double fai1 = Math.toRadians(Double.parseDouble(latitude.getText().toString()));
        double fai2 = Math.toRadians(Double.parseDouble(toP.getLatitude()));
        double lambda1 = Math.toRadians(Double.parseDouble(longitude.getText().toString()));
        double lambda2 = Math.toRadians(Double.parseDouble(toP.getLongitude()));
        double diffFai = Math.toRadians(Double.parseDouble(toP.getLatitude()) - Double.parseDouble(latitude.getText().toString()));
        double diffLambda = Math.toRadians(Double.parseDouble(toP.getLongitude()) - Double.parseDouble(longitude.getText().toString()));

        double a = Math.sin(diffFai/2) * Math.sin(diffFai/2) + Math.cos(fai1) * Math.cos(fai2) * Math.sin(diffLambda/2) *  Math.sin(diffLambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d =  R * c / 1000;
        java.text.DecimalFormat myformat =new java.text.DecimalFormat("0.0");
        String dis = myformat.format(d);

        double y = Math.sin(lambda2-lambda1)*Math.cos(fai2);
        double x = Math.cos(fai1) * Math.sin(fai2) -
                Math.sin(fai1) * Math.cos(fai2) * Math.cos(lambda2-lambda1);
        double brng = Math.toDegrees(Math.atan2(y, x));

        android.util.Log.d("calDistance",String.valueOf(brng));
        if(brng<0){
            brng = 360+brng;
        }
        int degree = (int)Math.floor(brng);
        double temp = getdPoint(brng)*60;
        int min = (int)Math.floor(temp);
        android.util.Log.d("calDistanceSec",String.valueOf(temp));
        int sec = (int)Math.floor(getdPoint(temp)*60);


        distance.setText("Great Circle Distance: " + dis + " km"+"\n"+
                "Initial bearing: " + degree+ " °" + min + " ′" + sec+" ″");
    }

    // compute difference for minute and second
    private static double getdPoint(double num){
        double d = num;
        int fInt = (int) d;
        BigDecimal b1 = new BigDecimal(Double.toString(d));
        BigDecimal b2 = new BigDecimal(Integer.toString(fInt));
        double dPoint = b1.subtract(b2).floatValue();
        return dPoint;
    }

    // AdapterView.OnItemSelectedListener method. Called when spinner selection Changes
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        toPlace = placeSpinner.getSelectedItem().toString();
        android.util.Log.d(this.getClass().getSimpleName(),"Spinner item "+
                placeSpinner.getSelectedItem().toString() + " selected.");
    }

    // AdapterView.OnItemSelectedListener method. Called when spinner selection Changes
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        android.util.Log.d(this.getClass().getSimpleName(),"In onNothingSelected: No item selected");
    }
}
