-- LISTSHOP-236 changes --

-- new view
CREATE OR REPLACE VIEW public.tag_extended AS
select t.tag_id,
       t.assign_select,
       t.category_updated_on,
       t.created_on,
       t.description,
       t.is_verified,
       t.name,
       t.power,
       t.removed_on,
       t.replacement_tag_id,
       t.search_select,
       t.tag_type,
       t.tag_type_default,
       t.to_delete,
       t.updated_on,
       r.parent_tag_id
from tag t
       join tag_relation r on t.tag_id = r.child_tag_id;

ALTER TABLE public.tag_extended
  OWNER TO postgres;

-- fixing tags - assign_select
update tag
set assign_select = false
where tag_id in (
  select distinct t.tag_id
  from tag t
         join tag_relation tr on t.tag_id = tr.parent_tag_id
    and assign_select = true);

-- fixing autotag
update auto_tag_instructions
set instruction_type = upper(instruction_type);

INSERT INTO auto_tag_instructions(instruction_type, instruction_id, assign_tag_id, is_invert, search_terms)
VALUES ('TAG', nextval('auto_tag_instructions_sequence'), 346, false, '9;88;368;372;374;375');

-- LISTSHOP-236 rollback --

-- drop view public.tag_extended;