package com.blogspot.sontx.myapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.sontx.iot.shared.model.bean.Device;
import com.blogspot.sontx.iot.shared.model.bean.RealTime;
import com.blogspot.sontx.myapp.R;
import com.blogspot.sontx.myapp.lib.ConnectionServer;

import java.util.ArrayList;
import java.util.List;

public class DevicesActivity extends AppCompatActivity implements Handler.Callback {
    private static final int UPDATE_AFTER = 1000;// 1s
    private ProcessingBox processingBox;
    private DeviceAdapter adapter;
    private boolean pendingStop = false;
    private Handler updaterHandler = new Handler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        final ListView devicesView = (ListView) findViewById(R.id.devices_lv_list);
        processingBox = new ProcessingBox(this);
        processingBox.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Device> devices = ConnectionServer.getInstance().getDevices();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processingBox.dismiss();
                        if (devices == null) {
                            Toast.makeText(DevicesActivity.this, "Network error!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            List<DeviceHolder> holders = new ArrayList<DeviceHolder>(devices.size());
                            for (Device device : devices) {
                                DeviceHolder holder = new DeviceHolder();
                                holder.device = device;
                                holder.state = 0;
                                holders.add(holder);
                            }
                            adapter = new DeviceAdapter(DevicesActivity.this, holders);
                            devicesView.setAdapter(adapter);
                            updateDevicesState(holders);
                        }
                    }
                });
            }
        }).start();
        registerForContextMenu(devicesView);
    }

    private void updateDevicesState(final List<DeviceHolder> devices) {
        pendingStop = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean brokenDown = false;
                while (!pendingStop && !brokenDown) {
                    boolean changed = false;
                    for (DeviceHolder holder : devices) {
                        RealTime realTime = ConnectionServer.getInstance().getRealtime(holder.device.getId());
                        if (realTime != null) {
                            if (holder.state != realTime.getState()) {
                                holder.state = realTime.getState();
                                changed = true;
                            }
                        } else {
                            brokenDown = true;
                            break;
                        }
                    }
                    if (changed)
                        updaterHandler.sendEmptyMessage(1);
                    long now = System.currentTimeMillis();
                    while (System.currentTimeMillis() - now < UPDATE_AFTER && !pendingStop && !brokenDown) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            pendingStop = true;
                            break;
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            adapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.devices_lv_list)
            getMenuInflater().inflate(R.menu.devices_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Device device = ((DeviceHolder) adapter.getItem(position)).device;
        if (item.getItemId() == R.id.devices_context_menu_realtime) {
            startTaskActivity(RealtimeActivity.class, device.getId());
        } else if (item.getItemId() == R.id.devices_context_menu_history) {
            startTaskActivity(HistoryActivity.class, device.getId());
        } else if (item.getItemId() == R.id.devices_context_menu_rename) {
            renameDevice(device);
        }
        return true;
    }

    private void renameDevice(final Device device) {
        InputBox box = new InputBox(this, device.getName());
        box.setMessage(String.format("Change name of %d", device.getId()));
        box.setOnInputCompletedListener(new InputBox.OnInputCompletedListener() {
            @Override
            public void inputCompleted(final InputBox box, @NonNull final String content) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean ok = ConnectionServer.getInstance().renameDevice(device.getId(),  content);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (ok) {
                                    device.setName(content);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(DevicesActivity.this, "OK", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DevicesActivity.this, "Error!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).start();


                box.dismiss();
            }
        });
        box.show();
    }

    private void startTaskActivity(Class target, int deviceId) {
        Intent intent = new Intent(this, target);
        Bundle bundle = new Bundle();
        bundle.putInt(TaskActivity.INTENT_DEVICE_ID, deviceId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private class DeviceHolder {
        public Device device = null;
        public byte state = 0;
    }

    private class DeviceAdapter extends BaseAdapter {
        private final List<DeviceHolder> devices;
        private final LayoutInflater inflater;

        public DeviceAdapter(Context context, List<DeviceHolder> devices) {
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            this.devices = devices;
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.device_item, null);
                holder = new ViewHolder();
                holder.nameView = (TextView) convertView.findViewById(R.id.device_item_tv_name);
                holder.idView = (TextView) convertView.findViewById(R.id.device_item_tv_id);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            DeviceHolder deviceHolder = devices.get(position);
            holder.nameView.setText(deviceHolder.device.getName());
            holder.idView.setText(String.format("ID: %d", deviceHolder.device.getId()));
            int colorId = deviceHolder.state != 0 ? R.color.colorActive : R.color.colorInactive;
            int color = ContextCompat.getColor(DevicesActivity.this, colorId);
            holder.nameView.setTextColor(color);
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView nameView;
        TextView idView;
    }
}
