/*
 * Copyright (C) 2014 Andrei Varabyeu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.avarabyeu.restendpoint.http;

import com.google.common.base.Strings;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * IO Helper. Added to avoid dependency to similar Apache commons-io library
 *
 * @author Andrei Varabyeu
 */
public class IOUtils {

    /**
     * Closes Resource without throwing any errors
     *
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    /**
     * Check whether provided url string is a valid java {@link java.net.URL}
     *
     * @param url
     * @return
     */
    public static boolean isValidUrl(String url) {
        try {
            if (Strings.isNullOrEmpty(url)) {
                return false;
            }
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Loads keystore
     *
     * @param keyStore
     * @param password
     * @return
     */
    public static KeyStore loadKeyStore(InputStream keyStore, String password) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        try {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(keyStore, password.toCharArray());
            return trustStore;
        } finally {
            IOUtils.closeQuietly(keyStore);
        }
    }
}
