package com.chafik.monapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button On,Off,Visible,list,listAround;
	private BluetoothAdapter BA;
	private Set<BluetoothDevice>pairedDevices;
	private ListView lvPaired , lvAround;
	private TextView tvPaired , tvAround;
	private ArrayList<String> tab;
	private boolean discoveryFinished;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		On = (Button)findViewById(R.id.button1);
		Off = (Button)findViewById(R.id.button2);
		Visible = (Button)findViewById(R.id.button3);
		list = (Button)findViewById(R.id.button4);
		listAround = (Button)findViewById(R.id.button5);

		lvPaired = (ListView)findViewById(R.id.listView1);
		lvAround = (ListView)findViewById(R.id.listView2);

		tvPaired = (TextView)findViewById(R.id.TextView2);
		tvAround = (TextView)findViewById(R.id.TextView3);

		tvPaired.setVisibility(View.GONE);
		tvAround.setVisibility(View.GONE);

		discoveryFinished = false;
		BA = BluetoothAdapter.getDefaultAdapter();

		
		tab = new ArrayList<String>() ;
	}

	public void on(View view){
		if (!BA.isEnabled()) {
			Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(turnOn, 0);
			Toast.makeText(getApplicationContext(),"Turned on" ,Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(getApplicationContext(),"Already on",Toast.LENGTH_LONG).show();
		}
	}
	public void list(View view)
	{


		tvPaired.setVisibility(View.VISIBLE);
		pairedDevices = BA.getBondedDevices();
		ArrayList list = new ArrayList();
		for(BluetoothDevice bt : pairedDevices)
		{
			list.add(bt.getName());
		}
		Toast.makeText(getApplicationContext(),"Showing the " + list.size() + " paired device", Toast.LENGTH_SHORT).show();
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,  list);

		lvPaired.setAdapter(adapter);
	}

	final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() 
	{



		public void onReceive(Context context, Intent intent)
		{
			
			String action = intent.getAction();
			
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) 
			{
				Toast.makeText(getApplicationContext(), "La recherche commence" , Toast.LENGTH_SHORT).show();
			}
			
			if (BluetoothDevice.ACTION_FOUND.equals(action)) 
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Toast.makeText(getApplicationContext(), "New Device = " + device.getName(), Toast.LENGTH_SHORT).show();
				tab.add(device.getName());

			}
			
			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) 
			{
				discoveryFinished = true;
				listAround(listAround);
				
			}
		}
	};

	public void listAround(View view)
	{

		if (discoveryFinished )
		{
			tvAround.setVisibility(View.VISIBLE);
	
			ArrayList list = new ArrayList();
			for (int i = 0 ;i<tab.size(); i++)
			{
				list.add(tab.get(i));
			}
			
			Toast.makeText(getApplicationContext(),"Showing the " + list.size() + " device(s) that are around", Toast.LENGTH_SHORT).show();
			ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,  list);
	
			lvAround.setAdapter(adapter);
			discoveryFinished = false;
		}
		else
		{
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
			registerReceiver(bluetoothReceiver, filter);
			BA.startDiscovery();
		}
		
	}
	public void off(View view){
		BA.disable();
		Toast.makeText(getApplicationContext(),"Turned off" ,
				Toast.LENGTH_LONG).show();
	}
	public void visible(View view){
		Intent getVisible = new Intent(BluetoothAdapter.
				ACTION_REQUEST_DISCOVERABLE);
		startActivityForResult(getVisible, 0);

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}