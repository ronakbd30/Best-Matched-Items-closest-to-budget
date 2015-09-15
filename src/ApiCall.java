import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONArray;  
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;  
import org.json.simple.parser.ParseException;

public class ApiCall {

	static int count;
	static String url;
	static ProductDetails []details;
	static String limit="20";
	static double max=0.0;
	static double totalBudget;
	static int productCount;
	static ArrayList<ProductDetails[]> alist=new ArrayList<ProductDetails[]>();
	static ArrayList<Double>sumList=new ArrayList<Double>();
	static ProductDetails[]bestSet;
	static ProductDetails []temp;
	static double min;
	public static void main(String[] args) throws Exception, IOException, ParseException {
	String product;
	String key="52ddafbe3ee659bad97fcce7c53592916a6bfd73";//user key
		
	BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		
		System.out.print("enter one of the product to search(keyword) : 'boots' or 'bags' or 'clothes' ");//asks user for a keyword to search
		product=br.readLine();
		System.out.println();
		
		//the  url to hit zappos api
		url="http://api.zappos.com/Search?term="+product+"&limit="+limit+"&excludes=[\"brandName\",\"colorId\",\"productUrl\",\"thumbnailImageUrl\",\"percentOff\",\"originalPrice\"]&key=";
		
		getJasonObject(url,key);// gets json object from restai and converts it into ProductDetails object. 
		
		System.out.println("The DATABASE : ");
		
		
		for(int i=0;i<Integer.parseInt(limit);i++){
			System.out.println(Double.parseDouble(details[i].getPrice())+"-----"+details[i].getProductId()+"-----"+details[i].getProductName()+"-----"+details[i].getStyleId());
		}
		
		
		System.out.println();
		try {
			min=Double.parseDouble(details[0].getPrice());
		} catch (Exception e) {
			
			
		}
		try {
			for(int i=0;i<details.length;i++){
				if(min>Double.parseDouble(details[i].getPrice())){
					min=Double.parseDouble(details[i].getPrice());
				}
			}
		} catch (Exception e) {
			
		}
		
		System.out.println("The total no. of available products is 20");
		
		do{//The no of product required by the user should be less than the total count
			System.out.println();
			System.out.print("Enter no. of products you want to buy :  ");
			productCount=Integer.parseInt(br.readLine());
			System.out.println();
			if(productCount>Integer.parseInt(limit)||productCount<=0){
				System.out.println("no. of products should be less than "+limit+" and greater than 0");
			}
		}while(productCount>Integer.parseInt(limit)||productCount<=0);
			
			
		do{//The no of product required by the user should be less than the total count
			System.out.print("Enter your budget for above products :  ");
			totalBudget=Double.parseDouble(br.readLine());
			System.out.println();
			if(totalBudget<=0){
				System.out.println("Please enter budget greater than zero");
			}
		}while(totalBudget<=0);
			
			
		
		
		temp=new ProductDetails[productCount];
		
		closestMatch(productCount,totalBudget,Integer.parseInt(limit),0);
		printClosestMatches();
		
	}

	/*gets the  json object and converts it into productDetail class object*/
	public static void getJasonObject(String apiUrl,String apiKey) throws ParseException{
		  try {
			  	details=new ProductDetails[Integer.parseInt(limit)];
  			  	String finalUrl=apiUrl+apiKey;
				URL url = new URL(finalUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
		 
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
		 
				BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
				JSONParser parser = new JSONParser();
				
				String output;
				output = br.readLine();
				Object obj=parser.parse(output);
				JSONObject jsonObject1 = (JSONObject) obj;
				JSONArray jarray = (JSONArray) jsonObject1.get("results");				
				for(int i=0;i<jarray.size();i++){
					JSONObject jsonObject2=(JSONObject)jarray.get(i);
					//System.out.println((String)jsonObject2.get("price"));
					
					String formatedPrice=formatPrice((String)jsonObject2.get("price"));
					details[i]=new ProductDetails();
					
					details[i].setPrice(formatedPrice);
					details[i].setProductId((String)jsonObject2.get("productId"));
					details[i].setProductName((String)jsonObject2.get("productName"));
					details[i].setStyleId((String)jsonObject2.get("styleId"));
					
				}
				
		 
				conn.disconnect();
		 
			  } catch (MalformedURLException e) {
		 
				e.printStackTrace();
		 
			  } catch (IOException e) {
		 
				e.printStackTrace();
			  }

	}
	
	//removes the Dollar sign that is appended to the price.
	static String formatPrice(String price){
		String returnValue;
		returnValue=price.substring(1);
		
		return returnValue;
		
		
	}
	
	//finds the closest match depending upon no. of products to be purchased and the budget.
	static void closestMatch(int no,double tBudget,int totalItems,int i){
		
		if(no>totalItems){
			return;
		}
		
		if(tBudget<0){
			
			return;
		}
		if(tBudget>=0.0 && no==0){
			
			bestSet=new ProductDetails[productCount];
			
			for(int j=0;j<bestSet.length;j++){
				bestSet[j]=new ProductDetails();
				bestSet[j].setPrice(temp[j].getPrice());
				bestSet[j].setProductId(temp[j].getProductId());
				bestSet[j].setProductName(temp[j].getProductName());
				bestSet[j].setStyleId(temp[j].getStyleId());
			}
			
			if(max<totalBudget-tBudget){
				max=totalBudget-tBudget;
				sumList.clear();
				alist.clear();
				sumList.add(max);
				alist.add(bestSet);
			}
			
			else if(max==totalBudget-tBudget){
				sumList.add(max);
				alist.add(bestSet);
			}
			
			return;
		}
		if(tBudget-Double.parseDouble(details[totalItems-1].getPrice())>=0){
			temp[i]=details[totalItems-1];
			
		}
		closestMatch(no-1, tBudget-Double.parseDouble(details[totalItems-1].getPrice()), totalItems-1, i+1);
		closestMatch(no, tBudget, totalItems-1, i);
		return;
		
		
	}
	
	public static void printClosestMatches(){
		System.out.println();
		if(totalBudget<min){
			System.out.println("sorry your budget is too less to buyany product");
		}
		System.out.println("The closest price for given budget is:"+max);
		
		System.out.println();
		System.out.println("Closest Sets for budget="+totalBudget+"  and no. of products="+productCount+" is: "+alist.size()+" possible sets");
		System.out.println();
		System.out.println();
		
		for(int i=0;i<alist.size();i++){
			ProductDetails[]p=alist.get(i);
			for(int j=0;j<p.length;j++){
				System.out.println("Price-->"+p[j].getPrice()+", ProductID-->"+p[j].getProductId()+", StyleID-->"+p[j].getStyleId()+", Name-->"+p[j].getProductName());
			}
			System.out.println();
		}
	}

}
	


 
