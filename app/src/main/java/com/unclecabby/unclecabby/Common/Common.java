package com.unclecabby.unclecabby.Common;

import com.unclecabby.unclecabby.Remote.FCMClient;
import com.unclecabby.unclecabby.Remote.GoogleMapsAPI;
import com.unclecabby.unclecabby.Remote.IFCMService;
import com.unclecabby.unclecabby.Remote.IGoogleAPI;

public class Common {

    public static boolean isDriverFound=false;
    public static String driverId="";

    public static final  String driver_tbl = "Drivers";
    public static final  String user_driver_tbl = "DriversInformation";
    public static final  String user_rider_tbl = "RidersInformation";
    public static final  String pickup_request_tbl = "PickupRequest";
    public static final  String token_tbl = "Tokens";
    public static final  String rating_detail_tbl = "DriverRatings";

    public  static final String user_field="rider_usr";
    public static final String pwd_field="rider_pwd";

    public static final String fcmURL = "https://fcm.googleapis.com/";
    public static final String googleAPIUrl = "https://maps.googleapis.com/";

    private  static double base_fare=0;
    private static double time_rate=1.2;
    private static  double distance_rate=8.5;

    public  static double getPrice(double km, int min)
    {
        km*=2;
        if(km<100)
        {
            distance_rate=14;
            km/=2;
        }
        else if(km>=50&&km<150) {
            km -= 40;
            if(km<0)
            {
                km=0;
            }
            base_fare = 170 * 4;
        }
        else if(km>=150)
        {
            distance_rate=8.5;
        }

        return  (base_fare+(time_rate*min*0)+(distance_rate*km));
    }

    public  static IFCMService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

    public  static IGoogleAPI getGoogleServices()
    {
        return GoogleMapsAPI.getClient(googleAPIUrl).create(IGoogleAPI.class);
    }
}
