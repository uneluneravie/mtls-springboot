CLIENT_KEYSTORE=client-keystore
SERVER_KEYSTORE=server-keystore

CLIENT_KEY_ALIAS=client
SERVER_KEY_ALIAS=server

# Generate a client and server RSA 2048 key pair
keytool -genkeypair -alias $CLIENT_KEY_ALIAS -keyalg RSA -keysize 2048 -dname "CN=Client,OU=Client,O=PlumStep,L=San Francisco,S=CA,C=U" -keypass 123456 -keystore $CLIENT_KEYSTORE.jks -storepass 123456
keytool -genkeypair -alias $SERVER_KEY_ALIAS -keyalg RSA -keysize 2048 -dname "CN=Server,OU=Server,O=PlumStep,L=San Francisco,S=CA,C=U" -keypass 123456 -keystore $SERVER_KEYSTORE.jks -storepass 123456

# Export public certificates for both the client and server
keytool -exportcert -alias $CLIENT_KEY_ALIAS -file $CLIENT_KEY_ALIAS-public.cer -keystore $CLIENT_KEYSTORE.jks -storepass 123456
keytool -exportcert -alias $SERVER_KEY_ALIAS -file $SERVER_KEY_ALIAS-public.cer -keystore $SERVER_KEYSTORE.jks -storepass 123456

# Import the client and server public certificates into each others keystore
keytool -importcert -keystore $CLIENT_KEYSTORE.jks -alias $SERVER_KEY_ALIAS-public-cert -file $SERVER_KEY_ALIAS-public.cer -storepass 123456 -noprompt
keytool -importcert -keystore $SERVER_KEYSTORE.jks -alias $CLIENT_KEY_ALIAS-public-cert -file $CLIENT_KEY_ALIAS-public.cer -storepass 123456 -noprompt

# Convert .jks to .p12
keytool -importkeystore -srckeystore $CLIENT_KEYSTORE.jks -destkeystore $CLIENT_KEYSTORE.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass 123456 -deststorepass 123456
keytool -importkeystore -srckeystore $SERVER_KEYSTORE.jks -destkeystore $SERVER_KEYSTORE.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass 123456 -deststorepass 123456