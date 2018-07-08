package com.example.polygons;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static com.example.polygons.R.id.map;


/**
 * An activity that displays a Google map with polylines to represent paths or routes,
 * and polygons to represent areas.
 */
public class PolyActivity extends AppCompatActivity
        implements
                OnMapReadyCallback,
                GoogleMap.OnPolylineClickListener,
                GoogleMap.OnPolygonClickListener {


    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    public void getData(View view) throws IOException, JSONException {
        Log.d("click btn","success");
        float[] candidate1 = new float[4];
        candidate1[0]=(float)-73.99703;
        candidate1[1]=(float)40.755344;
        candidate1[2]=(float)-73.99151;
        candidate1[3]=(float)40.7501;
        float[] candidate2 = new float[4];
        candidate2[0]=(float)-73.984436;
        candidate2[1]=(float)40.75991;
        candidate2[2]=(float)-73.99505;
        candidate2[3]=(float)40.759365;
        float[] candidate3 = new float[4];
        candidate3[0]=(float)-73.979935;
        candidate3[1]=(float)40.752007;
        candidate3[2]=(float)-73.98887;
        candidate3[3]=(float)40.758034;

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        LatLng orgin = new LatLng(candidate1[1], candidate1[0]);
        LatLng dest = new LatLng(candidate1[3], candidate1[2]);
        String strUrl = getDirectionsUrl(orgin, dest);

        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
//            iStream.close();
            urlConnection.disconnect();
        }
        System.out.println("url:" + strUrl + "---->   downloadurl:" + data);
        Log.d("get data:", data);
        JSONObject jsonObj = new JSONObject(data);
        JSONArray routes = jsonObj.getJSONArray("routes");
        JSONObject legs =  routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);
        JSONArray steps = legs.getJSONArray("steps");


        for (int i = 0; i<steps.length(); i++){
            JSONObject curstep = steps.getJSONObject(i);
            double lat1, lon1, lat2 ,lon2;

                JSONObject start = curstep.getJSONObject("start_location");
                lat1 =  start.getDouble("lat");
                lon1 = start.getDouble("lng");
            JSONObject end = curstep.getJSONObject("end_location");

            lat2 =  end.getDouble("lat");
            lon2 = end.getDouble("lng");

            Log.d("lat1",lat1+"");

        }


    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;



        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + "key=AIzaSyC4bjPuG_mzi0BlzJ3Rs3TrV7te95Say7k&mode=\"DRIVING\"";

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;
        System.out.println("getDerectionsURL--->: " + url);
        return url;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this tutorial, we add polylines and polygons to represent routes and areas on the map.
     */

    int getIColor(int i){
        switch (i % 8){
            case 0:
                return 0xffDC143C;
            case 1:
                return 0xff0000FF;
            case 2:
                return 0xff00BFFF;
            case 3:
                return 0xff00FF00;
            case 4:
                return 0xffFFFF00;
            case 5:
                return 0xffFF0000;
            case 6:
                return 0xff00FFFF;
            default:
                return COLOR_WHITE_ARGB;
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap){




        float[] candidate1 = new float[4];
        candidate1[0]=(float)-73.99703;
        candidate1[1]=(float)40.755344;
        candidate1[2]=(float)-73.99151;
        candidate1[3]=(float)40.7501;
        float[] candidate2 = new float[4];
        candidate2[0]=(float)-73.984436;
        candidate2[1]=(float)40.75991;
        candidate2[2]=(float)-73.99505;
        candidate2[3]=(float)40.759365;
        float[] candidate3 = new float[4];
        candidate3[0]=(float)-73.979935;
        candidate3[1]=(float)40.752007;
        candidate3[2]=(float)-73.98887;
        candidate3[3]=(float)40.758034;

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        LatLng orgin = new LatLng(candidate1[1], candidate1[0]);
        LatLng dest = new LatLng(candidate1[3], candidate1[2]);
        String strUrl = getDirectionsUrl(orgin, dest);

        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
//            iStream.close();
            urlConnection.disconnect();
        }
        System.out.println("url:" + strUrl + "---->   downloadurl:" + data);
        Log.d("get data:", data);
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray routes = null;
        try {
            routes = jsonObj.getJSONArray("routes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject legs = null;
        try {
            legs = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray steps = null;
        try {
            steps = legs.getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LatLng point1=null, point2=null, point3=null, point4=null;
        for (int i = 0; i<steps.length(); i++){
            JSONObject curstep = null;
            try {
                curstep = steps.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            double lat1 = 0, lon1=0, lat2=0 ,lon2 = 0;

            JSONObject start = null;
            try {
                start = curstep.getJSONObject("start_location");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                lat1 =  start.getDouble("lat");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                lon1 = start.getDouble("lng");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject end = null;
            try {
                end = curstep.getJSONObject("end_location");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                lat2 =  end.getDouble("lat");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                lon2 = end.getDouble("lng");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("lat1",lat1+"");
           if(i==0){
               point1 = new LatLng(lat1, lon1);
               point2 = new LatLng(lat2, lon2);
           }else{
               point3 = new LatLng(lat1, lon1);
               point4 = new LatLng(lat2, lon2);
           }

        }
        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(point1.latitude, point1.longitude),
                        new LatLng(point2.latitude, point2.longitude),
                        new LatLng(point3.latitude, point3.longitude),
                        new LatLng(point4.latitude, point4.longitude)
                ));
        polyline.setColor(getIColor(0));
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setJointType(JointType.ROUND);

        LatLng point5=null, point6=null, point7=null, point8=null, point9=null, point10=null, point11=null, point12=null;

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));
        Polyline polyline2 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(40.7477353, -73.9945424),
                        new LatLng( 40.7472392,   -73.9933645),
                        new LatLng(40.7453898, -73.99470939999999),
                        new LatLng(40.7489821, -74.00323969999999),
                        new LatLng(40.7552526, -73.9986612),
                        new LatLng(40.7564474, -74.0015035),
                        new LatLng(40.758057, -74.0003274)
                ));
        polyline2.setColor(getIColor(1));
        polyline2.setStartCap(new RoundCap());
        polyline2.setEndCap(new RoundCap());
        polyline2.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline2.setJointType(JointType.ROUND);
        // Set listeners for click events.
        googleMap.setOnPolylineClickListener(this);
        googleMap.setOnPolygonClickListener(this);
    }

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10));
                polyline.setColor(COLOR_BLACK_ARGB);
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                polyline.setColor(COLOR_WHITE_ARGB);
                break;

        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setJointType(JointType.ROUND);
    }

    /**
     * Styles the polygon, based on type.
     * @param polygon The polygon object that needs styling.
     */
    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }

    /**
     * Listens for clicks on a polyline.
     * @param polyline The polyline object that the user has clicked.
     */
    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Listens for clicks on a polygon.
     * @param polygon The polygon object that the user has clicked.
     */
    @Override
    public void onPolygonClick(Polygon polygon) {
        // Flip the values of the red, green, and blue components of the polygon's color.
        int color = polygon.getStrokeColor() ^ 0x00ffffff;
        polygon.setStrokeColor(color);
        color = polygon.getFillColor() ^ 0x00ffffff;
        polygon.setFillColor(color);

        Toast.makeText(this, "Area type " + polygon.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    public void onClick(View view) throws IOException, JSONException {
        getData(view);
    }
}
