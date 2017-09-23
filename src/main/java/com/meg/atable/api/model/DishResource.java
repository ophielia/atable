package com.meg.atable.api.model;


import com.meg.atable.api.controller.DishRestControllerApi;
import com.meg.atable.auth.data.entity.UserAccountEntity;
import com.meg.atable.data.entity.DishEntity;
import org.springframework.hateoas.ResourceSupport;

import java.security.Principal;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class DishResource extends ResourceSupport {

    private Dish dish;

    public DishResource(DishEntity dishEntity) {
        this.dish = ModelMapper.toModel(dishEntity);

        String username = dishEntity.getUserAccount().getUsername();
        this.add(linkTo(DishRestControllerApi.class, username).withRel("dish"));
        this.add(linkTo(methodOn(DishRestControllerApi.class, username)
          .readDish( null,dish.getId())).withSelfRel());
        this.add(linkTo(methodOn(DishRestControllerApi.class, username)
                .getTagsByDishId(null,dish.getId())).withRel("tags"));
    }
    public Dish getDish() {
        return dish;
    }
}