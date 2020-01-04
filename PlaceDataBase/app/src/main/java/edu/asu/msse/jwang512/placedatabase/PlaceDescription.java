package edu.asu.msse.jwang512.placedatabase;

import org.json.JSONObject;

import java.io.Serializable;

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

public class PlaceDescription implements Serializable {
    public String name;
    public String description;
    public String category;
    public String title;
    public String street;
    public String elevation;
    public String latitude;
    public String longitude;

    public PlaceDescription(){};

    public PlaceDescription(String name,String category,String title, String street, String description,String elevation,String latitude,String longitude){
        this.name = name;
        this.description = description;
        this.category = category;
        this.title = title;
        this.street = street;
        this.elevation = elevation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PlaceDescription(String jsonStr){
        try{
            JSONObject jo = new JSONObject(jsonStr);
            name = jo.getString("name");
            description = jo.getString("description");
            category = jo.getString("category");
            title = jo.getString("address-title");
            street = jo.getString("address-street");
            elevation = jo.getString("elevation");
            latitude = jo.getString("latitude");
            longitude = jo.getString("longitude");

        }catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),
                    "error converting to/from json");
        }
    }

    public String toJsonString(){
        String ret = "";
        try{
            JSONObject jo = new JSONObject();
            jo.put("name",name);
            jo.put("address-title",title);
            jo.put("category",category);
            jo.put("description",description);
            jo.put("address-street",street);
            jo.put("longitude",longitude);
            jo.put("latitude",latitude);
            jo.put("elevation",elevation);

            ret = jo.toString();
        }catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),
                    "error converting to/from json");
        }
        return ret;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDescription() { return description; }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getStreet() {
        return street;
    }

    public String getElevation() {
        return elevation;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getName() {

        return name;
    }


}
