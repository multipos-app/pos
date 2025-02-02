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
 
package cloud.multipos.pos.util

object States {

	 val states = listOf <StateMap> (StateMap ("", "State/Province"),
												StateMap ("AL", "ALABAMA"),
												StateMap ("AK", "ALASKA"),
												StateMap ("AS", "AMERICAN SAMOA"),
												StateMap ("AZ", "ARIZONA"),
												StateMap ("AR", "ARKANSAS"),
												StateMap ("CA", "CALIFORNIA"),
												StateMap ("CO", "COLORADO"),
												StateMap ("CT", "CONNECTICUT"),
												StateMap ("DE", "DELAWARE"),
												StateMap ("DC", "DISTRICT OF COLUMBIA"),
												StateMap ("FM", "FEDERATED STATES OF MICRONESIA"),
												StateMap ("FL", "FLORIDA"),
												StateMap ("GA", "GEORGIA"),
												StateMap ("GU", "GUAM GU"),
												StateMap ("HI", "HAWAII"),
												StateMap ("ID", "IDAHO"),
												StateMap ("IL", "ILLINOIS"),
												StateMap ("IN", "INDIANA"),
												StateMap ("IA", "IOWA"),
												StateMap ("KS", "KANSAS"),
												StateMap ("KY", "KENTUCKY"),
												StateMap ("LA", "LOUISIANA"),
												StateMap ("ME", "MAINE"),
												StateMap ("MH", "MARSHALL ISLANDS"),
												StateMap ("MD", "MARYLAND"),
												StateMap ("MA", "MASSACHUSETTS"),
												StateMap ("MI", "MICHIGAN"),
												StateMap ("MN", "MINNESOTA"),
												StateMap ("MS", "MISSISSIPPI"),
												StateMap ("MO", "MISSOURI"),
												StateMap ("MT", "MONTANA"),
												StateMap ("NE", "NEBRASKA"),
												StateMap ("NV", "NEVADA"),
												StateMap ("NH", "NEW HAMPSHIRE"),
												StateMap ("NJ", "NEW JERSEY"),
												StateMap ("NM", "NEW MEXICO"),
												StateMap ("NY", "NEW YORK"),
												StateMap ("NC", "NORTH CAROLINA"),
												StateMap ("ND", "NORTH DAKOTA"),
												StateMap ("MP", "NORTHERN MARIANA ISLANDS"),
												StateMap ("OH", "OHIO"),
												StateMap ("OK", "OKLAHOMA"),
												StateMap ("OR", "OREGON"),
												StateMap ("PW", "PALAU"),
												StateMap ("PA", "PENNSYLVANIA"),
												StateMap ("PR", "PUERTO RICO"),
												StateMap ("RI", "RHODE ISLAND"),
												StateMap ("SC", "SOUTH CAROLINA"),
												StateMap ("SD", "SOUTH DAKOTA"),
												StateMap ("TN", "TENNESSEE"),
												StateMap ("TX", "TEXAS"),
												StateMap ("UT", "UTAH"),
												StateMap ("VT", "VERMONT"),
												StateMap ("VI", "VIRGIN ISLANDS"),
												StateMap ("VA", "VIRGINIA"),
												StateMap ("WA", "WASHINGTON"),
												StateMap ("WV", "WEST VIRGINIA"),
												StateMap ("WI", "WISCONSIN"),
												StateMap ("WY", "WYOMING"),
												StateMap ("AE", "ARMED FORCES AFRICA, CANADA, EUROPE, MIDDLE EAST"),
												StateMap ("AA", "ARMED FORCES AMERICA (EXCEPT CANADA)"),
												StateMap ("AP", "ARMED FORCES PACIFIC"),
												StateMap ("AB", "ALBERTA"),
												StateMap ("BC", "BRITISH COLUMBIA"),
												StateMap ("MB", "MANITOBA"),
												StateMap ("NB", "NEW BRUNSWICK"),
												StateMap ("NL", "NEWFOUNDLAND AND LABRADOR"),
												StateMap ("NS", "NOVA SCOTIA"),
												StateMap ("ON", "ONTARIO"),
												StateMap ("PE", "PRINCE EDWARD ISLAND"),
												StateMap ("SK", "SASKATCHEWAN"),
												StateMap ("QC", "QUEBEC"))

	 fun list (): List <String> {

		  return states.map { it.desc }
	 }

	 fun abbr (index: Int): String {

		  return states [index].abbr
	 }

	 fun indexOf (key: String): Int {

		  var index = 0

		  for (state in states) {

				if (state.abbr == key) {

					 return index
				}
				index ++
		  }
		  return -1
	 }
	 
	 data class StateMap (val abbr: String, val desc: String)
}
