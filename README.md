# Full featured Android POS (point of sale)
This project is an Andoid POS application integrated with a cloud based back for reporting, POS data management and control. The POS is a mature application having been installed in retail and quick serve environments in the USA and Denmark. 

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

## Date
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
This is a basic Android app with all the pieces you need to build you own POS application (assuming you have an Android dev enviroment installed). Clone the repository, cd to pos and type ./gradlew assemble or .gradlew installDebug. Or import into android studio and build. How you get the resulting apk onto a device is up to you.

Once installed you will be presented with a log in screen. For your convience there is a demo configuration ready to use, at the prompt type username 'multipos', password 'eklutna'. After you log in you are promted for a location (store) and a configuration.

## Demo configurations
- Deli/convienence is set up as a counter top app in landscape mode, tested on a variety of 10 inch tablets.
- Handheld is just that, a handheld version with three tabs, a POS tab (line busting), an inventory tab, walk around the store scan an item and update inventory and an item entry tab to add/edit items in the store. This configuration also includes an android cammera scanner.

##