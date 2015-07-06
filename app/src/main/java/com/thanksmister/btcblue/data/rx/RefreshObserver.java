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

package com.thanksmister.btcblue.data.rx;

import rx.Observer;

/** An {@link rx.Observer} that toggles the refresh animation state. */
public abstract class RefreshObserver<T> implements Observer<T>
{
    /*@Override 
    public void onCompleted() 
    {
        onEnd();
    }

    @Override 
    public void onError(Throwable throwable) 
    {
        onEnd();
    }

    public abstract void onEnd();*/
    public abstract void onRefreshEnd();
    public abstract void onRefreshStart();
}
