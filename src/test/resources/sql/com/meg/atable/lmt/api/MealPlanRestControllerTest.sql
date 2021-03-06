-- meal plan

insert into public.meal_plan (meal_plan_id, created, meal_plan_type, name, user_id, target_id)
values (50485, '2021-04-10 12:46:34.053000', 'Manual', 'lingering hall', 20, null);


-- dishes
insert into public.dish (dish_id, description, dish_name, user_id, last_added, auto_tag_status, created_on, reference)
values (50000001, null, 'waffles', 20, '2021-04-10 12:48:42.888000', 105, '2019-07-14 04:04:14.773362', null),
       (13000011, null, 'green chili', 20, '2021-04-19 13:48:58.318000', 105, '2019-08-17 21:11:14.773362', '135'),
       (88000011, null, 'golden chicken rice', 20, '2021-04-10 12:48:42.939000', 105, '2019-08-17 19:56:14.773362',
        '101'),
       (56705001, null, 'Gumbo', 20, '2021-04-10 12:48:42.920000', 105, '2020-04-10 17:23:24.082000', null),
       (70000121, null, 'quick chicken curry', 20, '2021-04-10 12:48:42.948000', 105, '2019-08-17 21:17:14.773362',
        null);


-- meal_plan_slot
insert into public.meal_plan_slot (meal_plan_slot_id, dish_dish_id, meal_plan_id)
values (51734, 50000001, 50485),
       (51735, 56705001, 50485),
       (51736, 13000011, 50485),
       (51737, 88000011, 50485),
       (51738, 70000121, 50485);


-- tags
insert into public.dish_tags (dish_id, tag_id)
values (50000001, 400),
       (50000001, 396),
       (50000001, 327),
       (50000001, 427),
       (50000001, 422),
       (50000001, 64),
       (50000001, 324),
       (88000011, 344),
       (88000011, 322),
       (88000011, 218),
       (88000011, 426),
       (88000011, 400),
       (88000011, 396),
       (88000011, 419),
       (88000011, 363),
       (70000121, 344),
       (70000121, 396),
       (70000121, 419),
       (70000121, 425),
       (70000121, 317),
       (70000121, 399),
       (70000121, 321),
       (70000121, 328),
       (13000011, 344),
       (13000011, 396),
       (13000011, 218),
       (13000011, 400),
       (13000011, 419),
       (13000011, 324),
       (13000011, 327),
       (13000011, 427),
       (56705001, 426),
       (56705001, 344),
       (56705001, 396),
       (56705001, 218),
       (56705001, 400),
       (56705001, 363),
       (56705001, 419),
       (56705001, 322);