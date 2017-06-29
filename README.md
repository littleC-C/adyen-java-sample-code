Adyen Java Integration
==============
The code examples in this repository help you integrate with the Adyen platform using Java. Please go through the code examples and read the documentation in the files itself. Each code example requires you to change some parameters to connect to your Adyen account, such as merchant account and skincode.
## Java API Library
We have made a library available that contains all of these APIs to make the integration easier. The Library is open-source and available [here](https://github.com/Adyen/adyen-java-api-library).
In the section 'Library' you can see the code examples on how to use this library.
## Examples
```
1.HPP (Hosted Payment Page)
  - CreatePaymentOnHpp             : Simple form creating a payment on our HPP
  - CreatePaymentOnHppAdvanced     : Advanced form creating a payment on our HPP
  - CreatePaymentUrl               : Create payment URL on our HPP
2.API
  - Library
    - Authorise3dSecurePayment     : Authorise a 3D Secure payment using Java Library
    - Create3dSecurePayment        : Create a 3D Secure payment using Java Library
    - CreatePaymentAPI             : Create a payment via our API using Java Library
    - CreatePaymentCSE             : Create a Client-Side Encrypted payment using Java Library
  - JSON
    - Authorise3dSecurePayment     : Authorise a 3D Secure payment using JSON
    - Create3dSecurePayment        : Create a 3D Secure payment using JSON
    - CreatePaymentAPI             : Create a payment via our API using JSON
    - CreatePaymentCSE             : Create a Client-Side Encrypted payment using JSON
  - Soap
    - Authorise3dSecurePayment     : Authorise a 3D Secure payment using SOAP
    - Create3dSecurePayment        : Create a 3D Secure payment using SOAP
    - CreatePaymentAPI             : Create a payment via our API using SOAP
    - CreatePaymentCSE             : Create a Client-Side Encrypted payment using SOAP
3.Notifications
  - HttpPost
    - NotificationServer           : Receive our notifications using HTTP Post
  - JSON
    - NotificationServer           : Receive our notifications using JSON
4.Modifications
  - Library
    - CancelOrRefundPayment        : Cancel or refund a payment using Java Library
    - CancelPayment                : Cancel a payment using Java Library
    - CapturePayment               : Capture a payment using Java Library
    - RefundPayment                : Request a refund using Java Library
  - JSON
    - CancelOrRefundPayment        : Cancel or refund a payment using JSON
    - CancelPayment                : Cancel a payment using JSON
    - CapturePayment               : Capture a payment using JSON
    - RefundPayment                : Request a refund using JSON
  - Soap
    - CancelOrRefundPayment        : Cancel or refund a payment using SOAP
    - CancelPayment                : Cancel a payment using SOAP
    - CapturePayment               : Capture a payment using SOAP
    - RefundPayment                : Request a refund using SOAP
5.Recurring
  - Library
    - CreateRecurringPayment       : Create a recurring payment using Java Library
    - DisableRecurringContract     : Disable a recurring contract using Java Library
    - RetrieveRecurringContract    : Retrieve a recurring contract using Java Library
  - JSON
    - CreateRecurringPayment       : Create a recurring payment using JSON
    - DisableRecurringContract     : Disable a recurring contract using JSON
    - RetrieveRecurringContract    : Retrieve a recurring contract using JSON
  - Soap
    - CreateRecurringPayment       : Create a recurring payment using SOAP
    - DisableRecurringContract     : Disable a recurring contract using SOAP
    - RetrieveRecurringContract    : Retrieve a recurring contract using SOAP
6.PaymentMethods
  - Library
    - GetPaymentMethods            : Get payment methods available for merchant account using Java Library
  - JSON
    - GetPaymentMethods            : Get payment methods available for merchant account using JSON
7.CustomFields
  - HttpPost
    - CustomFieldsServer           : Custom fields service using HTTP Post
8.Payout
  - Soap
    - ConfirmPayoutRequest         : Confirm payout request using SOAP
    - DeclinePayoutRequest         : Decline payout request using SOAP
    - StorePayoutDetails           : Store payout details using SOAP
    - StorePayoutDetailsAndSubmit  : Store payout details and submit payout request using SOAP
    - SubmitPayoutRequest          : Submit payout request using SOAP
```

## Code structure
```
src
  - com.adyen.examples.hpp                : Java implementation of 1.HPP
  - com.adyen.examples.api                : Java implementation of 2.API
  - com.adyen.examples.notifications      : Java implementation of 3.Notifications
  - com.adyen.examples.modifications      : Java implementation of 4.Modifications
  - com.adyen.examples.recurring          : Java implementation of 5.Recurring
  - com.adyen.examples.paymentmethods     : Java implementation of 6.PaymentMethods
  - com.adyen.examples.openinvoice        : Java implementation of 7.OpenInvoice
  - com.adyen.examples.customfields       : Java implementation of 8.CustomFields
  - com.adyen.examples.payout             : Java implementation of 9.Payout
tools
  - wsdl2java.xml                         : Ant buildfile for generating SOAP classes
WebContent
  - 1.HPP
    - create-payment-on-hpp.jsp           : JSP template file for simple HPP
    - create-payment-on-hpp-advanced.jsp  : JSP template file for advanced HPP
    - create-payment-url.jsp              : JSP template file for payment URL
  - 2.API
    - create-payment-cse.jsp              : JSP template file for Client Side Encryption
    - js
      - adyen.encrypt.min.js              : JavaScript file required for encrypting card data
  - WEB-INF
    - lib/                                : Java libraries (JARs) used in the servlets
    - web.xml                             : Deployment descriptor
  index.jsp                               : Dynamic index with links to all examples
```

## Manuals
The code examples are based on our Integration manual and the API manual which provides rich information on how our platform works. Please find our manuals on the Developers section at www.adyen.com.

## Support
If you do have any suggestions or questions regarding the code examples please send an e-mail to support@adyen.com.
