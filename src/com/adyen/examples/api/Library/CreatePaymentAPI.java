package com.adyen.examples.api.Library;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentResult;
import com.adyen.service.Payment;

/**
 * Create Payment through the API (Java Library)
 *
 * Payments can be created through our API, however this is only possible if you are PCI Compliant. Java Library payments
 * are submitted using the authorise method. We will explain a simple credit card submission.
 *
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 *
 * @link /2.API/Library/CreatePaymentAPI
 * @author Created by Adyen - Payments Made Easy
 */

public class CreatePaymentAPI {

    public void doPayment() throws Exception{

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

        // Set Amount
        paymentRequest.setAmountData("123", "EUR");

        // Set Card
        paymentRequest.setCardData("5136333333333335", "John Doe", "08", "2018", "737");

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
