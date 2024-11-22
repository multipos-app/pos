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

package cloud.multipos.pos.devices

import cloud.multipos.pos.*
import cloud.multipos.pos.util.*
import cloud.multipos.pos.util.extensions.*
import cloud.multipos.pos.receipts.*
import cloud.multipos.pos.views.Views

import android.graphics.*
import com.starmicronics.starioextension.ICommandBuilder
import com.starmicronics.starioextension.StarIoExt
import com.starmicronics.starioextension.StarIoExt.Emulation
import com.starmicronics.starioextension.ICommandBuilder.BarcodeSymbology
import com.starmicronics.starioextension.ICommandBuilder.BarcodeWidth
import com.starmicronics.starioextension.ICommandBuilder.QrCodeModel
import com.starmicronics.starioextension.ICommandBuilder.QrCodeLevel
import com.starmicronics.starioextension.ICommandBuilder.PeripheralChannel
import com.starmicronics.starioextension.ICommandBuilder.CutPaperAction
import android.text.TextPaint
import android.text.StaticLayout
import android.text.Layout
import java.nio.charset.Charset
import java.util.ArrayList

class StarTSP100 : StarCommandBuilder {
	 
	 private val encoding: Charset? = null
	 private val deviceName = "StarTSP100"
	 private var typeface: Typeface? = null
	 private var textSize = 24
	 
	 override fun build (commands: PrintCommands): MutableList <ICommandBuilder> {
		  
        var image: Bitmap
        val builders = mutableListOf <ICommandBuilder> ()
        val builder = StarIoExt.createCommandBuilder (Emulation.StarGraphic)
		  
        builder.beginDocument ()
		  
        for (pc in commands.list) {
				
            when (pc.directive) {
					 
                PrintCommand.LEFT_TEXT -> {
						  
                    image = createBitmapFromText (pc.text, Layout.Alignment.ALIGN_NORMAL)
                    builder.appendBitmap (image, false)
                    reset ()
                }
					 
                PrintCommand.CENTER_TEXT -> {
						  
                    image = createBitmapFromText (pc.text, Layout.Alignment.ALIGN_CENTER)
                    builder.appendBitmap (image, false)
                    reset ()
                }
					 
                PrintCommand.BOLD_TEXT -> typeface = Views.receiptFontBold ()
                PrintCommand.ITALIC_TEXT -> typeface = Views.receiptFontItalic ()
                PrintCommand.BIG_TEXT -> textSize = BIG_TEXT
                PrintCommand.MEDIUM_TEXT -> textSize = MEDIUM_TEXT
                PrintCommand.SMALL_TEXT -> textSize = SMALL_TEXT
                PrintCommand.INVERSE_TEXT -> {}
					 
                PrintCommand.BARCODE -> {
						  
                    val text = BARCODE_PREFIX + pc.value
						  builder.appendBarcodeWithAbsolutePosition (text.toByteArray (),
																					BarcodeSymbology.Code128,
																					BarcodeWidth.Mode2,
																					BARCODE_HEIGHT,
																					false,
																					BARCODE_POSITION)
						  
						  builder.appendUnitFeed (32)
					 }

					 PrintCommand.LINE -> {
						  
						  // https://en.wikipedia.org/wiki/Box-drawing_character
						  
						  val line = "┅".fill (width () - 7)
						  
                    image = createBitmapFromText (line, Layout.Alignment.ALIGN_CENTER)
                    builder.appendBitmap (image, false)
                    reset ()
					 }
					 
					 PrintCommand.QR_CODE -> builder.appendQrCodeWithAbsolutePosition (pc.text.toByteArray (),
																											 QrCodeModel.No2,
																											 QrCodeLevel.L,
																											 5,
																											 200)
					 
					 PrintCommand.OPEN_DRAWER -> {
						  
						  builder.appendPeripheral (PeripheralChannel.No1, 2000)
						  builder.appendPeripheral (PeripheralChannel.No2, 2000)
					 }
					 
					 PrintCommand.CUT -> builder.appendCutPaper (CutPaperAction.PartialCutWithFeed)
				}
		  }
		  
		  builder.endDocument ()
		  builders.add (builder)
		  return builders
	 }

	 private fun reset () {
		  
		  typeface = Views.receiptFont ()
		  textSize = SMALL_TEXT
	 }

	 private fun createBitmapFromText (printText: String, align: Layout.Alignment): Bitmap {
		  
		  val paint = Paint ()
		  val bitmap: Bitmap
		  val canvas: Canvas
		  paint.textSize = textSize.toFloat ()
		  paint.typeface = typeface
		  paint.getTextBounds (printText, 0, printText.length, Rect ())
		  val textPaint = TextPaint (paint)
		  val staticLayout = StaticLayout (printText,
													  textPaint,
													  PAPER_SIZE_THREE_INCH,
													  align,
													  1f,
													  0f,
													  false)
		  
		  bitmap = Bitmap.createBitmap (staticLayout.width, staticLayout.height, Bitmap.Config.ARGB_8888)
		  
		  canvas = Canvas (bitmap)
		  canvas.translate (0f, 0f)
		  canvas.drawColor (Color.WHITE)
		  staticLayout.draw (canvas)
		  return bitmap
	 }
	 
	 override fun width (): Int { return 38 }
	 override fun boldWidth (): Int { return 19 }
	 override fun quantityWidth (): Int { return 4 }
	 override fun descWidth (): Int { return 20 }
	 override fun amountWidth (): Int { return 12 }
	 override fun deviceName (): String { return deviceName }
	 
	 companion object {
		  
		  const val PAPER_SIZE_THREE_INCH = 576
		  const val BIG_TEXT = 72
		  const val MEDIUM_TEXT = 36 // 32
		  const val SMALL_TEXT = 25
		  const val BARCODE_HEIGHT = 72
		  const val BARCODE_POSITION = 55
		  const val BARCODE_PREFIX = "{B"
		  }
	 }
