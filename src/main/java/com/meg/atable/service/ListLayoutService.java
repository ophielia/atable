package com.meg.atable.service;

import com.meg.atable.api.model.Category;
import com.meg.atable.api.model.ListLayoutType;
import com.meg.atable.data.entity.ListLayoutCategoryEntity;
import com.meg.atable.data.entity.ListLayoutEntity;
import com.meg.atable.data.entity.TagEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by margaretmartin on 06/11/2017.
 */
public interface ListLayoutService {
    List<ListLayoutEntity> getListLayouts();

    ListLayoutEntity getListLayoutByType(ListLayoutType listLayoutType);

    ListLayoutEntity createListLayout(ListLayoutEntity listLayoutEntity);

    ListLayoutEntity getListLayoutById(Long listLayoutId);

    void deleteListLayout(Long listLayoutId);

    void addCategoryToListLayout(Long listLayoutId, ListLayoutCategoryEntity entity);

    void deleteCategoryFromListLayout(Long listLayoutId, Long layoutCategoryId) throws ListLayoutException;

    ListLayoutCategoryEntity updateListLayoutCategory(Long listLayoutId, ListLayoutCategoryEntity listLayoutCategory);

    List<TagEntity> getUncategorizedTagsForList(Long listLayoutId);

    List<TagEntity> getTagsForLayoutCategory(Long layoutCategoryId);

    List<ListLayoutCategoryEntity> getCategoriesForTag(TagEntity tag);

    void addTagsToCategory(Long listLayoutId, Long layoutCategoryId, List<Long> tagIdList);

    void deleteTagsFromCategory(Long listLayoutId, Long layoutCategoryId, List<Long> tagIdList);

    List<ListLayoutCategoryEntity> getListCategoriesForIds(Set<Long> categoryIds);

    List<Category> getStructuredCategories(ListLayoutEntity listLayout);

    void structureCategories(Map<Long, Category> filledCategories, Long listLayoutId);

    void addCategoryToParent(Long categoryId, Long parentId) throws ListLayoutException;

    void moveCategory(Long categoryId, boolean moveUp) throws ListLayoutException;
}
