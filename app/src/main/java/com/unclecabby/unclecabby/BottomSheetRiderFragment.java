package com.unclecabby.unclecabby;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.unclecabby.unclecabby.Common.Common;
import com.unclecabby.unclecabby.Remote.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BottomSheetRiderFragment extends BottomSheetDialogFragment{
    String mLocation,mDestination;

    boolean isTapOnMap;

    IGoogleAPI mService;
    TextView txtCalculate,txtLocation,txtDestination;

    public static BottomSheetDialogFragment newInstance(String location,String destination, boolean isTapOnMap)
    {
        BottomSheetDialogFragment f = new BottomSheetRiderFragment();
        Bundle args = new Bundle();
        args.putString("location",location);
        args.putString("destination",destination);
        args.putBoolean("isTapOnMap",isTapOnMap);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = getArguments().getString("location");
        mDestination= getArguments().getString("destination");
        isTapOnMap= getArguments().getBoolean("isTapOnMap");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_rider,container,false);
        txtLocation=(TextView)view.findViewById(R.id.txtLocation);
        txtDestination=(TextView)view.findViewById(R.id.txtDestination);
        txtCalculate=(TextView)view.findViewById(R.id.txtCalculate);

        mService= Common.getGoogleServices();
        getPrice(mLocation,mDestination);
        if(isTapOnMap) {
            //
            txtLocation.setText(mLocation);
            txtDestination.setText(mDestination);
        }
            return view;

    }

    private void getPrice(String mLocation, String mDestination) {
        String requestUrl= null;
        try{
            requestUrl ="https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"
                    +"origin="+mLocation+"&"
                    +"destination="+mDestination+"&"
                    +"key="+getResources().getString(R.string.google_browser_api);
            Log.e("LINK",requestUrl);
            mService.getPath(requestUrl).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    //get object
                    try{
                        JSONObject jsonObject=new JSONObject(response.body().toString());
                        JSONArray routes=jsonObject.getJSONArray("routes");

                        JSONObject object=routes.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");

                        JSONObject legsObject=legs.getJSONObject(0);

                        //distance
                        JSONObject distance = legsObject.getJSONObject("distance") ;
                        String distance_text= distance.getString("text");

                        Double distance_value=Double.parseDouble(distance_text.replaceAll("[^0-9\\\\.]+",""));

                        //get time
                        JSONObject time=legsObject.getJSONObject("duration");
                        String time_txt= time.getString("text");
                        Integer time_value = Integer.parseInt(time_txt.replaceAll("\\D",""));

                        String final_calculate = String.format("%s + %s = ₹%.2f",distance_text,time_txt,
                                                                Common.getPrice(distance_value,time_value));
                        txtCalculate.setText(final_calculate);

                        if(isTapOnMap)
                        {
                            String start_address = legsObject.getString("start_address");
                            String end_address = legsObject.getString("end_address");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("ERROR",t.getMessage());

                }
            });
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
