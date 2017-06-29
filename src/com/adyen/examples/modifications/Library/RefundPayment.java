package com.adyen.examples.modifications.Library;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.modification.RefundRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.service.Modification;

/**
 * Refund a Payment (Java Library)
 *
 * Settled payments can be refunded by sending a modification request to the refund action of the API. This file shows
 * how a settled payment can be refunded by a modification request using Java Library.
 *
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 *
 * @link /4.Modifications/Library/RefundPayment
 * @author Created by Adyen - Payments Made Easy
 */

public class RefundPayment {

    public void refundPayment() throws Exception {

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
         * Perform refund request by sending in a RefundRequest, containing the following setters:
         *
         * <pre>
         * - setMerchantAccount(String merchantAccount)          : The merchant account used to process the payment.
         * - setOriginalReference(String originalReference)      : The pspReference that was assigned to the authorisation.
         * - setReference(String reference)                      : Your own reference or description of the modification. (optional)
         * - fillAmount (
         *                  String amount,                       : The amount to refund, must be less than or equal to the authorised amount.
         *                  String currency                      : The three character ISO currency code, must match that of the original payment request.
         *              )
         * </pre>
         */
        // Create new Refund Request
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setMerchantAccount("YourMerchantAccount");
        refundRequest.setOriginalReference("YourPspReference");

        // Set the amount to refund(Partial or full)
        refundRequest.fillAmount("YourAmount", "YourCurrency");

        // Refund the Payment
        ModificationResult modificationResult = modification.refund(refundRequest);

        /**
         * If the message was syntactically valid and merchantAccount is correct you will receive a
         * ModificationResult with the following getters:
         * - getPspReference()          : A new reference to uniquely identify this modification request.
         * - getResponse()              : A confirmation indicating we received the request: [capture-received].
         *
         * Please note: The result of the refund is sent via a notification with eventCode REFUND.
         */

        System.out.println("Modification Result:");
        System.out.println("- pspReference: " + modificationResult.getPspReference());
        System.out.println("- response: " + modificationResult.getResponse());

    }
}
