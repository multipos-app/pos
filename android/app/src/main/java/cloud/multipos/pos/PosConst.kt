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
 
package cloud.multipos.pos

class PosConst {
	 
	 companion object {
		  
		  const val TOTALS_COMPLETE =           100
		  const val TICKET =                    101
		  const val LOGIN =                     102
		  const val PAYMENT_CANCELLED =         103
		  const val SETTLE =                    104
		  const val PAYMENT_FAILED =            105
		  const val CONFIG_UPDATE =             106
		  const val MANAGER_OVERRIDE =          107
		  const val EMAIL_RECEIPT =             110
		  const val EMPLOYEE =                  111
		  const val SCAN =                      112
		  const val HOME =                      113
		  const val WAIT =                      114
		  const val REMOTE_TICKET =             115
		  const val TICKET_NOT_FOUND =          116
		  const val DISPLAY_MESSAGE =           117
		  const val DOWNLOAD =                  121
		  const val CONTROL =                   122
		  const val MESSAGE =                   123
		  const val ORDERS =                    124

		  const val PRINTER_START =             200
		  const val PRINTER_STOP =              201
		  const val PRINTER_PAYMENT =           202
		  const val PRINTER_DISCOVER =          203
		  const val PRINTER_SEARCH_COMPLETE =   204
		  const val PRINTER_TEST =              205
		  const val PRINTER_CC_RECEIPT =        206
		  const val PRINTER_CC_RECEIPT_W_TIP =  207
		  const val PRINTER_RECEIPT =           208
		  const val PRINTER_OPEN_DRAWER =       209
		  const val PRINTER_REPORT =            210
		  const val PRINTER_SIGN_RECEIPT =      211
		  const val PRINTER_CHECK_RECEIPT =     212
		  const val PRINTER_KITCHEN =           213
		  const val PRINTER_EXCHANGE_RECEIPT =  214
		  const val PRINTER_REPRINT =           215
		  const val PRINTER_ON_LINE =           216
		  const val PRINTER_OFF_LINE =          217
		  const val PRINTER_PAPER_LOW =         218
		  const val PRINTER_PAPER_OUT =         219
		  const val PRINTER_INIT =              220
		  const val PRINTER_SESSION =           221
		  const val PRINTER_COMPLETE =          222
		  const val PRINTER_ORDER =             223
		  
		  const val DEVICE_STATUS =             300

		  // Payment funtions

		  const val PAYMENT_START =             400
		  const val PAYMENT_READY =             401
		  const val PAYMENT_SALE =              402
		  const val PAYMENT_AUTH =              403
		  const val PAYMENT_ADJUST =            404
		  const val PAYMENT_COMPLETE =          405
		  const val PAYMENT_REFUND =            406
		  const val PAYMENT_CANCEL =            407
		  const val PAYMENT_STATUS =            408
		  const val PAYMENT_PRINTER_REQUEST =   409
		  const val PAYMENT_CONFIRM_REQUEST =   410
		  const val PAYMENT_ON_LINE =           411
		  const val PAYMENT_OFF_LINE =          412

		  const val WWW_ON_LINE =               501
		  const val WWW_OFF_LINE =              502

		  const val SCANNER_ON_LINE =           600
		  const val SCANNER_OFF_LINE =          601
		  const val SCANNER_PAIR =              602

		  // activity requests

		  const val ITEM_SEARCH =               700
		  const val ITEM_RESULT =               701

		  // service messages
		  
		  const val CLOUD_READY =               800
		  const val POS_SETUP =                 801
	 }
}
