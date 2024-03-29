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

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.SQLException;

public class ResultSet {

	 public ResultSet () { 
		  this.cursor = null;
	 }

	 public ResultSet (Cursor cursor) { 
		  this.cursor = cursor;
	 }

	 public boolean next () {

		  if (cursor == null) return false;

		  if (first) {
				first = false;
		  		if (cursor.moveToFirst ()) {
					 return true;
				}
		  }
		  else {
				if (cursor.moveToNext ()) {
					 return true;
				}
		  }
		  return false;
	 }

	 public int getInt (int col) {
		  return cursor.getInt (col);
	 }
	 public String getString (int col) {
		  return cursor.getString (col);
	 }
	 public double getDouble (int col) {
		  return cursor.getDouble (col);
	 }

	 private Cursor cursor;
	 private boolean first = false;

}
