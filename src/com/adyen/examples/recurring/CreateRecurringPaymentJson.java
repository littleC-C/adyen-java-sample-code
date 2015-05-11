package com.adyen.examples.recurring;

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
 * Create Recurring Payment (JSON)
 * 
 * You can submit a recurring payment using a specific recurringDetails record or by using the last created
 * recurringDetails record. The request for the recurring payment is done using a paymentRequest. This file shows how a
 * recurring payment can be submitted using our JSON API.
 * 
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 * 
 * @link /5.Recurring/Json/CreateRecurringPayment
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/5.Recurring/Json/CreateRecurringPayment" })
public class CreateRecurringPaymentJson extends HttpServlet {

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
		 * A recurring payment can be submitted with a HTTP Post request to the API, containing the following variables:
		 * 
		 * <pre>
		 * - selectedRecurringDetailReference : The recurringDetailReference you want to use for this payment.
		 *                        The value LATEST can be used to select the most recently used recurring detail.
		 * - recurring
		 *     - contract       : This should be the same value as recurringContract in the payment where the recurring
		 *                        contract was created. However if ONECLICK,RECURRING was specified initially then this
		 *                        field can be either ONECLICK or RECURRING.
		 * - shopperInteraction : Set to ContAuth if the contract value is RECURRING, or Ecommerce if the contract
		 *                        value is ONECLICK.
		 * 
		 * - merchantAccount    : The merchant account for which you want to process the payment.
		 * - amount
		 *     - currency       : The three character ISO currency code.
		 *     - value          : The transaction amount in minor units (e.g. EUR 1,00 = 100).
		 * - reference          : Your reference for this payment.
		 * - shopperEmail       : The email address of the shopper. This does not have to match the email address
		 *                        supplied with the initial payment since it may have changed in the mean time.
		 * - shopperReference   : The reference to the shopper. This shopperReference must be the same as the
		 *                        shopperReference used in the initial payment.
		 * - shopperIP          : The shopper's IP address. (recommended)
		 * - fraudOffset        : An integer that is added to the normal fraud score. (optional)
		 * - card
		 *     - CVC            : The card validation code. (only required for OneClick card payments)
		 * </pre>
		 */
		
		// Create new payment request
		JSONObject paymentRequest = new JSONObject();
		paymentRequest.put("merchantAccount", "YourMerchantAccount");
		paymentRequest.put("reference", "TEST-PAYMENT-" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()));
		paymentRequest.put("shopperIP", "123.123.123.123");
		paymentRequest.put("shopperEmail", "test@example.com");
		paymentRequest.put("shopperReference", "TheShopperReference");
		paymentRequest.put("fraudOffset", "0");
		
		// Set amount
		JSONObject amount = new JSONObject();
		amount.put("currency", "EUR");
		amount.put("value", "199");
		paymentRequest.put("amount", amount);

		// Set recurring contract
		paymentRequest.put("selectedRecurringDetailReference", "LATEST");
		paymentRequest.put("shopperInteraction", "Ecommerce");

		JSONObject recurring = new JSONObject();
		recurring.put("contract", "ONECLICK");
		paymentRequest.put("recurring", recurring);
		
		// CVC is only required for OneClick card payments
		JSONObject card = new JSONObject();
		card.put("cvc", "737");
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
		 * If the recurring payment passes validation a risk analysis will be done and, depending on the outcome, an
		 * authorisation will be attempted. You receive a payment response with the following fields:
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
