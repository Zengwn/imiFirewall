package com.imiFirewall.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Environment;
import android.util.JsonReader;
import android.util.JsonWriter;

import com.imiFirewall.PermEntity;
import com.imiFirewall.PersonEntity;

public class imiJson {

	public static Map<String, PermEntity> readPerm() {
		Map<String, PermEntity> perms = new HashMap<String, PermEntity>();
		try {
			FileInputStream in = new FileInputStream(
					Environment.getExternalStorageDirectory() + "/imiFirewall/permission.json");
			JsonReader reader = new JsonReader(new InputStreamReader(in,"UTF-8"));
			reader.beginArray();
			while (reader.hasNext()) {
				reader.beginObject();
				while (reader.hasNext()) {
					PermEntity permEntity = new PermEntity();
					if (reader.nextName().equals("Key"))
						permEntity.setKey(reader.nextString());
					if (reader.nextName().equals("Title"))
						permEntity.setTitle(reader.nextString());
					if (reader.nextName().equals("Memo"))
						permEntity.setMemo(reader.nextString());
					if (reader.nextName().equals("Level"))
						permEntity.setLevel(reader.nextString());
					perms.put(permEntity.getKey(), permEntity);
				}
				reader.endObject();
			}
			reader.endArray();
			reader.close();
			return perms;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<PersonEntity> readBlack() {
		List<PersonEntity> blackTel = new ArrayList<PersonEntity>();
		try {
			FileInputStream in = new FileInputStream(Environment.getExternalStorageDirectory() + "/imiFirewall/black.json");
			JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
			reader.beginArray();
			while (reader.hasNext()) {
				reader.beginObject();
				while (reader.hasNext()) {
					PersonEntity person = new PersonEntity();
					if (reader.nextName().equals("Name"))
						person.setName(reader.nextString());
					if (reader.nextName().equals("Tel"))
						person.setTel(reader.nextString());
					blackTel.add(person);
				}
				reader.endObject();
			}
			reader.endArray();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return blackTel;
	}
	
	public static void writeBlack(List<PersonEntity> persons) {
		try {
			FileOutputStream out  = new FileOutputStream(Environment.getExternalStorageDirectory() + "/imiFirewall/black.json");
			JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
			writer.setIndent(" ");
			writer.beginArray();
			for (PersonEntity person:persons) {
				writer.beginObject();
				writer.name("Name").value(person.getName());
				writer.name("Tel").value(person.getTel());
				writer.endObject();
			}
			writer.endArray();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//¶ÌÐÅ
//	public static void writeMsg(String name, String content) {
//		try {
//			FileOutputStream out = new FileOutputStream(
//					Environment.getExternalStorageDirectory() + "/imiFirewall/message/" + name + ".json");
//			JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
//			writer.setIndent(" ");
//			writer.beginObject();
//			writer.name("Content").value(content);
//			writer.endObject();
//			writer.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
//	public static String readMsg(String name) {
//		
//	}
	
	//µç»°
	
//	public static void writePerm() {
//		try {
//			FileOutputStream out  = new FileOutputStream(Environment.getExternalStorageDirectory() + "/imiFirewall/permissiontest.json");
//			JsonWriter w = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
//			w.setIndent(" ");
//			w.beginObject();
//			w.name("test").value("cesi");
//			w.endObject();
//			w.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//	}

}
