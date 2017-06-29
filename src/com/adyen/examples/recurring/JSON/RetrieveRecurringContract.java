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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Retrieve recurring contract details (JSON)
 * 
 * Once a shopper has stored RECURRING details with Adyen you are able to process a RECURRING payment. This file shows
 * you how to retrieve the RECURRING contract(s) for a shopper using JSON.
 * 
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 * 
 * @link /5.Recurring/JSON/RetrieveRecurringContract
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/5.Recurring/JSON/RetrieveRecurringContract" })
public class RetrieveRecurringContract extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * JSON settings
		 * - apiUrl: URL of the Adyen API you are using (Test/Live)
		 * - wsUser: your web service user
		 * - wsPassword: your web service user's password
		 */
		String apiUrl = "https://pal-test.adyen.com/pal/servlet/Recurring/v10/listRecurringDetails";
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
		 * - merchantAccount        : Your merchant account.
		 * - shopperReference       : The reference to the shopper. This shopperReference must be the same as the
		 *                            shopperReference used in the initial payment.
		 * - recurring
		 *     - contract           : This should be the same value as recurringContract in the payment where the
		 *                            recurring contract was created. However if ONECLICK,RECURRING was specified
		 *                            initially then this field can be either ONECLICK or RECURRING.
		 * </pre>
		 */
		JSONObject recurringRequest = new JSONObject();
		recurringRequest.put("merchantAccount", "YourMerchantAccount");
		recurringRequest.put("shopperReference", "TheShopperReference");

		JSONObject recurring = new JSONObject();
		recurring.put("contract", "ONECLICK");
		recurringRequest.put("recurring", recurring);

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
		 * The recurring details response will contain the following fields:
		 * 
		 * <pre>
		 * - creationDate
		 * - lastKnownShopperEmail
		 * - shopperReference
		 * - recurringDetail              : A list of zero or more details, containing:
		 *     - recurringDetailReference : The reference the details are stored under.
		 *     - variant                  : The payment method (e.g. mc, visa, elv, ideal, paypal).
		 *                                  For some variants, like iDEAL, the sub-brand is returned like idealrabobank.
		 *     - creationDate             : The date when the recurring details were created.
		 *     - card                     : A container for credit card data.
		 *     - elv                      : A container for ELV data.
		 *     - bank                     : A container for BankAccount data.
		 * </pre>
		 * 
		 * The recurring contracts are stored in the same object types as you would have submitted in the initial
		 * payment. Depending on the payment method one or more fields may be blank or incomplete (e.g. CVC for
		 * card). Only one of the detail containers (card/elv/bank) will be returned per detail block, the others will
		 * be null. For PayPal there is no detail container.
		 */
		PrintWriter out = response.getWriter();

		out.println("Recurring Details Result:");
		out.println("- creationDate: " + recurringResult.get("creationDate"));
		out.println("- lastKnownShopperEmail: " + recurringResult.get("lastKnownShopperEmail"));
		out.println("- shopperReference: " + recurringResult.get("shopperReference"));
		out.println("- recurringDetail:");
		
		JSONArray recurringDetails = (JSONArray) recurringResult.get("details");

		for (Object recurringDetail : recurringDetails) {
			JSONObject detail = (JSONObject) ((JSONObject) recurringDetail).get("RecurringDetail");
			out.println("  > * recurringDetailReference: " + detail.get("recurringDetailReference"));
			out.println("    * variant: " + detail.get("variant"));
			out.println("    * creationDate: " + detail.get("creationDate"));
			out.println("    * bank: " + detail.get("bank"));
			out.println("    * card: " + detail.get("card"));
			out.println("    * elv: " + detail.get("elv"));
			out.println("    * name: " + detail.get("name"));
		}
		
	}

}
