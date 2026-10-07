package lab.auth;

final class TlsSettings {
    private TlsSettings() { }

    static void configureServerFromEnvironment() {
        setIfPresent("PRINTSERVER_KEYSTORE", "javax.net.ssl.keyStore");
        setIfPresent("PRINTSERVER_KEYSTORE_PASSWORD", "javax.net.ssl.keyStorePassword");
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
    }

    static void configureClientFromEnvironment() {
        setIfPresent("PRINTSERVER_TRUSTSTORE", "javax.net.ssl.trustStore");
        setIfPresent("PRINTSERVER_TRUSTSTORE_PASSWORD", "javax.net.ssl.trustStorePassword");
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
    }

    private static void setIfPresent(String environmentName, String propertyName) {
        String value = System.getenv(environmentName);
        if (value != null && !value.isEmpty()) {
            System.setProperty(propertyName, value);
        }
    }
}
