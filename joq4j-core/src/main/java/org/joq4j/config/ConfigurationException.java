/*
 * Copyright 2020 AGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.joq4j.config;

import org.joq4j.Joq4jException;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class ConfigurationException extends Joq4jException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Object... args) {
        super(message, args);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
