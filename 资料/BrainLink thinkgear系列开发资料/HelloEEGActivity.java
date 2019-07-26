package com.test.helloeeg;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.*;

public class HelloEEGActivity extends Activity {
    
    private static final String TAG = "HelloEEG";

    BluetoothAdapter            bluetoothAdapter;
    TGDevice                    tgDevice;
    final boolean               rawEnabled = true;

    ScrollView                  sv;
    TextView                    tv;
    Button                      b;

		
    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
        
        sv = (ScrollView)findViewById( R.id.scrollView1 );
        tv = (TextView)findViewById( R.id.textView1 );
        tv.setText( "" );
        tv.append( "Android version: " + Integer.valueOf(android.os.Build.VERSION.SDK) + "\n" );
        
        // Check if Bluetooth is available on the Android device
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if( bluetoothAdapter == null ) {
            
        	// Alert user that Bluetooth is not available
        	Toast.makeText( this, "Bluetooth not available", Toast.LENGTH_LONG ).show();
        	finish();
        	return;
        	
        } else {
            
        	// Create the TGDevice 	
        	tgDevice = new TGDevice( bluetoothAdapter, handler );    	
            tv.append( "Android SDK version: " + tgDevice.getVersion() + "\n" );
        }
    
    } /* end onCreate() */
    
    @Override
    public void onStop() {
        super.onStop();
        tgDevice.close();
    }
    
    @Override
    public void onDestroy() {
    	tgDevice.close();
        super.onDestroy();
    }
    
    final Handler handler = new Handler() {
    	
        @Override
        public void handleMessage( Message msg ) {

            switch( msg.what ) {
                case TGDevice.MSG_STATE_CHANGE:
    
                    switch( msg.arg1 ) {
    	                case TGDevice.STATE_IDLE:
    	                    break;
    	                case TGDevice.STATE_ERR_BT_OFF:
    	                    tv.append( "Bluetooth is off.  Turn on Bluetooth and try again." );
    	                    break;
    	                case TGDevice.STATE_CONNECTING:       	
    	                	tv.append( "Connecting...\n" );
    	                	break;		                    
                        case TGDevice.STATE_ERR_NO_DEVICE:
                            tv.append( "No Bluetooth devices paired.  Pair your device and try again.\n" );
                            break;
    	                case TGDevice.STATE_NOT_FOUND:
    	                	tv.append( "Could not connect any of the paired BT devices.  Turn them on and try again.\n" );
    	                	break;
                        case TGDevice.STATE_CONNECTED:
                            tv.append( "Connected.\n" );
                            tgDevice.start();
                            break;
    	                case TGDevice.STATE_DISCONNECTED:
    	                	tv.append( "Disconnected.\n" );
                    } /* end switch on msg.arg1 */

                    break;
    
                case TGDevice.MSG_POOR_SIGNAL:
                	tv.append( "PoorSignal: " + msg.arg1 + "\n" );
                    break;
                
                case TGDevice.MSG_RAW_DATA:	  
                	/* Handle raw EEG/EKG data here */
                	break;
                
                case TGDevice.MSG_HEART_RATE:
            		tv.append( "Heart rate: " + msg.arg1 + "\n" );
                    break;
                
                case TGDevice.MSG_ATTENTION:
                    tv.append( "Attention: " + msg.arg1 + "\n" );
                	break;
                
                case TGDevice.MSG_MEDITATION:
                    tv.append( "Meditation: " + msg.arg1 + "\n" );
                	break;

                case TGDevice.MSG_BLINK:
                	tv.append( "Blink: " + msg.arg1 + "\n" );
                	break;
    
                default:
                	break;
                	
        	} /* end switch on msg.what */
            
        	sv.fullScroll( View.FOCUS_DOWN );
        	
        } /* end handleMessage() */
        
    }; /* end Handler */
    
    /**
     * This method is called when the user clicks on the "Connect" button.
     * 
     * @param view
     */
    public void doStuff( View view ) {

    	if( tgDevice.getState() != TGDevice.STATE_CONNECTING && 
    	    tgDevice.getState() != TGDevice.STATE_CONNECTED ) {
    	    
    		tgDevice.connect( rawEnabled );
    	}
    	
    } /* end doStuff() */
    
} /* end HelloEEGActivity() */