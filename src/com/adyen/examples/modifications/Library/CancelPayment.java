package com.adyen.examples.modifications.Library;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.modification.CancelRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.service.Modification;

/**
 * Cancel a Payment (Java Library)
 *
 * In order to cancel an authorised (card) payment you send a modification request to the cancel action. This file shows
 * how an authorised payment should be canceled by sending a modification request using Java Library.
 *
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 *
 * @link /4.Modifications/Library/CancelPayment
 * @author Created by Adyen - Payments Made Easy
 */

public class CancelPayment {

    public void cancelPayment() throws Exception {

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
         * Perform cancel request by sending in a CancelRequest, containing the following setters:
         *
         * <pre>
         * - setMerchantAccount(String merchantAccount)          : The merchant account used to process the payment.
         * - setOriginalReference(String originalReference)      : The pspReference that was assigned to the authorisation.
         * - setReference(String reference)                      : Your own reference or description of the modification. (optional)
         * </pre>
         */
        // Create new Cancel Request
        CancelRequest cancelRequest = new CancelRequest();
        cancelRequest.setMerchantAccount("YourMerchantAccount");
        cancelRequest.setOriginalReference("YourPspReference");
        cancelRequest.setReference("YourReference");

        // Cancel the Payment
        ModificationResult modificationResult = modification.cancel(cancelRequest);

        /**
         * If the message was syntactically valid and merchantAccount is correct you will receive a
         * ModificationResult with the following getters:
         * - getPspReference()          : A new reference to uniquely identify this modification request.
         * - getResponse()              : A confirmation indicating we received the request: [cancel-received].
         *
         * Please note: The result of the cancellation is sent via a notification with eventCode CANCELLATION.
         */

        System.out.println("Modification Result:");
        System.out.println("- pspReference: " + modificationResult.getPspReference());
        System.out.println("- response: " + modificationResult.getResponse());
    }
}
