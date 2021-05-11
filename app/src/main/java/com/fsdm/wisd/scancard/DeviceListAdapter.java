package com.fsdm.wisd.scancard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> deviceList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView _Name, _Address;
        LinearLayout linearLayout;

        public ViewHolder(View v) {
            super(v);
            _Name = v.findViewById(R.id.textViewDeviceName);
            _Address = v.findViewById(R.id.textViewDeviceAddress);
            linearLayout = v.findViewById(R.id.linearLayoutDeviceInfo);
        }
    }

    public DeviceListAdapter(Context context, List<Object> deviceList) {
        this.context = context;
        this.deviceList = deviceList;

    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deviceinfolayout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        final DeviceInfoModel deviceInfoModel = (DeviceInfoModel) deviceList.get(position);
        itemHolder._Name.setText(deviceInfoModel.getDeviceName());
        itemHolder._Address.setText(deviceInfoModel.getDeviceHardwareAddress());

        // When a device is selected
        itemHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Home.class);
                // Send device details to the MainActivity
                intent.putExtra("deviceName", deviceInfoModel.getDeviceName());
                intent.putExtra("deviceAddress",deviceInfoModel.getDeviceHardwareAddress());
                // Call MainActivity
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }



}
