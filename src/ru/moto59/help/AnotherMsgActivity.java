package ru.moto59.help;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class AnotherMsgActivity extends Activity {

	// Menu
	public static final int IDM_SETTINGS = 101;
	public static final int IDM_RULES = 102;
	
	// Dialogs
    private static final int SEND_SMS_DIALOG_ID = 0;
    private final static int PHONE_DIALOG_ID = 1;
    private final static int RULES_DIALOG_ID = 2;
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
	
	// SMS thread
    ThreadSendSMS mThreadSendSMS;
	
	// Views
	TextView GPSstate;
	Button sendBtn;
	CheckBox checkBox;
	EditText smsEdit;
	
	// Globals
	private String phoneNumber;
	private String coordsToSend;
	
  


		
	
    
    
	@Override
	protected void onResume() {
		super.onResume();
		//Resume_GPS_Scanning();
	}
		
	@Override
	protected void onPause() {
		super.onPause();
		//Pause_GPS_Scanning();

	}
	

	


	
	// ------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_msg);
        
        Button btn = (Button)findViewById(R.id.button1);
        btn.setOnClickListener(new OnClickListener() {

        	@Override
            public void onClick(View v) {
        		MainActivity.smsEdit.setText("");
        		finish();
        		
            }
        });
       
    }
    
	// ------------------------------------------------------------------------------------------
    
}
