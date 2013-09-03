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

package org.n52.oss;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

    private static Logger log = LoggerFactory.getLogger(Util.class);

    public static String readResourceFile(String s) {
        URL resource = Util.class.getResource(s);
        Path path;
        try {
            path = Paths.get(resource.toURI());
        }
        catch (URISyntaxException e) {
            log.error("Could not read from resource path " + s, e);
            return "";
        }
        return readResourceFile(path);
    }

    public static String readResourceFile(Path p) {
        log.debug("Reading {}", p);
        StringBuilder sb = new StringBuilder();
        try (Reader reader = new FileReader(p.toFile());) {

            int data = reader.read();
            while (data != -1) {
                char dataChar = (char) data;
                sb.append(dataChar);
                data = reader.read();
            }
            reader.close();
        }
        catch (IOException e) {
            log.error("Could not read from file " + p, e);
        }

        return sb.toString();
    }

}
