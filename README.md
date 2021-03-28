MQTT Client using Paho Client

Implementation
1. Connecting to AWS IoT Core Custom Domain
2. Device Certificate Authentication
3. Device Certificate is complete chain certificate
    1. This is required for AWS JITP to kicks in when first time connecting to MQTT Broker.
    2. Subsequent connection can be with full chain or only leaf certificate

Certificate and Key Files
1. ca.crt - AWS IoT core custom domain CA (Private CA root certificate)
   1. Added in trustore-test.jks
2. iot.aloksingh.info.crt - AWS IoT core custom domain certificate 
   1. Added in truststore-test.jks - this is not needed, but without this server trust is not being established. To be analysed.
2. rootCA_iot.crt - device certificate CA
3. deviceCert.crt - device certificate (leaf certificate)   
4. chain-deviceCert-rootCA_iot.crt - device chain certificate
   1. Added in keystore.jks
5. deviceCert.key - device key
   1. Added in keystore.jks
    

**Trust Store**

``
keytool -import -alias iot-domain-ca -file ca.crt -storetype JKS -keystore truststore-test.jks
``

``
keytool -import -alias iot-domain -file ~/cert/IoT/new/us-east-1/domainCert/iot.aloksingh.info.crt -storetype JKS -keystore truststore-test.jks
``

**Key Store**

``
cat deviceCert.crt rootCA_iot.crt >chain-deviceCert-rootCA_iot.crt
``
``
openssl pkcs12 -export -in chain-deviceCert-rootCA_iot.crt -inkey deviceCert.key -name device > keystore.p12
``

``
keytool -importkeystore -srckeystore keystore.p12 -destkeystore keystore.jks -srcstoretype pkcs12 -alias device 
``