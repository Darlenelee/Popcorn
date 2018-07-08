package com.example.vm510l.myapplication2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.*;

public class GirlHackathon {
    private float[] getOn = new float[2];
    private float[] getOff = new float[2];
    private Date date = new Date();
    private int[] time = new int[3];
    private int num;
    private double duration;


    private static final String BIGQUERY_SCOPE = "https://www.googleapis.com/auth/bigquery";
    // Note that we use NetHttpTransport in this standalone app. On App Engine
    // we would use UrlFetchTransport instead.
    private static final HttpTransport TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();;

    // Copied from our project in the Developer Console:
    private static final String PROJECT_ID = "my-project-1530865014396";
    private static final String P12 = "My Project-24b9ef2a0e1e.p12";
    private static final String SERVICE_ACCOUNT_EMAIL = "query-529@my-project-1530865014396.iam.gserviceaccount.com";

    public static GoogleCredential createGoogleCredential() throws IOException,
            GeneralSecurityException, URISyntaxException {

        // Open the certificate file
        URL theCertificate = GirlHackathon.class.getResource(P12);
        File privateKey = new File(theCertificate.toURI());

        return new GoogleCredential.Builder()
                .setTransport(TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                .setServiceAccountScopes(
                        Arrays.asList(new String[] { BIGQUERY_SCOPE }))
                .setServiceAccountPrivateKeyFromP12File(privateKey).build();

    }

    public int getNum(){
        return this.num;
    }

    public float getGetOnLongitude(){
        return this.getOn[0];
    }

    public float getGetOnLatitude(){
        return this.getOn[1];
    }

    public float getGetOffLongitude(){
        return this.getOff[0];
    }

    public float getGetOffLatitude(){
        return this.getOff[1];
    }

    public Date getDate(){
        return this.date;
    }

    public double getDuration(){
        return this.duration;
    }

    public void setNum(int num){
        this.num=num;
    }

    public void setGetOn(float longitude,float latitude) {
        this.getOn[0] = longitude;
        this.getOn[1]=latitude;
    }

    public void setGetOff(float longitude,float latitude) {
        this.getOff[0] = longitude;
        this.getOff[1]=latitude;
    }

    public void setDate(Date date){
        this.date=date;
    }

    public void setTime(int hour,int minutes,int seconds) {
        this.time[0] = hour;
        this.time[1] = minutes;
        this.time[2] = seconds;
    }

    public void setDuration(double duration){
        this.duration=duration;
    }

    public static Vector body(GirlHackathon my) throws IOException,
            GeneralSecurityException, URISyntaxException{
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + my.getGetOnLatitude()+","+my.getGetOnLongitude() + "&destinations=" + my.getGetOffLatitude()+","+my.getGetOffLongitude() + "&mode=driving&language=fr-FR&avoid=tolls&key=YOUR_API_KEY";
        my.setDuration(Math.pow(my.getGetOffLatitude()-my.getGetOnLatitude(),2)+Math.pow(my.getGetOffLongitude()-my.getGetOnLongitude(),2));
        Vector<GirlHackathon> data = new Vector<GirlHackathon>();
        Bigquery bigQuery = new Bigquery.Builder(TRANSPORT, JSON_FACTORY,
                createGoogleCredential()).setApplicationName(PROJECT_ID)
                .build();

        // Let's construct a query
        String sql = "SELECT  * FROM [train_new.candidate] ";
        System.out.println(sql);

        // Create the request
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setQuery(sql);

        // And finally, it is time to send the query to BigQuery.
        // Notice that the Project ID is in the request and it is used by
        // BigQuery to determine where the bill goes for this query.
        QueryResponse queryResponse = bigQuery.jobs()
                .query(PROJECT_ID, queryRequest).execute();

        System.out.println("Results:");
        System.out.print("\n");
        TableSchema schema = queryResponse.getSchema();
        int j = 0;
        for (TableFieldSchema field : schema.getFields()) {
            if (j > 0) {
                System.out.print("\t");
            }
            System.out.print(field.getName());
            j++;
        }
        System.out.print("\n");
        for (TableRow row : queryResponse.getRows()) {
            GirlHackathon passenger = new GirlHackathon();
            passenger.setNum(Integer.parseInt(row.getF().get(6).getV().toString()));
            passenger.setGetOn(Float.parseFloat(row.getF().get(7).getV().toString()),Float.parseFloat(row.getF().get(8).getV().toString()));
            passenger.setGetOff(Float.parseFloat(row.getF().get(9).getV().toString()),Float.parseFloat(row.getF().get(10).getV().toString()));
            int hour = Integer.parseInt(row.getF().get(3).getV().toString().substring(0,1));
            int minute = Integer.parseInt(row.getF().get(3).getV().toString().substring(3,4));
            int second = Integer.parseInt(row.getF().get(3).getV().toString().substring(6,7));
            passenger.setTime(hour,minute,second);
            if (my.testCandidate(passenger))
                data.add(passenger);
        }
        System.out.print("\n");
        return data;
    }

    public boolean testCandidate(GirlHackathon passenger){
        double getOnToGetOn = Math.pow(getGetOnLatitude()-passenger.getGetOnLatitude(),2)+Math.pow(getGetOnLongitude()-passenger.getGetOnLongitude(),2);
        double getOffToGetOff= Math.pow(getGetOffLatitude()-passenger.getGetOffLatitude(),2)+Math.pow(getGetOffLongitude()-passenger.getGetOffLongitude(),2);
        double passenger_duration=Math.pow(passenger.getGetOffLatitude()-passenger.getGetOnLatitude(),2)+Math.pow(passenger.getGetOffLongitude()-passenger.getGetOnLongitude(),2);
        if ((getOnToGetOn+getOffToGetOff+Math.min(getDuration(),passenger_duration))/getDuration()>=0.5)
            return true;
        return false;
    }

 /**   public static void main(String[] args){
        try {
            GirlHackathon my = new GirlHackathon();
            body(my);
        }
        catch(Exception e){
            System.out.print(e);
        }
    }**/
}
