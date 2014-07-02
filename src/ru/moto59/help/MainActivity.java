package ru.moto59.help;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	// Menu
	public static final int IDM_SETTINGS = 101;

	// My GPS states
	public static final int GPS_PROVIDER_DISABLED = 1;
	public static final int GPS_GETTING_COORDINATS = 2;
	public static final int GPS_GOT_COORDINATS = 3;
	public static final int GPS_PROVIDER_UNAVIALABLE = 4;
	public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
	public static final int GPS_PAUSE_SCANNING = 6;
	
	// Views
	private TextView GPSstate;
	private Button sendBtn;
	private CheckBox checkBox;
	private EditText smsEdit;
	
	// Location manager
	private LocationManager manager;
		
	private LocationListener locListener = new LocationListener() {
		
		public void onLocationChanged(Location argLocation) {
			printLocation(argLocation, GPS_GOT_COORDINATS);
		}
	
		@Override
		public void onProviderDisabled(String arg0) {
			printLocation(null, GPS_PROVIDER_DISABLED);
		}
	
		@Override
		public void onProviderEnabled(String arg0) {
		}
	
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
//				switch (arg1) {
//			    case LocationProvider.OUT_OF_SERVICE:
//			    	printLocation(null, GPS_PROVIDER_OUT_OF_SERVICE);
//			        break;
//			    case LocationProvider.TEMPORARILY_UNAVAILABLE:
//			    	printLocation(null, GPS_PROVIDER_UNAVIALABLE);
//			        break;
//			    case LocationProvider.AVAILABLE:
//			    	printLocation(null, GPS_GETTING_COORDINATS);
//			        break;
//			    }
		}
	};
	
	private void Pause_GPS_Scanning() {
		manager.removeUpdates(locListener);
	} 
	
	private void Resume_GPS_Scanning() {
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
		printLocation(null, GPS_GETTING_COORDINATS);
	} 
	
	private void printLocation(Location loc, int state) {
		
		switch (state) {
		case GPS_PROVIDER_DISABLED :
			GPSstate.setText("GPS выключен, коснитесь для включения");
			GPSstate.setTextColor(Color.RED);
			break;
		case GPS_GETTING_COORDINATS :
			GPSstate.setText("Определение координат...");
			GPSstate.setTextColor(Color.YELLOW);
			break;
		case GPS_PAUSE_SCANNING :
			GPSstate.setText("");
			break;	
		case GPS_GOT_COORDINATS :
			if (loc != null)
			{
				GPSstate.setText("Координаты определены:\t" + 
						"\nДолгота:\t" + loc.getLongitude() + 	    
		                "\nШирота:\t" + loc.getLatitude());
				GPSstate.setTextColor(Color.GREEN);
				sendBtn.setEnabled(true);
			}
			else {
				GPSstate.setText("Данные о местоположении недоступны");
				GPSstate.setTextColor(Color.RED);
			}
			break;
		}
	
	}
		
	// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		//return true;
	
		menu.add(Menu.NONE, IDM_SETTINGS, Menu.NONE, "Настройки тут будут");
		return(super.onCreateOptionsMenu(menu));
	}
		
	@Override
	protected void onPause() {
		super.onPause();
		Pause_GPS_Scanning();
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		Resume_GPS_Scanning();
	}
	
	// Throw user to GPS settings
	public void onClickLocationSettings(View view) {
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			startActivity(new Intent(
		        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //LinearLayout ll = (LinearLayout) findViewById( R.id.linearLayoutSum );
        //ll.setBackgroundColor(Color.BLACK);

        // RelativeLayout ll = (RelativeLayout) findViewById( R.id.relLay );
        // ll.setBackgroundColor(Color.BLACK);
        
        // Checkbox
        checkBox = (CheckBox)findViewById(R.id.checkBox1);
        checkBox.setChecked(true);
    	checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    	    {
    	        if ( isChecked )
    	        {
    	        	Resume_GPS_Scanning();
    	        	sendBtn.setEnabled(false);
    	        } else {
    	        	Pause_GPS_Scanning();
    	        	// только здесь, т.к. скрыть текст нужно только 
    	        	// при отключении флага, а не при паузе активности к примеру
    	    		printLocation(null, GPS_PAUSE_SCANNING); 
    	        	sendBtn.setEnabled(true);
    	        }

    	    }
    	});
        
    	
    	// GPS-state TextView
        GPSstate = (TextView)findViewById(R.id.textView1);
        GPSstate.setTextColor(Color.GREEN);
        //smsText.setTextColor(Color.parseColor("#F5DC49"));
        
        
        // Disable Send button
        sendBtn = (Button)findViewById(R.id.button1);
        sendBtn.setEnabled(false);
        

        // GPS init
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);		
		//manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
		//Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		//printLocation(loc);
        //setContentView(R.layout.activity_main);
        
        // Show keyboard
        smsEdit = (EditText)findViewById(R.id.editText2);
        smsEdit.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        
    }
}
