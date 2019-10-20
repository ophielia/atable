package com.meg.atable.lmt.service.impl;

import com.meg.atable.auth.data.entity.UserEntity;
import com.meg.atable.lmt.api.model.ModelMapper;
import com.meg.atable.lmt.api.model.Statistic;
import com.meg.atable.lmt.data.entity.ListTagStatistic;
import com.meg.atable.lmt.data.entity.TagEntity;
import com.meg.atable.lmt.data.repository.ListTagStatisticRepository;
import com.meg.atable.lmt.service.CollectedItem;
import com.meg.atable.lmt.service.ListItemCollector;
import com.meg.atable.lmt.service.ListTagStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
public class ListTagStatisticServiceImpl implements ListTagStatisticService {


    @Autowired
    private ListTagStatisticRepository listTagStatisticRepo;

    @Override
    public void countTagAddedToDish(Long userId, Long tagId) {
        ListTagStatistic statistic = listTagStatisticRepo.findByUserIdAndTagId(userId, tagId);

        if (statistic == null) {
            statistic = new ListTagStatistic();
            statistic.setTagId(tagId);
            statistic.setUserId(userId);
        }
        int addedCount = statistic.getAddedToDishCount() != null ? statistic.getAddedToDishCount() : 0;
        statistic.setAddedToDishCount(addedCount + 1);
        listTagStatisticRepo.save(statistic);
    }

    @Override
    public void processCollectorStatistics(Long userId, ListItemCollector collector) {
        // get statistics for tags - hash by tagid
        Map<Long, ListTagStatistic> statLkup = listTagStatisticRepo.findByUserIdAndTagIdIn(userId, collector.getAllTagIds()).stream()
                .collect(Collectors.toMap(ListTagStatistic::getTagId, Function.identity()));

        // go through list tags - return list of stats
        List<ListTagStatistic> statList = new ArrayList<>();
        List<CollectedItem> itemList = collector.getCollectedTagItems();

        for (CollectedItem item : itemList) {
            if (!item.isUpdated() & !item.isRemoved()) {
                continue;
            }
            TagEntity tag = item.getTag();
            if (item.isAdded()) {
                if (statLkup.containsKey(tag.getId())) {
                    ListTagStatistic stat = statLkup.get(tag.getId());
                    boolean frequentCrossOff = isFrequentCrossOff(stat);
                    item.setFrequent(frequentCrossOff);
                }
                ListTagStatistic stat = addOrRemoveItem(statLkup, userId, tag.getId(), item.getAddCount(), 0);
                statList.add(stat);
            } else if (item.isRemoved()) {
                if (statLkup.containsKey(tag.getId())) {
                    ListTagStatistic stat = statLkup.get(tag.getId());
                    boolean frequentCrossOff = isFrequentCrossOff(stat);
                    item.setFrequent(frequentCrossOff);


                }
                ListTagStatistic stat = addOrRemoveItem(statLkup, userId, tag.getId(), 0, item.getRemovedCount());
                statList.add(stat);
            } else if (item.isRemoved()) {
                if (statLkup.containsKey(tag.getId())) {
                    ListTagStatistic stat = statLkup.get(tag.getId());
                    boolean frequentCrossOff = isFrequentCrossOff(stat);
                    item.setFrequent(frequentCrossOff);


                }
                ListTagStatistic stat = addOrRemoveItem(statLkup, userId, tag.getId(), 0, item.getRemovedCount());
                statList.add(stat);
            }

        }

        // save list of stats
        listTagStatisticRepo.saveAll(statList);
    }

    @Override
    public List<ListTagStatistic> getStatisticsForUser(Long userId, int resultLimit) {

        List<ListTagStatistic> statistics = listTagStatisticRepo.findByUserId(userId);
        if (statistics.size() > resultLimit) {
            return statistics.subList(0, resultLimit);
        }
        return statistics;
    }

    @Override
    public List<ListTagStatistic> createStatisticsForUser(UserEntity user, List<Statistic> statisticList) {
        // this is done from a context in which the user has just been created, and doesn't
        // have any statistics
        if (statisticList.isEmpty()) {
            return new ArrayList<>();
        }
        List<ListTagStatistic> createdStatistics = new ArrayList<>();
        for (Statistic statistic : statisticList) {
            ListTagStatistic statisticEntity = ModelMapper.toEntity(statistic);
            statisticEntity.setUserId(user.getId());
            createdStatistics.add(statisticEntity);
        }
        return listTagStatisticRepo.saveAll(createdStatistics);
    }

    private ListTagStatistic addCounted(ListTagStatistic statistic) {
        Integer counted = statistic.getAddedCount() == null ? 0 : statistic.getAddedCount();
        statistic.setAddedCount(counted + 1);
        return statistic;
    }

    private ListTagStatistic addRemoved(ListTagStatistic stat) {
        Integer counted = stat.getRemovedCount() == null ? 0 : stat.getRemovedCount();
        stat.setRemovedCount(counted + 1);
        return stat;
    }

    public boolean isFrequentCrossOff(ListTagStatistic listTagStatistic) {
        // return less than 3
        if (listTagStatistic.getAddedCount() < 3) {
            return false;
        }
        Integer addedCount = listTagStatistic.getAddedCount();
        Double percentage = (listTagStatistic.getRemovedCount().doubleValue() / addedCount.doubleValue()) * 100.0;
        // process 3 - 5
        if (addedCount <= 5) {
            return percentage >= 65.0;
        } else if (addedCount <= 10) {
            return percentage >= 75.0;
        }
        return percentage >= 70.0;

    }

    private ListTagStatistic addOrRemoveItem(Map<Long, ListTagStatistic> statLkup, Long userId, Long tagId, int addCount, int removeCount) {

        // get statistic for tag
        ListTagStatistic statistic = statLkup.get(tagId);
        // if it doesn't exist, create it
        if (statistic == null) {
            statistic = new ListTagStatistic();
            statistic.setUserId(userId);
            statistic.setTagId(tagId);
            statistic.setAddedCount(0);
            statistic.setRemovedCount(0);
        }

        // increment added or removed
        if (addCount > 0) {
            statistic = addCounted(statistic);
        } else if (removeCount > 0) {
            statistic = addRemoved(statistic);
        }
        // save statistic
        return statistic;
    }


}
