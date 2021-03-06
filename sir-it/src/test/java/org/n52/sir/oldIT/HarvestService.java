/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sir.oldIT;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.oss.ui.beans.HarvestServiceBean;
import org.x52North.sir.x032.HarvestServiceRequestDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class HarvestService extends SirTest {

    private String serviceURL = "http://v-swe.uni-muenster.de:8080/WeatherSOS/sos";

    private String serviceType = "SOS";

    @Test
    public void testPost() throws Exception {
        HarvestServiceBean hsb = new HarvestServiceBean(this.serviceURL, this.serviceType);

        hsb.buildRequest();

        String response = client.sendPostRequest(hsb.getRequestString());

        // parse and validate response
        HarvestServiceResponseDocument cd = HarvestServiceResponseDocument.Factory.parse(response);
        assertTrue(cd.validate());
    }

    @Test
    public void testPostExample() throws Exception {
        File f = getPostExampleFile("HarvestService_WeatherSOS.xml");
        HarvestServiceRequestDocument hsrd = HarvestServiceRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(hsrd);

        // parse and validate response
        HarvestServiceResponseDocument cd = HarvestServiceResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(cd.validate());
    }

}