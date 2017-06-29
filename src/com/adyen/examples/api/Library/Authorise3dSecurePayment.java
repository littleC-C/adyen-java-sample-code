package com.adyen.examples.api.Library;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.service.Payment;
import com.adyen.model.PaymentRequest3d;
import com.adyen.model.BrowserInfo;
import com.adyen.model.Amount;

/**
 * Authorise 3D Secure payment (Java Library)
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
 * This example demonstrates the second API call to complete the payment using Java Library.
 * See the API Manual for a full explanation of the steps required to process 3D Secure payments.
 *
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 *
 * @link /2.API/Library/Authorise3dSecurePayment
 * @author Created by Adyen - Payments Made Easy
 */
public class Authorise3dSecurePayment {

    public void authorise3dPayment() throws Exception{

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

        // Create new 3d Payment Request
        PaymentRequest3d paymentRequest3d = new PaymentRequest3d();

        // Set Browser Info
        BrowserInfo browserInfo = new BrowserInfo();
        browserInfo.setUserAgent("YourUserAgent");
        browserInfo.setAcceptHeader("YourAcceptHeader");

         /**
         * After the shopper's identity is authenticated by the issuer, they will be returned to your
         * site by sending an HTTP POST request to the TermUrl containing the MD parameter and a new
         * parameter called PaRes (see API manual). These will be needed to complete the payment.
         *
         * To complete the payment, a PaymentRequest3d should be submitted to the authorise3D method
         * of the PaymentResult Class. The PaymentRequest3d should contain the following setters:
         *
         * <pre>
         * - setMerchantAccount(String merchantAccount)             : This should be the same as the Merchant Account used in the original authorise request.
         * - setBrowserInfo(BrowserInfo browserInfo)                : It is safe to use the values from the original authorise request, as they
         *                                                          are unlikely to change during the course of a payment.
         * - set3DRequestData(
         *                      String md,                          : The value of the MD parameter received from the issuer.
         *                      String paResponse                   : The value of the PaRes parameter received from the issuer.
         *                   )
         * - setShopperIP(String shopperIP)                         : The IP address of the shopper. We recommend that you provide this data, as
         *                                                          it is used in a number of risk checks, for example, the number of payment
         *                                                          attempts and location based checks.
         * </pre>
         */
        // Set 3d Payment Request
        paymentRequest3d.setMerchantAccount("YourMerchantAccount");
        paymentRequest3d.setBrowserInfo(browserInfo);
        paymentRequest3d.set3DRequestData("YourMD","YourPaResponse");
        paymentRequest3d.setShopperIP("1.2.3.4");
        PaymentResult paymentResult = payment.authorise3D(paymentRequest3d);

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
