# Scanners

All currently supported scanners must operate in keyboard wedge mode (either hardwired or BT) and send a carriage return at the end of the line. Most USB (hardwired) sacnners should work out of the box. 

The Zebra DS 2278 BT has been tested. Get the scantoconnect tool from Zebra [ ScanToConnect](https://www.zebra.com/us/en/software/scanner-software/scan-to-connect.html),
download and install the apk, this will attach the scanner to your device in keyboard mode.

After that set up the scanner to send a carriage return, see sectin 5-29 of the following manual, Miscellaneous Scanner Parameters in the [DS-2200 user manual](https://www.zebra.com/content/dam/zebra_new_ia/en-us/manuals/barcode-scanners/general/ds2208/ds2208-prg-en.pdf).

Other scanners (other than keyboard mode) can be supported by implementing the cloud.multipos.pos.devices.Scanner class.

Note: scanners should send the full UPC A, 12 digit, UCC-N, 8 digite and EAN-13 (keep it simple).
