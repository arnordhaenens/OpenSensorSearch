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

package org.n52.sor.request;

/**
 * @created 20-Okt-2008 16:44:48
 * @version 1.0
 */
public class SorGetDefinitionRequest implements ISorRequest {

    /**
     * The input URI for the definition
     */
    private String inputURI;

    public SorGetDefinitionRequest(String inputURI) {
        this.inputURI = inputURI;
    }

    /**
     * Get the input URI
     * 
     * @return the inputURI
     */
    public String getInputURI() {
        return this.inputURI;
    }

    /**
     * Set the input URI
     * 
     * @param inputURI
     *        the inputURI to set
     */
    public void setInputURI(String inputURI) {
        this.inputURI = inputURI;
    }

    @Override
    public String toString() {
        return "GetDefinition request: InputURI: " + this.inputURI;
    }

}