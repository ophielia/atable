package com.meg.atable.api.model;

import com.meg.atable.data.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 15/09/2017.
 */
public class ModelMapper {
    private ModelMapper() {
        throw new IllegalAccessError("Utility class");
    }

    public static Dish toModel(DishEntity dishEntity) {
        List<Tag> tags = toModel(dishEntity.getTags());
        return new Dish(dishEntity.getId())
                .description(dishEntity.getDescription())
                .dishName(dishEntity.getDishName())
                .tags(tags)
                .userId(dishEntity.getUserId());
    }

    private static List<Tag> toModel(List<TagEntity> tagEntities) {
        List<Tag> tags = new ArrayList<>();
        if (tagEntities == null) {
            return tags;
        }
        for (TagEntity entity : tagEntities) {
            tags.add(toModel(entity));
        }
        return tags;
    }

    public static Tag toModel(TagEntity tagEntity) {
        if (tagEntity == null) {
            return null;
        }
        return new Tag(tagEntity.getId())
                .name(tagEntity.getName())
                .description(tagEntity.getDescription())
                .tagType(tagEntity.getTagType().name())
                .ratingFamily(tagEntity.getRatingFamily());
    }


    public static TagExtended toExtendedModel(TagEntity tagEntity) {
        return new TagExtended(tagEntity.getId(),
                tagEntity.getName(),
                tagEntity.getDescription(),
                tagEntity.getTagType(),
                tagEntity.getRatingFamily(),
                tagEntity.getParentId(),
                tagEntity.getChildrenIds());
    }

    public static MealPlan toModel(MealPlanEntity mealPlanEntity) {
        List<Slot> slots = slotsToModel(mealPlanEntity.getSlots());
        return new MealPlan(mealPlanEntity.getId())
                .name(mealPlanEntity.getName())
                .mealPlanType(mealPlanEntity.getMealPlanType().name())
                .userId(mealPlanEntity.getUserId().toString())
                .slots(slots);

    }

    private static Slot toModel(SlotEntity slotEntity) {
        return new Slot(slotEntity.getMealPlanSlotId())
                .mealPlanId(slotEntity.getMealPlan().getId())
                .dish(toModel(slotEntity.getDish()));
    }

    private static List<Slot> slotsToModel(List<SlotEntity> slots) {
        List<Slot> slotList = new ArrayList<>();
        if (slots == null) {
            return slotList;
        }
        for (SlotEntity entity : slots) {
            slotList.add(toModel(entity));
        }
        return slotList;
    }

    public static ShoppingList toModel(ShoppingListEntity shoppingListEntity) {
        List<Category> categories = itemsToModel(shoppingListEntity.getItems());
        return new ShoppingList(shoppingListEntity.getId())
                .createdOn(shoppingListEntity.getCreatedOn())
                .listType(shoppingListEntity.getListType().name())
                .layoutType(shoppingListEntity.getListLayoutType().name())
                .categories(categories)
                .userId(shoppingListEntity.getUserId());

    }

    private static List<Category> itemsToModel(List<ItemEntity> items) {
        List<Category> categories = new ArrayList<>();
        if (items == null) {
            return categories;
        }
        HashMap<String, List<Item>> sortedByCategories = new HashMap<>();
        // go through items,
        //      convert to Item
        //      sort out and place into Hash for categories
        for (ItemEntity itemEntity : items) {
            Item item = toModel(itemEntity);
            String key = item.getListCategory();
            if (!sortedByCategories.containsKey(key)) {
                sortedByCategories.put(key, new ArrayList<>());
            }
            sortedByCategories.get(key).add(item);
        }


        for (Map.Entry<String, List<Item>> entry : sortedByCategories.entrySet()) {
            Category category = new Category(entry.getKey())
                    .items(entry.getValue().stream().sorted().collect(Collectors.toList()));
            categories.add(category);
        }
        return categories;
    }

    private static Item toModel(ItemEntity itemEntity) {
        String itemSource = itemEntity.getItemSource() != null ?
                itemEntity.getItemSource().name() : null;
        return new Item(itemEntity.getId())
                .tag(toModel(itemEntity.getTag()))
                .itemSource(itemSource)
                .listId(itemEntity.getListId().toString())
                .addedOn(itemEntity.getAddedOn())
                .crossedOff(itemEntity.getCrossedOff())
                .usedCount(itemEntity.getUsedCount())
                .freeText(itemEntity.getFreeText())
                .listCategory(itemEntity.getListCategory());
    }

    public static TagEntity toEntity(Tag tag) {
        if (tag == null) {
            return null;
        }
        Long tagId = tag.getId() != null ? new Long(tag.getId()) : null;
        TagEntity tagEntity = new TagEntity(tagId);

        tagEntity.setName(tag.getName());
        tagEntity.setDescription(tag.getDescription());
        tagEntity.setTagType(TagType.valueOf(tag.getTagType()));
        tagEntity.setRatingFamily(tag.getRatingFamily());

        return tagEntity;
    }

    public static MealPlanEntity toEntity(MealPlan mealPlan) {
        if (mealPlan == null) {
            return null;
        }
        Long mealPlanId = mealPlan != null && mealPlan.getMealPlanId() != null ? mealPlan.getMealPlanId() : null;
        MealPlanEntity mealPlanEntity = new MealPlanEntity(mealPlanId);

        mealPlanEntity.setName(mealPlan.getName());
        mealPlanEntity.setMealPlanType(MealPlanType.valueOf(mealPlan.getMealPlanType()));
        if (mealPlan.getUserId() != null) {
            mealPlanEntity.setUserId(new Long(mealPlan.getUserId()));
        }

        return mealPlanEntity;
    }

    public static ItemEntity toEntity(Item input) {
        Long id = input.getId() != null ?
                input.getId() : null;
        ItemSourceType sourceType = input.getItemSource() != null ?
                ItemSourceType.valueOf(input.getItemSource()) : null;
        Long listId = input.getListId() != null ?
                Long.valueOf(input.getListId()) : null;
        Long tagId = input.getTagId() != null ?
                Long.valueOf(input.getTagId()) : null;
        ItemEntity itemEntity = new ItemEntity(id);
        itemEntity.setTag(toEntity(input.getTag()));
        itemEntity.setFreeText(input.getFreeText());
        itemEntity.setItemSource(sourceType);
        itemEntity.setListId(listId);
        itemEntity.setTagId(tagId);
        return itemEntity;
    }

    public static ShoppingListEntity toEntity(ShoppingList shoppingList) {
        ShoppingListEntity shoppingListEntity = new ShoppingListEntity(shoppingList.getList_id());
        ListType listType = ListType.valueOf(shoppingList.getListType());
        shoppingListEntity.setListType(listType);
        shoppingListEntity.setUserId(shoppingList.getUserId());
        ListLayoutType layoutType = ListLayoutType.valueOf(shoppingList.getLayoutType());
        shoppingListEntity.setListLayoutType(layoutType);
        // not setting items here, since items will be updated individually from client
        return shoppingListEntity;
    }


}
