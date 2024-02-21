/*
 * Copyright (c) 2023 General Motors GTO LLC
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * SPDX-FileType: SOURCE
 * SPDX-FileCopyrightText: 2023 General Motors GTO LLC
 * SPDX-License-Identifier: Apache-2.0
 */

package org.eclipse.uprotocol.example.client;

public class JsonNodeTest {
    public static final String TEST_DOMAIN = "domain_name";
    public static final String TEST_DEVICE = "device_name";
    public static final String TEST_ENTITY_NAME = "example.service";
    public static final String TEST_PROPERTY1 = "ent_prop1";
    public static final String TEST_PROPERTY2 = "ent_prop2";
    public static final String JSON_HIERARCHY = "hierarchy";
    public static final String JSON_AUTHORITY = "authority";
    public static final String JSON_TTL = "ttl";
    public static final String JSON_DATA = "data";
    public static final String JSON_HASH = "hash";
    public static final String TEST_AUTHORITY_NAME = String.join(".", TEST_DEVICE, TEST_DOMAIN);
    public final static String REGISTRY_JSON = "{\n"
            + "       \"uri\": \"//" + TEST_DOMAIN + "\",\n"
            + "       \"nodes\": [\n"
            + "         {\n"
            + "           \"uri\": \"//" + TEST_AUTHORITY_NAME + "\",\n"
            + "           \"nodes\": [\n"
            + "             {\n"
            + "               \"uri\": \"//" + TEST_AUTHORITY_NAME + "/" + TEST_ENTITY_NAME + "\",\n"
            + "               \"nodes\": [\n"
            + "                 {\n"
            + "                   \"uri\": \"//" + TEST_AUTHORITY_NAME + "/" + TEST_ENTITY_NAME + "/1\",\n"
            + "                   \"nodes\": [\n"
            + "                     {\n"
            + "                       \"uri\": \"//" + TEST_AUTHORITY_NAME + "/" + TEST_ENTITY_NAME + "/1/door.front_left\",\n"
            + "                       \"properties\": {\n"
            + "                         \"" + TEST_PROPERTY1 + "\": {\n"
            + "                           \"uInteger\": 1\n"
            + "                         },\n"
            + "                         \"" + TEST_PROPERTY2 + "\": {\n"
            + "                           \"uInteger\": 2\n"
            + "                         }\n"
            + "                       },\n"
            + "                       \"type\": \"TOPIC\"\n"
            + "                     },\n"
            + "                     {\n"
            + "                       \"uri\": \"//" + TEST_AUTHORITY_NAME + "/" + TEST_ENTITY_NAME + "/1/door.front_right\",\n"
            + "                       \"properties\": {\n"
            + "                         \"topic_prop1\": {\n"
            + "                           \"uInteger\": 1\n"
            + "                         },\n"
            + "                         \"topic_prop2\": {\n"
            + "                           \"uInteger\": 2\n"
            + "                         }\n"
            + "                       },\n"
            + "                       \"type\": \"TOPIC\"\n"
            + "                     },\n"
            + "                     {\n"
            + "                       \"uri\": \"//" + TEST_AUTHORITY_NAME + "/" + TEST_ENTITY_NAME + "/1/door.rear_left\",\n"
            + "                       \"properties\": {\n"
            + "                         \"topic_prop1\": {\n"
            + "                           \"uInteger\": 1\n"
            + "                         },\n"
            + "                         \"topic_prop2\": {\n"
            + "                           \"uInteger\": 2\n"
            + "                         }\n"
            + "                       },\n"
            + "                       \"type\": \"TOPIC\"\n"
            + "                     },\n"
            + "                     {\n"
            + "                       \"uri\": \"//" + TEST_AUTHORITY_NAME + "/" + TEST_ENTITY_NAME + "/1/door.rear_right\",\n"
            + "                       \"properties\": {\n"
            + "                         \"topic_prop1\": {\n"
            + "                           \"uInteger\": 1\n"
            + "                         },\n"
            + "                         \"topic_prop2\": {\n"
            + "                           \"uInteger\": 2\n"
            + "                         }\n"
            + "                       },\n"
            + "                       \"type\": \"TOPIC\"\n"
            + "                     }\n"
            + "                   ],\n"
            + "                   \"type\": \"VERSION\"\n"
            + "                 }\n"
            + "               ],\n"
            + "               \"properties\": {\n"
            + "                 \"" + TEST_PROPERTY1 + "\": {\n"
            + "                   \"uInteger\": 1\n"
            + "                 },\n"
            + "                 \"" + TEST_PROPERTY2 + "\": {\n"
            + "                   \"uInteger\": 2\n"
            + "                 }\n"
            + "               },\n"
            + "               \"type\": \"ENTITY\"\n"
            + "             }\n"
            + "           ],\n"
            + "           \"properties\": {\n"
            + "             \"device_prop1\": {\n"
            + "               \"uInteger\": 1\n"
            + "             },\n"
            + "             \"device_prop2\": {\n"
            + "               \"uInteger\": 2\n"
            + "             }\n"
            + "           },\n"
            + "           \"type\": \"DEVICE\"\n"
            + "         }\n"
            + "       ],\n"
            + "       \"properties\": {\n"
            + "         \"domain_prop1\": {\n"
            + "           \"uInteger\": 1\n"
            + "         },\n"
            + "         \"domain_prop2\": {\n"
            + "           \"uInteger\": 2\n"
            + "         }\n"
            + "       },\n"
            + "       \"type\": \"DOMAIN\"\n"
            + "     }";

    protected static final String JSON_PROTOBUF = "{\n" +
            "  \"uri\": \"//device_name.domain_name/example.service\",\n" +
            "  \"nodes\": [\n" +
            "    {\n" +
            "      \"uri\": \"//device_name.domain_name/example.service/1\",\n" +
            "      \"nodes\": [\n" +
            "        {\n" +
            "          \"uri\": \"//device_name.domain_name/example.service/1/doors.front_left#Doors\",\n" +
            "          \"properties\": {\n" +
            "            \"id\": {\n" +
            "              \"uInteger\": 16\n" +
            "            }\n" +
            "          },\n" +
            "          \"type\": \"TOPIC\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"properties\": {\n" +
            "        \"id\": {\n" +
            "          \"uInteger\": 27\n" +
            "        },\n" +
            "        \"major_version\": {\n" +
            "          \"uInteger\": 1\n" +
            "        },\n" +
            "        \"minor_version\": {\n" +
            "          \"uInteger\": 0\n" +
            "        }\n" +
            "      },\n" +
            "      \"type\": \"VERSION\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"type\": \"ENTITY\"\n" +
            "}";
    protected static final String JSON_PROTOBUF_EXCEPTION = "{\n"
            + "  \"" + JSON_AUTHORITY + "\": \"//" + TEST_AUTHORITY_NAME + "\",\n"
            + "  \"" + JSON_DATA + "\": {\n"
            + "    \"" + JSON_HIERARCHY + "\": { \"dummy_data\" : \"hello world\" }\n"
            + "  },\n"
            + "  \"" + JSON_HASH + "\": {\n"
            + "    \"" + JSON_HIERARCHY + "\": \"zfXfhrFVEIWr6D1bDtJJgqOgNj+keleiiqDfTDCOo8w=\"\n"
            + "  },\n"
            + "  \"" + JSON_TTL + "\": {\n"
            + "    \"" + JSON_HIERARCHY + "\": {}\n"
            + "  }\n"
            + "}";
}
