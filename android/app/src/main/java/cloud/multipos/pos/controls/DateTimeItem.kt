package cloud.multipos.pos.controls

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.views.DateTimeView;

class DateTimeItem (): DefaultItem (), InputListener {

	 override fun controlAction (jar: Jar) {

		  jar (jar)
		  DateTimeView (this, Pos.app.getString (R.string.enter_date_time))
	 }
	 
	 override fun accept (result: Jar) {
		  
		  jar ()
            .put ("item_desc", (Pos.app.getString ("due_date") + " " + result.getString ("date_time")).toUpperCase ())

        super.controlAction (jar ())
	 }	 
}
