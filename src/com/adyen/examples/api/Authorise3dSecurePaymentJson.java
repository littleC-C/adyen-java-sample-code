package com.adyen.examples.api;

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
 * Authorise 3D Secure payment (JSON)
 * 
 * 3D Secure (Verifed by VISA / MasterCard SecureCode) is an additional authentication
 * protocol that involves the shopper being redirected to their card issuer where their
 * identity is authenticated prior to the payment proceeding to an authorisation request.
 * 
 * In order to start processing 3D Secure transactions, the following changes are required:
 * 1. Your Merchant Account needs to be confgured by Adyen to support 3D Secure. If you would
 *    like to have 3D Secure enabled, please submit a request to the Adyen Support Team (support@adyen.com).
 * 2. Your integration should support redirecting the shopper to the card issuer and submitting
 *    a second API call to complete the payment.
 *
 * This example demonstrates the second API call to complete the payment using JSON.
 * See the API Manual for a full explanation of the steps required to process 3D Secure payments.
 * 
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 * 
 * @link /2.API/Json/Authorise3dSecurePayment
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/2.API/Json/Authorise3dSecurePayment" })
public class Authorise3dSecurePaymentJson extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * JSON settings
		 * - apiUrl: URL of the Adyen API you are using (Test/Live)
		 * - wsUser: your web service user
		 * - wsPassword: your web service user's password
		 */
		String apiUrl = "https://pal-test.adyen.com/pal/servlet/Payment/v10/authorise3d";
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
		 * After the shopper's identity is authenticated by the issuer, they will be returned to your
		 * site by sending an HTTP POST request to the TermUrl containing the MD parameter and a new
		 * parameter called PaRes (see API manual). These will be needed to complete the payment.
		 *
		 * To complete the payment, a payment request should be submitted to the authorise3d action
		 * of the web service. The request should contain the following variables:
		 * 
		 * <pre>
		 * - merchantAccount: This should be the same as the Merchant Account used in the original authorise request.
		 * - browserInfo:     It is safe to use the values from the original authorise request, as they
		                      are unlikely to change during the course of a payment.
		 * - md:              The value of the MD parameter received from the issuer.
		 * - paReponse:       The value of the PaRes parameter received from the issuer.
		 * - shopperIP:       The IP address of the shopper. We recommend that you provide this data, as
		                      it is used in a number of risk checks, for example, the number of payment
		                      attempts and location based checks.
		* </pre>
		*/
		
		// Create payment request
		JSONObject paymentRequest = new JSONObject();
		paymentRequest.put("merchantAccount", "YourMerchantAccount");
		paymentRequest.put("md", request.getParameter("MD"));
		paymentRequest.put("paResponse", request.getParameter("PaRes"));
		paymentRequest.put("shopperIP", "123.123.123.123");
		
		// Set browser info
		JSONObject browserInfo = new JSONObject();
		browserInfo.put("userAgent", request.getHeader("User-Agent"));
		browserInfo.put("acceptHeader", request.getHeader("Accept"));
		paymentRequest.put("browserInfo", browserInfo);

		/**
		 * Send the HTTP request with the specified variables in JSON.
		 */
		HttpPost httpRequest = new HttpPost(apiUrl);
		httpRequest.addHeader("Content-Type", "application/json");
		httpRequest.setEntity(new StringEntity(paymentRequest.toString(), "UTF-8"));

		HttpResponse httpResponse = client.execute(httpRequest);
		String paymentResponse = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

		/**
		 * Keep in mind that you should handle errors correctly.
		 * If the Adyen platform does not accept or store a submitted request, you will receive a HTTP response with
		 * status different than 200 OK. In this case, the error details are populated in the paymentResponse.
		 */
		
		// Parse JSON response
		JSONParser parser = new JSONParser();
		JSONObject paymentResult;
		
		try {
			paymentResult = (JSONObject) parser.parse(paymentResponse);
		} catch (ParseException e) {
			throw new ServletException(e);
		}
		
		// If the request was rejected, raise an exception
		if (httpResponse.getStatusLine().getStatusCode() != 200) {
			String faultString = paymentResult.get("errorType") + " " + paymentResult.get("errorCode") + " " + paymentResult.get("message");
			throw new ServletException(faultString);
		}

		/**
		 * If the payment passes validation a risk analysis will be done and, depending on the outcome, an authorisation
		 * will be attempted. You receive a payment response with the following fields:
		 * 
		 * <pre>
		 * - pspReference    : Adyen's unique reference that is associated with the payment.
		 * - resultCode      : The result of the payment. Possible values: Authorised, Refused, Error or Received.
		 * - authCode        : The authorisation code if the payment was successful. Blank otherwise.
		 * - refusalReason   : Adyen's mapped refusal reason, populated if the payment was refused.
		 * </pre>
		 */
		PrintWriter out = response.getWriter();

		out.println("Payment Result:");
		out.println("- pspReference: " + paymentResult.get("pspReference"));
		out.println("- resultCode: " + paymentResult.get("resultCode"));
		out.println("- authCode: " + paymentResult.get("authCode"));
		out.println("- refusalReason: " + paymentResult.get("refusalReason"));

	}

}
