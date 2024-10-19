package tc.tlouro_c.stock_exchange_simulator.stocks;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.URI;

import org.json.JSONObject;

public class StockInfoProvider {

	private static StockInfoProvider instance;
	private String api_key;
	private String base_url;

	private StockInfoProvider() {
		Properties properties = new Properties();
        try {
            var fis = new FileInputStream("../.env");
            properties.load(fis);
            api_key = properties.getProperty("alpha-vantage-api-key");
        } catch (IOException e) {
            e.printStackTrace();
        }
		base_url = "https://www.alphavantage.co/query?";
	}

	public static StockInfoProvider getInstance() {
		if (instance == null) {
			synchronized(StockInfoProvider.class) {
				if (instance == null) {
					instance = new StockInfoProvider();
				}
			}
		}
		return instance;
	}

	public double fetchLiveStockPrice(String stockSymbol) {
		double price = -1.0;
		var urlString = String.format(base_url
			+ "function=TIME_SERIES_INTRADAY&symbol=%s&interval=1min&apikey=%s", 
			stockSymbol, api_key);
		
		try {
			HttpRequest request = HttpRequest.newBuilder().uri(new URI(urlString)).build();
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			var bodyObj = new JSONObject(response.body()).get("Time Series (1min)");
			var timeSeriesObj = new JSONObject(bodyObj.toString());
			var latestRecordKey = timeSeriesObj.keys().next();
			var latestRecordObj = new JSONObject(timeSeriesObj.get(latestRecordKey).toString());
			price = Double.parseDouble(latestRecordObj.getString("4. close"));			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return price;
	}

}
