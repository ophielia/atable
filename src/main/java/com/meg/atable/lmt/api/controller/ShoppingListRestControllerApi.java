package com.meg.atable.lmt.api.controller;

import com.meg.atable.lmt.api.model.*;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/shoppinglist")
@CrossOrigin
public interface ShoppingListRestControllerApi {


    @GetMapping(produces = "application/json")
    ResponseEntity<Resources<ShoppingListResource>> retrieveLists(Principal principal);

    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> createList(Principal principal, @RequestBody ListGenerateProperties listGenerateProperties);

    @PutMapping(value = "/shared", produces = "application/json")
    ResponseEntity<MergeResultResource> mergeList(Principal principal, @RequestBody MergeRequest mergeRequest);

    @GetMapping(value = "/shared/{listLayoutId}", produces = "application/json")
    ResponseEntity<List<ListItemRefreshResource>> refreshListItems(Principal principal, @PathVariable("listLayoutId") Long listLayoutId, @RequestParam(value = "after", required = true) Date changedAfter);

    @PutMapping(value = "/{listId}", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> updateList(Principal principal, @PathVariable("listId") Long listId, @RequestBody ShoppingList shoppingList);

    @Deprecated
    @GetMapping(value = "/type/{listType}", produces = "application/json")
    ResponseEntity<ShoppingListResource> retrieveListByType(Principal principal, @PathVariable("listType") String listType);

    @GetMapping(value = "/mostrecent", produces = "application/json")
    ResponseEntity<ShoppingListResource> retrieveMostRecentList(Principal principal);

    @GetMapping(value = "/starter", produces = "application/json")
    ResponseEntity<ShoppingListResource> retrieveStarterList(Principal principal);

    @GetMapping(value = "/{listId}", produces = "application/json")
    ResponseEntity<ShoppingListResource> retrieveListById(Principal principal, @PathVariable("listId") Long listId,
                                                          @RequestParam(value = "highlightDish", required = false, defaultValue = "0") Long highlightDish,
                                                          @RequestParam(value = "highlightListId", required = false, defaultValue = "0") Long highlightListId,
                                                          @RequestParam(value = "showPantry", required = false, defaultValue = "false") Boolean showPantry);

    @Deprecated
    @GetMapping(value = "/active", produces = "application/json")
    ResponseEntity<ShoppingListResource> retrieveActiveList(Principal principal,
                                                            @RequestParam(value = "highlightDish", required = false, defaultValue = "0") Long highlightDish,
                                                            @RequestParam(value = "highlightListType", required = false, defaultValue = "0") String highlightListType,
                                                            @RequestParam(value = "showPantry", required = false, defaultValue = "false") Boolean showPantry);


    @RequestMapping(method = RequestMethod.DELETE, value = "/{listId}", produces = "application/json")
    ResponseEntity<ShoppingList> deleteList(Principal principal, @PathVariable("listId") Long listId);

    @RequestMapping(method = RequestMethod.POST, value = "/{listId}/item", produces = "application/json")
    ResponseEntity<Object> addItemToList(Principal principal, @PathVariable Long listId, @RequestBody Item input);

    @PostMapping(value = "/{listId}/tag/{tagId}", produces = "application/json")
    ResponseEntity<Object> addItemToListByTag(Principal principal, @PathVariable Long listId, @PathVariable Long tagId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{listId}/item/{itemId}", produces = "application/json")
    ResponseEntity<Object> deleteItemFromList(Principal principal, @PathVariable Long listId, @PathVariable Long itemId,
                                              @RequestParam(value = "removeEntireItem", required = false, defaultValue = "false") Boolean removeEntireItem,
                                              @RequestParam(value = "sourceId", required = false, defaultValue = "0") String sourceId
    );

    @RequestMapping(method = RequestMethod.POST, value = "/{listId}/item/shop/{itemId}", produces = "application/json")
    ResponseEntity<Object> setCrossedOffForItem(Principal principal, @PathVariable Long listId, @PathVariable Long itemId,
                                              @RequestParam(value = "crossOff", required = false, defaultValue = "false") Boolean crossedOff
    );

    @RequestMapping(method = RequestMethod.POST, value = "/{listId}/item/shop", produces = "application/json")
    ResponseEntity<Object> crossOffAllItemsOnList(Principal principal, @PathVariable Long listId,
                                                  @RequestParam(value = "crossOff", required = false, defaultValue = "false") Boolean crossedOff);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{listId}/item", produces = "application/json")
    ResponseEntity<Object> deleteAllItemsFromList(Principal principal, @PathVariable Long listId);

    @RequestMapping(method = RequestMethod.POST, value = "/mealplan/{mealPlanId}", produces = "application/json")
    ResponseEntity<Object> generateListFromMealPlan(Principal principal, @PathVariable Long mealPlanId);

    @RequestMapping(method = RequestMethod.POST, value = "/{listId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> addDishToList(Principal principal, @PathVariable Long listId, @PathVariable Long dishId);

    @RequestMapping(method = RequestMethod.DELETE, value = "/{listId}/dish/{dishId}", produces = "application/json")
    ResponseEntity<Object> removeDishFromList(Principal principal, @PathVariable Long listId, @PathVariable Long dishId);

    @PostMapping(value = "/{listId}/list/{fromListId}", produces = "application/json")
    ResponseEntity<Object> addToListFromList(Principal principal, @PathVariable Long listId, @PathVariable Long fromListId);


    @RequestMapping(method = RequestMethod.DELETE, value = "/{listId}/list/{fromListId}", produces = "application/json")
    ResponseEntity<Object> removeFromListByList(Principal principal, @PathVariable Long listId, @PathVariable Long fromListId);

    @RequestMapping(method = RequestMethod.POST, value = "/{listId}/layout/{layoutId}", produces = "application/json")
    ResponseEntity<Object> changeListLayout(Principal principal, @PathVariable Long listId, @PathVariable Long layoutId);


}
