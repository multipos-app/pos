delete from pos_config_templates;
insert into pos_config_templates values (default, 'Mobile POS', 'Mobile POS', 'Tabs, POS, Inventory, Add Item', null, null, '{
    "timezone": "America\/New_York",
    "locale": "en_US",
    "currency": "USD",
    "update_interval": 20000,
    "language": "US",
    "country": "en",
    "variant": "",
    "print_receipt": false,
    "prompt_open_amount": false,
    "show_previous_receipt": true,
    "disable_quantity_void": true,
    "root_layout": "multipos_1",
    "keypad_layout": "pos_keypad_2",
    "init_devices": [],
    "menus": {
        "main_menu": {
            "menu_description": "Main Menu"
        }
    },
    "displays": [
        {
            "layout": "ticket",
            "name": "ticket",
            "type": "ticket_display"
        }
    ],
    "main_menu": {
        "horizontal_menus": [
            {
                "name": "menu_1",
                "type": "controls"
            }
		  ],
        "vertical_menus": []
    },
	 "menu_1": {
        "name": "MENU 1",
        "width": 5,
        "height": 5,
        "buttons": [
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
            },
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
            }
		  ]
	 },
    "receipt_header": [],
    "receipt_trailer": [
        {
            "text": "Thank You!",
            "justify": "center",
            "font": "bold",
            "size": "big",
            "feed": "0"
        }
    ],
    "enter_clerk": false,
    "confirm_tender": true,
    "show_previous_receipt_no_print": false,
    "surveillance": false,
    "customers": false,
    "cash_management": false,
    "merge_like_items": false
}', 0);
insert into pos_config_templates values (default, 'Generic POS', 'Generic POS', '', null, null, '{
    "locale": "en_US",
    "currency": "USD",
    "update_interval": 20000,
    "language": "US",
    "country": "en",
    "variant": "",
    "print_receipt": false,
    "prompt_open_amount": false,
    "show_previous_receipt": true,
    "disable_quantity_void": true,
    "root_layout": "layout_1",
    "item_line_layout": "ticket_item_line",
    "keypad_layout": "pos_keypad_2",
    "init_devices": [],
    "login_devices": [],
    "menus": {
        "main_menu": {
            "menu_description": "Main Menu"
        },
        "function_menu": {
            "menu_description": "Function Menu"
        }
    },
    "displays": [
        {
            "layout": "ticket",
            "name": "ticket",
            "type": "ticket_display"
        }
    ],
    "main_menu": {
        "horizontal_menus": [
            {
                "name": "menu_1",
                "type": "controls"
            }
		  ],
        "vertical_menus": []
    },
    "function_menu": {
        "horizontal_menus": [
            {
                "name": "function_1",
                "type": "controls"
            }
		  ],
        "vertical_menus": []
    },
	 "menu_1": {
        "name": "MENU 1",
        "width": 5,
        "height": 5,
        "buttons": [
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
            },
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
            }
		  ]
	 },
	 "function_1": {
        "name": "FUNCTION 1",
        "width": 5,
        "height": 3,
        "buttons": [
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				},
				{
                "text": "",
                "class": "Null",
                "color": "transparent",
                "params": []
				}
		  ]
	 },
    "receipt_header": [],
    "receipt_trailer": [
        {
            "text": "Thank You!",
            "justify": "center",
            "font": "bold",
            "size": "big",
            "feed": "0"
        }
    ],
    "enter_clerk": false,
    "confirm_tender": true,
    "show_previous_receipt_no_print": false,
    "surveillance": false,
    "scanner": "keyboard",
    "cash_discount": "3.999",
    "customers": false,
    "cash_management": false,
    "merge_like_items": false
}
', 0);
