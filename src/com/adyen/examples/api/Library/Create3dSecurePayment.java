package com.adyen.examples.api.Library;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentResult;
import com.adyen.service.Payment;

/**
 * Create 3D Secure payment (Java Library)
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
 * This example demonstrates the initial API call to create the 3D secure payment using Java Library,
 * and shows the redirection the the card issuer.
 * See the API Manual for a full explanation of the steps required to process 3D Secure payments.
 *
 * @link /2.API/Library/Create3dSecurePayment
 * @author Created by Adyen - Payments Made Easy
 */

public class Create3dSecurePayment {

    public void do3dPayment() throws Exception{

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
         * <pre>
         * - setMerchantAccount(merchantAccount)                    : The merchant account for which you want to process the payment
         * - setAmountData(
         *                      String amount,                      : The transaction amount.
         *                      String currency                     : The three character ISO currency code.
         *                )
         * - setReference(String reference)                         : Your reference for this payment.
         * - setShopperIP(String shopperIP)                         : The shopper's IP address. (recommended)
         * - setShopperEmail(String shopperEmail)                   : The shopper's email address. (recommended)
         * - setShopperReference(String shopperReference)           : An ID that uniquely identifes the shopper, such as a customer id. (recommended)
         * - setFraudOffset(Integer fraudOffset)                    : An integer that is added to the normal fraud score. (optional)
         * - setCardData(
         *                      String cardNumber,                  : The card number.
         *                      String cardHolder,                  : The card holder's name, as embossed on the card.
         *                      String expiryMonth,                 : The expiration date's month written as a 2-digit string,
         *                                                          padded with 0 if required (e.g. 03 or 12).
         *                      String expiryYear,                  : The expiration date's year written as in full (e.g. 2016).
         *                      String cvc                          : The card validation code, which is the CVC2 (MasterCard),
         *                                                          CVV2 (Visa) or CID (American Express).
         *              )
         * - setBrowserInfoData(
         *                      String userAgent,                   : The user agent string of the shopper's browser (required).
         *                      String acceptHeader                 : The accept header string of the shopper's browser (required).
         *                      )
         * - setBillingAddress(Address billingAddress) (recommended)
         *         - setStreet(String street)                       : The street name.
         *         - setHouseNumberOrName(String houseNumberOrName) : The house number (or name).
         *         - setCity(String city)                           : The city.
         *         - setPostalCode(String postalCode)               : The postal/zip code.
         *         - setStateOrProvince(String stateOrProvince)     : The state or province.
         *         - setCountry(String country)                     : The country in ISO 3166-1 alpha-2 format (e.g. NL).
         * </pre>
         */
        // Create new Payment Request
        PaymentRequest paymentRequest= new PaymentRequest();
        paymentRequest.setMerchantAccount("YourMerchantAccount");
        paymentRequest.setReference("YourReference");
        paymentRequest.setBrowserInfoData("YourUserAgent", "YourAcceptHeader");

        // Set Amount
        paymentRequest.setAmountData("123", "EUR");

        // Set 3dCard
        paymentRequest.setCardData("6731012345678906", "John Doe", "08", "2018", "737");

        // Authorise the 3dPayment Request
        PaymentResult paymentResult = payment.authorise(paymentRequest);

        /**
         * Once your account is configured for 3-D Secure, the Adyen system performs a directory
         * inquiry to verify that the card is enrolled in the 3-D Secure programme.
         * If it is not enrolled, the PaymentResult is the same as a normal API authorisation.
         * If, however, it is enrolled, the PaymentResult contains these getters:
         *
         * - getPaRequest()     : The 3-D request data for the issuer.
         * - getMd()            : The payment session.
         * - getIssuerUrl()     : The URL to direct the shopper to.
         * - getResultCode()    : The resultCode will be RedirectShopper.
         *
         * The paRequest and md fields should be included in an HTML form, which needs to be submitted
         * using the HTTP POST method to the issuerUrl. You must also include a termUrl parameter
         * in this form, which contains the URL on your site that the shopper will be returned to
         * by the issuer after authentication. In this example we are redirecting to another example
         * which completes the 3D Secure payment.
         *
         * @see Authorise3dSecurePaymentJson.java
         *
         * We recommend that the form is "self-submitting" with a fallback in case javascript is disabled.
         * A sample form is implemented in the file below.
         *
         * @see WebContent/2.API/create-3d-secure-payment.jsp
         */

        System.out.println("3d Payment Request:");
        System.out.println("- paRequest: " + paymentResult.getPaRequest());
        System.out.println("- md: " + paymentResult.getMd());
        System.out.println("- issuerUrl: " + paymentResult.getIssuerUrl());
        System.out.println("- resultCode: " + paymentResult.getResultCode());

    }
}
