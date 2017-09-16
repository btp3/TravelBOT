import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.gson.Gson;

import models.LocationModel;
import retrofit.APIServices;
import retrofit.APIUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class BotMain {
	private APIServices mAPIService;
	private static BotMain bot = new BotMain();
	private ArrayList<LocationModel> locationArray;	
	
	public static void main(String args[]) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter Loc Type (user/transport) : ");
		String userType = br.readLine();
		System.out.println("Enter ID : ");
		String id = br.readLine();
		System.out.println("Select a route :  ");
		System.out.println("   1. College To GKM  ");
		System.out.println("   2. College To Hazira  ");
		System.out.println("   3. BH1 To GKM  ");
		System.out.println("   4. BH1 To GKM 2  ");
		String selection = br.readLine();
		
		switch(selection){
			case "1": 
				bot.readGPXFile("CollegeToGKM.json",userType,id);
				break;
			case "2": 
				bot.readGPXFile("CollegeToHazira.json",userType,id);
				break;
			case "3": 
				bot.readGPXFile("BhToGKMJSON.json",userType,id);
				break;
			case "4": 
				bot.readGPXFile("BHToGKM.json",userType,id);
				break;
			default:
				bot.readGPXFile("test.json",userType,id);
				break;
		}
		
		for(LocationModel l : bot.locationArray){
			System.out.println(l.toString());
			try{
				  Thread.sleep(3000);
				  bot.sendLocation(l);
				}catch(InterruptedException ex){
				  ex.printStackTrace();
				}
		}
	}

	 public void sendLocation(LocationModel locationModel){
		    mAPIService= APIUtil.getAPIService();
	        Gson g = new Gson();
	        System.out.println("In sendLocation : "+ g.toJson(locationModel));
	        mAPIService.savePost(locationModel).enqueue(new Callback<LocationModel>() {
	            @Override
	            public void onResponse(Call<LocationModel> call, Response<LocationModel> response) {
	                
	                if(response.isSuccessful()) {
	                	System.out.println("In response : "+ response.toString());
	                }
	            }

	            @Override
	            public void onFailure(Call<LocationModel> call, Throwable t) {
	            	System.out.println("SendLocation : "+ "Unable to submit post to API.");
	            }
	        });
	    }
	 
	 
	 public void readGPXFile(String fileName , String loc_type, String id){
		
		 JSONParser parser = new JSONParser();
		 bot.locationArray = new ArrayList<>();
	        try {     
	            Object obj = parser.parse(new FileReader(fileName));
	            
	            JSONObject jsonObject =  (JSONObject) obj;
	            JSONObject Points = (JSONObject) jsonObject.get("Points");
	            
	            // loop array
	            JSONArray rpt = (JSONArray) Points.get("rpt");
	            
	            double speed = 20.0;
	            
	            // Start journey if user
	            if(loc_type.equals("user")){
	            	LocationModel l0 = new LocationModel("-1","-1",String.valueOf(speed),loc_type , Long.parseLong(id));
	            	locationArray.add(l0);
	            }
	            
	            for (Object c : rpt)
	            {
	            	speed += 0.882;
	            	JSONObject coordinates = (JSONObject)c;
	            	String latitude = coordinates.get("-lat")+ " ";
	            	String longitude = coordinates.get("-lon")+ " ";
	            	LocationModel l = new LocationModel(latitude,longitude,String.valueOf(speed),loc_type , Long.parseLong(id));
	            	locationArray.add(l);
	            }
	            
	            // End journey if user
	            if(loc_type.equals("user")){
	            	LocationModel le = new LocationModel("+1","+1",String.valueOf(speed),loc_type , Long.parseLong(id));
	            	locationArray.add(le);
	            }
	            
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	    }
		
	 }

