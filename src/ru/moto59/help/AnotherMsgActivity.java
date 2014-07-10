package ru.moto59.help;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class AnotherMsgActivity extends Activity {
    
	@Override
	protected void onResume() {
		super.onResume();
	}
		
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	// ------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_msg);
        
        Button btn = (Button)findViewById(R.id.button1);
        btn.requestFocus();
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
