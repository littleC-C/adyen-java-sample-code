package com.adyen.examples.recurring.Library;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.recurring.DisableRequest;
import com.adyen.model.recurring.DisableResult;
import com.adyen.service.Recurring;

/**
 * Disable recurring contract (Java Library)
 *
 * Disabling a recurring contract (detail) can be done by calling the disable action on the Recurring service with a
 * request. This file shows how you can disable a recurring contract using Java Library.
 *
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 *
 * @link /5.Recurring/Library/DisableRecurringContract
 * @author Created by Adyen - Payments Made Easy
 */

public class DisableRecurringContract {

    public void disableRecurring() throws Exception{

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
        Recurring recurring = new Recurring(client);

        /**
         * The DisableRequest object should contain the following variables:
         *
         * <pre>
         * - setMerchantAccount(String merchantAccount)                     : Your merchant account.
         * - setShopperReference(String shopperReference)                   : The reference to the shopper. This shopperReference must be the same as the
         *                                                                  shopperReference used in the initial payment.
         * - setRecurringDetailReference(String recurringDetailReference)   : The recurringDetailReference of the details you wish to disable. If you do
         *                                                                  not supply this field all details for the shopper will be disabled including
         *                                                                  the contract! This means that you can not add new details anymore.
         * </pre>
         */

        // Create new Disable Request
        DisableRequest disableRequest = new DisableRequest();
        disableRequest.setShopperReference("YourShopperReference");
        disableRequest.setMerchantAccount("YourMerchantAccount");
        disableRequest.setRecurringDetailReference("YourRecurringDetailReference");
        DisableResult result = recurring.disable(disableRequest);

        /**
         * The response will be a result object with a single field response. If a single detail was disabled the value
         * of this field will be [detail-successfully-disabled] or, if all details are disabled, the value is
         * [all-details-successfully-disabled].
         */

        System.out.println("Disable Recurring Result:");
        System.out.println("- response: " + result.getResponse());

   }
}
