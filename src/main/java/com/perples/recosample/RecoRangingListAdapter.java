/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Perples, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.perples.recosample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.graphics.Color;

import com.perples.recosdk.RECOBeacon;

import java.util.ArrayList;
import java.util.Collection;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class RecoRangingListAdapter extends BaseAdapter {
    private ArrayList<RECOBeacon> mRangedBeacons;
    private LayoutInflater mLayoutInflater;
    private boolean recoStatus = true;
    private int failNumber = 0;
    private int successNumber = 0;
    private int tFailNumber = 0;
    private int tSuccessNumber = 0;
    private int tNum = 0;

    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries11;


    public RecoRangingListAdapter(Context context) {
        super();
        mRangedBeacons = new ArrayList<RECOBeacon>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void updateBeacon(RECOBeacon beacon) {
        synchronized (mRangedBeacons) {
            if(mRangedBeacons.contains(beacon)) {
                mRangedBeacons.remove(beacon);
            }
            mRangedBeacons.add(beacon);
        }
    }

    public void updateAllBeacons(Collection<RECOBeacon> beacons) {
        synchronized (beacons) {
            mRangedBeacons = new ArrayList<RECOBeacon>(beacons);
        }
    }

    public void clear() {
        mRangedBeacons.clear();
    }

    public void setStatusSF(boolean sts) {
        recoStatus = sts;
    }

    @Override
    public int getCount() {
        return mRangedBeacons.size();
    }

    @Override
    public Object getItem(int position) {
        return mRangedBeacons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_ranging_beacon, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.recoStatus = (TextView)convertView.findViewById(R.id.recoStatus);
            viewHolder.recoProximityUuid = (TextView)convertView.findViewById(R.id.recoProximityUuid);
            viewHolder.recoMajor = (TextView)convertView.findViewById(R.id.recoMajor);
            viewHolder.recoMinor = (TextView)convertView.findViewById(R.id.recoMinor);
            viewHolder.recoTxPower = (TextView)convertView.findViewById(R.id.recoTxPower);
            viewHolder.recoRssi = (TextView)convertView.findViewById(R.id.recoRssi);
            viewHolder.recoProximity = (TextView)convertView.findViewById(R.id.recoProximity);
            viewHolder.recoAccuracy = (TextView)convertView.findViewById(R.id.recoAccuracy);
            viewHolder.recoNumFail = (TextView)convertView.findViewById(R.id.recoNumFail);
            viewHolder.recoNumSuccess = (TextView)convertView.findViewById(R.id.recoNumSuccess);
            viewHolder.recoGraph = (GraphView)convertView.findViewById(R.id.recoGraph);

            mSeries1 = new LineGraphSeries<DataPoint>();
            mSeries11 = new LineGraphSeries<DataPoint>();
            viewHolder.recoGraph.addSeries(mSeries1);
            //viewHolder.recoGraph.addSeries(mSeries11);
            viewHolder.recoGraph.getViewport().setXAxisBoundsManual(true);
            viewHolder.recoGraph.getViewport().setMinX(0);
            viewHolder.recoGraph.getViewport().setMaxX(20);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        RECOBeacon recoBeacon = mRangedBeacons.get(position);

        String proximityUuid = recoBeacon.getProximityUuid();

        tNum++;
        if ( recoStatus ) {
          tSuccessNumber++;
          viewHolder.recoStatus.setText("Search Success : " + ++successNumber);
          viewHolder.recoStatus.setTextColor(Color.rgb(0, 0x66,0xCC));
          failNumber = 0;
          mSeries1.appendData(new DataPoint((double)tNum*1.0,(double)tSuccessNumber*1.0),true, 20);
        }
        else  {
          tFailNumber++;
          viewHolder.recoStatus.setText("Search Fail : " + ++failNumber);
          viewHolder.recoStatus.setTextColor(Color.rgb(0xFF,0xA5,0));
          successNumber = 0;
          //mSeries11.resetData(generateData(tFailNumber));
          mSeries1.appendData(new DataPoint(tNum*1.0,tFailNumber*1.0),true, 20);
        }
        viewHolder.recoProximityUuid.setText("UUID:" + String.format("%s-%s-%s-%s-%s", proximityUuid.substring(0, 8), proximityUuid.substring(8, 12), proximityUuid.substring(12, 16), proximityUuid.substring(16, 20), proximityUuid.substring(20) ));
        viewHolder.recoMajor.setText(recoBeacon.getMajor() + "");
        viewHolder.recoMinor.setText(recoBeacon.getMinor() + "");
        viewHolder.recoTxPower.setText(recoBeacon.getTxPower() + "");
        viewHolder.recoRssi.setText(recoBeacon.getRssi() + "");
        viewHolder.recoProximity.setText("Proximity:" + recoBeacon.getProximity() + "");
        viewHolder.recoAccuracy.setText("Accuracy:" + String.format("%.2f", recoBeacon.getAccuracy()));
        viewHolder.recoNumFail.setText("FailNumber:" + tFailNumber);
        viewHolder.recoNumSuccess.setText("SuccessNumber:" + tSuccessNumber);

        return convertView;
    }

    private DataPoint[] generateData(int val) {
        int count = 1;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i * 1.0;
            double y = val * 1.0;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }



    static class ViewHolder {
        TextView recoStatus;
        TextView recoProximityUuid;
        TextView recoMajor;
        TextView recoMinor;
        TextView recoTxPower;
        TextView recoRssi;
        TextView recoProximity;
        TextView recoAccuracy;
        TextView recoNumFail;
        TextView recoNumSuccess;
        GraphView recoGraph;
    }

}
