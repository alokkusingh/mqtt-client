
**Trust Store**

``
keytool -import -alias iot-domain-ca -file ca.crt -storetype JKS -keystore truststore-test.jks
``

``
keytool -import -alias iot-domain -file ~/cert/IoT/new/us-east-1/domainCert/iot.aloksingh.info.crt -storetype JKS -keystore truststore-test.jks
``

**Key Store**

``
openssl pkcs12 -export -in chain-deviceCert-rootCA_iot.crt -inkey deviceCert.key -name device > keystore.p12
``

``
keytool -importkeystore -srckeystore keystore.p12 -destkeystore keystore.jks -srcstoretype pkcs12 -alias device 
``