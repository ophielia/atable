-- lists - ids 500-503
-- base list - id 500
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now(), 1, 'General', 20, 509990, 'added to', false);
-- active list - id 501
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 20, 509991, 'added from', true);
-- user 504 doesn't have a starter list
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 504, 509999, 'added from', false);
-- user 504 doesn't have a starter list
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 20, 500777, 'merge test', false);
-- copy from / to: sourceList
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 20, 7777, 'list operations - source', false);
-- copy from / to: destinationList
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 20, 6666, 'list operations - source', false);
-- copy from / to: sourceList
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 20, 77777, 'list operations - source', false);

-- test list for removing from lists
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now(), 1, 'General', 500, 609990, 'added to', false);
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now(), 1, 'General', 500, 609991, 'remove from this list', false);


-- list items - four items, for active list - id 501,502,503,500
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509990, 501, 509990, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509990, 502, 509991, now(), null, null, 1, null, '509991');
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509990, 503, 509992, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509990, 500, 509993, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509990, 504, 509994, now(), null, null, 1, null, '509991');


-- list items - four items, for active list - id 501,502,503,500
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509999, 501, 509999, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509999, 502, 509981, now(), null, null, 1, null, '509991');
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509999, 503, 509982, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509999, 500, 509983, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (509999, 504, 509984, now(), null, null, 1, null, '509991');

-- list items for list from which to delete a list
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609990, 501, 609990, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609990, 502, 609991, now(), null, null, 1, null, '609991');
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609990, 503, 609992, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609990, 500, 609993, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609990, 504, 609994, now(), null, null, 1, null, '609991');

INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609991, 501, 609980, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (609991, 504, 609984, now(), null, null, 1, null, null);


-- merge list items - four items, for active list - id 501,502,503,500
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500777, 501, 500770, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500777, 502, 500771, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500777, 503, 500772, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500777, 500, 500773, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (500777, 504, 500774, '2019-07-12', null, null, 1, null, null);

-- operation list items, source - three items- id 500,501,502
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (7777, 500, 70773, '2019-07-12', '2019-07-12', null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (7777, 501, 70770, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (7777, 502, 70771, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (7777, 505, 707711, '2019-07-12', '2019-07-12', null, 1, null, null);

-- operation list items, source - three items- id 500,501,502
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (77777, 500, 770773, '2019-07-12', '2019-07-12', null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (77777, 501, 770770, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (77777, 502, 770771, '2019-07-12', '2019-07-12', null, 1, null, null);


-- operation list items, source - three items- id 500,501,502
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (6666, 501, 60660, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (6666, 502, 60661, '2019-07-12', null, null, 1, null, null);

INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (6666, 503, 60662, '2019-07-12', null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (6666, 505, 60663, '2019-07-12', null, null, 1, null, null);
-- test with 501, 503, 504

-- test add to list from meal plan
-- list 51000 with 3 items
insert into list (created_on, list_layout_id, list_types, user_id, list_id, name, is_starter_list)
values (now() - interval '3 days', 11, 'General', 500, 51000, 'added from', false);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (51000, 81, 510001, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (51000, 1, 510002, now(), null, null, 1, null, null);
INSERT INTO list_item(list_id, tag_id, item_id, added_on, crossed_off, free_text, used_count, dish_sources,
                      list_sources)
VALUES (51000, 12, 510003, now(), null, null, 1, null, null);
-- meal plan with 2 dishes
-- contained in meal plan are tags 502, 503, and 510
-- dishes
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id)
values (null, null, 'dish1', null, 500, 66500);
insert into dish (auto_tag_status, description, dish_name, last_added, user_id, dish_id)
values (null, null, 'dish1', null, 500, 66501);
--dish 1 - tags 502 and 510
insert into dish_tags (dish_id, tag_id)
values (66500, 1);
insert into dish_tags (dish_id, tag_id)
values (66500, 436);
-- dish 2 - tags 502 and 503
insert into dish_tags (dish_id, tag_id)
values (66501, 1);
insert into dish_tags (dish_id, tag_id)
values (66501, 12);

insert into meal_plan (created, meal_plan_type, name, user_id, meal_plan_id)
values (now(), 'Manual', 'meal plan 5', 500, 65505);

-- slots for meal plan -
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (66501, 65505, 65500);
insert into meal_plan_slot (dish_dish_id, meal_plan_id, meal_plan_slot_id)
values (66500, 65505, 65501);

