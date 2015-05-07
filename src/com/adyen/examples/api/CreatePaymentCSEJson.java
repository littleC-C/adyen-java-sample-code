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
 * Create Client-Side Encryption Payment (JSON)
 * 
 * Merchants that require more stringent security protocols or do not want the additional overhead of managing their PCI
 * compliance, may decide to implement Client-Side Encryption (CSE). This is particularly useful for Mobile payment
 * flows where only cards are being offered, as it may result in faster load times and an overall improvement to the
 * shopper flow. The Adyen Hosted Payment Page (HPP) provides the most comprehensive level of PCI compliancy and you do
 * not have any PCI obligations. Using CSE reduces your PCI scope when compared to implementing the API without
 * encryption.
 * 
 * If you would like to implement CSE, please provide the completed PCI Self Assessment Questionnaire (SAQ) A to the
 * Adyen Support Team (support@adyen.com). The form can be found here:
 * https://www.pcisecuritystandards.org/security_standards/documents.php?category=saqs
 * 
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 * 
 * @link /2.API/Json/CreatePaymentCSE
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/2.API/Json/CreatePaymentCSE" })
public class CreatePaymentCSEJson extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Generate current time server-side and set it as request attribute
		request.setAttribute("generationTime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date()));

		// Forward request to corresponding JSP page
		request.getRequestDispatcher("/2.API/create-payment-cse.jsp").forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
		 * - merchantAccount           : The merchant account for which you want to process the payment
		 * - amount
		 *     - currency              : The three character ISO currency code.
		 *     - value                 : The transaction amount in minor units (e.g. EUR 1,00 = 100).
		 * - reference                 : Your reference for this payment.
		 * - shopperIP                 : The shopper's IP address. (recommended)
		 * - shopperEmail              : The shopper's email address. (recommended)
		 * - shopperReference          : An ID that uniquely identifes the shopper, such as a customer id. (recommended)
		 * - fraudOffset               : An integer that is added to the normal fraud score. (optional)
		 * - additionalData.card.encrypted.json: The encrypted card catched by the POST variables.
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
		
		// Set additional data
		JSONObject additionalData = new JSONObject();
		additionalData.put("card.encrypted.json", request.getParameter("adyen-encrypted-data"));
		paymentRequest.put("additionalData", additionalData);

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
