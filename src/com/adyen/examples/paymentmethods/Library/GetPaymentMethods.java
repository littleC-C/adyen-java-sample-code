package com.adyen.examples.paymentmethods.Library;
import com.adyen.Client;
import com.adyen.Config;
import com.adyen.enums.Environment;
import com.adyen.model.hpp.DirectoryLookupRequest;
import com.adyen.model.hpp.PaymentMethod;
import com.adyen.service.HostedPaymentPages;
import java.util.List;

/**
 * Get Payment Methods(Java Library)
 *
 * You may decide to skip the Adyen payment method selection page so that the shopper starts directly on the payment
 * details entry page. This is done by calling details.shtml instead of pay.shtml/select.shtml. An additional parameter,
 * brandCode and where applicable issuerId, should be provided with the selected payment method listed. Please refer to
 * section 2.9 of the Integration Manual for more details.
 *
 * The directory service can also be used to determine which payment methods are available for the shopper on your
 * Merchant Account. This is done by calling directory.shtml, with a normal payment request. Please note that the
 * countryCode field is mandatory to receive back the correct payment methods.
 *
 * This file provides a code example showing how to retrieve the payment methods enabled for the specified merchant
 * account.
 *
 * @link /6.PaymentMethods/Library/GetPaymentMethods
 * @author Created by Adyen - Payments Made Easy
 */
public class GetPaymentMethods {

    public void getPaymentMethods() throws Exception{

        /**
         * Directory Service settings
         *
         * Client settings
         * - setEnvironment(Environment environment)        : The Environment you are using (Environment.TEST/Environment.LIVE)
         */
        // Create new Client
        Client client = new Client();
        client.setEnvironment(Environment.TEST);

        /**
         * The following fields are required for the directory service.
         *
         * DirectoryLookupRequest Settings
         * - setMerchantAccount(String merchantAccount)     : The merchant account used to access the Adyen directory service
         * - setHmacKey(String hmacKey)                     : shared secret key used to encrypt the signature
         * - setSkinCode(String skinCode)                   : your skin code
         * - setCountryCode(String countryCode)             : The three character ISO currency code.
         * - setPaymentAmount(String amount)                : The transaction amount.
         * - setCurrencyCode(String currencyCode)           : The three character ISO currency code.
         * - setMerchantReference(String merchantReference) : Your merchant reference.
         *
         * HMAC key can be set up: Adyen CA >> Skins >> Choose your Skin >> Edit Tab >> Edit HMAC key for Test & Live.
         */
        // Create a new Directory Lookup Request
        DirectoryLookupRequest directoryLookupRequest = new DirectoryLookupRequest();
        directoryLookupRequest.setMerchantAccount("YourMerchantAccount");
        directoryLookupRequest.setHmacKey("YourHmacSecretKey");
        directoryLookupRequest.setSkinCode("YourSkinCode");
        directoryLookupRequest.setCountryCode("NL");
        directoryLookupRequest.setCurrencyCode("EUR");
        directoryLookupRequest.setPaymentAmount("100");
        directoryLookupRequest.setMerchantReference("YourMerchantReference");

        /**
         * The result is a PaymentMethod List containing the available payment methods for the merchant account.
         */
        // Retrieve Payment Methods and store them in a List
        HostedPaymentPages hostedPaymentPages = new HostedPaymentPages(client);
        List<PaymentMethod> paymentMethods = hostedPaymentPages.getPaymentMethods(directoryLookupRequest);

        System.out.println("Payment Methods:");
        System.out.println(paymentMethods);
    }

}
