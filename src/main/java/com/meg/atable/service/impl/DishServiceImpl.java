package com.meg.atable.service.impl;

import com.meg.atable.api.DishNotFoundException;
import com.meg.atable.api.UnauthorizedAccessException;
import com.meg.atable.api.UserNotFoundException;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.auth.data.repository.UserRepository;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.repository.DishRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public Collection<DishEntity> getDishesForUserName(String userName) throws UserNotFoundException {
        UserAccountEntity user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new UserNotFoundException(userName);
        }
        return dishRepository.findByUserId(user.getId());
    }

    @Override
    public Optional<DishEntity> getDishById(Long dishId) {
        return Optional.of(dishRepository.findOne(dishId));
    }


    @Override
    public Optional<DishEntity> getDishForUserById(String username, Long dishId) {
        UserAccountEntity user = userRepository.findByUsername(username);
        DishEntity dish = dishRepository.findOne(dishId);
        if (dish == null) {
            throw new DishNotFoundException(dishId);
        }
        if (dish.getUserId() != user.getId()) {
            throw new UnauthorizedAccessException("Dish [" + dishId + "] doesn't belong to user [" + username + "].");
        }
        return Optional.of(dish);
    }

    @Override
    public DishEntity save(DishEntity dish) {
        return dishRepository.save(dish);
    }

    @Override
    public  List<DishEntity> save(List<DishEntity> dishes) {
return dishRepository.save(dishes);
    }
    @Override
    public List<DishEntity> getDishes(List<Long> dishIds) {
        return dishRepository.findAll(dishIds);
    }
}
