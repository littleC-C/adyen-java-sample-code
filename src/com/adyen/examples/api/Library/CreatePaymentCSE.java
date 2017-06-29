package com.adyen.examples.api.Library;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentResult;
import com.adyen.service.Payment;

/**
 * Create Client-Side Encryption Payment (Java Library)
 *
 * Merchants that require more stringent security protocols or do not want the additional overhead of managing their PCI
 * compliance, may decide to implement Client-Side Encryption (CSE). This is particularly useful for Mobile payment
 * flows where only cards are being offered, as it may result in faster load times and an overall improvement to the
 * shopper flow. The Adyen Hosted Payment Page (HPP) provides the most comprehensive level of PCI compliancy and you do
 * not have any PCI obligations. Using CSE reduces your PCI scope when compared to implementing the API without
 * encryption.
 *
 * If you would like to implement CSE, please provide the completed PCI Self Assessment Questionnaire (SAQ) A to the
 * Adyen Support Team (support@adyen.com). The form can be found here:
 * https://www.pcisecuritystandards.org/security_standards/documents.php?category=saqs
 *
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 *
 * @link /2.API/Library/CreatePaymentCSE
 * @author Created by Adyen - Payments Made Easy
 */

public class CreatePaymentCSE {

    public void doCSEPayment() throws Exception {

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
        Payment payment = new Payment(client);

        /**
         * A payment can be submitted to the authorise method of the PaymentResult Class with a PaymentRequest Object,
         * containing the following setters:
         *
         * - setMerchantAccount(merchantAccount)            : The merchant account for which you want to process the payment
         * - setAmountData(
         *                 String amount,                   : The transaction amount.
         *                 String currency                  : The three character ISO currency code.
         *                )
         * - setReference(String reference)                 : Your reference for this payment.
         * - setShopperIP(String shopperIP)                 : The shopper's IP address. (recommended)
         * - setShopperEmail(String shopperEmail)           : The shopper's email address. (recommended)
         * - setShopperReference(String shopperReference)   : An ID that uniquely identifes the shopper, such as a customer id. (recommended)
         * - setFraudOffset(Integer fraudOffset)            : An integer that is added to the normal fraud score. (optional)
         * - setCSEToken(String cseToken)                   : The encrypted card catched by the POST variables.
         */
        // Create new Payment Request
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setMerchantAccount("YourMerchantAccount");
        paymentRequest.setReference("YourReference");

        // Set Amount
        paymentRequest.setAmountData("123", "EUR");

        // Set the CSE token
        paymentRequest.setCSEToken("YourCSEToken");

        // Authorise the Payment Request
        PaymentResult paymentResult = payment.authorise(paymentRequest);

        /**
         * If the payment passes validation a risk analysis will be done and, depending on the outcome, an authorisation
         * will be attempted. You receive a payment response with the following getters:
         *
         * <pre>
         * - getPspReference()    : Adyen's unique reference that is associated with the payment.
         * - getResultCode()      : The result of the payment. Possible values: Authorised, Refused, Error or Received.
         * - getAuthCode()        : The authorisation code if the payment was successful. Blank otherwise.
         * - getRefusalReason()   : Adyen's mapped refusal reason, populated if the payment was refused.
         * </pre>
         */

        System.out.println("Payment Result:");
        System.out.println("- pspReference: " + paymentResult.getPspReference());
        System.out.println("- resultCode: " + paymentResult.getResultCode());
        System.out.println("- authCode: " + paymentResult.getAuthCode());
        System.out.println("- refusalReason: " + paymentResult.getRefusalReason());
    }

}