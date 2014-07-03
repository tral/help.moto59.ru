package ru.moto59.help;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// Menu
	public static final int IDM_SETTINGS = 101;
	public static final int IDM_RULES = 102;
	
	// Dialogs
    private static final int SEND_SMS_DIALOG_ID = 0;
    private final static int PHONE_DIALOG_ID = 1;
	ProgressDialog mSMSProgressDialog;

	// My GPS states
	public static final int GPS_PROVIDER_DISABLED = 1;
	public static final int GPS_GETTING_COORDINATS = 2;
	public static final int GPS_GOT_COORDINATS = 3;
	public static final int GPS_PROVIDER_UNAVIALABLE = 4;
	public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
	public static final int GPS_PAUSE_SCANNING = 6;
	
	// Location manager
	private LocationManager manager;
	
	// SMS Broadcast Receivers
	BroadcastReceiver sendBroadcastReceiver = new sentReceiver();
    BroadcastReceiver deliveryBroadcastReciever = new deliverReceiver();;
	
	// Views
	TextView GPSstate;
	Button sendBtn;
	CheckBox checkBox;
	EditText smsEdit;
	
	// Globals
	private String phoneNumber;
	private String coordsToSend;
	
    // Database
    DBHelper dbHelper;
	

	// Functions sends an SMS message
	private void sendSMS(String phoneNumber, String message) {
		
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";
		
		showDialog(SEND_SMS_DIALOG_ID);
		
		if (checkBox.isChecked()) {
			message = message + " " + coordsToSend;
		}
		
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
		
		registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
		registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));
		
		SmsManager sms = SmsManager.getDefault();

		ArrayList<String> mArray = sms.divideMessage(message);
		ArrayList<PendingIntent> sentArrayIntents = new ArrayList<PendingIntent>();
		ArrayList<PendingIntent> deliveredArrayIntents = new ArrayList<PendingIntent>();
		
		for(int i = 0; i < mArray.size(); i++) {
			sentArrayIntents.add(sentPI);
			deliveredArrayIntents.add(deliveredPI);
		}
		  
		sms.sendMultipartTextMessage("+7" + phoneNumber, null, mArray, sentArrayIntents, deliveredArrayIntents);

    }
	
	// Deliver SMS Receiver
    class deliverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {
            case Activity.RESULT_OK:
                MainActivity.this.ShowToast(R.string.info_sms_delivered, Toast.LENGTH_SHORT);
                break;
            case Activity.RESULT_CANCELED:
                MainActivity.this.ShowToast(R.string.info_sms_not_delivered, Toast.LENGTH_SHORT);
                break;
            }
        }
    }

    // Send SMS Receiver 
    class sentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
        
        	dismissDialog(SEND_SMS_DIALOG_ID);
        
            switch (getResultCode()) {
            case Activity.RESULT_OK:
                MainActivity.this.ShowToast(R.string.info_sms_sent, Toast.LENGTH_SHORT);
                //startActivity(new Intent(MainActivity.this, ChooseOption.class));
                //overridePendingTransition(R.anim.animation, R.anim.animation2);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                MainActivity.this.ShowToast(R.string.info_sms_generic, Toast.LENGTH_SHORT);
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                MainActivity.this.ShowToast(R.string.info_sms_noservice, Toast.LENGTH_SHORT);
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                MainActivity.this.ShowToast(R.string.info_sms_nullpdu, Toast.LENGTH_SHORT);
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                MainActivity.this.ShowToast(R.string.info_sms_radioof, Toast.LENGTH_SHORT);
                break;
            }
        }
    }
	
	// Location events (we use GPS only)
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
		}
	};
	
	private void Pause_GPS_Scanning() {
		manager.removeUpdates(locListener);
		if (!checkBox.isChecked()) {
			sendBtn.setEnabled(true);
		}
	} 
	
	private void Resume_GPS_Scanning() {
		if (checkBox.isChecked()) {
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
			sendBtn.setEnabled(false);
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				printLocation(null, GPS_GETTING_COORDINATS);
			}
		}
	} 
	
	private void printLocation(Location loc, int state) {
		
		String accuracy;
		
		switch (state) {
		case GPS_PROVIDER_DISABLED :
			GPSstate.setText(R.string.gps_state_disabled);
			GPSstate.setTextColor(Color.RED);
			break;
		case GPS_GETTING_COORDINATS :
			GPSstate.setText(R.string.gps_state_in_progress);
			GPSstate.setTextColor(Color.YELLOW);
			break;
		case GPS_PAUSE_SCANNING :
			GPSstate.setText("");
			break;	
		case GPS_GOT_COORDINATS :
			if (loc != null) {

				coordsToSend = String.format(Locale.US , "%2.5f", loc.getLatitude()) + " " + String.format(Locale.US ,"%3.5f", loc.getLongitude());

				// Accuracy
				if (loc.getAccuracy() < 0.0001) {accuracy = "?"; }
					else if (loc.getAccuracy() > 99) {accuracy = "> 99";}
						else {accuracy = String.format("%2.0f", loc.getAccuracy());};
				
				GPSstate.setText("Координаты получены, точность: " + accuracy + " м. ");
				//+ "\t\nШирота: " + loc.getLatitude() + "Долгота: " + loc.getLongitude());
				GPSstate.setTextColor(Color.GREEN);
				sendBtn.setEnabled(true);
				
			}
			else {
				GPSstate.setText(R.string.gps_state_unavialable);
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
	
		menu.add(Menu.NONE, IDM_SETTINGS, Menu.NONE, R.string.menu_item_settings);
		menu.add(Menu.NONE, IDM_RULES, Menu.NONE, R.string.menu_item_rules);
		return(super.onCreateOptionsMenu(menu));
	}
		
		
	// Dialogs
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        
        case SEND_SMS_DIALOG_ID:
        	  mSMSProgressDialog = new ProgressDialog(MainActivity.this);
        	  //mCatProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        	  mSMSProgressDialog.setCanceledOnTouchOutside(true);
        	  mSMSProgressDialog.setCancelable(true);
        	  mSMSProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        	  mSMSProgressDialog.setMessage("Отправка SMS...");
        	  return mSMSProgressDialog;
        	  
        case PHONE_DIALOG_ID:
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.phone_dialog, (ViewGroup)findViewById(R.id.phone_dialog_layout));
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(layout);
            
            // Stored phone number
            final EditText keyDlgEdit = (EditText) layout.findViewById(R.id.phone_edit_text);
    		dbHelper = new DBHelper(this);
         	SQLiteDatabase db = dbHelper.getWritableDatabase();
         	keyDlgEdit.setText(dbHelper.getPhone());
    		dbHelper.close();
    		
            builder.setMessage("Номер телефона для SMS");
            
            builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                	// update phone number
                	dbHelper = new DBHelper(MainActivity.this);
	        		SQLiteDatabase db = dbHelper.getWritableDatabase();
	        		ContentValues cv = new ContentValues();
	                cv.put("phone", keyDlgEdit.getText().toString());
	                int updCount = db.update("phone", cv, "_id = ?", new String[] { "1" });
	                dbHelper.close();
	                phoneNumber = keyDlgEdit.getText().toString();
	                keyDlgEdit.selectAll(); // чтобы при повторном открытии номер был выделен
                }
            });
            
            builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    }
            });
            
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            // show keyboard automatically
            keyDlgEdit.selectAll();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            return dialog;

        }
        return null;
    }
		
    // Menu
 	@Override
 	public boolean onOptionsItemSelected(MenuItem item) {
        CharSequence message;
        switch (item.getItemId()) {
            case IDM_RULES:
            	/*
            	// 
              	dbHelper = new DBHelper(this);
           		SQLiteDatabase db = dbHelper.getWritableDatabase();
           		Cursor tmp = db.query(true,"data_", null,null,null,null,null,null,null);
           	   
           		if (tmp.getCount()>0 ) {
           			MainActivity.this.ShowToast("Сначала синхронизируйте расходы!", Toast.LENGTH_LONG);
 				} else {
 					showDialog(CAT_PROGRESS_DIALOG_ID);
 					// Запускаем новый поток для вытягивания категорий с удаленного сервера
 					mThreadUpdateCategories = new ThreadUpdateCategories(handler);
 					mThreadUpdateCategories.setSecretKey(dbHelper.getSecretKey());
 					mThreadUpdateCategories.setState(ThreadUpdateCategories.STATE_RUNNING);
 				    mThreadUpdateCategories.start();
 				}
           	    
           		tmp.close();
           		dbHelper.close();
            	*/
                break;
            case IDM_SETTINGS:
            	showDialog(PHONE_DIALOG_ID);
                break;    
                
            default:
                return false;
        }
        
        return true;
    }
    
    
	@Override
	protected void onResume() {
		super.onResume();
		Resume_GPS_Scanning();
	}
		
	@Override
	protected void onPause() {
		super.onPause();
		Pause_GPS_Scanning();
		 try {
	            unregisterReceiver(sendBroadcastReceiver);
	            unregisterReceiver(deliveryBroadcastReciever);
	     } catch (Exception e) {
	            e.printStackTrace();
	     }
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    try {
	        unregisterReceiver(sendBroadcastReceiver);
	        unregisterReceiver(deliveryBroadcastReciever);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	// Throw user to GPS settings
	public void onClickLocationSettings(View view) {
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			startActivity(new Intent(
		        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
	};
	

	// Small util to show text messages
	protected void ShowToast(int txt, int lng) {
		Toast toast = Toast.makeText(MainActivity.this, txt, lng);
	    toast.setGravity(Gravity.TOP, 0, 0);
	    toast.show();
	} 
	
	// ------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Stored phone number
		dbHelper = new DBHelper(this);
     	phoneNumber = dbHelper.getPhone();
		dbHelper.close();
        
        // Checkbox
        checkBox = (CheckBox)findViewById(R.id.checkBox1);
        checkBox.setChecked(true);
    	checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	        if ( isChecked ) {
    	        	Resume_GPS_Scanning();
    	        } else {
    	        	Pause_GPS_Scanning();
    	        	// только здесь, т.к. скрыть текст нужно только 
    	        	// при отключении флага, а не при паузе активности к примеру
    	    		printLocation(null, GPS_PAUSE_SCANNING); 
    	        }
    	    }
    	});
    	
    	// GPS-state TextView
        GPSstate = (TextView)findViewById(R.id.textView1);
        GPSstate.setTextColor(Color.GREEN);
        
        // GPS init
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);		
      
        // Show keyboard
        smsEdit = (EditText)findViewById(R.id.editText2);
        smsEdit.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        
        //Prepare SMS Listeners, prepare Send button
        sendBtn = (Button)findViewById(R.id.button1);
        sendBtn.setEnabled(false);
        
        sendBtn.setOnClickListener(new OnClickListener() {

        	@Override
            public void onClick(View v) {
                if (smsEdit.getText().toString().equals("")
                        | smsEdit.getText().toString().equals(null)) {
                    MainActivity.this.ShowToast(R.string.error_sms_empty, Toast.LENGTH_LONG);
                } else {
                	sendSMS(phoneNumber, smsEdit.getText().toString());
                }
            }
        });
        
        
    }
    
	// ------------------------------------------------------------------------------------------
    
}
