package com.meg.atable.service.impl;

import com.meg.atable.api.model.MealPlanType;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.service.UserService;
import com.meg.atable.data.entity.*;
import com.meg.atable.data.repository.MealPlanRepository;
import com.meg.atable.data.repository.SlotRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.service.DishService;
import com.meg.atable.service.MealPlanService;
import com.meg.atable.service.TargetProposalService;
import me.atrox.haikunator.Haikunator;
import me.atrox.haikunator.HaikunatorBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 20/10/2017.
 */
@Service
public class MealPlanServiceImpl implements MealPlanService {

    private static final Logger logger = LogManager.getLogger(ShoppingListServiceImpl.class);


    @Autowired
    private UserService userService;

    @Autowired
    private MealPlanRepository mealPlanRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private DishService dishService;

    @Autowired
    private TargetProposalService targetProposalService;

    @Autowired
    private TagRepository tagRepository;

    public List<MealPlanEntity> getMealPlansForUserName(String username) {
        // get user
        UserAccountEntity user = userService.getUserByUserName(username);

        return mealPlanRepository.findByUserId(user.getId());
    }

    public MealPlanEntity createMealPlan(String username, MealPlanEntity mealPlanEntity) {
        // get username
        UserAccountEntity user = userService.getUserByUserName(username);

        // check name - if null or empty, autoname
        if (mealPlanEntity.getName() == null || mealPlanEntity.getName().isEmpty())  {
            Haikunator haikunator = new HaikunatorBuilder().setTokenLength(0).setDelimiter(" ").build();
            String mealPlanName = haikunator.haikunate();
            mealPlanEntity.setName(mealPlanName);
        }
        // createMealPlan with repository and return
        mealPlanEntity.setUserId(user.getId());
        mealPlanEntity.setCreated(new Date());
        return mealPlanRepository.save(mealPlanEntity);
    }

    @Override
    public MealPlanEntity createMealPlanFromProposal(String username, Long proposalId) {
        // get username
        UserAccountEntity user = userService.getUserByUserName(username);
        // get proposal
        TargetProposalEntity proposalEntity = targetProposalService.getTargetProposalById(username, proposalId);

        if (proposalEntity == null) {
            return null;
        }

        // create the Meal Plan Entity
        MealPlanEntity mealPlan = new MealPlanEntity();
        mealPlan.setUserId(user.getId());
        mealPlan.setCreated(new Date());
        mealPlan.setMealPlanType(MealPlanType.Targeted);
        mealPlan.setName("generated from " + proposalEntity.getTargetName());
        mealPlan = mealPlanRepository.save(mealPlan);

        // get targets for proposal
        List<TargetProposalSlotEntity> proposalSlots = proposalEntity.getProposalSlots();
        if (proposalSlots == null) {
            return mealPlan;
        }
        for (TargetProposalSlotEntity proposalSlot : proposalSlots) {
            Long dishId = proposalSlot.getSelectedDishId();
            Optional<DishEntity> dishOpt = dishService.getDishForUserById(username, dishId);
            if (!dishOpt.isPresent()) {
                continue;
            }
            DishEntity dish = dishOpt.get();
            // add new meal plan slot
            SlotEntity slot = new SlotEntity();
            slot.setMealPlan(mealPlan);
            slot.setDish(dish);
            slotRepository.save(slot);
        }
        return mealPlan;
    }

    public MealPlanEntity getMealPlanById(String userName, Long mealPlanId) {
        UserAccountEntity user = userService.getUserByUserName(userName);

        Optional<MealPlanEntity> mealPlanEntityOpt = mealPlanRepository.findById(mealPlanId);
        if (!mealPlanEntityOpt.isPresent()) {
            return null;
        }
        MealPlanEntity mealPlanEntity = mealPlanEntityOpt.get();
        // ensure that this meal plan belongs to the user
        if (mealPlanEntity != null && mealPlanEntity.getUserId().equals(user.getId()) ) {
            return mealPlanEntity;
        }
        return null;
    }


    public void addDishToMealPlan(String username, Long mealPlanId, Long dishId) {
        // get meal plan
        MealPlanEntity mealPlan = getMealPlanById(username, mealPlanId);
        // MM need check here for mealplan not found
        // get dish
        Optional<DishEntity> dishOpt = dishService.getDishForUserById(username, dishId);
        if (!dishOpt.isPresent()) {
            logger.error("Dish can't be found for id [" + dishId + "] belonging to user [" + username + "]");
            return;
        }
        DishEntity dish =dishOpt.get();

        // add slot to dish
        List<SlotEntity> slotList = slotRepository.findByMealPlan(mealPlan);

        // add new slot
        SlotEntity slot = new SlotEntity();
        slot.setMealPlan(mealPlan);
        slot.setDish(dish);
        slotRepository.save(slot);

        slotList.add(slot);
        mealPlan.setSlots(slotList);
        mealPlanRepository.save(mealPlan);
    }

    public void deleteDishFromMealPlan(String username, Long mealPlanId, Long dishId) {
        // get meal plan
        MealPlanEntity mealPlan = getMealPlanById(username, mealPlanId);
        // get slots
        List<SlotEntity> slotList = slotRepository.findByMealPlan(mealPlan);

        SlotEntity toDelete = null;
        List<SlotEntity> toSave = new ArrayList<>();
        for (SlotEntity slot : slotList) {
            if (slot.getDish().getId().longValue() == dishId.longValue()) {
                toDelete = slot;
            } else {
                toSave.add(slot);
            }
        }
        // filter slot to be deleted from plan
        mealPlan.setSlots(toSave);
        mealPlanRepository.save(mealPlan);
        if (toDelete != null) {
            slotRepository.delete(toDelete);
        }


    }


    public boolean deleteMealPlan(String name, Long mealPlanId) {
        MealPlanEntity toDelete = getMealPlanById(name, mealPlanId);

        if (toDelete != null) {
            if (!toDelete.getSlots().isEmpty()) {
                slotRepository.deleteAll(toDelete.getSlots());
                toDelete.setSlots(null);
            }
            mealPlanRepository.delete(toDelete);
            return true;
        }
        return false;
    }

    public void renameMealPlan(String userName, Long mealPlanId, String newName) {
        MealPlanEntity mealPlan = getMealPlanById(userName,mealPlanId);
        mealPlan.setName(newName);
        mealPlanRepository.save(mealPlan);
    }

    public List<TagEntity> fillInDishTags(MealPlanEntity mealPlan) {
        List<Long> dishIds = mealPlan.getSlots().stream()
                .map(s -> s.getDish().getId())
                .collect(Collectors.toList());

        return tagRepository.getIngredientTagsForDishes(dishIds);
    }

    public void updateLastAddedDateForDishes(MealPlanEntity mealPlan) {
        if (mealPlan == null || mealPlan.getSlots() == null) {
            return;
        }
        // get ids for dishes
        List<Long> dishIds = mealPlan.getSlots().stream()
                .map(s -> s.getDish().getId())
                .collect(Collectors.toList());
        // update lastAdded date
        List<DishEntity> dishes = dishService.getDishes(dishIds);
        for (DishEntity dish : dishes) {
            dish.setLastAdded(new Date());
        }
        // save dishes
        dishService.save(dishes);
    }

    public List<TagEntity> getTagsForSlot(SlotEntity slot) {
        // MM implement this
        return null;
    }
}
