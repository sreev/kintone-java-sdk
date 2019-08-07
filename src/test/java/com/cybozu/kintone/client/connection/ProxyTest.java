package com.cybozu.kintone.client.connection;

import com.cybozu.kintone.client.TestConstants;
import com.cybozu.kintone.client.authentication.Auth;
import com.cybozu.kintone.client.exception.KintoneAPIException;
import com.cybozu.kintone.client.module.app.App;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class ProxyTest {

    private int APP_ID;
    private Auth auth;

    public static void clearAuthCache() {
        try {
            // this is evil, but there is no official way to clear the authentication cache...
            // use of Reflection API so no sun classes are imported
            Class<?> authCacheValueClass = Class.forName("sun.net.www.protocol.http.AuthCacheValue");
            Class<?> authCacheClass = Class.forName("sun.net.www.protocol.http.AuthCache");
            Class<?> authCacheImplClass = Class.forName("sun.net.www.protocol.http.AuthCacheImpl");
            Constructor<?> authCacheImplConstructor = authCacheImplClass.getConstructor();
            Method setAuthCacheMethod = authCacheValueClass.getMethod("setAuthCache", authCacheClass);
            setAuthCacheMethod.invoke(null, authCacheImplConstructor.newInstance());
        } catch (Throwable t) {
        }
    }

    @Before
    public void setup() {
        clearAuthCache();
        Auth auth = new Auth();
        auth.setPasswordAuth(TestConstants.USERNAME, TestConstants.PASSWORD);
        this.auth = auth;
    }

    @Test(expected = KintoneAPIException.class)
    public void checkAppWithWrongAccount() throws KintoneAPIException {
        Connection connection2 = new Connection(TestConstants.DOMAIN, auth);
        connection2.setProxy(TestConstants.PROXY_HOST, TestConstants.PROXY_PORT, "xxx", "xxx");

        App app2 = new App(connection2);
        app2.getApp(APP_ID).getAppId();
    }


    @Test(expected = KintoneAPIException.class)
    public void checkAppWithEmptyAccount() throws KintoneAPIException {
        Connection connection = new Connection(TestConstants.DOMAIN, auth);
        connection.setProxy(TestConstants.PROXY_HOST, TestConstants.PROXY_PORT, "", "");

        App app = new App(connection);
        app.getApp(APP_ID).getAppId();

    }

    @Test(expected = KintoneAPIException.class)
    public void checkAppWithoutAccount() throws KintoneAPIException {
        Connection connection = new Connection(TestConstants.DOMAIN, auth);
        connection.setProxy(TestConstants.PROXY_HOST, TestConstants.PROXY_PORT);
        App app = new App(connection);
        app.getApp(APP_ID).getAppId();
    }

    @Test(expected = KintoneAPIException.class)
    public void checkAppWithHttpServer() throws KintoneAPIException {
        Connection connection = new Connection(TestConstants.DOMAIN, auth);
        connection.setProxy(TestConstants.PROXY_HOST_HTTP, TestConstants.PROXY_PORT_HTTP, TestConstants.PROXY_USERNAME, TestConstants.PROXY_PASSWORD);
        App app = new App(connection);

        app.getApp(APP_ID).getAppId();
    }

    @Test
    public void checkAppWithRightAccount() throws KintoneAPIException {
        Connection connection = new Connection(TestConstants.DOMAIN, auth);
        connection.setProxy(TestConstants.PROXY_HOST, TestConstants.PROXY_PORT, TestConstants.PROXY_USERNAME, TestConstants.PROXY_PASSWORD);
        App app = new App(connection);

        Assert.assertNotNull(app.getApp(APP_ID).getAppId());
    }

    @Test
    public void testProxyHttpNoAuth() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("cybozu", "cybozu");
        Connection connection = new Connection("qasd-vuong.cybozu-dev.com", auth);
        connection.setProxy("localhost", 3128);
        App app = new App(connection);

        Assert.assertNotNull(app.getApp(2).getAppId());
    }

    @Test
    public void testProxyHttpAuth() throws KintoneAPIException {
        Auth auth = new Auth();
        auth.setPasswordAuth("cybozu", "cybozu");
        Connection connection = new Connection("qasd-vuong.cybozu-dev.com", auth);
        connection.setProxy("localhost", 13128, "cybozu", "cybozu");
        App app = new App(connection);

        Assert.assertNotNull(app.getApp(2).getAppId());
    }

    @Test
    public void testProxyHttpsNoAuth() throws KintoneAPIException, KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslcontext = SSLContext.getInstance("SSL");
        sslcontext.init(new KeyManager[0], new TrustManager[] {new TrustAnyTrustManager()}, new SecureRandom());
        SSLContext.setDefault(sslcontext);

        Auth auth = new Auth();
        auth.setPasswordAuth("cybozu", "cybozu");
        Connection connection = new Connection("qasd-vuong.cybozu-dev.com", auth);
        connection.setHttpsProxy("localhost", 443);
        App app = new App(connection);

        Assert.assertNotNull(app.getApp(2).getAppId());
    }

    @Test
    public void testProxyHttpsAuth() throws KintoneAPIException, KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslcontext = SSLContext.getInstance("SSL");
        sslcontext.init(new KeyManager[0], new TrustManager[] {new TrustAnyTrustManager()}, new SecureRandom());
        SSLContext.setDefault(sslcontext);

        Auth auth = new Auth();
        auth.setPasswordAuth("cybozu", "cybozu");
        Connection connection = new Connection("qasd-vuong.cybozu-dev.com", auth);
        connection.setHttpsProxy("localhost", 1443, "cybozu", "cybozu");
        App app = new App(connection);

        Assert.assertNotNull(app.getApp(2).getAppId());
    }
}
