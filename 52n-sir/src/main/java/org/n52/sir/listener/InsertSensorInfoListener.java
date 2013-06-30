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

package org.n52.sir.listener;

import java.util.Collection;
import java.util.Date;

import org.n52.sir.SirConfigurator;
import org.n52.sir.SirConstants;
import org.n52.sir.api.IdentifierGenerator;
import org.n52.sir.datastructure.SirInfoToBeInserted;
import org.n52.sir.datastructure.SirInfoToBeInserted_SensorDescription;
import org.n52.sir.datastructure.SirInfoToBeInserted_ServiceReference;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirSensorIDInSir;
import org.n52.sir.datastructure.SirServiceReference;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirInsertSensorInfoRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirInsertSensorInfoResponse;
import org.n52.sir.sml.SensorMLDecoder;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.IValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 * 
 */
public class InsertSensorInfoListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(InsertSensorInfoListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.InsertSensorInfo.name();

    private IInsertSensorInfoDAO insSensInfoDao;

    private IValidatorFactory validatorFactory;

    @Inject
    private IdentifierGenerator idGen;

    public InsertSensorInfoListener() throws OwsExceptionReport {
        SirConfigurator configurator = SirConfigurator.getInstance();

        IDAOFactory factory = configurator.getFactory();
        this.validatorFactory = configurator.getValidatorFactory();

        try {
            this.insSensInfoDao = factory.insertSensorInfoDAO();
        }
        catch (OwsExceptionReport se) {
            log.error("Error while creating the insertSensorInfoDAO", se);
            throw se;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ISirRequestListener#getOperationName()
     */
    @Override
    public String getOperationName() {
        return InsertSensorInfoListener.OPERATION_NAME;
    }

    /**
     * @param response
     * @param insertedSensors
     * @param sensorInfo
     * @param serviceInfos
     * @param sensor
     * @return
     * @throws OwsExceptionReport
     */
    private void insertSensor(SirInsertSensorInfoResponse response,
                              Collection<SirServiceReference> serviceRefs,
                              SirSensor sensor) throws OwsExceptionReport {
        if (sensor.getSensorMLDocument() != null) {
            IProfileValidator profileValidator = this.validatorFactory.getSensorMLProfile4DiscoveryValidator();
            boolean isValid = profileValidator.validate(sensor.getSensorMLDocument());
            if (isValid) {
                String id = this.idGen.generate();
                sensor.setSensorIDInSIR(id);
                // TODO add test if id is already taken

                String sensorIdInSir = this.insSensInfoDao.insertSensor(sensor);
                if (sensorIdInSir != null) {
                    response.setNumberOfNewSensors(response.getNumberOfNewSensors() + 1);
                    response.getInsertedSensors().add(sensorIdInSir);

                    if (log.isDebugEnabled())
                        log.debug("Inserted Sensor: " + sensorIdInSir);

                    if (serviceRefs != null) {
                        for (SirServiceReference servRef : serviceRefs) {
                            this.insSensInfoDao.addNewReference(new SirSensorIDInSir(sensorIdInSir), servRef);
                            response.setNumberOfNewServiceReferences(response.getNumberOfNewServiceReferences() + 1);
                        }
                        if (log.isDebugEnabled())
                            log.debug("Inserted " + serviceRefs.size() + " for sensor " + sensorIdInSir + ": "
                                    + serviceRefs);
                    }
                }
                else {
                    log.error("Could not insert sensor to database!");
                }
            }
            else {
                log.error("SensorML is not profile conform: "
                        + String.valueOf(profileValidator.getValidationFailuresAsString()));
            }

            profileValidator = null;
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 "InsertSensorInfoListener.receiveRequest()",
                                 "Missing parameter: To insert a sensor, a sensorInfo element is required!");
            throw se;
        }
    }

    /**
     * 
     * @param response
     * @param newReference
     * @throws OwsExceptionReport
     */
    private void insertServiceReferences(SirInsertSensorInfoResponse response,
                                         SirInfoToBeInserted_ServiceReference newReference) throws OwsExceptionReport {
        Collection<SirServiceReference> referenceArray = newReference.getServiceReferences();
        for (SirServiceReference sirServiceReference : referenceArray) {
            String id = this.insSensInfoDao.addNewReference(newReference.getSensorIDinSIR(), sirServiceReference);
            if (log.isDebugEnabled())
                log.debug("Inserted service reference for sensor " + id + ": " + sirServiceReference.getService());

            response.setNumberOfNewServiceReferences(response.getNumberOfNewServiceReferences() + 1);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.n52.sir.ISirRequestListener#receiveRequest(org.n52.sir.request. AbstractSirRequest)
     */
    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {

        SirInsertSensorInfoRequest sirRequest = (SirInsertSensorInfoRequest) request;
        SirInsertSensorInfoResponse response = new SirInsertSensorInfoResponse();

        try {
            for (SirInfoToBeInserted infoToBeInserted : sirRequest.getInfoToBeInserted()) {

                if (infoToBeInserted instanceof SirInfoToBeInserted_SensorDescription) {
                    SirInfoToBeInserted_SensorDescription newSensor = (SirInfoToBeInserted_SensorDescription) infoToBeInserted;
                    SirSensor sensor = SensorMLDecoder.decode(newSensor.getSensorDescription());
                    sensor.setLastUpdate(new Date());

                    Collection<SirServiceReference> serviceReferences = newSensor.getServiceReferences();

                    // INSERT
                    insertSensor(response, serviceReferences, sensor);
                }
                else if (infoToBeInserted instanceof SirInfoToBeInserted_ServiceReference) {
                    SirInfoToBeInserted_ServiceReference newReference = (SirInfoToBeInserted_ServiceReference) infoToBeInserted;

                    // INSERT
                    insertServiceReferences(response, newReference);
                }
            }
        }
        catch (OwsExceptionReport e) {
            return new ExceptionResponse(e.getDocument());
        }

        return response;
    }

}
