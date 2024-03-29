/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.aspirephile.taskcompanion.util.kankan.widget.adapters;

import java.util.LinkedList;
import java.util.List;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;

/**
 * Abstract Wheel adapter.
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractWheelAdapter implements WheelViewAdapter {
    Logger l = new Logger(AbstractWheelTextAdapter.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);

    // Observers
    private List<DataSetObserver> datasetObservers;

    @Override
    public View getEmptyItem(View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        if (datasetObservers == null) {
            datasetObservers = new LinkedList<>();
        }
        datasetObservers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (asserter.assertPointer(datasetObservers)) {
            datasetObservers.remove(observer);
        }
    }

    /**
     * Notifies observers about data changing
     */
    protected void notifyDataChangedEvent() {
        if (asserter.assertPointer(datasetObservers)) {
            for (DataSetObserver observer : datasetObservers) {
                observer.onChanged();
            }
        }
    }

    /**
     * Notifies observers about invalidating data
     */
    protected void notifyDataInvalidatedEvent() {
        if (asserter.assertPointer(datasetObservers)) {
            for (DataSetObserver observer : datasetObservers) {
                observer.onInvalidated();
            }
        }
    }
}
