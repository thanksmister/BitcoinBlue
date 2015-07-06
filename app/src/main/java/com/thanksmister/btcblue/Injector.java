/*
 * Copyright (c) 2015 ThanksMister LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.thanksmister.btcblue;

import dagger.ObjectGraph;

public class Injector {
    
    private static ObjectGraph objectGraph;

    public static void init(BaseApplication application) {
        
        ApplicationModule module = new ApplicationModule(application);
        objectGraph = ObjectGraph.create(module);
    }

    public static <T> T inject(T object) {
        return objectGraph.inject(object);
    }

    public static void reset(BaseApplication application) 
    {
        init(application);
    }
}
