package com.adyen.examples.modifications.Library;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.service.Modification;

/**
 * Capture a Payment (Java Library)
 *
 * Authorised (card) payments can be captured to get the money from the shopper. Payments can be automatically captured
 * by our platform. A payment can also be captured by performing an API call. In order to capture an authorised (card)
 * payment you have to send a modification request. This file shows how an authorised payment should be captured by
 * sending a modification request using Java Library.
 *
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 *
 * @link /4.Modifications/Library/CapturePayment
 * @author Created by Adyen - Payments Made Easy
 */

public class CapturePayment {

    public void capturePayment() throws Exception {

        /**
         * Client settings
         * - Client  (
         *              String username,            : your web service user
         *              String password,            : your web service user's password
         *              Environment environment,    : The Environment you are using (Environment.TEST/Environment.LIVE)
         *              String applicationName      : your application name
         *           )
         */
        // Create new Client
        Client client = new Client("YourWSUser", "YourWSPassword", Environment.TEST, "myTestPayment");
        Modification modification = new Modification(client);

        /**
         * Perform capture request by sending in a CaptureRequest, containing the following setters:
         *
         * <pre>
         * - setMerchantAccount(String merchantAccount)          : The merchant account used to process the payment.
         * - setOriginalReference(String originalReference)      : The pspReference that was assigned to the authorisation.
         * - setReference(String reference)                      : Your own reference or description of the modification. (optional)
         * - fillAmount (
         *                  String amount,                       : The amount to capture, must be less than or equal to the authorised amount.
         *                  String currency                      : The three character ISO currency code, must match that of the original payment request.
         *              )
         * </pre>
         */
        // Create new Capture Request
        CaptureRequest captureRequest = new CaptureRequest();
        captureRequest.setMerchantAccount("YourMerchantAccount");
        captureRequest.setOriginalReference("YourPspReference");
        captureRequest.setReference("YourReference");

        // Set the amount to capture(Partial or full)
        captureRequest.fillAmount("YourAmount", "YourCurrency");

        // Capture the Payment
        ModificationResult modificationResult = modification.capture(captureRequest);

        /**
         * If the message was syntactically valid and merchantAccount is correct you will receive a
         * ModificationResult with the following getters:
         * - getPspReference()          : A new reference to uniquely identify this modification request.
         * - getResponse()              : A confirmation indicating we received the request: [capture-received].
         */

        System.out.println("Modification Result:");
        System.out.println("- pspReference: " + modificationResult.getPspReference());
        System.out.println("- response: " + modificationResult.getResponse());
    }
}
