/*****************************************************************
BioZen

Copyright (C) 2011 The National Center for Telehealth and 
Technology

Eclipse Public License 1.0 (EPL-1.0)

This library is free software; you can redistribute it and/or
modify it under the terms of the Eclipse Public License as
published by the Free Software Foundation, version 1.0 of the 
License.

The Eclipse Public License is a reciprocal license, under 
Section 3. REQUIREMENTS iv) states that source code for the 
Program is available from such Contributor, and informs licensees 
how to obtain it in a reasonable manner on or through a medium 
customarily used for software exchange.

Post your updates and modifications to our GitHub or email to 
t2@tee2.org.

This library is distributed WITHOUT ANY WARRANTY; without 
the implied warranty of MERCHANTABILITY or FITNESS FOR A 
PARTICULAR PURPOSE.  See the Eclipse Public License 1.0 (EPL-1.0)
for more details.
 
You should have received a copy of the Eclipse Public License
along with this library; if not, 
visit http://www.opensource.org/licenses/EPL-1.0

*****************************************************************/
package com.t2.compassionMeditation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;

//Need the following import to get access to the app resources, since thisclass is in a sub-package.
import com.t2.R;

import com.t2.compassionDB.BioSession;
import com.t2.compassionDB.BioUser;
import com.t2.compassionDB.DatabaseHelper;

public class SelectUserActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	private static final String TAG = "BFDemo";
	private static final String mActivityVersion = "1.0";
	static private SelectUserActivity instance;
	
	/**
	 * Currently selected user name (as selected at the start of the session)
	 */
	private String mCurrentBioUserName;

	private Dao<BioSession, Integer> mBioSessionDao;
	private Dao<BioUser, Integer> mBioUserDao;

	/**
	 * UI ListView for users list
	 */
	private ListView mListView;

	/**
	 * Ordered list of available BioUser 
	 * 
	 * note that we keep this list only so we can reference the currently selected session for deletion
	 */
	private List<BioUser> mCurrentUsers;	

	/**
	 * Index of currently selected user
	 * @see mCurrentUsers
	 */	
	private int mSelectedId;
	
	public void onButtonClick(View v)
	{
		 final int id = v.getId();
		    switch (id) {
		    case R.id.buttonAddUser:
				AlertDialog.Builder alert1 = new AlertDialog.Builder(this);

				alert1.setMessage("Enter new user name");

				// Set an EditText view to get user input 
				final EditText input = new EditText(this);
				alert1.setView(input);

				alert1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String newUserName = input.getText().toString();
					
					boolean found = false;
					if (mCurrentUsers != null) {
						// Make sure that the user isn't already there
						for (BioUser user: mCurrentUsers) {
							if (user.name.equalsIgnoreCase(newUserName))
								found = true;
						}
					}
					
					if (found) {
						AlertDialog.Builder alert2 = new AlertDialog.Builder(instance);

						alert2.setMessage("User Already exists");
						alert2.show();	
					}
					else {
						BioUser newuser = new BioUser(newUserName, System.currentTimeMillis());
						try {
							mBioUserDao.create(newuser);
							updateListView();						
						} catch (SQLException e) {
							Log.e(TAG, "Error adding new user" + e.toString());
						}
					}
				  }
				});

				alert1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				  }
				});

				alert1.show();		    	
				break;
		    }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);		// This needs to happen BEFORE setContentView
		
		this.setContentView(R.layout.select_user_activity_layout);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        
		
		mListView = (ListView)findViewById(R.id.listViewUsers);
		
		
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				String seletedItem = (String) mListView.getAdapter().getItem(i);
				Intent resultIntent;
				resultIntent = new Intent();
				resultIntent.putExtra(BioZenConstants.SELECT_USER_ACTIVITY_RESULT, seletedItem);
				setResult(RESULT_OK, resultIntent);
				finish();				
				
			}
		});		
		
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				mCurrentBioUserName = (String) mListView.getAdapter().getItem(i);
				mSelectedId = i;
				
				
				AlertDialog.Builder alert = new AlertDialog.Builder(instance);
				alert.setTitle("Choose Activity");
				
		    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            }
		        });				
				
		    	alert.setNeutralButton("Delete User", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	
						AlertDialog.Builder alert2 = new AlertDialog.Builder(instance);
						alert2.setMessage("Are you sure?");

						alert2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
			            	try {
								mBioUserDao.delete(mCurrentUsers.get(mSelectedId));
								updateListView();						
								
							} catch (SQLException e) {
								Log.e(TAG, "Error deleting user" + e.toString());
							}
						}
						});

						alert2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						  public void onClick(DialogInterface dialog, int whichButton) {
						  }
						});

						alert2.show();
		            }
		        });				
				
				alert.show();					
				return true;
			}
		});		

		updateListView();		
	}

	/**
	 * Populates the UI list view with current available users
	 */
	void updateListView() {
		
		ArrayList<String>  strUsers = new ArrayList<String>();
	
		// Retrieve the BuiUser object associated with object mSelectedUserName
		try {

			mBioUserDao = getHelper().getBioUserDao();
			mBioSessionDao = getHelper().getBioSessionDao();
			mCurrentUsers = mBioUserDao.queryForAll();				
			
			// Save the BioUsers so we have a list of objects we can manipulate later
			for (BioUser user: mCurrentUsers) {
				strUsers.add(user.name);
			}
			
		} catch (SQLException e) {
			Log.e(TAG, "Error Looking for accounts" + e.toString());
			AlertDialog.Builder alert2 = new AlertDialog.Builder(instance);
			alert2.setMessage("Database error " + e.toString());
			alert2.show();
		}
	
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.row_layout, R.id.label, strUsers);
	
		mListView.setAdapter(adapter);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}