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

import cloud.multipos.pos.Pos;
import cloud.multipos.pos.util.*;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.SQLException;

public class DbResult {

	 public DbResult (String query, DB db) {

		  if (!db.ready ()) {

				return;
		  }
		  
		  this.query = query;
		  logger (query);

		  synchronized (this) {

				cursor = db.database ().rawQuery (query, null);
				cols = new String [cursor.getColumnCount ()];
				for (int col = 0; col < cursor.getColumnCount (); col ++) {
					 cols [col] = cursor.getColumnName (col);
				}

				resultSet = new ResultSet (cursor);
		  }
	 }
	 
	 public DbResult (String table, String column, String search, DB db) {

		  this.query = "select * from " + table + " where " + column + " like " + "'" + search + "%'";
		  		  
		  synchronized (this) {
				
				cursor = db.database ().rawQuery (query, null);
				cols = new String [cursor.getColumnCount ()];
				for (int col = 0; col < cursor.getColumnCount (); col ++) {
					 cols [col] = cursor.getColumnName (col);
				}

				resultSet = new ResultSet (cursor);
		  }
	 }

	 public DbResult (String table, String column, DB db) {

		  this.query = "select * from " + table + " order by " + column;
		  
		  synchronized (this) {
				
				cursor = db.database ().rawQuery (query, null);
				cols = new String [cursor.getColumnCount ()];
				for (int col = 0; col < cursor.getColumnCount (); col ++) {
					 cols [col] = cursor.getColumnName (col);
				}
				
				resultSet = new ResultSet (cursor);
		  }
	 }
	 
	 public boolean fetchRow () {

		  if (resultSet == null) {
				
				return false;
		  }
		  
		  if (resultSet.next ()) {

				entity = new Jar ();
				for (int col = 0; col < cols.length; col ++) {
						  					 					 
					 logger ("fetch row... " + cursor.getColumnName (col) + " " + resultSet.getString (col));
					 
					 switch (cursor.getType (col)) {

					 case Cursor.FIELD_TYPE_NULL:
						  break;
					 						  
					 case Cursor.FIELD_TYPE_INTEGER:
						  entity.put (cursor.getColumnName (col), resultSet.getInt (col));
						  break;
						  
					 case Cursor.FIELD_TYPE_FLOAT:
						  entity.put (cursor.getColumnName (col), resultSet.getDouble (col));
						  break;
						  
					 case Cursor.FIELD_TYPE_STRING:
						  entity.put (cursor.getColumnName (col), resultSet.getString (col));
						  break;
						  
					 case Cursor.FIELD_TYPE_BLOB:
						  entity.put (cursor.getColumnName (col), resultSet.getString (col));
						  break;
					 }
					 
				}				
				return true;
		  }

		  return false;
	 }

	 public Jar row () {
		  return entity;
	 }

	 public static Jar findByID (String table, int id, DB db) {
		  
		  DbResult result  = new DbResult ("select * from " + table + " where id = " + id, db);
		  if (result.fetchRow ()) {
				
				Jar e = result.row ();
				return e;
		  }
		  return null;
	 }

	 public void logger (String text) {

		  if (debug) {
				Logger.d (text);
		  }
	 }

	 public void debug (boolean debug) { this.debug = debug; }
	 private boolean debug = false;
	 
	 private String query;
	 private ResultSet resultSet;
	 private Cursor cursor;
	 private String [] cols;
	 private Jar entity;
}
