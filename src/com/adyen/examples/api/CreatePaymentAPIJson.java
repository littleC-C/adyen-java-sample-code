package com.adyen.examples.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

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
 * Create Payment through the API (JSON)
 * 
 * Payments can be created through our API, however this is only possible if you are PCI Compliant. JSON API payments
 * are submitted using a HTTP request to the authorise action. We will explain a simple credit card submission.
 * 
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 * 
 * @link /2.API/Json/CreatePaymentAPI
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/2.API/Json/CreatePaymentAPI" })
public class CreatePaymentAPIJson extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * JSON settings
		 * - apiUrl: URL of the Adyen API you are using (Test/Live)
		 * - wsUser: your web service user
		 * - wsPassword: your web service user's password
		 */
		String apiUrl = "https://pal-test.adyen.com/pal/servlet/Payment/v10/authorise";
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
		 * A payment can be submitted with a JSON request to the authorise action of the API,
		 * containing the following variables:
		 * 
		 * <pre>
		 * - merchantAccount           : The merchant account for which you want to process the payment
		 * - amount
		 *     - currency              : The three character ISO currency code.
		 *     - value                 : The transaction amount in minor units (e.g. EUR 1,00 = 100).
		 * - reference                 : Your reference for this payment.
		 * - shopperIP                 : The shopper's IP address. (recommended)
		 * - shopperEmail              : The shopper's email address. (recommended)
		 * - shopperReference          : An ID that uniquely identifes the shopper, such as a customer id. (recommended)
		 * - fraudOffset               : An integer that is added to the normal fraud score. (optional)
		 * - card
		 *     - expiryMonth           : The expiration date's month written as a 2-digit string,
		 *                               padded with 0 if required (e.g. 03 or 12).
		 *     - expiryYear            : The expiration date's year written as in full (e.g. 2016).
		 *     - holderName            : The card holder's name, as embossed on the card.
		 *     - number                : The card number.
		 *     - cvc                   : The card validation code, which is the CVC2 (MasterCard),
		 *                               CVV2 (Visa) or CID (American Express).
		 *     - billingAddress (recommended)
		 *         - street            : The street name.
		 *         - houseNumberOrName : The house number (or name).
		 *         - city              : The city.
		 *         - postalCode        : The postal/zip code.
		 *         - stateOrProvince   : The state or province.
		 *         - country           : The country in ISO 3166-1 alpha-2 format (e.g. NL).
		 * </pre>
		 */
		
		// Create new payment request
		JSONObject paymentRequest = new JSONObject();
		paymentRequest.put("merchantAccount", "YourMerchantAccount");
		paymentRequest.put("reference", "TEST-PAYMENT-" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()));
		paymentRequest.put("shopperIP", "123.123.123.123");
		paymentRequest.put("shopperEmail", "test@example.com");
		paymentRequest.put("shopperReference", "YourReference");
		paymentRequest.put("fraudOffset", "0");
		
		// Set amount
		JSONObject amount = new JSONObject();
		amount.put("currency", "EUR");
		amount.put("value", "199");
		paymentRequest.put("amount", amount);

		// Set card
		JSONObject card = new JSONObject();
		card.put("expiryMonth", "06");
		card.put("expiryYear", "2016");
		card.put("holderName", "John Doe");
		card.put("number", "5555444433331111");
		card.put("cvc", "737");
		
		JSONObject billingAddress = new JSONObject();
		billingAddress.put("street", "Simon Carmiggeltstraat");
		billingAddress.put("houseNumberOrName", "6-50");
		billingAddress.put("postalCode", "1011 DJ");
		billingAddress.put("city", "Amsterdam");
		billingAddress.put("stateOrProvince", "");
		billingAddress.put("country", "NL");
		card.put("billingAddress", billingAddress);

		paymentRequest.put("card", card);
		
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
