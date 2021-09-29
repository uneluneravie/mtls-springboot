CLIENT_KEYSTORE_DIR=../client/src/main/resources
SERVER_KEYSTORE_DIR=../server/src/main/resources
CLIENT_KEYSTORE=$CLIENT_KEYSTORE_DIR/client-nonprod.jks
SERVER_KEYSTORE=$SERVER_KEYSTORE_DIR/server-nonprod.jks
JAVA_CA_CERTS=$JAVA_HOME/jre/lib/security/cacerts

CLIENT_KEY_ALIAS=client
SERVER_KEY_ALIAS=server

# Generate a client and server RSA 2048 key pair
keytool -genkeypair -alias $CLIENT_KEY_ALIAS -keyalg RSA -keysize 2048 -dname "CN=Client,OU=Client,O=PlumStep,L=San Francisco,S=CA,C=U" -keypass changeme -keystore $CLIENT_KEYSTORE -storepass changeme
keytool -genkeypair -alias $SERVER_KEY_ALIAS -keyalg RSA -keysize 2048 -dname "CN=Server,OU=Server,O=PlumStep,L=San Francisco,S=CA,C=U" -keypass changeme -keystore $SERVER_KEYSTORE -storepass changeme

# Export public certificates for both the client and server
keytool -exportcert -alias $CLIENT_KEY_ALIAS -file $CLIENT_KEY_ALIAS-public.cer -keystore $CLIENT_KEYSTORE -storepass changeme
keytool -exportcert -alias $SERVER_KEY_ALIAS -file $SERVER_KEY_ALIAS-public.cer -keystore $SERVER_KEYSTORE -storepass changeme

# Import the client and server public certificates into each others keystore
keytool -importcert -keystore $CLIENT_KEYSTORE -alias $SERVER_KEY_ALIAS-public-cert -file $SERVER_KEY_ALIAS-public.cer -storepass changeme -noprompt
keytool -importcert -keystore $SERVER_KEYSTORE -alias $CLIENT_KEY_ALIAS-public-cert -file $CLIENT_KEY_ALIAS-public.cer -storepass changeme -noprompt