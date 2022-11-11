// This file is licensed under the Elastic License 2.0. Copyright 2021-present, StarRocks Inc.

package com.starrocks.mysql.ssl;

import com.starrocks.StarRocksFE;
import com.starrocks.mysql.privilege.Auth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class SSLChannelImpClassLoader {
    private static final Logger LOG = LogManager.getLogger(Auth.class);
    public static final String SSL_CHANNEL_CLASS_NAME = "com.starrocks.plugins.ssl.SSLChannelImp";
    public static final String SSL_CHANNEL_JAR_PATH = StarRocksFE.STARROCKS_HOME_DIR + "/lib/starrocks-ssl-1.0.jar";

    private static Class<? extends SSLChannel> clazz;

    public static Class<? extends SSLChannel> loadSSLChannelImpClazz() {
        if (clazz != null) {
            return clazz;
        }

        File jarFile = new File(SSL_CHANNEL_JAR_PATH);
        if (!jarFile.exists()) {
            LOG.error("can not found jar file at {}", SSL_CHANNEL_JAR_PATH);
            return null;
        }
        try {
            ClassLoader loader = URLClassLoader.newInstance(new URL[] {jarFile.toURI().toURL()},
                    SSLChannelImpClassLoader.class.getClassLoader());
            clazz = Class.forName(SSL_CHANNEL_CLASS_NAME, true, loader).asSubclass(SSLChannel.class);
        } catch (Exception e) {
            LOG.error("load ssl channel class failed", e);
            return null;
        }
        return clazz;
    }
}