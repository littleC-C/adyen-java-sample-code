package com.adyen.examples.customfields.HttpPost;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

/**
 * Custom fields service (HTTP Post)
 * 
 * Custom fields are a powerful feature of the payment pages that allow you to add form fields to the HPP. These will be
 * sent to you before final payment submission for approval; you may use this feature to capture any additional data or
 * permissions that you may require, such as collecting shipping data, forcing the shopper to accept terms and
 * conditions, or checking a validation code.
 *
 * Customfields are not enabled by default, they have to be enabled per skin.
 * 1. Enable for skin: Adyen CA >> Skins >> Select Skin >> Custom Fields >> Set up URLs.
 * 2. Add HTML input fields whose name attribute is prefixed with customfields (e.g."customfields.subscribe") to the
 * skin. Please see the Skin Creation Manual for more details.
 * 
 * @link /8.CustomFields/HttpPost/CustomFieldsServer
 * @author Created by Adyen - Payments Made Easy
 */

@WebServlet(urlPatterns = { "/8.CustomFields/HttpPost/CustomFieldsServer" })
public class CustomFieldsServer extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * Check authentication
		 * 
		 * We recommend you to secure your custom fields server. You can secure it using a username/password which can
		 * be configured in the CA. The username and password will be available in the Authorization header of the
		 * request. Alternatively, is to allow only traffic that comes from Adyen servers.
		 */
		String notificationUser = "TestUser";
		String notificationPassword = "TestPassword";

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null) {
			// Return 401 Unauthorized if Authorization header is not available
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		else {
			// Decode username and password from Authorization header
			String encodedAuth = authHeader.split(" ")[1];
			String decodedAuth = new String(Base64.decodeBase64(encodedAuth));

			String requestUser = decodedAuth.split(":")[0];
			String requestPassword = decodedAuth.split(":")[1];

			// Return 403 Forbidden if username and/or password are incorrect
			if (!notificationUser.equals(requestUser) || !notificationPassword.equals(requestPassword)) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
		}

		/**
		 * Handle custom fields request
		 * 
		 * The following request parameters are available:
		 * - request.merchantAccount
		 * - request.merchantReference
		 * 
		 * - request.customFields.0.name: SomeField1
		 * - request.customFields.0.value: SomeValue1
		 * - request.customFields.1.name: SomeField1
		 * - request.customFields.1.value: SomeValue1
		 * - (..)
		 * 
		 * - request.sessionFields.0.name: paymentAmount
		 * - request.sessionFields.0.value: 199
		 * - request.sessionFields.1.name: currencyCode
		 * - request.sessionFields.1.value: : EUR
		 * - (..)
		 */

		// Check how many custom fields are submitted
		int customFieldsCount = 0;
		Enumeration<String> paramNames = request.getParameterNames();

		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();

			if (paramName.contains("request.customFields")) {
				customFieldsCount++;
			}
		}

		// Collect all custom fields
		Map<String, String> customFields = new HashMap<String, String>();

		for (int i = 0; i < customFieldsCount / 2; i++) {
			String name = request.getParameter("request.customFields." + i + ".name");
			String value = request.getParameter("request.customFields." + i + ".value");
			customFields.put(name, value);
		}

		/**
		 * Return result
		 * 
		 * If you respond with [accepted] the payment is allowed to continue. If not, you can specify which fields
		 * failed validation and the validation messages to display. If you need to store the custom fields data, you
		 * must do so at this point, the data cannot be sent to you via the Notification server.
		 * 
		 * In this example we only expect one parameter: terms_conditions = true. A simple check can be done to verify
		 * if the shopper has thicked the checkbox. Obviously, more sophisticated validations can be added.
		 */
		PrintWriter out = response.getWriter();

		if (customFields.get("terms_conditions") != null) {
			out.print("response.sessionFields.0.name=terms_conditions&");
			out.print("response.sessionFields.0.value=true&");
			out.print("response.response=[accepted]");
		}
		else {
			out.print("response.customFields.0.name=terms_conditions&");
			out.print("response.customFields.0.value=Please agree with terms and conditions!&");
			out.print("response.response=[invalid]");
		}

	}

}
