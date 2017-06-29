package com.adyen.examples.modifications.JSON;

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
 * Cancel or Refund a Payment (JSON)
 * 
 * If you do not know if the payment is captured but you want to reverse the authorisation you can send a modification
 * request to the cancelOrRefund action. This file shows how a payment can be cancelled or refunded by a modification
 * request using JSON.
 * 
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 * 
 * @link /4.Modifications/JSON/CancelOrRefundPayment
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet("/4.Modifications/JSON/CancelOrRefundPayment")
public class CancelOrRefundPayment extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * JSON settings
		 * - apiUrl: URL of the Adyen API you are using (Test/Live)
		 * - wsUser: your web service user
		 * - wsPassword: your web service user's password
		 */
		String apiUrl = "https://pal-test.adyen.com/pal/servlet/Payment/v10/cancelOrRefund";
		String wsUser = "YourWSUser";
		String wsPassword = "YourWSUserPassword";

		/**
		 * Create HTTP Client (using Apache HttpComponents library) and set up Basic Authentication
		 */
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(wsUser, wsPassword);
		provider.setCredentials(AuthScope.ANY, credentials);

		HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

		/**
		 * Perform cancel or refund request by sending in a modification request, containing the following variables:
		 * 
		 * <pre>
		 * - merchantAccount        : The merchant account used to process the payment.
		 * - originalReference      : The pspReference that was assigned to the authorisation.
		 * - reference              : Your own reference or description of the modification. (optional)
		 * </pre>
		 */
		JSONObject modificationRequest = new JSONObject();
		modificationRequest.put("merchantAccount", "YourMerchantAccount");
		modificationRequest.put("originalReference", "PspReferenceOfTheAuthorisedPayment");
		modificationRequest.put("reference", "YourReference");

		/**
		 * Send the HTTP request with the specified variables in JSON.
		 */
		HttpPost httpRequest = new HttpPost(apiUrl);
		httpRequest.addHeader("Content-Type", "application/json");
		httpRequest.setEntity(new StringEntity(modificationRequest.toString(), "UTF-8"));

		HttpResponse httpResponse = client.execute(httpRequest);
		String paymentResponse = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

		/**
		 * Keep in mind that you should handle errors correctly.
		 * If the Adyen platform does not accept or store a submitted request, you will receive a HTTP response with
		 * status different than 200 OK. In this case, the error details are populated in the paymentResponse.
		 */
		
		// Parse JSON response
		JSONParser parser = new JSONParser();
		JSONObject modificationResult;
		
		try {
			modificationResult = (JSONObject) parser.parse(paymentResponse);
		} catch (ParseException e) {
			throw new ServletException(e);
		}
		
		// If the request was rejected, raise an exception
		if (httpResponse.getStatusLine().getStatusCode() != 200) {
			String faultString = modificationResult.get("errorType") + " " + modificationResult.get("errorCode") + " " + modificationResult.get("message");
			throw new ServletException(faultString);
		}

		/**
		 * If the message was syntactically valid and merchantAccount is correct you will receive a modification
		 * response with the following fields:
		 * - pspReference: A new reference to uniquely identify this modification request.
		 * - response: A confirmation indicating we receievd the request: [cancelOrRefund-received].
		 * 
		 * If the payment is authorised, but not yet captured, it will be cancelled. In other cases the payment will be
		 * fully refunded (if possible).
		 * 
		 * Please note: The actual result of the cancel or refund is sent via a notification with eventCode
		 * CANCEL_OR_REFUND.
		 */
		PrintWriter out = response.getWriter();

		out.println("Modification Result:");
		out.println("- pspReference: " + modificationResult.get("pspReference"));
		out.println("- response: " + modificationResult.get("response"));

	}

}
