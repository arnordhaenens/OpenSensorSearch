/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sir.listener;

import org.n52.sir.RequestOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * http://code.google.com/p/google-guice/wiki/Multibindings
 * 
 * @author Daniel
 * 
 */
public class ListenerModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(ListenerModule.class);

    @Override
    protected void configure() {
        bind(RequestOperator.class);

        Multibinder<ISirRequestListener> uriBinder = Multibinder.newSetBinder(binder(), ISirRequestListener.class);

        uriBinder.addBinding().to(ConnectToCatalogListener.class);
        uriBinder.addBinding().to(DeleteSensorInfoListener.class);
        uriBinder.addBinding().to(DescribeSensorListener.class);
        uriBinder.addBinding().to(DisconnectFromCatalogListener.class);
        uriBinder.addBinding().to(GetCapabilitiesListener.class);
        uriBinder.addBinding().to(GetSensorStatusListener.class);
        uriBinder.addBinding().to(HarvestServiceListener.class);
        uriBinder.addBinding().to(InsertSensorInfoListener.class);
        uriBinder.addBinding().to(InsertSensorStatusListener.class);
        uriBinder.addBinding().to(SearchSensorListener.class);
        uriBinder.addBinding().to(CancelSensorStatusSubscriptionListener.class);
        uriBinder.addBinding().to(RenewSensorStatusSubscriptionListener.class);
        uriBinder.addBinding().to(SubscribeSensorStatusListener.class);
        uriBinder.addBinding().to(UpdateSensorDescriptionListener.class);

        log.debug("Configured {}", this);
    }

}
