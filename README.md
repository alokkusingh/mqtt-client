MQTT Client using Paho Client

**Implementation**
1. Connecting to AWS IoT Core Custom Domain
2. Device Certificate Authentication
3. IoT Core broker policy was added to validate clientId against CN value in certificate subject
4. Device Certificate is complete chain certificate
    1. This is required for AWS JITP to kicks in when first time connecting to MQTT Broker.
    2. Subsequent connection can be with full chain or only leaf certificate
5. Client Online/Offline update
   1. Set LWT topic with a message "OFFLINE"
   2. On connect publish to the LWT topic "ONLINE"
   3. Before a disconnect publish to the LWT topic "OFFLINE"
   4. On heart-beat missing from client broker will publish to the LWT topic with LWT message (OFFLINE)

**Certificate and Key Files**
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

**Few Facts about AWS IoT Core**
1. Doesn't support Retained Message Flag - true
2. Doesn't support QoS 2 - once and exactly once

**Trust Store**

``
keytool -import -alias iot-domain-ca -file ca.crt -storetype JKS -keystore truststore-test.jks
``

``
keytool -import -alias iot-domain -file ~/cert/IoT/new/us-east-1/domainCert/iot.aloksingh.info.crt -storetype JKS -keystore truststore-test.jks
``

**Key Store**

Alok Device Certificates

``
cat deviceCert.crt rootCA_iot.crt >chain-deviceCert-rootCA_iot.crt
``
``
openssl pkcs12 -export -in chain-deviceCert-rootCA_iot.crt -inkey deviceCert.key -name device > keystore.p12
``

``
keytool -importkeystore -srckeystore keystore.p12 -destkeystore keystore.jks -srcstoretype pkcs12 -alias device 
``

Rachna Device Certificates

``
cat deviceCert-signedByRachna.crt rootCA_iot_rachna.crt >chain-deviceCert-rootCA_iot_rachna.crt
``
``
openssl pkcs12 -export -in chain-deviceCert-rootCA_iot_rachna.crt -inkey deviceCert-signedByRachna.key -name deviceRachna > keystoreRachna.p12
``

``
keytool -importkeystore -srckeystore keystoreRachna.p12 -destkeystore keystoreRachna.jks -srcstoretype pkcs12 -alias deviceRachna
``

**SSL Debug**

``
java -Djavax.net.debug=all -Djavax.net.ssl.trustStore=truststore-test.jks -Djavax.net.ssl.trustStorePassword=***** SSLPoke iot.aloksingh.info 8883
``
