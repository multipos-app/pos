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

public class DbQuery {

	 public DbQuery (String table) {

		  query = new StringBuffer ("select * from " + table);
	 }
         
	 public DbQuery conditions (String where []) {

		  query
				.append (" where ");
		  
		  for (int i=0; i<where.length; i++) {

				if (i > 0) {
					 query.append (" and ");
				}
				query.append (where [i]);
		  }
		  
		  return this;
	 }

	 public String query () { return query.toString (); }

	 private StringBuffer query;
}
	 
