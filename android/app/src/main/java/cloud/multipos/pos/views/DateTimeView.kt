package cloud.multipos.pos.views

import cloud.multipos.pos.R
import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.controls.*

import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.DatePicker 
import android.widget.TimePicker
import android.widget.NumberPicker
import java.util.Date
import java.util.Locale
import java.util.Calendar
import java.util.GregorianCalendar
import java.text.SimpleDateFormat;
import android.content.res.Resources
import android.widget.TimePicker.OnTimeChangedListener
import android.widget.DatePicker.OnDateChangedListener
import android.widget.LinearLayout
import android.os.Build
import android.os.Build.VERSION_CODES

class DateTimeView (val listener: InputListener, title: String): DialogView (title) {

	 val days = listOf ("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
	 val months = listOf ("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
	 lateinit var datePicker: DatePicker
	 lateinit var timePicker: TimePicker

	 init {
		  
		  Pos.app.inflater.inflate (R.layout.date_time_layout, dialogLayout)
		  
		  datePicker = findViewById (R.id.date_picker) as DatePicker
		  timePicker = findViewById (R.id.time_picker) as TimePicker
		  
		  datePicker.setMinDate (Date ().getTime ())  // only future dates
		  setInterval (timePicker)  // no minutes
		  val confirm = findViewById (R.id.date_time_accept) as Button
		  
		  Logger.d ("build version... " + Build.VERSION.SDK_INT)
		  
		  if (Build.VERSION.SDK_INT > 23) { // marshmellow
				
				datePicker.setOnDateChangedListener (object: OnDateChangedListener {

																	  override fun onDateChanged (view: DatePicker, year: Int, month: Int, day: Int) {
																			
																			val dateTime = formatDateTime (datePicker, timePicker)
																			confirm.text = dateTime.getString ("date_time")
																	  }
				})
				
				timePicker.setOnTimeChangedListener (object: OnTimeChangedListener {
																	  
																	  override fun onTimeChanged (view: TimePicker, hour: Int, minute: Int) {
																			
																			val dateTime = formatDateTime (datePicker, timePicker)
																			confirm.text = dateTime.getString ("date_time")
																	  }
				})
		  }

		  val dateTime = formatDateTime (datePicker, timePicker)
		  confirm.text = dateTime.getString ("date_time")
		  
		  DialogControl.addView (this)
	 }
	 
	 override fun actions (dialogView: DialogView) {
		  
		  Pos.app.inflater.inflate (R.layout.date_time_actions_layout, dialogActions)
  		  
		  val accept = findViewById (R.id.date_time_accept) as Button
		  accept.setOnClickListener () {

            listener.accept (formatDateTime (datePicker, timePicker))
				DialogControl.close ()
		  }
	 }
	 
	 private fun setInterval (timePicker: TimePicker) {
		  
            val minutePicker = timePicker.findViewById (Resources.getSystem ().getIdentifier ("minute", "id", "android")) as NumberPicker?
				
            val display = arrayOf ("00")

            minutePicker?.setMinValue (0)
            minutePicker?.setMaxValue (display.size - 1)
            minutePicker?.setDisplayedValues (display)
    }

	 private fun formatDateTime (datePicker: DatePicker, timePicker: TimePicker): Jar {

		  // return a readable time and a db timestamp
		  
		  var cal = GregorianCalendar ()
		  cal.set (datePicker.getYear (),
					  datePicker.getMonth (),
					  datePicker.getDayOfMonth (),
					  timePicker.getHour (),
					  timePicker.getMinute ())
		  
		  var hour = cal.get (Calendar.HOUR_OF_DAY) % 12

		  hour = if (hour == 0) 12 else hour
		  val ampm = if (cal.get (Calendar.HOUR_OF_DAY) < 13) "AM" else "PM"
		  
		  val dateTime = (days.get (cal.get (Calendar.DAY_OF_WEEK) - 1) + " " +
								months.get (cal.get (Calendar.MONTH)) + " " +
								cal.get (Calendar.DAY_OF_MONTH) + ", " +
								hour + ampm).toString ()
	 
		  val timestamp = String.format ("%4d-%02d-%02d %02d:00:00",
													cal.get (Calendar.YEAR),
													cal.get (Calendar.MONTH),
													cal.get (Calendar.DAY_OF_MONTH),
													cal.get (Calendar.HOUR_OF_DAY))

		  return Jar ()
				.put ("date_time", dateTime)
				.put ("timestamp", timestamp)
	 }
}
