package com.adyen.examples.recurring.Library;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentResult;
import com.adyen.service.Payment;
import com.adyen.model.recurring.Recurring;

/**
 * Create Recurring Payment (Java Library)
 *
 * You can submit a recurring payment using a specific recurringDetails record or by using the last created
 * recurringDetails record. The request for the recurring payment is done using a paymentRequest. This file shows how a
 * recurring payment can be submitted using our Java Library.
 *
 * Please note: using our API requires a web service user. Set up your Webservice user:
 * Adyen CA >> Settings >> Users >> ws@Company. >> Generate Password >> Submit
 *
 * @link /5.Recurring/Library/CreateRecurringPayment
 * @author Created by Adyen - Payments Made Easy
 */

public class CreateRecurringPayment {

    public void recurringPayment() throws Exception{

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
         * A recurring payment can be submitted to the authorise method of the PaymentResult Class with a PaymentRequest Object,
         * containing the following setters:
         *
         * <pre>
         * - setSelectedRecurringDetailReference(String selectedRecurringDetailReference)            : The recurringDetailReference you want to use for this payment.
         *                                                                                           The value LATEST can be used to select the most recently used recurring detail.
         * - setRecurring(Recurring recurring)
         *     - setContract(Recurring.ContractEnum contract)     : This should be the same value as recurringContract in the payment where the recurring
         *                                                        contract was created. However if ONECLICK,RECURRING was specified initially then this
         *                                                        field can be either ONECLICK or RECURRING.
         * - setShopperInteraction(AbstractPaymentRequest.ShopperInteractionEnum shopperInteraction) : Set to ContAuth if the contract value is RECURRING, or Ecommerce if the contract
         *                                                                                           value is ONECLICK.
         *
         * - setMerchantAccount(String merchantAccount)           : The merchant account for which you want to process the payment.
         * - setAmountData(
         *                  String amount,                        : The transaction amount.
         *                  String currency                       : The three character ISO currency code.
         *                 )
         * - setReference(String reference)                       : Your reference for this payment.
         * - setShopperEmail(String shopperEmail)                 : The email address of the shopper. This does not have to match the email address
         *                                                        supplied with the initial payment since it may have changed in the mean time.
         * - setShopperReference(String shopperReference)         : The reference to the shopper. This shopperReference must be the same as the
         *                                                        shopperReference used in the initial payment.
         * - setShopperIP(String shopperIP)                       : The shopper's IP address. (recommended)
         * - setFraudOffset(Integer fraudOffset)                  : An integer that is added to the normal fraud score. (optional)
         * - setCard(Card card)
         *     - setCvc(String cvc)                               : The card validation code. (only required for OneClick card payments)
         * </pre>
         */
        // Create new Payment Request
        PaymentRequest paymentRequest= new PaymentRequest();
        paymentRequest.setReference("123456");
        paymentRequest.setMerchantAccount("YourMerchantAccount");
        paymentRequest.setShopperEmail("YourShopperEmail");

        // Set Amount
        paymentRequest.setAmountData("37", "EUR");

        // Set recurring contract
        Recurring modelRecurring = new Recurring();
        modelRecurring.setContract(com.adyen.model.recurring.Recurring.ContractEnum.RECURRING);
        paymentRequest.setRecurring(modelRecurring);
        paymentRequest.setShopperReference("YourShopperReference");
        paymentRequest.setShopperInteraction(AbstractPaymentRequest.ShopperInteractionEnum.CONTAUTH);
        paymentRequest.setSelectedRecurringDetailReference("LATEST");

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
