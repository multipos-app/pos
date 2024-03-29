# POS Configurations

There are 3 types of visual components in the pos

- Displays, components that are updated as transactions are processed
- Menus, static areas that contain control buttons
- Dialogs, views that require the cashier to do some special operation, such as confirm a payment or cancel an operation

This directory contains JSON files that describe the content and features of the POS. For instance the DELI/CONVIENECE + PAX S3
is using the convienece_pax_s3.json. If you load your POS with that configuration you will see the cooresponding buttons 
in the JSON file.

The POS layout is specified by the root_layout field in the JSON file, this points to the xml layout:

app/src/main/res/layout/layout_2.xml

Menus are further specified in the XML file:

```
        <cloud.multipos.pos.views.PosMenus
                                custom:menus="main_menu"
                                android:layout_width="match_parent"
                                android:layout_height="0dp"
                                android:layout_weight="2"
                                android:background="@color/transparent"/>

```

This tells the POS app to load 'main_menu' from the JSON file in the layout section of the POS screen.

Between the android XML files and the JSON files in this directory you could build various POS layouts without changing any
code.
