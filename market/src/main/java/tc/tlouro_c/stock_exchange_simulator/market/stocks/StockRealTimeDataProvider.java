package tc.tlouro_c.stock_exchange_simulator.market.stocks;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.URI;

import org.json.JSONObject;
import tc.tlouro_c.utils.Logger;

public class StockRealTimeDataProvider {

	private static StockRealTimeDataProvider instance;
	private String api_key;

	private StockRealTimeDataProvider() {
		Properties properties = new Properties();
        try {
            var fis = new FileInputStream("./.env");
            properties.load(fis);
            api_key = properties.getProperty("api-key");
        } catch (IOException e) {
			Logger.WARNING("API Key currently unavailable. Fictional prices will be used for the stock prices.");
        }
	}

	public static StockRealTimeDataProvider getInstance() {
		if (instance == null) {
			synchronized(StockRealTimeDataProvider.class) {
				if (instance == null) {
					instance = new StockRealTimeDataProvider();
				}
			}
		}
		return instance;
	}

	public double fetchLiveStockPrice(String stockSymbol) {
		if (api_key == null) {
			double randomPrice = 50 + (Math.random() * (300 - 50));
			return Math.round(randomPrice * 100) / 100.00;
		}
		double price = -1.0;
		String polygonUrl = "https://api.polygon.io/v2/aggs/ticker/" + stockSymbol + "/prev?apiKey=" + api_key;
		
		try {
			HttpRequest request = HttpRequest.newBuilder().uri(new URI(polygonUrl)).build();
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			httpClient.close();
			var jsonResponse = new JSONObject(response.body());
			var results = jsonResponse.getJSONArray("results").getJSONObject(0);
			price = results.getDouble("c");

		} catch (Exception e) {
			System.err.println(e.getMessage() + ": API currently unavailable "
				+ "(max 5 requests per minute or invalid key), returning dummy value");
			double randomPrice = 50 + (Math.random() * (300 - 50));
			return Math.round(randomPrice * 100) / 100.00;
		}
		return price;
	}

}
