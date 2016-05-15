package backend;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Backend {
	public Object embark(String phlinkId, String ticketId, int fareId) throws Exception {
		final String KEY = "ign0q-RD2N9-g7TUc-LR6pI";
		String transactionId = UUID.randomUUID().toString();
		String url = "http://localhost:3000/users/" + phlinkId +
			"/transactions/" + transactionId +
			"?type=embark&key=" + KEY;
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("PUT");
		con.setRequestProperty("Content-Type", "application/json");
		
		JSONObject json = new JSONObject();
		
		json.put("phlinkId", phlinkId);
		json.put("ticketId", ticketId);
		json.put("fareId", fareId);
		String body = json.toJSONString();
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(body);
		wr.flush();
		wr.close();
		
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject)parser.parse(response.toString());
		return jsonObj.get("balance");
	}
}
