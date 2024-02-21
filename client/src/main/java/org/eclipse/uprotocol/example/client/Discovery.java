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

import static org.eclipse.uprotocol.common.util.UStatusUtils.checkArgument;
import static org.eclipse.uprotocol.common.util.UStatusUtils.checkStatusOk;
import static org.eclipse.uprotocol.common.util.UStatusUtils.toStatus;
import static org.eclipse.uprotocol.common.util.log.Formatter.join;
import static org.eclipse.uprotocol.core.udiscovery.v3.UDiscovery.METHOD_ADD_NODES;
import static org.eclipse.uprotocol.core.udiscovery.v3.UDiscovery.METHOD_DELETE_NODES;
import static org.eclipse.uprotocol.core.udiscovery.v3.UDiscovery.METHOD_FIND_NODES;
import static org.eclipse.uprotocol.core.udiscovery.v3.UDiscovery.METHOD_FIND_NODE_PROPERTIES;
import static org.eclipse.uprotocol.core.udiscovery.v3.UDiscovery.METHOD_LOOKUP_URI;
import static org.eclipse.uprotocol.core.udiscovery.v3.UDiscovery.METHOD_REGISTER_FOR_NOTIFICATIONS;
import static org.eclipse.uprotocol.core.udiscovery.v3.UDiscovery.METHOD_UNREGISTER_FOR_NOTIFICATIONS;
import static org.eclipse.uprotocol.core.udiscovery.v3.UDiscovery.METHOD_UPDATE_NODE;
import static org.eclipse.uprotocol.core.udiscovery.v3.UDiscovery.METHOD_UPDATE_PROPERTY;
import static org.eclipse.uprotocol.example.client.JsonNodeTest.JSON_PROTOBUF;
import static org.eclipse.uprotocol.example.client.JsonNodeTest.TEST_AUTHORITY_NAME;
import static org.eclipse.uprotocol.example.client.JsonNodeTest.TEST_ENTITY_NAME;
import static org.eclipse.uprotocol.transport.builder.UPayloadBuilder.pack;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.protobuf.util.JsonFormat;

import org.eclipse.uprotocol.UPClient;
import org.eclipse.uprotocol.common.util.log.Key;
import org.eclipse.uprotocol.core.udiscovery.v3.AddNodesRequest;
import org.eclipse.uprotocol.core.udiscovery.v3.DeleteNodesRequest;
import org.eclipse.uprotocol.core.udiscovery.v3.FindNodesRequest;
import org.eclipse.uprotocol.core.udiscovery.v3.Node;
import org.eclipse.uprotocol.core.udiscovery.v3.UDiscovery;
import org.eclipse.uprotocol.uri.serializer.LongUriSerializer;
import org.eclipse.uprotocol.v1.UAuthority;
import org.eclipse.uprotocol.v1.UCode;
import org.eclipse.uprotocol.v1.UEntity;
import org.eclipse.uprotocol.v1.UMessage;
import org.eclipse.uprotocol.v1.UResource;
import org.eclipse.uprotocol.v1.UStatus;
import org.eclipse.uprotocol.v1.UUri;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Discovery extends Fragment implements AdapterView.OnItemSelectedListener, UPClient.ServiceLifecycleListener {

    public static final UEntity SERVICE = UEntity.newBuilder().setName("example.client").setVersionMajor(1).build();
    private static final String TAG = "Discovery";
    private static final String UDISCOVERY_SERVICE_PACKAGE = "org.eclipse.uprotocol.core.udiscovery";
    private static final ComponentName UDISCOVERY_SERVICE_COMPONENET =
            new ComponentName(UDISCOVERY_SERVICE_PACKAGE, UDISCOVERY_SERVICE_PACKAGE + ".UDiscoveryService");
    private final Logger mLog = new Logger();
    private final ScheduledExecutorService mExecutor = Executors.newScheduledThreadPool(1);
    private final Intent UDISCOVERYSERVICE = new Intent().setComponent(UDISCOVERY_SERVICE_COMPONENET);
    private final Intent intent = new Intent().setComponent(UDISCOVERYSERVICE.getComponent());
    private UDiscovery.Stub mUDiscovery;
    private UPClient mUpClient;

    private static final UResource DOOR_FRONT_LEFT = UResource.newBuilder()
            .setName("doors")
            .setInstance("front_left")
            .setMessage("Doors")
            .build();
    private static final UUri TOPIC_DOOR_FRONT_LEFT = UUri.newBuilder()
            .setEntity(SERVICE)
            .setResource(DOOR_FRONT_LEFT)
            .build();

    public Discovery() {
    }


    private void startService() {
        try {
            ComponentName componentName = requireContext().startForegroundService(intent);
            mLog.i(TAG, join("Start Foreground Service", Key.COMPONENT, componentName));
        } catch (Exception e) {
            mLog.i(TAG, join("Exception Start Foreground Service", toStatus(e), Key.PACKAGE, UDISCOVERY_SERVICE_PACKAGE));
        }
    }

    private void stopService() {
        requireContext().stopService(intent);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.udiscovery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View layout = requireView();
        initializeUPClient();
        Spinner spinner = layout.findViewById(R.id.udiscovery_spinner);
        Button execute = layout.findViewById(R.id.execute);
        mLog.setOutput(layout.findViewById(R.id.output), layout.findViewById(R.id.output_scroller));
        layout.findViewById(R.id.clear_output_button).setOnClickListener(bview -> mLog.clear());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.discovery_values,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        execute.setOnClickListener(v -> onItemSelected(spinner, null, 0, 0));
    }

    @Override
    public void onDestroyView() {
        mLog.reset();
        mUpClient.disconnect();
        stopService();
        super.onDestroyView();
    }

    private void initializeUPClient() {
        startService();
        mUpClient = UPClient.create(requireContext(), mExecutor, this);
        mUDiscovery = UDiscovery.newStub(mUpClient);
        mUpClient.connect().thenApply(connected -> {
            if (connected.getCode() == UCode.OK) {
                mLog.i(TAG, "UPClient connected");
            } else {
                mLog.w(TAG, "UPClient failed to connect");
            }
            return connected;
        });
    }

    private void lookupUri() {
        UEntity uEntity = UEntity.newBuilder().setName(TEST_ENTITY_NAME).setVersionMajor(1).build();
        UAuthority authority = UAuthority.newBuilder().setName(TEST_AUTHORITY_NAME).build();
        final UUri lookupUriRequest = UUri.newBuilder().setEntity(uEntity).setAuthority(authority).setResource(DOOR_FRONT_LEFT).build();
        final String uri = LongUriSerializer.instance().serialize(lookupUriRequest);
        mLog.i(TAG, join(Key.EVENT, "lookupUri", uri));
        mUDiscovery.lookupUri(lookupUriRequest)
                .thenApply(uriResponse -> {
                    final UStatus status = uriResponse.getStatus();
                    checkStatusOk(status);
                    checkArgument(uriResponse.hasUris(), "LookupUri failed");
                    final UUri uUri = uriResponse.getUris().getUris(0);
                    mLog.i(TAG, join(Key.EVENT, "received LookupUri response " + uUri, status));
                    return uriResponse;
                }).exceptionally(e -> {
                    mLog.e(TAG, join(Key.EVENT, "lookupUri Exception", toStatus(e)));
                    return null;
                });
    }

    private void findNodes() {
        UAuthority authority = UAuthority.newBuilder().setName(TEST_AUTHORITY_NAME).build();
        final UUri uri = UUri.newBuilder().setAuthority(authority).build();
        final String uUri = LongUriSerializer.instance().serialize(uri);
        final FindNodesRequest findNodesRequest = FindNodesRequest.newBuilder().setUri(uUri).setDepth(-1).build();
        mLog.i(TAG, join(Key.EVENT, "findNodes", uri));
        mUDiscovery.findNodes(findNodesRequest)
                .thenApply(findNodesResponse -> {
                    final UStatus status = findNodesResponse.getStatus();
                    checkStatusOk(status);
                    checkArgument(findNodesResponse.hasStatus(), "FindNodes failed");
                    final int nodesCount = findNodesResponse.getNodesCount();
                    mLog.i(TAG, join(Key.EVENT, "received FindNodesResponse -> Node count and UStatus" + nodesCount, status));
                    return findNodesResponse;
                }).exceptionally(e -> {
                    mLog.e(TAG, join(Key.EVENT, "findNodes Exception", toStatus(e)));
                    return null;
                });
    }

    private void addNodes() {
        try {
            Node.Builder nodeBuilder = Node.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(JSON_PROTOBUF, nodeBuilder);
            UAuthority authority = UAuthority.newBuilder().setName(TEST_AUTHORITY_NAME).build();
            UUri uri = UUri.newBuilder().setAuthority(authority).build();
            String uUri = LongUriSerializer.instance().serialize(uri);
            AddNodesRequest addNodesRequest = AddNodesRequest.newBuilder().addNodes(nodeBuilder).setParentUri(uUri).build();
            UMessage message = UMessage.newBuilder().setPayload(pack(addNodesRequest)).build();
            mLog.i(TAG, join(Key.EVENT, "addNodes", message.getAttributes().getSource()));
            mUDiscovery.addNodes(addNodesRequest)
                    .thenApply(status -> {
                        checkStatusOk(status);
                        mLog.i(TAG, join(Key.EVENT, "received AddNodes response", status));
                        return status;
                    }).exceptionally(e -> {
                        mLog.e(TAG, join(Key.EVENT, "addNodes Exception", toStatus(e)));
                        return null;
                    });
        } catch (Exception e) {
            mLog.e(TAG, join(Key.EVENT, "addNodes Exception", toStatus(e)));
        }
    }

    private void deleteNodes() {
        final UAuthority uAuthority = UAuthority.newBuilder().setName(TEST_AUTHORITY_NAME).build();
        final UEntity entity = UEntity.newBuilder().setName(TEST_ENTITY_NAME).setVersionMajor(1).build();
        final UUri uri = UUri.newBuilder().setAuthority(uAuthority).setEntity(entity).setResource(DOOR_FRONT_LEFT).build();
        final String uUri = LongUriSerializer.instance().serialize(uri);
        mLog.i(TAG, join(Key.EVENT, "deleteNodes", uUri));
        DeleteNodesRequest deleteNodesRequest = DeleteNodesRequest.newBuilder().addUris(uUri).build();
        mUDiscovery.deleteNodes(deleteNodesRequest)
                .thenApply(uStatus -> {
                    checkStatusOk(uStatus);
                    mLog.i(TAG, join(Key.EVENT, "received DeleteNodes response", uStatus));
                    return uStatus;
                }).exceptionally(e -> {
                    mLog.e(TAG, join(Key.EVENT, "deleteNodes Exception", toStatus(e)));
                    return null;
                });
    }

    private void notYetImplemented() {
        mLog.i(TAG, join(Key.MESSAGE, "Not yet implemented"));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        try {
            if (mUpClient.isDisconnected()) {
                mLog.w(TAG, join(Key.EVENT, "UPClient is not connected"));
                return;
            }
            final String viewSelectedItem = adapterView.getSelectedItem().toString();
            mLog.i(TAG, join(Key.EVENT, "onItemSelected", viewSelectedItem));
            switch (viewSelectedItem) {
                case METHOD_LOOKUP_URI -> lookupUri();
                case METHOD_FIND_NODES -> findNodes();
                case METHOD_ADD_NODES -> addNodes();
                case METHOD_DELETE_NODES -> deleteNodes();
                case METHOD_UPDATE_NODE,
                        METHOD_FIND_NODE_PROPERTIES,
                        METHOD_UPDATE_PROPERTY,
                        METHOD_REGISTER_FOR_NOTIFICATIONS,
                        METHOD_UNREGISTER_FOR_NOTIFICATIONS -> notYetImplemented();
                default -> {
                }
            }
        } catch (Exception e) {
            mLog.e(TAG, join(Key.EVENT, "onItemSelected Exception", toStatus(e)));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        if (mUpClient.isDisconnected()) {
            mLog.w(TAG, join(Key.EVENT, "UPClient is not connected"));
            return;
        }
        final String viewSelectedItem = adapterView.getSelectedItem().toString();
        final String method = getResources().getStringArray(R.array.discovery_values)[0];
        if (viewSelectedItem.equals(method)) {
            mLog.i(TAG, "Please Select a method to execute");
        }
    }

    @Override
    public void onLifecycleChanged(UPClient upClient, boolean isConnected) {
        if (isConnected) {
            mLog.i(TAG, join(Key.EVENT, "onLifecycleChanged: UPClient connected"));
        } else {
            mLog.w(TAG, join(Key.EVENT, "onLifecycleChanged: UPClient disconnected"));
        }
    }
}
