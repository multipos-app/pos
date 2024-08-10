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
 
package cloud.multipos.pos.util;

public class ProgressUpdate {

	 public ProgressUpdate (int progress, int max, int count) {

		  this.progress = progress;
		  this.max = max;
		  this.count = count;
	 }

	 public String toString () {

		  return "progress update... " + progress + " " + max + " " + count;
	 }
	 
	 private int progress;
	 private int max;
	 private int count;

	 public int progress () { return progress; }
	 public int max () { return max; }
	 public int count () { return count; }
}
