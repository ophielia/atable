package com.meg.atable.lmt.service;

import com.meg.atable.lmt.data.entity.DishEntity;
import com.meg.atable.lmt.data.entity.TargetSlotEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 13/05/2017.
 */
public interface DishSearchService {

    List<DishEntity> findDishes(DishSearchCriteria criteria);

    List<DishTagSearchResult> retrieveDishResultsForTags(Long userId, TargetSlotEntity targetSlotEntity, int size, List<String> tagListForSlot,
                                                         Map<Long, List<Long>> searchGroups, List<Long> sqlFilteredDishes);
}