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

package org.n52.sor.listener;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.PhenomenonManager;
import org.n52.sor.request.ISorRequest;
import org.n52.sor.request.SorInsertDefinitionRequest;
import org.n52.sor.response.ISorResponse;
import org.n52.sor.response.SorInsertDefinitionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class InsertDefinitionListener implements IRequestListener {

    private static Logger log = LoggerFactory.getLogger(InsertDefinitionListener.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.process.ISorProcess#receiveRequest(org.n52.request.ISorRequest)
     */
    @Override
    public ISorResponse receiveRequest(ISorRequest request) throws OwsExceptionReport {
        SorInsertDefinitionRequest sorRequest = (SorInsertDefinitionRequest) request;
        SorInsertDefinitionResponse sorResponse = new SorInsertDefinitionResponse();

        // set definition URI
        sorResponse.setDefinitionURI(sorRequest.getDefinitionURI());

        // insert
        boolean b = PhenomenonManager.getInstance().insertInPhenomenonList(sorRequest.getDefinitionURI(),
                                                                           sorRequest.getPhenomenon());
        // set update successful
        sorResponse.setInsertSuccessful(b);

        if ( !b)
            log.warn("Insert NOT successful! " + sorRequest);

        return sorResponse;
    }

}