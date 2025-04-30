/**
 * Copyright (C) 2023 multiPOS, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package cloud.multipos.pos.db;

import android.app.Activity;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.SQLException;

import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.HashMap;
import java.util.Map;

import cloud.multipos.pos.Pos;
import cloud.multipos.pos.util.*;

public class DB {

	 public DB (Activity activity) {

		  this.activity = activity;
		  instance = this;
		  
		  tmap = new Jar ();
		  for (String [] table: tables) {

				String tmp = table [1].replace ("(10,2)", "");
								
				String [] cols = tmp.split (",");
													 
				tmap.put (table [0], new Jar ().put ("id", INTEGER));

				for (int i=1; i<cols.length; i++) {

					 String [] col =  cols [i].trim ().split (" ");
					 
					 int colType = -1;

					 if ((col [1].startsWith ("integer")) || (col [1].startsWith ("bigint"))) {
						  colType = INTEGER;
					 }
					 else if (col [1].startsWith ("tinyint")) {
						  colType = INTEGER;
					 }
					 else if ((col [1].startsWith ("varchar")) || (col [1].startsWith ("char"))) {
						  colType = STRING;
					 }
					 else if (col [1].startsWith ("time")) {
						  colType = TIMESTAMP;
					 }
					 else if (col [1].startsWith ("decimal") || col [1].startsWith ("double")) {
						  colType = DECIMAL;
					 }
					 else if (col [1].startsWith ("text")) {
						  colType = TEXT;
					 }

					 tmap.get (table [0])
						  .put (col [0], colType);
				}
		  }
	 }

	 /**
	  *
	  */
	 
	 public void open () {

		  if (dbHelper == null) {
				
				dbHelper = new DBHelper (activity);
				database = dbHelper.getWritableDatabase ();
		  }
		  
		  ready = true;
	 }

	 /**
	  *
	  */
	 
	 public void close () {

		  if (dbHelper != null) {
				dbHelper.close ();
		  }
		  
		  dbHelper = null;
		  ready = false;
	 }

	 public boolean ready () { return ready; }
	 
	 private class DBHelper extends SQLiteOpenHelper {

		  // Database creation sql statement
		  
		  public DBHelper (Context context) {
				super (context, DB.DATABASE_NAME, null, DB.DATABASE_VERSION);
		  }

		  @Override
		  public void onCreate (SQLiteDatabase database) {

				Logger.i ("onCreate db");
				for (int i=0; i<tables.length; i++){

					 Logger.x ("create table " + tables [i] [0] + tables [i] [1]);
					 database.execSQL ("create table " + tables [i] [0] + tables [i] [1]);										
				}

				open ();
		  }

		  @Override
		  public void onUpgrade (SQLiteDatabase database, int oldVersion, int newVersion) {

				Logger.i ("onUpgrade db " + oldVersion + " " + newVersion);

				switch (newVersion) {

					 // tables -> tab and add session_id and employee id

				case 340:
					 
					 break;
				}
		  }
	 }
	 
	 /**
	  *
	  */
	 
	 public int insert (String table, Jar entity) {

		  int id = 0;
		  synchronized (activity) {

				if ((!entity.has ("id")) || (entity.getInt ("id") == 0)) {
				
					 id = nextID (table);
					 entity.put ("id", id);
				}
		  		  
				ContentValues values = new ContentValues ();
				Iterator keys = entity.keys ();
		  
				while (keys.hasNext ()) {

					 Map.Entry pair = (Map.Entry) keys.next ();
					 String key = (String) pair.getKey ();

					 if ((tmap.get (table) != null) && tmap.get (table).has (key)) {  // valid column?

						  if (entity.getString (key, null) != null) {
						  
								values.put (key, entity.getString (key));
						  }
					 }
				}

				
				database.insertOrThrow (table, null, values);
		  }
		  
		  entity.put ("id", id);
		  return id;
	 }
	 
	 /**
	  *
	  */
	 
	 public int nextID (String table) {

		  DbResult nextResult = new DbResult ("select max(id) from " + table, Pos.app.db ());
		  if (nextResult.fetchRow ()) {

				int next = nextResult.row ().getInt ("max(id)");
				return next + 1;
		  }
		  else {

				return -1;
		  }
	 }
	 
	 /**
	  *
	  */
	 public void update (String table, int id, Jar fields) {
		  
		  String update = "update " + table + " set ";
		  String sep = "";
		  Iterator iter = fields.entrySet ().iterator ();
		  
		  while (iter.hasNext ()) {
					 
				Map.Entry pair = (Map.Entry) iter.next ();
					 
				String col = (String) pair.getKey ();
				Object val = pair.getValue ();
				
				if ((tmap.get (table) != null) && tmap.get (table).has (col)) {  // valid column?
					 
					 if (val instanceof Integer) {

						  update += sep + col + " = " + ((Integer) val).intValue ();
					 }
					 else if (val instanceof Double) {
						  
						  update += sep + col + " = " + ((Double) val).doubleValue ();
					 }
					 else if (val instanceof String) {
						  
						  update += sep + col + " = '" + (String) val + "'";
					 }
					 sep = ", ";
				}
		  }

		  update += " where id = " + id;

		  // Logger.x ("update... " + update);

		  synchronized (activity) {
				
				database.execSQL (update);										
		  }
	 
	 }

	 public void update (String table, Jar updateParams) {
		  
		  String update = "update " + table + " set ";
		  String sep = "";
		  		  
		  Iterator cols = updateParams.keys ();
		  
		  while (cols.hasNext ()) {

				Map.Entry pair = (Map.Entry) cols.next ();
					 
				String col = (String) pair.getKey ();

				if ((tmap.get (table) != null) && tmap.get (table).has (col)) {  // valid column?
					 
					 switch (tmap.get (table).getInt (col)) {
					 
					 case INTEGER:
					 case BOOLEAN:
						  
						  update += sep + col + " = " + updateParams.getInt (col);
						  break;
						  
					 case DECIMAL:

						  update += sep + col + " = " + updateParams.getDouble (col);
						  break;
						  
					 case STRING:
					 case TEXT:
					 case TIMESTAMP:
						  
						  update += sep + col + " = '" + updateParams.getString (col) + "'";
						  break;
					 }
					 
					 sep = ", ";
				}	  
		  }
		  
		  update += " where id = " + updateParams.getInt ("id");
		  
		  synchronized (activity) {
				database.execSQL (update);
		  }
	 }

	 /**
	  *
	  */
	 public void delete (String table, int value) {
		  
		  synchronized (activity) {

				if (ready ()) {
					 
					 database.delete (table, "id=" + value, null);
				}
		  }
	 }

	 /**
	  *
	  */
	 public void exec (String query) {
		  		  
		  synchronized (activity) {
				
				if (ready ()) {
					 
					 database.execSQL (query);
				}
		  }
	 }

	 /**
	  *
	  */
	 public ResultSet query (String query) {

		  synchronized (activity) {

				Cursor cursor = database.rawQuery (query, null);

				if (cursor == null) {
					 
					 return new ResultSet (); 
				}
				else {
					 
					 return new ResultSet (cursor);
				}
		  }
	 }

	 /**
	  *
	  */
	 public DbQuery find (String table) {

		  return new DbQuery (table);
	 }

	 /**
	  *
	  */
	 public boolean has (String table) {

		  for (int i=0; i<tables.length; i++){
				if (table.equals (tables [i] [0])) return true;
		  }
		  return false;
	 }

	 public double getDouble (int col) {
		  if (cursor != null) {
				return cursor.getDouble (col);
		  }
		  else {
				return 0;
		  }
	 }

	 public int getInt (int col) {
		  if (cursor != null) {
				return cursor.getInt (col);
		  }
		  else {
				return 0;
		  }
	 }

	 public String getString (int col) {
		  
		  if (cursor != null) {
				return cursor.getString (col);
		  }
		  else {
				return null;
		  }
	 }

	 public boolean isEmpty () { return isEmpty; }

	 public String timestamp (Date date) {

		  SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		  df.setTimeZone (TimeZone.getTimeZone ("GMT"));
		  return df.format (date);
	 }

	 private boolean hasCol (String table, String col) {
		  
		  return ((tmap.get (table) != null) && tmap.get (table).has (col));
	 }

	 public boolean hasTable (String table) {

		  return tmap.get (table).size () > 0;
	 }

	 
	 /**
	  * tables
	  */

	 private static String [] [] tables = {

		  { "bo_updates", 
			 "(id integer not null, " +
			 "business_unit_id integer DEFAULT NULL," +
			 "update_time timestamp DEFAULT NULL ," +
			 "update_table varchar(50) DEFAULT NULL," +
			 "update_id integer DEFAULT NULL," +
			 "update_action integer DEFAULT NULL," +
			 "execution_time timestamp DEFAULT NULL)"},

		  { "business_units",
			 "(id integer not null, " +
			 "business_unit_id integer DEFAULT NULL," +
			 "business_name varchar(100) DEFAULT NULL," + 
			 "business_type integer DEFAULT '0'," + 
			 "contact varchar(100) DEFAULT NULL," + 
			 "email varchar(100) DEFAULT NULL," + 
			 "phone_1 varchar(20) DEFAULT NULL," + 
			 "phone_2 varchar(20) DEFAULT NULL," +
			 "business_number varchar(100) DEFAULT NULL," +
			 "addr_1 varchar(100) DEFAULT NULL," +
			 "addr_2 varchar(100) DEFAULT NULL," +
			 "city varchar(100) DEFAULT NULL," +
			 "state varchar(100) DEFAULT NULL," +
			 "postal_code varchar(100) DEFAULT NULL," +
			 "start_of_week integer DEFAULT NULL," +
			 "timezone varchar(100) DEFAULT NULL," +
			 "locale varchar(10) DEFAULT NULL," +
			 "lat integer DEFAULT NULL," +
			 "lon integer DEFAULT NULL," +
			 "jar text DEFAULT '')"},

		  { "business_unit_hours",
			 "(id integer NOT NULL," + 
			 "location_id integer DEFAULT NULL," + 
			 "dow integer DEFAULT NULL," + 
			 "twenty_four tinyint(1) DEFAULT '0'," + 
			 "open timestamp DEFAULT NULL," + 
			 "close timestamp DEFAULT NULL)"},

		  { "currencies",
			 "(id integer not null," + 
			 "currency_code varchar(10)," + 
			 "currency_name varchar(50)," + 
			 "locale varchar(50)," + 
			 "default_currency integer DEFAULT 0," + 
			 "conversion_rate double DEFAULT NULL," + 
			 "update_time timestamp DEFAULT NULL)"},
	 
		  { "currency_denoms",
			 "(id integer NOT NULL," + 
			 "currency_id integer DEFAULT NULL," + 
			 "denom_name varchar(50)," + 
			 "denom integer DEFAULT '0'," + 
			 "denom_type integer DEFAULT '0')"},
	 	 
		  { "customers",
			 "(id integer not null, " +
			 "uuid varchar(100) default null,  " +
			 "customer_no varchar(32) DEFAULT NULL," + 
			 "name varchar(32) DEFAULT NULL," + 
			 "contact varchar(32) DEFAULT NULL," + 
			 "fname varchar(32) DEFAULT NULL," + 
			 "lname varchar(32) DEFAULT NULL," + 
			 "mi char(13) DEFAULT NULL," + 
			 "salutation varchar(32) DEFAULT NULL," + 
			 "addr_1 varchar(100) DEFAULT NULL," +
			 "addr_2 varchar(100) DEFAULT NULL," +
			 "city varchar(100) DEFAULT NULL," +
			 "state varchar(100) DEFAULT NULL," +
			 "postal_code varchar(100) DEFAULT NULL," +
			 "country_code varchar(10) DEFAULT NULL," +
			 "currency_code varchar(10) DEFAULT NULL," +
			 "email varchar(100) DEFAULT NULL," + 
			 "phone varchar(20) DEFAULT NULL," + 
			 "mobile varchar(20) DEFAULT NULL," +
			 "tax_exempt tinyint(1) DEFAULT '0'," + 
			 "blocked tinyint(1) DEFAULT '0'," + 
			 "reason_code integer DEFAULT '0'," + 
			 "balance decimal(10,2) DEFAULT 0," + 
			 "over_due_amount decimal(10,2) DEFAULT 0," + 
			 "pin varchar(10) DEFAULT NULL," +
			 "last_visit timestamp DEFAULT NULL ," + 
			 "total_visits integer DEFAULT '0'," + 
			 "total_sales decimal(10,2) DEFAULT 0," + 
			 "loyalty_points integer DEFAULT '0'," + 
			 "last_update timestamp NOT NULL default current_timestamp)"},
	 
		  { "departments",
			 "(id integer not null, " +
			 "uuid varchar(100) default null,  " +
			 "department_id integer DEFAULT '0'," + 
			 "update_time timestamp DEFAULT NULL ," + 
			 "department_desc varchar(100) DEFAULT NULL," + 
			 "department_no integer DEFAULT NULL," + 
			 "department_type integer DEFAULT '0'," + 
			 "locked tinyint(1) DEFAULT '0'," + 
			 "is_negative tinyint(1) DEFAULT '0'," + 
			 "department_order integer DEFAULT '0'," +
			 "business_unit_id integer DEFAULT '0'," +
			 "tax_group_id integer DEFAULT NULL," +
			 "params text)"},

		  { "department_sizes",
			 "(id integer not null, " +
			 "department_id integer DEFAULT '0'," + 
			 "size_order integer DEFAULT '0'," + 
			 "size_desc varchar(100) DEFAULT NULL)"},

		  { "department_mods",
			 "(id integer not null, " +
			 "department_id integer DEFAULT '0'," + 
			 "update_time timestamp DEFAULT NULL ," + 
			 "mod_type integer DEFAULT 0," + 
			 "mod_desc varchar(100) DEFAULT NULL," + 
			 "cost decimal(10,2) DEFAULT 0," + 
			 "price decimal(10,2) DEFAULT 0)"},

		  { "employees",
			 "(id integer not null, " +
			 "profile_id integer DEFAULT '0'," + 
			 "username varchar(32) DEFAULT NULL," + 
			 "password varchar(32) DEFAULT NULL," + 
			 "fname varchar(32) DEFAULT NULL," + 
			 "lname varchar(32) DEFAULT NULL," + 
			 "mi varchar(32) DEFAULT NULL," + 
			 "update_time timestamp DEFAULT NULL," + 
			 "last_logon timestamp DEFAULT CURRENT_TIMESTAMP, " + 
			 "logged_on tinyint(1) DEFAULT '0')"},

		  { "items", 
			 "(id integer not null, " +
			 "uuid varchar(100) default null,  " +
			 "update_time timestamp default null,  " +
			 "department_id integer default '0',  " +
			 "sku varchar(20) default null,  " +
			 "item_desc varchar(100) default null,  " +
			 "locked tinyint(1) default '0',  " +
			 "enabled tinyint(1) default '1')"},

		  { "item_prices",
			 "(id integer not null, " +
			 "item_id integer DEFAULT '0'," + 
			 "business_unit_id integer DEFAULT '0'," + 
			 "tax_group_id integer DEFAULT '0'," + 
			 "tax_inclusive tinyint(1) DEFAULT '0'," + 
			 "tax_exempt tinyint(1) DEFAULT '0'," + 
			 "price decimal(10,2) DEFAULT 0," + 
			 "cost decimal(10,2) DEFAULT 0," + 
			 "class varchar(100) default null, " +
			 "pricing text)"},
		  
		  { "item_links",
			 "(id integer not null, " +
			 "item_id integer DEFAULT '0'," + 
			 "item_link_id integer DEFAULT '0'," + 
			 "addon_id integer DEFAULT '0'," + 
			 "business_unit_id integer DEFAULT '0'," +
			 "link_type integer DEFAULT '0'," +
			 "apply_addons tinyint(1) DEFAULT '0')"},

		  { "addons",
			 "(id integer not null," +
			 "description varchar(50) DEFAULT NULL," +
			 "class varchar(50) DEFAULT NULL," +
			 "addon_type integer DEFAULT NULL DEFAULT '0'," + 
			 "jar text, " +
			 "reapply integer DEFAULT 0," +
			 "priority integer DEFAULT 0)"},

		  { "addon_links",
			 "(id integer not null," +
			 "addon_id integer DEFAULT '0'," + 
			 "addon_link_id integer DEFAULT '0'," + 
			 "addon_type integer DEFAULT '0')"},

		  { "pos_events",
			 "(id integer primary key autoincrement NOT NULL," + 
			 "category integer DEFAULT NULL," + 
			 "severity integer DEFAULT NULL," + 
			 "create_time timestamp default current_timestamp," +
			 "event_text text)"},
	 
		  { "pos_sessions",
			 "(id integer primary key autoincrement not null," +
			 "business_unit_id integer DEFAULT NULL," + 
			 "pos_no integer DEFAULT NULL," + 
			 "start_time timestamp default current_timestamp," + 
			 "complete_time timestamp DEFAULT NULL)"},

		  { "pos_session_totals",
			 "(id integer primary key autoincrement not null," +
			 "pos_session_id integer DEFAULT NULL," + 
			 "total_type integer DEFAULT NULL," + 
			 "total_type_id integer DEFAULT NULL," + 
			 "total_type_desc varchar(32) DEFAULT NULL," +
			 "total_sub_type_desc varchar(32) DEFAULT NULL," +
			 "quantity integer DEFAULT NULL," + 
			 "amount double DEFAULT NULL," + 
			 "update_time timestamp default null," + 
			 "create_time timestamp default null)"},
		  
		  { "pos_session_counts",
			 "(id integer primary key autoincrement not null," +
			 "pos_session_id integer DEFAULT 0," + 
			 "denom_id integer DEFAULT 0," + 
			 "denom_count integer DEFAULT 0)"},

		  { "profile_permissions",
			 "(id integer NOT NULL," + 
			 "profile_id integer DEFAULT NULL," + 
			 "profile_desc varchar(32) DEFAULT NULL," +
			 "profile_class varchar(100) DEFAULT NULL)"},
		  
		  { "profiles",
			 "(id integer NOT NULL," + 
			 "profile_desc varchar(32) DEFAULT NULL)"},

		  { "tax_groups",
			 "(id integer NOT NULL," + 
			 "short_desc varchar(100) DEFAULT NULL)"},

		  { "taxes",
			 "(id integer NOT NULL," + 
			 "tax_group_id integer DEFAULT NULL," + 
			 "rate double DEFAULT NULL," + 
			 "alt_rate double DEFAULT NULL," + 
			 "short_desc varchar(100) DEFAULT NULL," +
			 "type varchar(20))"},

		  { "tickets",
			 "(id integer primary key autoincrement not null," +
			 "pos_session_id integer DEFAULT NULL," + 
			 "ticket_no varchar(20) DEFAULT NULL," + 
			 "business_unit_id integer DEFAULT NULL," + 
			 "pos_no integer DEFAULT NULL," + 
			 "training_mode tinyint(1) DEFAULT '0'," + 
			 "drawer_no integer DEFAULT '0'," + 
			 "ticket_type integer DEFAULT '0'," + 
			 "state integer DEFAULT 0," + 
			 "flags integer DEFAULT 0," + 
			 "reason_code integer DEFAULT 0," + 
			 "create_time timestamp NOT NULL default current_timestamp," + 
			 "start_time timestamp DEFAULT NULL," + 
			 "complete_time timestamp DEFAULT NULL," + 
			 "due_date timestamp DEFAULT NULL," + 
			 "employee_id integer DEFAULT '0'," + 
			 "customer_id integer DEFAULT '0'," + 
			 "clerk_id integer DEFAULT '0'," + 
			 "table_id integer DEFAULT '0'," + 
			 "tag varchar(32) DEFAULT NULL," + 
			 "total decimal(10,2) DEFAULT 0," + 
			 "discounts decimal(10,2) DEFAULT 0," + 
			 "item_count integer DEFAULT 0," + 
			 "void_items integer DEFAULT 0," + 
			 "tender_desc varchar(32) DEFAULT NULL," + 
			 "uuid text DEFAULT ''," +
			 "recall_key text DEFAULT ''," +
			 "ticket_text text DEFAULT ''," +
			 "take_out integer DEFAULT 0," +
			 "tip integer DEFAULT 0, " + 
			 "ticket_id integer DEFAULT 0, " + 
			 "data_capture text default '')"},

		  { "ticket_items",
			 "(id integer primary key autoincrement not null," +
			 "ticket_id integer DEFAULT NULL DEFAULT '0'," + 
			 "apply_addons tinyint(1) default '1',  " +
			 "item_id integer DEFAULT '0'," + 
			 "sku varchar(20) DEFAULT NULL," + 
			 "quantity integer DEFAULT 0," + 
			 "amount decimal(10,2) DEFAULT 0," + 
			 "cost decimal(10,2) DEFAULT 0," + 
			 "pricing_opt varchar(32) DEFAULT NULL," + 
			 "item_desc varchar(100) DEFAULT NULL," + 
			 "state integer DEFAULT 0," + 
			 "flags integer DEFAULT 0," + 
			 "complete integer DEFAULT 0," + 
			 "reason_code integer DEFAULT 0," + 
			 "department_id integer DEFAULT 0," + 
			 "tax_exempt integer DEFAULT 0," + 
			 "tax_incl integer DEFAULT 0," + 
			 "tax_group_id integer DEFAULT 0," + 
			 "ticket_item_id integer DEFAULT 0," + 
			 "link_type integer DEFAULT 0," + 
			 "metric real DEFAULT 0," + 
			 "metric_type integer DEFAULT 0," + 
			 "entry_mode varchar(20) DEFAULT NULL," + 
			 "data_capture text default '')"}, 

		  { "ticket_item_addons",
			 "(id integer primary key autoincrement not null," +
			 "ticket_item_id integer DEFAULT NULL DEFAULT '0'," + 
			 "addon_id integer DEFAULT NULL DEFAULT '0'," + 
			 "addon_type integer DEFAULT NULL DEFAULT '0'," + 
			 "addon_amount decimal(10,2) DEFAULT NULL," + 
			 "addon_quantity int DEFAULT '1'," + 
			 "addon_description text," + 
			 "addon_data_capture text default '')"}, 

		  { "ticket_addons",
			 "(id integer primary key autoincrement not null," +
			 "ticket_id integer DEFAULT NULL DEFAULT '0'," + 
			 "addon_id integer DEFAULT NULL DEFAULT '0'," + 
			 "addon_type integer DEFAULT NULL DEFAULT '0'," + 
			 "addon_amount decimal(10,2) DEFAULT NULL," + 
			 "addon_quantity double DEFAULT '1'," + 
			 "addon_description text," + 
			 "addon_data_capture text default '')"}, 

		  { "ticket_taxes",
			 "(id integer primary key autoincrement not null," +
			 "ticket_id integer DEFAULT NULL DEFAULT '0'," + 
			 "tax_group_id integer DEFAULT NULL," + 
			 "tax_incl integer DEFAULT NULL," + 
			 "tax_amount decimal(10,2) DEFAULT NULL," + 
			 "short_desc varchar(100) DEFAULT NULL)"}, 

		  { "ticket_tenders",
			 "(id integer primary key autoincrement not null," +
			 "ticket_id integer DEFAULT NULL," +
			 "tender_id integer DEFAULT NULL," +
			 "status integer DEFAULT NULL," +
			 "complete integer DEFAULT 0," + 
			 "tender_type varchar(32) DEFAULT NULL," +
			 "sub_tender_type varchar(32) DEFAULT NULL," +
			 "amount decimal(10,2) DEFAULT 0," +
			 "returned_amount decimal(10,2) DEFAULT 0," +
			 "tendered_amount decimal(10,2) DEFAULT 0," +
			 "tip decimal(10,2) DEFAULT 0," +
			 "locale_language char(2) DEFAULT NULL," +
			 "locale_country char(2) DEFAULT NULL," +
			 "locale_variant varchar(4) DEFAULT NULL," +
			 "data_capture text default '')"},

		  { "pos_updates", 
			 "(id integer primary key autoincrement not null," +
			 "create_time timestamp default current_timestamp," +
			 "status integer default 0," +
			 "update_table varchar(50) DEFAULT NULL," +
			 "update_id integer DEFAULT NULL," +
			 "update_action integer DEFAULT NULL)"},
		  
		  { "tenders", 
			 "(id integer primary key autoincrement not null," +
			 "tender_desc varchar(32) default null," +
			 "locale_language char(2) DEFAULT NULL," +
			 "locale_country char(2) DEFAULT NULL," +
			 "locale_variant varchar(4) DEFAULT NULL)" },

		  { "pos_units",
			 "(id integer not null, " +
			 "business_unit_id integer DEFAULT NULL," +
			 "update_time timestamp default current_timestamp," +
			 "create_time timestamp default NULL," +
			 "dbname varchar(50) default null," +
			 "pos_no integer DEFAULT '0')"},

		  { "pos_configs",
			 "(id integer not null, " +
			 "config_desc varchar(200) DEFAULT NULL," +
			 "update_time timestamp default current_timestamp," +
			 "create_time timestamp default NULL," +
			 "config_type integer DEFAULT NULL," +
			 "config text DEFAULT NULL," +
			 "status integer DEFAULT '0'," +
			 "business_unit_id integer DEFAULT '0')"},
		  
		  { "suppliers",
			 "(id integer primary key autoincrement not null," +
			 "supplier_name varchar(100) DEFAULT NULL," +
			 "addr1 varchar(100) DEFAULT NULL," +
			 "addr2 varchar(100) DEFAULT NULL," +
			 "addr3 varchar(100) DEFAULT NULL," +
			 "addr4 varchar(100) DEFAULT NULL," +
			 "addr5 varchar(100) DEFAULT NULL," +
			 "addr6 varchar(100) DEFAULT NULL," +
			 "phone1 varchar(20) DEFAULT NULL," +
			 "phone2 varchar(20) DEFAULT NULL," +
			 "phone3 varchar(20) DEFAULT NULL," +
			 "country_code varchar(32) DEFAULT NULL," +
			 "time_zone varchar(32) DEFAULT NULL," +
			 "email varchar(100) DEFAULT NULL," +
			 "contact1 varchar(100) DEFAULT NULL," +
			 "contact2 varchar(100) DEFAULT NULL," +
			 "contact3 varchar(100) DEFAULT NULL," +
			 "supplier_no varchar(100) DEFAULT NULL," +
			 "jar text DEFAULT NULL)"},
		  		  
		  { "videos",
			 "(id integer primary key autoincrement not null," +
			 "file_name varchar(200) DEFAULT NULL," +
			 "start_time bigint," +
			 "complete_time bigint," +
			 "video_type integer DEFAULT NULL)"},
		  
		  { "volumes",
			 "(id integer not null," + 
			 "volume_desc varchar(50)," + 
			 "unit_of_measure double DEFAULT NULL," + 
			 "update_time timestamp DEFAULT NULL)"},
	 
		  { "volume_sizes",
			 "(id integer NOT NULL," + 
			 "volume_id integer DEFAULT NULL," + 
			 "volume_desc varchar(50)," + 
			 "default_volume integer defualt '0'," + 
			 "volume integer DEFAULT '0'," + 
			 "volume_order integer DEFAULT '0')"},
	 
	 };

	 public static final int DATABASE_VERSION = 340;
	 public static final String DATABASE_NAME = "pos.db";
	 
	 public static final int INTEGER =   1;
	 public static final int DECIMAL =   2;
	 public static final int STRING =    3;
	 public static final int TIMESTAMP = 4;
	 public static final int TEXT =      5;
	 public static final int BOOLEAN =   6;
	 
	 /**
	  * vars...
	  */

	 private Cursor cursor = null;
	 private boolean first = false;
	 private boolean isEmpty = false;
	 private SQLiteDatabase database = null;
	 private static DBHelper dbHelper = null;
	 private static DB instance = null;
	 private boolean ready = false;
	 private Jar tmap;
	 
	 private Activity activity;
	 private boolean debug = false;
	 public void debug (boolean debug) { this.debug = debug; }

	 public SQLiteDatabase database () { return database; }
}
