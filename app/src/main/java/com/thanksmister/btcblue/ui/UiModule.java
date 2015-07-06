/*
 * Copyright (c) 2014. ThanksMister LLC
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

package com.thanksmister.btcblue.ui;

import com.thanksmister.btcblue.data.DataModule;
import com.thanksmister.btcblue.db.DbModule;
import com.thanksmister.btcblue.domain.DomainModule;

import dagger.Module;


@Module(
        injects = {MainActivity.class, CalculatorActivity.class},
        includes = {DomainModule.class, DataModule.class, DbModule.class},
        complete = false,
        library = true
)
public class UiModule 
{
        
}
