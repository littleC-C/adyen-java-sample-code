package com.adyen.examples.modifications.Library;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.modification.CancelOrRefundRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.service.Modification;

/**
 * Cancel or Refund a Payment (Java Library)
 *
 * If you do not know if the payment is captured but you want to reverse the authorisation you can send a modification
 * request to the cancelOrRefund action. This file shows how a payment can be cancelled or refunded by a modification
 * request using Java Library.
 *
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 *
 * @link /4.Modifications/Library/CancelOrRefundPayment
 * @author Created by Adyen - Payments Made Easy
 */

public class CancelOrRefundPayment {

    public void cancelOrRefundPayment() throws Exception {

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
         * Perform cancel or refund request by sending in a CancelOrRefundRequest, containing the following setters:
         *
         * <pre>
         * - setMerchantAccount(String merchantAccount)          : The merchant account used to process the payment.
         * - setOriginalReference(String originalReference)      : The pspReference that was assigned to the authorisation.
         * - setReference(String reference)                      : Your own reference or description of the modification. (optional)
         * </pre>
         */
        // Create new Cancel Request
        CancelOrRefundRequest cancelOrRefundRequest = new CancelOrRefundRequest();
        cancelOrRefundRequest.setMerchantAccount("YourMerchantAccount");
        cancelOrRefundRequest.setOriginalReference("YourPspReference");
        cancelOrRefundRequest.setReference("YourReference");

        // Cancel or Refund the Payment
        ModificationResult modificationResult = modification.cancelOrRefund(cancelOrRefundRequest);

        /**
         * If the message was syntactically valid and merchantAccount is correct you will receive a
         * ModificationResult with the following getters:
         * - getPspReference()          : A new reference to uniquely identify this modification request.
         * - getResponse()              : A confirmation indicating we received the request: [cancelOrRefund-received].
         *
         * If the payment is authorised, but not yet captured, it will be cancelled. In other cases the payment will be
         * fully refunded (if possible).
         *
         * Please note: The actual result of the cancel or refund is sent via a notification with eventCode
         * CANCEL_OR_REFUND.
         */

        System.out.println("Modification Result:");
        System.out.println("- pspReference: " + modificationResult.getPspReference());
        System.out.println("- response: " + modificationResult.getResponse());

    }
}
