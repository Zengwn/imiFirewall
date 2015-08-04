package com.imiFirewall.activity;

import java.util.List;

import com.imiFirewall.PersonEntity;
import com.imiFirewall.R;
import com.imiFirewall.imiDataBase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

public class ActivityBlack extends ListActivity implements OnItemClickListener, OnClickListener{
	
	ListView lv = null;
	List<PersonEntity> blackList = null;
	ImageButton ibtBack, ibtAdd;
	ArrayAdapter<PersonEntity> adapter = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.black_layout);
		
		imiDataBase imidb = new imiDataBase(ActivityBlack.this);
		imidb.open();
		blackList = imidb.queryBlack();
		imidb.close();
		
		ibtBack = (ImageButton) findViewById(R.id.black_back);
		ibtBack.setOnClickListener(this);
		
		ibtAdd = (ImageButton) findViewById(R.id.black_add);
		ibtAdd.setOnClickListener(this);
		
		lv = getListView();
		
		adapter = new ArrayAdapter<PersonEntity>(this, R.layout.blacklist, blackList);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final int po = position;
		AlertDialog.Builder dialog =  new AlertDialog.Builder(this);
		String[] op = {"从黑名单中移除"};
		dialog.setTitle("选择操作");
		DialogInterface.OnClickListener itemClickLisenter = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					blackList.remove(po);
					adapter.notifyDataSetChanged();
				}
			}
		};
		dialog.setItems(op, itemClickLisenter);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.black_add)
			startActivityForResult(new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI), 0);
		if (v.getId() == R.id.black_back) {
			this.finish();
			//存储数据
			new Thread(new Runnable() {
				@Override
				public void run() {
					imiDataBase imidb = new imiDataBase(ActivityBlack.this);
					imidb.open();
					imidb.dropAllBlack("BlackTable");
					for (PersonEntity personEntity:blackList) {
						imidb.insert("BlackTable", personEntity);
					}
					imidb.close();
				}
			}).start();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			ContentResolver contentResolver = getContentResolver();
			Uri contactData = data.getData();
			Cursor cursor = managedQuery(contactData, null, null,null, null);
			cursor.moveToFirst();
			String userName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor phone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
                    null, 
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, 
                    null, 
                    null);
			while (phone.moveToNext()) {
				PersonEntity personEntity = new PersonEntity();
				personEntity.setName(userName);
				personEntity.setTel(phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
				blackList.add(personEntity);
				adapter.notifyDataSetChanged();
			}
		}
	}
	

}
