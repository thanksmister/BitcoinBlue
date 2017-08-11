/*
 * <!--
 *   ~ Copyright (c) 2017. ThanksMister LLC
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License. 
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~ http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software distributed 
 *   ~ under the License is distributed on an "AS IS" BASIS, 
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *   ~ See the License for the specific language governing permissions and 
 *   ~ limitations under the License.
 *   -->
 */

package com.thanksmister.btcblue.ui.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thanksmister.btcblue.R;
import com.thanksmister.btcblue.data.api.model.DisplayExchange;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ExchangeAdapter extends ArrayAdapter<DisplayExchange> {
    private LayoutInflater inflater;
    private List<DisplayExchange> values;

    public ExchangeAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<DisplayExchange> values) {
        this.values = values;
        notifyDataSetInvalidated();
    }

    public int getCount() {
        if (values == null)
            return 0;

        return values.size();
    }

    public DisplayExchange getItem(int position) {
        if (values == null)
            return null;

        return values.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.spinner_header, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        // Fix for exchange being removed from BitcoinAverage list after 
        // previously been available (Coinbase)
        if (!values.isEmpty()) {
            DisplayExchange exchange = null;
            try {
                exchange = values.get(position);
                holder.displayName.setText(exchange.getDisplayName());
            } catch (IndexOutOfBoundsException e) {
                exchange = values.get(0);
                holder.displayName.setText(exchange.getDisplayName());
            }
        } else {
            holder.displayName.setText(R.string.spinner_no_exchange_data);
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.spinner_dropdown, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        DisplayExchange exchange = values.get(position);
        holder.displayName.setText(exchange.getDisplayName());
        return view;
    }

    class ViewHolder {
        @InjectView(R.id.displayName)
        TextView displayName;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}