package com.adyen.examples.recurring.Library;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.recurring.RecurringDetailsRequest;
import com.adyen.model.recurring.RecurringDetailsResult;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.service.Recurring;

import java.util.List;

/**
 * Retrieve recurring contract details (Java Library)
 *
 * Once a shopper has stored RECURRING details with Adyen you are able to process a RECURRING payment. This file shows
 * you how to retrieve the RECURRING contract(s) for a shopper using Java Library.
 *
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 *
 * @link /5.Recurring/Library/RetrieveRecurringContract
 * @author Created by Adyen - Payments Made Easy
 */

public class RetrieveRecurringContract {

    public void retrieveRecurringDetails() throws Exception{

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
         * The RecurringDetailsRequest object should contain the following variables:
         *
         * <pre>
         * - setMerchantAccount(String merchantAccount)      : Your merchant account.
         * - setShopperReference(String shopperReference)    : The reference to the shopper. This shopperReference must be the same as the
         *                                                   shopperReference used in the initial payment.
         * - selectRecurringContract()                       : The method called should match the same value as recurringContract in the payment where the
         *   or                                              recurring contract was created. However if ONECLICK,RECURRING was specified
         *   selectOneClickContract()                        initially then this field can be either ONECLICK or RECURRING.
         * </pre>
         */
        // Create new Recurring Details Request
        RecurringDetailsRequest recurringDetailsRequest = new RecurringDetailsRequest();
        recurringDetailsRequest.setShopperReference("YourShopperReference");
        recurringDetailsRequest.setMerchantAccount("YourMerchantAccount");
        recurringDetailsRequest.selectRecurringContract();
        RecurringDetailsResult result = recurring.listRecurringDetails(recurringDetailsRequest);

        /**
         * The RecurringDetailsResult object will contain the following getters:
         *
         * <pre>
         * - getCreationDate()
         * - getLastKnownShopperEmail()
         * - getShopperReference()
         * - getRecurringDetails()             : A list of zero or more details, containing:
         *     - getRecurringDetailReference() : The reference the details are stored under.
         *     - getVariant()                  : The payment method (e.g. mc, visa, elv, ideal, paypal).
         *                                     For some variants, like iDEAL, the sub-brand is returned like idealrabobank.
         *     - getCreationDate()             : The date when the recurring details were created.
         *     - getCard()                     : A container for credit card data.
         *     - getElv()                      : A container for ELV data.
         *     - getBank()                     : A container for BankAccount data.
         * </pre>
         *
         * The recurring contracts are stored in the same object types as you would have submitted in the initial
         * payment. Depending on the payment method one or more fields may be blank or incomplete (e.g. CVC for
         * card). Only one of the detail containers (card/elv/bank) will be returned per detail block, the others will
         * be null. For PayPal there is no detail container.
         */

        System.out.println("Recurring Details Result:");
        System.out.println("- creationDate: " + result.getCreationDate());
        System.out.println("- lastKnownShopperEmail: " + result.getLastKnownShopperEmail());
        System.out.println("- shopperReference: " + result.getShopperReference());
        System.out.println("- recurringDetail:");

        List<RecurringDetail> recurringDetails = result.getRecurringDetails();

        for (Object recurringDetail : recurringDetails) {
            System.out.println("  > * recurringDetailReference: " + recurringDetail.getRecurringDetailReference());
            System.out.println("    * variant: " + recurringDetail.getVariant());
            System.out.println("    * creationDate: " + recurringDetail.getCreationDate());
            System.out.println("    * bank: " + recurringDetail.getBank());
            System.out.println("    * card: " + recurringDetail.getCard());
            System.out.println("    * elv: " + recurringDetail.getElv();
            System.out.println("    * name: " + recurringDetail.getName());


    }

}
