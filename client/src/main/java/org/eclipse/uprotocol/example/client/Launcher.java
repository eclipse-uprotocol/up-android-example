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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicBoolean;

public class Launcher extends AppCompatActivity {

    AtomicBoolean fragmentOneSelected = new AtomicBoolean(false);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        Button subcritptionButton = findViewById(R.id.usubscription);

        if (null  == savedInstanceState) {
            fragmentOneSelected.set(true);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, USubcription.class, null)
                    .commit();
        }
        subcritptionButton.setOnClickListener(this::selectFragment);
        Button discoveryButton = findViewById(R.id.udiscovery);
        discoveryButton.setOnClickListener(this::selectFragment);
    }

    public void selectFragment(View view) {
        if(view == findViewById(R.id.usubscription) && fragmentOneSelected.get()) {
            fragmentOneSelected.set(false);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, USubcription.class, null)
                    .commit();
        } else if(view == findViewById(R.id.udiscovery) && !fragmentOneSelected.get()) {
            fragmentOneSelected.set(true);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, Discovery.class, null)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fragmentOneSelected.get()) {
            fragmentOneSelected.set(false);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, USubcription.class, null)
                    .commit();
        } else {
            fragmentOneSelected.set(true);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, Discovery.class, null)
                    .commit();
        }
    }
}
