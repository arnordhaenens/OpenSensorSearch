/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sor.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sor.OwsExceptionReport;
import org.n52.sor.OwsExceptionReport.ExceptionCode;
import org.n52.sor.PropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A simple client to connect to a SOR. It sends the given request string to the url defined
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class Client {

    private static Logger log = LoggerFactory.getLogger(Client.class);

    private static final String GET_METHOD = "GET";

    private static final String POST_METHOD = "POST";

    /**
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws HttpException
     */
    private static XmlObject doSend(String request, String requestMethod) throws UnsupportedEncodingException,
            IOException {
        if (log.isDebugEnabled())
            log.debug("Sending request (first 100 characters): "
                    + request.substring(0, Math.min(request.length(), 100)));

        PropertiesManager pm = PropertiesManager.getInstance();

        // create and set up HttpClient
        HttpClient client = new DefaultHttpClient();

        HttpRequestBase method = null;
        if (requestMethod.equals(GET_METHOD)) {
            String sorURL = pm.getServiceEndpointGet();
            if (log.isDebugEnabled())
                log.debug("Client connecting via GET to " + sorURL);

            HttpGet get = new HttpGet(request);
            method = get;
        }
        else if (requestMethod.equals(POST_METHOD)) {
            String sorURL = pm.getServiceEndpointPost();
            if (log.isDebugEnabled())
                log.debug("Client connecting via POST to " + sorURL);
            HttpPost postMethod = new HttpPost(sorURL.toString());

            postMethod.setEntity(new StringEntity(request,
                                                  PropertiesManager.getInstance().getClientRequestContentType(),
                                                  PropertiesManager.getInstance().getClientRequestEncoding()));

            method = postMethod;
        }
        else {
            throw new IllegalArgumentException("requestMethod not supported!");
        }

        HttpResponse httpResponse = client.execute(method);

        XmlObject response = null;
        try {
            response = XmlObject.Factory.parse(httpResponse.getEntity().getContent());
        }
        catch (XmlException e) {
            log.error("Error parsing response.", e);
        }
        return response;
    }

    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws HttpException
     * @throws IOException
     */
    public static String sendGetRequest(String request) throws UnsupportedEncodingException, HttpException, IOException {
        if (request.isEmpty()) {
            return "The request is empty!";
        }
        return xSendGetRequest(request).xmlText();
    }

    public static String sendPostRequest(String request) throws IOException, OwsExceptionReport {
        if (request.isEmpty()) {
            return "The request is empty!";
        }
        XmlObject response = doSend(request, POST_METHOD);
        if (response != null)
            return response.toString();

        throw new OwsExceptionReport(ExceptionCode.NoApplicableCode, "sendPostRequest", "Sent request returned null: "
                + request);
    }

    /**
     * 
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws HttpException
     * @throws IOException
     */
    public static XmlObject xSendGetRequest(String request) throws UnsupportedEncodingException,
            HttpException,
            IOException {
        if (log.isDebugEnabled())
            log.debug("Sending request: " + request);
        XmlObject response = doSend(request, GET_METHOD);
        return response;
    }

    public static XmlObject xSendPostRequest(XmlObject request) throws IOException {
        if (log.isDebugEnabled())
            log.debug("Sending request: " + request);
        XmlObject response = doSend(request.xmlText(), POST_METHOD);
        return response;
    }

}