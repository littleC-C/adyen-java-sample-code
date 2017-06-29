package com.adyen.examples.paymentmethods.JSON;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Get Payment Methods(JSON)
 * 
 * You may decide to skip the Adyen payment method selection page so that the shopper starts directly on the payment
 * details entry page. This is done by calling details.shtml instead of pay.shtml/select.shtml. An additional parameter,
 * brandCode and where applicable issuerId, should be provided with the selected payment method listed. Please refer to
 * section 2.9 of the Integration Manual for more details.
 * 
 * The directory service can also be used to determine which payment methods are available for the shopper on your
 * Merchant Account. This is done by calling directory.shtml, with a normal payment request. Please note that the
 * countryCode field is mandatory to receive back the correct payment methods.
 * 
 * This file provides a code example showing how to retreive the payment methods enabled for the specified merchant
 * account.
 * 
 * @link /6.PaymentMethods/JSON/GetPaymentMethods
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/6.PaymentMethods/GetPaymentMethods" })
public class GetPaymentMethods extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * Directory Service settings
		 * - apiUrl: URL of the Adyen directory service
		 * - hmacKey: shared secret key used to encrypt the signature
		 * 
		 * Both variables are dependent on the environment which should be used (Test/Live).
		 * HMAC key can be set up: Adyen CA >> Skins >> Choose your Skin >> Edit Tab >> Edit HMAC key for Test & Live.
		 */
		String apiUrl = "https://test.adyen.com/hpp/directory.shtml";
		String hmacKey = "YourHmacSecretKey";

		/**
		 * The following fields are required for the directory service.
		 */

		// Generate date
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		Date sessionDate = calendar.getTime(); // current date + 1 day

		// Define variables
		String merchantReference = "Request payment methods";
		String paymentAmount = "100";
		String currencyCode = "EUR";
		String skinCode = "YourSkinCode";
		String merchantAccount = "YourMerchantAccount";
		String sessionValidity = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(sessionDate);

		// Calculate merchant signature
		String signingString = paymentAmount + currencyCode + merchantReference + skinCode + merchantAccount
				+ sessionValidity;

		String merchantSig;
		try {
			merchantSig = calculateHMAC(hmacKey, signingString);
		} catch (GeneralSecurityException e) {
			throw new ServletException(e);
		}

		// Set HTTP Post variables
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		Collections.addAll(postParameters,
			new BasicNameValuePair("merchantReference", merchantReference),
			new BasicNameValuePair("paymentAmount", paymentAmount),
			new BasicNameValuePair("currencyCode", currencyCode),
			new BasicNameValuePair("skinCode", skinCode),
			new BasicNameValuePair("merchantAccount", merchantAccount),
			new BasicNameValuePair("sessionValidity", sessionValidity),
			new BasicNameValuePair("merchantSig", merchantSig)
			);

		/**
		 * Create HTTP Client (using Apache HttpComponents library) and send the request with the specified variables.
		 */
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost httpPost = new HttpPost(apiUrl);
		httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

		HttpResponse httpResponse = client.execute(httpPost);
		String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

		/**
		 * Keep in mind that you should handle errors correctly.
		 */
		if (httpResponse.getStatusLine().getStatusCode() == 500) {
			throw new ServletException(result);
		}
		else if (httpResponse.getStatusLine().getStatusCode() != 200) {
			throw new ServletException(httpResponse.getStatusLine().toString());
		}

		/**
		 * The result contains a JSON array containing the available payment methods for the merchant account.
		 */
		PrintWriter out = response.getWriter();
		out.println(result);
	}

	/**
	 * Computes the Base64 encoded signature using the HMAC algorithm with the SHA-1 hashing function.
	 */
	private String calculateHMAC(String hmacKey, String signingString) throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeySpec keySpec = new SecretKeySpec(hmacKey.getBytes(), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(keySpec);

		byte[] result = mac.doFinal(signingString.getBytes("UTF-8"));
		return Base64.encodeBase64String(result);
	}

}
