package com.adyen.examples.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Create 3D Secure payment (HTTP Post)
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
 * This example demonstrates the initial API call to create the 3D secure payment using HTTP Post,
 * and shows the redirection the the card issuer.
 * See the API Manual for a full explanation of the steps required to process 3D Secure payments.
 *
 * @link /2.API/HttpPost/Create3dSecurePayment
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/2.API/HttpPost/Create3dSecurePayment" })
public class Create3dSecurePaymentHttpPost extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * HTTP Post settings
		 * - apiUrl: URL of the Adyen API you are using (Test/Live)
		 * - wsUser: your web service user
		 * - wsPassword: your web service user's password
		 */
		String apiUrl = "https://pal-test.adyen.com/pal/adapter/httppost";
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
		 * A payment can be submitted by sending a PaymentRequest to the authorise action of the web service.
		 * The initial API call for both 3D Secure and non-3D Secure payments is almost identical.
		 * However, for 3D Secure payments, you must supply the browserInfo object as a sub-element of the payment request.
		 * This is a container for the acceptHeader and userAgent of the shopper's browser.
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
		 * - browserInfo
		 *     - userAgent             : The user agent string of the shopper's browser (required).
		 *     - acceptHeader          : The accept header string of the shopper's browser (required).
		 * </pre>
		 */
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		Collections.addAll(postParameters,
			new BasicNameValuePair("action", "Payment.authorise"),

			new BasicNameValuePair("paymentRequest.merchantAccount", "YourMerchantAccount"),
			new BasicNameValuePair("paymentRequest.reference",
				"TEST-3D-SECURE-PAYMENT-" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date())),
			new BasicNameValuePair("paymentRequest.shopperIP", "123.123.123.123"),
			new BasicNameValuePair("paymentRequest.shopperEmail", "test@example.com"),
			new BasicNameValuePair("paymentRequest.shopperReference", "YourReference"),
			new BasicNameValuePair("paymentRequest.fraudOffset", "0"),

			new BasicNameValuePair("paymentRequest.amount.currency", "EUR"),
			new BasicNameValuePair("paymentRequest.amount.value", "199"),

			new BasicNameValuePair("paymentRequest.card.expiryMonth", "06"),
			new BasicNameValuePair("paymentRequest.card.expiryYear", "2016"),
			new BasicNameValuePair("paymentRequest.card.holderName", "John Doe"),
			new BasicNameValuePair("paymentRequest.card.number", "5212345678901234"),
			new BasicNameValuePair("paymentRequest.card.cvc", "737"),

			new BasicNameValuePair("paymentRequest.card.billingAddress.street", "Simon Carmiggeltstraat"),
			new BasicNameValuePair("paymentRequest.card.billingAddress.houseNumberOrName", "6-50"),
			new BasicNameValuePair("paymentRequest.card.billingAddress.postalCode", "1011 DJ"),
			new BasicNameValuePair("paymentRequest.card.billingAddress.city", "Amsterdam"),
			new BasicNameValuePair("paymentRequest.card.billingAddress.stateOrProvince", ""),
			new BasicNameValuePair("paymentRequest.card.billingAddress.country", "NL"),

			new BasicNameValuePair("paymentRequest.browserInfo.userAgent", request.getHeader("User-Agent")),
			new BasicNameValuePair("paymentRequest.browserInfo.acceptHeader", request.getHeader("Accept"))
			);

		/**
		 * Send the HTTP Post request with the specified variables.
		 */
		HttpPost httpPost = new HttpPost(apiUrl);
		httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

		HttpResponse httpResponse = client.execute(httpPost);
		String paymentResponse = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

		/**
		 * Keep in mind that you should handle errors correctly.
		 * If the Adyen platform does not accept or store a submitted request, you will receive a HTTP response with
		 * status code 500 Internal Server Error. The fault string can be found in the paymentResponse.
		 */
		if (httpResponse.getStatusLine().getStatusCode() == 500) {
			throw new ServletException(paymentResponse);
		}
		else if (httpResponse.getStatusLine().getStatusCode() != 200) {
			throw new ServletException(httpResponse.getStatusLine().toString());
		}

		/**
		 * Once your account is configured for 3-D Secure, the Adyen system performs a directory
		 * inquiry to verify that the card is enrolled in the 3-D Secure programme.
		 * If it is not enrolled, the response is the same as a normal API authorisation.
		 * If, however, it is enrolled, the response contains these fields:
		 *
		 * - paRequest     : The 3-D request data for the issuer.
		 * - md            : The payment session.
		 * - issuerUrl     : The URL to direct the shopper to.
		 * - resultCode    : The resultCode will be RedirectShopper.
		 *
		 * The paRequest and md fields should be included in an HTML form, which needs to be submitted
		 * using the HTTP POST method to the issuerUrl. You must also include a termUrl parameter
		 * in this form, which contains the URL on your site that the shopper will be returned to
		 * by the issuer after authentication. In this example we are redirecting to another example
		 * which completes the 3D Secure payment.
		 *
		 * @see Authorise3dSecurePaymentHttpPost.java
		 *
		 * We recommend that the form is "self-submitting" with a fallback in case javascript is disabled.
		 * A sample form is implemented in the file below.
		 *
		 * @see WebContent/2.API/create-3d-secure-payment.jsp
		 */
		Map<String, String> paymentResult = parseQueryString(paymentResponse);

		if (paymentResult.get("paymentResult.resultCode").equals("RedirectShopper")) {
			// Set request parameters for use on the JSP page
			request.setAttribute("IssuerUrl", paymentResult.get("paymentResult.issuerUrl"));
			request.setAttribute("PaReq", paymentResult.get("paymentResult.paRequest"));
			request.setAttribute("MD", paymentResult.get("paymentResult.md"));
			request.setAttribute("TermUrl", "YOUR_URL_HERE/Authorise3dSecurePayment");

			// Set correct character encoding
			response.setCharacterEncoding("UTF-8");

			// Forward request data to corresponding JSP page
			request.getRequestDispatcher("/2.API/create-3d-secure-payment.jsp").forward(request, response);
		}
		else {
			PrintWriter out = response.getWriter();

			out.println("Payment Result:");
			out.println("- pspReference: " + paymentResult.get("paymentResult.pspReference"));
			out.println("- resultCode: " + paymentResult.get("paymentResult.resultCode"));
			out.println("- authCode: " + paymentResult.get("paymentResult.authCode"));
			out.println("- refusalReason: " + paymentResult.get("paymentResult.refusalReason"));
		}

	}

	/**
	 * Parse the result of the HTTP Post request (will be returned in the form of a query string)
	 */
	private Map<String, String> parseQueryString(String queryString) throws UnsupportedEncodingException {
		Map<String, String> parameters = new HashMap<String, String>();
		String[] pairs = queryString.split("&");

		for (String pair : pairs) {
			String[] keyval = pair.split("=");
			parameters.put(URLDecoder.decode(keyval[0], "UTF-8"), URLDecoder.decode(keyval[1], "UTF-8"));
		}

		return parameters;
	}

}
