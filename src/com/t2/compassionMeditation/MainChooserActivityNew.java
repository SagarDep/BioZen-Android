/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t2.compassionMeditation;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.t2.R;
import com.t2.filechooser.FileChooser;


public class MainChooserActivityNew extends Activity implements OnTouchListener {
	private static final String TAG = "BFDemo";	

    private DisplayMetrics mDisplayMetrics = new DisplayMetrics();	

	private MainChooserActivityNew mInstance;
	private Gallery mGallery;
	private float fX, fY;	
	
    // Vibration (haptic feedback)
    private Vibrator mVibrator;
    private static final long VIBRATE_SHORT = 20;  // msec
    private static final long VIBRATE_LONG = 20;  // msec

    
	// ID index variables - The enumerations MUST match the image references below
	private static final int ID_NONE = 0;
	private static final int ID_LEARN = 1;
	private static final int ID_VIEW_ACTIVITY = 2;
	private static final int ID_NEW_SESSION = 3;
	private static final int ID_REVIEW = 4;
	private static final int ID_BLUETOOTH_SETTINGS = 5;
	private static final int ID_PREFERENCES = 6;
	private static final int ID_VIEW_FILES = 7;
	private static final int ID_ABOUT = 8;
	
	
    private Integer[] mThumbIds = {
            R.drawable.biozen_select,
            R.drawable.biozen_learn_up_new,
            R.drawable.biozen_view_up_new,
            R.drawable.biozen_newsession_up_new,
            R.drawable.biozen_review_up_new,
            R.drawable.biozen_bluetooth_settings,
            R.drawable.biozen_preferences,
            R.drawable.biozen_view_files,
            R.drawable.biozen_about
            };    
    
	private int mLastButtonPressed;

	/**
	 * User mode - Determines whether or not to show a dialog showing potential users
	 * @see BioZenConstants.java
	 *  PREF_USER_MODE_DEFAULT, PREF_USER_MODE_SINGLE_USER, PREF_USER_MODE_PROVIDER
	 */
	int mUserMode;
	
	/**
	 * Application version info determined by the package manager
	 */
	private String mApplicationVersion = "";	
	
	
	
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	Log.d(TAG, "keycode = " + keyCode);
		return super.onKeyDown(keyCode, event);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Log.i(TAG, this.getClass().getSimpleName() + ".onCreate()"); 	        
        mInstance = this;
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);		// This needs to happen BEFORE setContentView
        
        
        setContentView(R.layout.main_chooser_activity_layout_new);
        
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);            


        mGallery = (Gallery) findViewById(R.id.gallery);
        mGallery.setAdapter(new ImageAdapter(this));
        mGallery.setOnTouchListener(this);        
     
        mGallery.setSelection(1);	// Do this so more of the ribbon shows on startup
        String s = SharedPref.getString(this,BioZenConstants.PREF_USER_MODE,BioZenConstants.PREF_USER_MODE_DEFAULT);
        mUserMode = Integer.parseInt(s);
        
        
        
		try {
			PackageManager packageManager = this.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);			
			mApplicationVersion = info.versionName;
			Log.i(TAG, "BioZen Application Version: " + mApplicationVersion);
		} 
		catch (NameNotFoundException e) {
			   	Log.e(TAG, e.toString());
		}   
		
		if (mUserMode == BioZenConstants.PREF_USER_MODE_PROVIDER) {
			Intent intent2 = new Intent(this, SelectUserActivity.class);
			this.startActivityForResult(intent2, BioZenConstants.SELECT_USER_ACTIVITY);		
			
		} else {
	    	SharedPref.putString(this, "SelectedUser", 	"");
		}        
		
		
		Eula.show(this); 		
		
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
        Log.i(TAG, this.getClass().getSimpleName() + ".onActivityResult()"); 	        
		
		switch(requestCode) {
//			case BioZenConstants.FILECHOOSER_USER_ACTIVITY:
//				if (data == null)
//					return;
//				String sessionName = data.getStringExtra(BioZenConstants.FILECHOOSER_USER_ACTIVITY_RESULT);
//		    	Toast.makeText(this, "File Clicked: " + sessionName, Toast.LENGTH_SHORT).show();
//		    	
//				Intent intent = new Intent(this, ViewHistoryActivity.class);
//				Bundle bundle = new Bundle();
//	
//				bundle.putString(BioZenConstants.EXTRA_SESSION_NAME,sessionName);
//	
//				//Add this bundle to the intent
//				intent.putExtras(bundle);				
//				
//				this.startActivity(intent);			    	
//
//				break;
//				

			case FileChooser.FILECHOOSER_USER_ACTIVITY:
				if (data == null)
					return;
				String sessionName = data.getStringExtra(FileChooser.FILECHOOSER_USER_ACTIVITY_RESULT);
		    	Toast.makeText(this, "File Clicked: " + sessionName, Toast.LENGTH_SHORT).show();
		    	
				Intent intent = new Intent(this, Graphs1Activity.class);
				Bundle bundle = new Bundle();
		
				bundle.putString(BioZenConstants.EXTRA_SESSION_NAME,sessionName);
				bundle.putString(FileChooser.EXTRA_PROMPT_NAME,"Re-run test");
		
				//Add this bundle to the intent
				intent.putExtras(bundle);				
				
				this.startActivity(intent);			    	
		
				break;
				

		
		
		
		    case (BioZenConstants.SELECT_USER_ACTIVITY) :  
			      if (resultCode == RESULT_OK) {
			  		if (data == null)
						return;

			    	// We can't write the note yet because we may not have been re-initialized
			    	// since the not dialog put us into pause.
			    	// We'll save the note and write it at restore
			    	String userName = data.getStringExtra(BioZenConstants.SELECT_USER_ACTIVITY_RESULT);

			    	if (userName == null) {
			    		userName = "";
			    	}

			    	SharedPref.putString(this, "SelectedUser", 	userName);
			    	  
			      } 
			      break; 	
			      
		    case (BioZenConstants.INSTRUCTIONS_USER_ACTIVITY):
		    	
		    	if (mLastButtonPressed == ID_NEW_SESSION) {
					Intent intent1 = new Intent(this, MeditationActivity.class);
					this.startActivity(intent1);		
		    	}
		    	break;
		    	
		    case (BioZenConstants.USER_MODE_ACTIVITY):
		    	break;
		}
	}

    public void onNothingSelected(AdapterView parent) {
    	Log.d(TAG, "Nothing selected");
    }

//    public View makeView() {
//        ImageView i = new ImageView(this);
//        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
//        return i;
//    }


    public class ImageAdapter extends BaseAdapter {
        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);

            i.setImageResource(mThumbIds[position]);
            i.setAdjustViewBounds(true);
            i.setLayoutParams(new Gallery.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            
            i.setBackgroundColor(Color.WHITE);
            return i;
        }

        private Context mContext;

    }




	@Override
	public boolean onTouch(View v, MotionEvent event) {
	      final int actionPerformed = event.getAction();
	      final int widgetID = v.getId();
	      final float MAXPRESSRANGE = 10.0f;
	      
	      
	      if (actionPerformed == MotionEvent.ACTION_DOWN) {
	    	  fX=event.getRawX();
              fY=event.getRawY();	   
      			vibrate(VIBRATE_LONG);
              
	      }
	        
	      if (actionPerformed == MotionEvent.ACTION_UP) {
	    	  
              final float posX = event.getRawX();
              final float posY = event.getRawY();
              
              //detect if user performed a simple press
              if ((posX < fX+MAXPRESSRANGE) && (posX > fX-MAXPRESSRANGE))
              {
                  if ((posY < fY+MAXPRESSRANGE) && (posY > fY-MAXPRESSRANGE))
                  {
                      //valid press detected!

                      //convert gallery coordinates to a position
                      final int position = mGallery.pointToPosition((int)event.getX(), (int)event.getY());

                      //this is necessary to obtain the index of the view currently visible and pressed
                      final int iVisibleViewIndex = position - mGallery.getFirstVisiblePosition();

                      
                      //get a reference to the child and modify the border
                      View child = mGallery.getChildAt(iVisibleViewIndex);       
//                      Drawable d = child.getBackground();
//                      d.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
//                      child.setBackgroundColor(Color.BLACK);
 //                     ImageView i = child.g
                      
                      if (position != AdapterView.INVALID_POSITION)
                      {
                    	  mGallery.setSelection(position, true);
            	    	  startActivity(position);
      
                      }

                      // consume event
                      return true;
                  }
              }
	    	  
	      }
	        
		return false;
	}

	
	void startActivity(int position) {
		mLastButtonPressed = position; 

		switch (position) {
		
	    	case ID_LEARN:
	    		Intent intent1 = new Intent(mInstance, InstructionsActivity.class);
	    		mInstance.startActivityForResult(intent1, BioZenConstants.INSTRUCTIONS_USER_ACTIVITY);	    		
			break;
			
		    case ID_NEW_SESSION:
    			boolean instructionsOnStart = SharedPref.getBoolean(mInstance, 
				BioZenConstants.PREF_INSTRUCTIONS_ON_START, 
				BioZenConstants.PREF_INSTRUCTIONS_ON_START_DEFAULT);        

				if (instructionsOnStart) {
					intent1 = new Intent(mInstance, InstructionsActivity.class);
					mInstance.startActivityForResult(intent1, BioZenConstants.INSTRUCTIONS_USER_ACTIVITY);		
				} else {
					Intent intent = new Intent(mInstance, MeditationActivity.class);
					mInstance.startActivity(intent);		
				}		    	
		    	break;
		    case ID_REVIEW:
    			Intent intent = new Intent(mInstance, ViewSessionsActivity.class);
//    			Intent intent = new Intent(mInstance, ViewParametersActivity.class);
    			mInstance.startActivityForResult(intent, FileChooser.FILECHOOSER_USER_ACTIVITY); 		    	
		    	break;
		    case ID_VIEW_ACTIVITY:
    			intent = new Intent(mInstance, Graphs1Activity.class);
    			mInstance.startActivity(intent);		    	
		    	break;			
	    	case ID_BLUETOOTH_SETTINGS:
				intent = new Intent(this, DeviceManagerActivity.class);			
				this.startActivity(intent);		    		
	    		break;
	    	case ID_PREFERENCES:
				intent = new Intent(this, BioZenPreferenceActivity.class);
				this.startActivity(intent);		    		
	    		break;
	    	case ID_VIEW_FILES:
				intent = new Intent(mInstance, FileChooser.class);
				// We need to add an extra telling FileChooser to add a menu item for re-running the test
				Bundle bundle = new Bundle();
				bundle.putString(FileChooser.EXTRA_PROMPT_NAME,"Re-run test");
				intent.putExtras(bundle);				
				mInstance.startActivityForResult(intent, FileChooser.FILECHOOSER_USER_ACTIVITY); 			
	    		break;
	    	case ID_ABOUT:
				String content = "National Center for Telehealth and Technology (T2)\n\n";
				content += "BioZen Application\n";
				content += "Application Version: " + mApplicationVersion + "\n";
				
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				
				alert.setTitle("About");
				alert.setMessage(content);	
				alert.show();			
	    		
	    		break;
	    		
		
		}
		
	}

    /**
     * Triggers haptic feedback.
     */
    private synchronized void vibrate(long duration) {
        if (mVibrator == null) {
            mVibrator = (android.os.Vibrator)
                    this.getSystemService(Context.VIBRATOR_SERVICE);
        }
        mVibrator.vibrate(duration);
    }	

}
