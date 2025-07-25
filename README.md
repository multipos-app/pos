# Android POS (point of sale)
This project is an Andoid POS application integrated with a cloud based back for reporting, POS data management and control. The POS is a mature application having been installed in retail and quick serve environments in the USA and Denmark.

## Update 7/25/2025

- Complete laundermat wash and fold functions
- Redesign the ticket model to use a builder pattern
- Redesign the tender control to use a builder pattern
- Start on pull-tabs business support
- clean up swipe gestures

## Update 4/30/2025

- Add laundermat wash and fold functions

## New 4/22/2025

- Rewrite dialog views, dialogs roll out from the side, swipe to clear, see DialogView.kt, DialogControl.kt
- Added item modifiers, (i.e. cheese burger, no onions), create a subdepartment for a department (burgers), all items in the modfier department will be displayed in a dialog. Long press on the item in the sale display to add a modifier.

## New 2/10/2025

- Instant loyalty, print customer loyalty token on request (no customer data required), scan token on subsequent visits to accumulate loyalty points/awards

## New 2/3/2025

- add USE_CAMERA to BuildConfig (app/build.gradle) so initial android permission request is optional
- add POS based add/edit of items and customers

## New 12/20/2024
New sale discount and item markdown functionality

Update handheld configurations with built in android cammera scanner

## New 11/27/2024

Swipe left on ticket dislay to suspend a ticket, added Tabs control to display/select suspended tickets

Youtube POS Registration https://www.youtube.com/watch?v=XiFy3Gc5Cmo

Youtube POS demo https://www.youtube.com/watch?v=mMtDKhiB6AY

Youtube dark/light themes https://www.youtube.com/watch?v=W7eabIH1uAk

Some compiled APKs https://apps.multipos.cloud

## Includes most retail and restaurant features
- Cash, credit, debit, EBT foodstams, gift card, check, split
- Void item, void sale
- Discounts, %, fixed amount, mix and match (buy 2, get one free)
- Item markdown by % or fixed
- Non-tax, tax sales
- Manager functions, X/Z
- End-of-shift cash drawer count
- Suspend/recall transaction
- Open tabs
- Open item (enter price)
- Paid IN/OUT
- Cash drop
- Float
- Item inventory
- Multi site (store) support
- Customize receipt, header and footer
- No sale/open drawer
- Price check
- Price override
- Manager override
- Enter quantity
- Linked items (deposits, packages)
- Repeat last order
- Sale discount percent (i.e. employee disount)
- Split single transaction into multiple transactions
- Return sale
- Comp sale
- Kitchen print
- Dark/Light themes (new)

## Pricing Options
- Standard pricing, scanned, enter sku or tied to a button
- Size, i.e. Fountain drinks, small, medium, large
- Metric, pound, liter, kg

## Hardware
- StarMicronics receipt printers
- Citizen printers
- Zebra BT scanner
- USB Scanner
- Embedded Android camera scanner (QR Code, Barcode)
- Cash Drawer
- Second Android customer display

## All-in-one support
- ELO 13, 15 inch PayPoint
- PAX E800 and A920

## Payment
- PAX S300
- PAX E800
- PAX A920
- PAX Q series payment terminals

## Data
- Items
- Departments
- Employees
- Employees profiles
- Discounts
- Customers
- Suppliers
- Inventory

## Configurable screen/menu layouts
User can set size and number of buttons per page, text and color, unlimited sub-menu layers. Updated menus are (almost) immediately available at the POS.

# Getting started
This is a basic Android app with all the pieces you need to build you own POS application (assuming you have an Android dev enviroment installed). Clone the repository, cd to pos/android and type ./gradlew tasks, then ./gradlew assemble or .gradlew installDebug. Or import into android studio and build. How you get the resulting apk onto a device is up to you.

Once installed you will be prompted for android.permission.CAMERA (the integrated scanner uses the back camera). After that you are presented with a log in screen. For your convience there is a demo configuration ready to use, at the prompt type username 'multipos', password 'eklutna'. After you log in you are prompted for a location (store) and a configuration.

### Note: the demo database has around 2000 items so the initial load can take 5-10+ minutes.

After download completes login in as cashier 1, PIN 1.

## Demo configurations
- Deli/convienence is set up as a counter top app in landscape mode, tested on a variety of 10 inch tablets.
- Handheld is just that, a handheld version with three tabs, a POS tab (line busting), an inventory tab, walk around the store scan an item and update inventory and an item entry tab to add/edit items in the store. This configuration also includes an android cammera scanner... more
- The generic configuration demonstrates an alternate layout and has no attached hardware

### See the configs directory for more information.

## Back Office

Go to https://manager.multipos.cloud and log in using the same credentials that you used for the POS, i.e. 'multipos' and 'eklutna'. In a production environment each customer would have a dedicated server side database.

### The back office reporting and maintenance includes...

- Sales summary, a summary of the current weeks sales
- Item history, sales by itm
- Hourly sales, net sales by day/hour
- Transactions, indidvidual transaction detail
- Store Ops, maintenance for items, departments, etc.
- POS menus, edit configurations and menus
- Account, store location information


