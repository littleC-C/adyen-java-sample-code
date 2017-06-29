package com.adyen.examples.recurring.JSON;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Disable recurring contract (JSON)
 * 
 * Disabling a recurring contract (detail) can be done by calling the disable action on the Recurring service with a
 * request. This file shows how you can disable a recurring contract using JSON.
 * 
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 * 
 * @link /5.Recurring/JSON/DisableRecurringContract
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/5.Recurring/JSON/DisableRecurringContract" })
public class DisableRecurringContract extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * JSON settings
		 * - apiUrl: URL of the Adyen API you are using (Test/Live)
		 * - wsUser: your web service user
		 * - wsPassword: your web service user's password
		 */
		String apiUrl = "https://pal-test.adyen.com/pal/servlet/Recurring/v10/disable";
		String wsUser = "YourWSUser";
		String wsPassword = "YourWSPassword";

		/**
		 * Create HTTP Client (using Apache HttpComponents library) and set up Basic Authentication
		 */
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(wsUser, wsPassword);
		provider.setCredentials(AuthScope.ANY, credentials);

		HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

		/**
		 * The recurring details request should contain the following variables:
		 * 
		 * <pre>
		 * - merchantAccount            : Your merchant account.
		 * - shopperReference           : The reference to the shopper. This shopperReference must be the same as the
		 *                                shopperReference used in the initial payment.
		 * - recurringDetailReference   : The recurringDetailReference of the details you wish to disable. If you do
		 *                                not supply this field all details for the shopper will be disabled including
		 *                                the contract! This means that you can not add new details anymore.
		 * </pre>
		 */
		JSONObject recurringRequest = new JSONObject();
		recurringRequest.put("merchantAccount", "YourMerchantAccount");
		recurringRequest.put("shopperReference", "TheShopperReference");
		recurringRequest.put("recurringDetailReference", "TheDetailReferenceOfTheContract");

		/**
		 * Send the HTTP request with the specified variables in JSON.
		 */
		HttpPost httpRequest = new HttpPost(apiUrl);
		httpRequest.addHeader("Content-Type", "application/json");
		httpRequest.setEntity(new StringEntity(recurringRequest.toString(), "UTF-8"));

		HttpResponse httpResponse = client.execute(httpRequest);
		String recurringResponse = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

		/**
		 * Keep in mind that you should handle errors correctly.
		 * If the Adyen platform does not accept or store a submitted request, you will receive a HTTP response with
		 * status different than 200 OK. In this case, the error details are populated in the recurringResponse.
		 */
		
		// Parse JSON response
		JSONParser parser = new JSONParser();
		JSONObject recurringResult;
		
		try {
			recurringResult = (JSONObject) parser.parse(recurringResponse);
		} catch (ParseException e) {
			throw new ServletException(e);
		}
		
		// If the request was rejected, raise an exception
		if (httpResponse.getStatusLine().getStatusCode() != 200) {
			String faultString = recurringResult.get("errorType") + " " + recurringResult.get("errorCode") + " " + recurringResult.get("message");
			throw new ServletException(faultString);
		}

		/**
		 * The response will be a result object with a single field response. If a single detail was disabled the value
		 * of this field will be [detail-successfully-disabled] or, if all details are disabled, the value is
		 * [all-details-successfully-disabled].
		 */
		PrintWriter out = response.getWriter();

		out.println("Disable Recurring Result:");
		out.println("- response: " + recurringResult.get("response"));
	}

}
