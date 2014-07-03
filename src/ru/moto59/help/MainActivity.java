package ru.moto59.help;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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
	
	// Dialogs
    private static final int SEND_SMS_DIALOG_ID = 0;

	// My GPS states
	public static final int GPS_PROVIDER_DISABLED = 1;
	public static final int GPS_GETTING_COORDINATS = 2;
	public static final int GPS_GOT_COORDINATS = 3;
	public static final int GPS_PROVIDER_UNAVIALABLE = 4;
	public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
	public static final int GPS_PAUSE_SCANNING = 6;
	
	// For SMS Result Listeners
	BroadcastReceiver sendBroadcastReceiver = new sentReceiver();
    BroadcastReceiver deliveryBroadcastReciever = new deliverReceiver();;
	
	// Views
	TextView GPSstate;
	Button sendBtn;
	CheckBox checkBox;
	EditText smsEdit;
	ProgressDialog mSMSProgressDialog;
	
	// Phone number
	private String phoneNumber;
	
	// ���������� ��� ��������
	private String coordsToSend;
	
	// Location manager
	private LocationManager manager;
	
	

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
		  
		sms.sendMultipartTextMessage(phoneNumber, null, mArray, sentArrayIntents, deliveredArrayIntents);

    }
	
	// Deliver SMS Receiver
    class deliverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {
            case Activity.RESULT_OK:
                //Toast.makeText(getBaseContext(),  R.string.info_sms_delivered, Toast.LENGTH_SHORT).show();
                MainActivity.this.ShowToast(R.string.info_sms_delivered, Toast.LENGTH_LONG);
                break;
            case Activity.RESULT_CANCELED:
                //Toast.makeText(getBaseContext(), "sms_not_delivered", Toast.LENGTH_SHORT).show();
                MainActivity.this.ShowToast(R.string.info_sms_not_delivered, Toast.LENGTH_LONG);
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
                
//              Toast.makeText(getBaseContext(), R.string.info_sms_sent, Toast.LENGTH_SHORT).show();
                MainActivity.this.ShowToast(R.string.info_sms_sent, Toast.LENGTH_LONG);
                
                //startActivity(new Intent(MainActivity.this, ChooseOption.class));
                //overridePendingTransition(R.anim.animation, R.anim.animation2);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                //Toast.makeText(getBaseContext(), R.string.info_sms_generic, Toast.LENGTH_SHORT).show();
                MainActivity.this.ShowToast(R.string.info_sms_generic, Toast.LENGTH_LONG);
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                //Toast.makeText(getBaseContext(), R.string.info_sms_noservice, Toast.LENGTH_SHORT).show();
                MainActivity.this.ShowToast(R.string.info_sms_noservice, Toast.LENGTH_LONG);
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                //Toast.makeText(getBaseContext(), R.string.info_sms_nullpdu, Toast.LENGTH_SHORT).show();
                MainActivity.this.ShowToast(R.string.info_sms_nullpdu, Toast.LENGTH_LONG);
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                //Toast.makeText(getBaseContext(), R.string.info_sms_radioof, Toast.LENGTH_SHORT).show();
                MainActivity.this.ShowToast(R.string.info_sms_radioof, Toast.LENGTH_LONG);
                break;
            }
        }
    }
	
	
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
			GPSstate.setText("GPS ��������, ��������� ��� ���������");
			GPSstate.setTextColor(Color.RED);
			break;
		case GPS_GETTING_COORDINATS :
			GPSstate.setText("����������� ���������...");
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
				
				GPSstate.setText("���������� ��������, ��������: " + accuracy + " �. ");
						//+ "\t\n�������:\t" + loc.getLongitude() 	    
		                //+ "\n������:\t" + loc.getLatitude());
				GPSstate.setTextColor(Color.GREEN);
				sendBtn.setEnabled(true);
				
			}
			else {
				GPSstate.setText("������ � �������������� ����������");
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
	
		menu.add(Menu.NONE, IDM_SETTINGS, Menu.NONE, "��������� ��� �����");
		return(super.onCreateOptionsMenu(menu));
	}
		
		
// �������
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case SEND_SMS_DIALOG_ID:
        	  mSMSProgressDialog = new ProgressDialog(MainActivity.this);
        	  //mCatProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        	  mSMSProgressDialog.setCanceledOnTouchOutside(false);
        	  mSMSProgressDialog.setCancelable(false);
        	  mSMSProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        	  mSMSProgressDialog.setMessage("�������� SMS...");
        	  return mSMSProgressDialog;
        }
        return null;
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
	
	// Send SMS
	/*
	public void onClickSendSMS(View view) {
		String message = smsEdit.getText().toString();
		
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(phoneNumber, null, message, null, null);
	};*/
	
	protected void ShowToast(int txt, int lng) {
		Toast toast = Toast.makeText(MainActivity.this, txt, lng);
	    toast.setGravity(Gravity.TOP, 0, 0);
	    toast.show();
	} 
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        phoneNumber = "+79197066604";
        
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
    	        } else {
    	        	Pause_GPS_Scanning();
    	        	// ������ �����, �.�. ������ ����� ����� ������ 
    	        	// ��� ���������� �����, � �� ��� ����� ���������� � �������
    	    		printLocation(null, GPS_PAUSE_SCANNING); 
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
        
      
        /*
        SendSms sendSms = new SendSms();
		DeliverySms deliverySms = new DeliverySms();
		registerReceiver(sendSms, new IntentFilter(SENT));
		registerReceiver(deliverySms, new IntentFilter(DELIVERED));
		 
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
		PendingIntent delivertPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
        */
	
        // Show keyboard
        smsEdit = (EditText)findViewById(R.id.editText2);
        smsEdit.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        
        //Prepare SMS Listeners
        sendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (smsEdit.getText().toString().equals("")
                        | smsEdit.getText().toString().equals(null)) {
                    //Toast.makeText(MainActivity.this, "enter_ph_no", Toast.LENGTH_LONG).show();
                    MainActivity.this.ShowToast(R.string.error_sms_empty, Toast.LENGTH_LONG);
                } else {

				
                        sendSMS(phoneNumber/*smsEdit.getText().toString()*/, smsEdit.getText().toString());
                        //finish();


                }

            }
        });
        
        
        
    }
}
