package com.meg.atable.data.repository.impl;

import com.meg.atable.data.entity.ItemEntity;
import com.meg.atable.data.repository.ItemChangeRepository;
import com.meg.atable.data.repository.ItemRepository;
import com.meg.atable.service.ListItemCollector;
import com.meg.atable.service.ListTagStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by margaretmartin on 05/04/2018.
 */
@Component
public class ListItemRepositoryImpl implements ItemChangeRepository {

    @Autowired
    private ListTagStatisticService listTagStatisticService;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void saveItemChanges(ListItemCollector collector, Long userId) {
        listTagStatisticService.processStatistics(userId, collector);

        List<ItemEntity> toUpdate = collector.getItemsToUpdate();
        List<ItemEntity> toDelete = collector.getItemsToDelete();

        if (!toUpdate.isEmpty()) {
            itemRepository.save(toUpdate);
        }

        if (!toDelete.isEmpty()) {
            itemRepository.delete(toDelete);
        }

    }
}