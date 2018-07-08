package hack;
import java.net.*;
import java.io.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;


public class Route {
    private static final String KEY = "AIzaSyC4bjPuG_mzi0BlzJ3Rs3TrV7te95Say7k";
    
    public static void main(String[] args) throws Exception {
    	Route r1 = new Route();
    	r1.getTimeInfo(40.748012542724609,-73.994338989257812,40.758079528808594,-74.000381469726562);
        
    }
	public int getTimeInfo(double lat1, double lon1, double lat2, double lon2) throws IOException {
		PrintWriter out = null;
        BufferedReader in = null;
        JSONObject jsonObject = null;
        String result = "";
        int time = 0;
      
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?";
        String param = "origins=" +lat1+","+lon1 + "&destinations=" + lat2+","+lon2 + "&key="+KEY;
        	// Caution: android studio http request add a line of gradle code ?
        
        URL realUrl = new URL(url+param);
        // 打开和URL之间的连接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // 发送POST请求必须设置如下两行
       // conn.setDoOutput(true);
      //  conn.setDoInput(true);
        // 获取URLConnection对象对应的输出流（设置请求编码为UTF-8）
      //  out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
      //  out.print(param);
     
        // flush输出流的缓冲
     //   out.flush();
        // 获取请求返回数据（设置返回数据编码为UTF-8）
        in = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }
        jsonObject = JSONObject.fromObject(result);
        System.out.println(jsonObject);
        
 
		JSONArray rows = jsonObject.getJSONArray("rows");
		JSONObject row = rows.getJSONObject(0);
		JSONObject elements = row.getJSONObject("elements");
		JSONObject duration = elements.getJSONObject("duration");
		time = duration.getInt("value");
        System.out.println(time); //TODO: DELETE out    
       return time;
	}
/*
       
        try {

            jsonObject = new JSONObject(stringBuilder.toString());

            JSONArray array = jsonObject.getJSONArray("routes");

            JSONObject routes = array.getJSONObject(0);

            JSONArray legs = routes.getJSONArray("legs");

            JSONObject steps = legs.getJSONObject(0);

            JSONObject distance = steps.getJSONObject("distance");

            Log.i("Distance", distance.toString());
            dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]","") );

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
    
}
