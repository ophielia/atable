package com.meg.listshop.lmt.service.impl;

import com.meg.listshop.Application;
import com.meg.listshop.auth.data.entity.UserEntity;
import com.meg.listshop.auth.service.UserService;
import com.meg.listshop.configuration.ListShopPostgresqlContainer;
import com.meg.listshop.lmt.api.model.*;
import com.meg.listshop.lmt.data.entity.DishEntity;
import com.meg.listshop.lmt.data.entity.ItemEntity;
import com.meg.listshop.lmt.data.entity.ShoppingListEntity;
import com.meg.listshop.lmt.data.entity.TagEntity;
import com.meg.listshop.lmt.data.repository.ShoppingListRepository;
import com.meg.listshop.lmt.service.ShoppingListException;
import com.meg.listshop.lmt.service.ShoppingListProperties;
import com.meg.listshop.lmt.service.ShoppingListService;
import com.meg.listshop.lmt.service.tag.TagService;
import com.meg.listshop.test.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
@Sql(value = {"/sql/com/meg/atable/lmt/service/impl/ShoppingListServiceTest-rollback.sql",
        "/sql/com/meg/atable/lmt/service/impl/ShoppingListServiceTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ShoppingListServiceImplTest {

    @ClassRule
    public static ListShopPostgresqlContainer postgreSQLContainer = ListShopPostgresqlContainer.getInstance();

    @Autowired
    private ShoppingListService shoppingListService;
    @Autowired
    private UserService userService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ShoppingListProperties shoppingListProperties;
    @Autowired
    private ShoppingListRepository shoppingListRepository;
    private UserEntity userAccount;  // user_id 500
    private UserEntity addUserAccount;  // user_id 501
    private TagEntity tag1; // 500
    private TagEntity cheddarTag; // 18

    @Before
    public void setUp() {
        userAccount = userService.getUserByUserEmail(TestConstants.USER_1_NAME);
        addUserAccount = userService.getUserByUserEmail(TestConstants.USER_2_NAME);
        // make tags
        tag1 = tagService.getTagById(TestConstants.TAG_1_ID);
        cheddarTag = tagService.getTagById(18L); // 18 is cheddar tag id;
    }

    @Test
    public void testGetListsByUsername() {
        List<ShoppingListEntity> results = shoppingListService.getListsByUsername(TestConstants.USER_1_NAME);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 5);
    }

    @Test
    public void testGetListByUsername() {
        ShoppingListEntity result = shoppingListService.getListById(userAccount.getEmail(), TestConstants.LIST_1_ID);


        Assert.assertNotNull(result);
        Assert.assertEquals(TestConstants.LIST_1_ID, result.getId());
    }


    @Test
    public void testGetListByUsername_BadUser() {
        ShoppingListEntity result = shoppingListService.getListById("noseyusername",
                TestConstants.LIST_1_ID);

        Assert.assertNull(result);
    }



    @Test
    public void testCreateList() throws ShoppingListException {
        ListGenerateProperties properties = new ListGenerateProperties();

        ShoppingListEntity result = shoppingListService.generateListForUser(addUserAccount.getEmail(), properties);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCreatedOn());
        Assert.assertNotNull(result.getId());
    }

    @Test
    public void testDeleteList() {
        boolean result = shoppingListService.deleteList(userAccount.getEmail(), TestConstants.LIST_3_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testGenerateListFromMealPlan() {
        ShoppingListEntity result = shoppingListService.generateListFromMealPlan(userAccount.getEmail(), TestConstants.MEAL_PLAN_1_ID);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetProperties() {
        Assert.assertNotNull(shoppingListProperties);
        Assert.assertNotNull(shoppingListProperties.getTestValue());
        Assert.assertEquals("beep", shoppingListProperties.getTestValue());
    }

    @Test
    public void testCategorizeList() {
        ShoppingListEntity result = shoppingListService.getListById(TestConstants.USER_1_NAME, TestConstants.LIST_1_ID);

        List<Category> categoryEntities = shoppingListService.categorizeList(result);
        Assert.assertNotNull(categoryEntities);

        // count items and subcategories
        int itemcount = 0;
        int subcatcount = 0;
        for (Category categoryResult : categoryEntities) {
            ItemCategory cr = (ItemCategory) categoryResult;
            itemcount += cr.getItemEntities().size();
            if (!categoryResult.getSubCategories().isEmpty()) {
                subcatcount += categoryResult.getSubCategories().size();
                itemcount += categoryResult.getSubCategories()
                        .stream()
                        .mapToInt(sc -> ((ItemCategory) sc).getItemEntities().size())
                        .sum();
            }

        }
        Assert.assertEquals(4, itemcount);
        Assert.assertEquals(2, subcatcount);
    }

    @Test
    public void addDishToList() throws ShoppingListException {
        // use test data list which contains onions
        final Long LIST_ID = TestConstants.LIST_2_ID;
        final Long ONION_TAG_ID = 16L;
        final Long HAMBURGER_TAG_ID = 435L;
        final Long DISH_ID = 16L;
        final String USER_NAME = TestConstants.USER_3_NAME;

        // add dish cheeseburger maccoroni  // dish_id 16
        this.shoppingListService.addDishToList(USER_NAME, LIST_ID, DISH_ID);

        // get list
        ShoppingListEntity list = this.shoppingListService.getListById(USER_NAME, LIST_ID);

        boolean hasHamburger = false, hasOnion = false;
        for (ItemEntity item : list.getItems()) {
            if (item.getTag().getId().equals(HAMBURGER_TAG_ID)) {
                hasHamburger = true;
                Assert.assertTrue(item.getRawDishSources().contains(String.valueOf(DISH_ID)));
            } else if (item.getTag().getId().equals(ONION_TAG_ID)) {
                hasOnion = true;
                Assert.assertEquals(2L, item.getUsedCount().longValue());
                Assert.assertTrue(item.getRawDishSources().contains(String.valueOf(DISH_ID)));
            }
        }
        // TODO - test for sources, when sources are complete
        Assert.assertTrue(hasHamburger);
        Assert.assertTrue(hasOnion);
    }

    @Test
    public void removeDishFromList() throws Exception {
        // use list 2 - modification
        // list 2 has dish kate salad tuna, chickpeas and scallion
        ShoppingListEntity list = shoppingListService.getListById(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID);

        // get total count of items (including usedcount)
        Integer startSum = list.getItems().stream()
                .mapToInt(t -> t.getUsedCount()).sum();

        // call remove dish
        shoppingListService.removeDishFromList(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID, 83L);

        // retrieve list
        ShoppingListEntity result = shoppingListService.getListById(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID);

        // get total count of items (including usedcount)
        Integer resultSum = result.getItems().stream()
                .mapToInt(t -> t.getUsedCount()).sum();

        // should be 3 items less
        Assert.assertTrue(startSum - resultSum.longValue() <= 4);

        // go through all ensuring
        //  no tuna (210)
        //  no chickpeas (113)
        //  no scallion (211)
        // and no dish_source for kate salad (83)
        for (ItemEntity item : result.getItems()) {
            if (item.getTag().getId().equals(210L) && item.getRemovedOn() == null) {
                Assert.fail("tuna found");
            }
            if (item.getTag().getId().equals(113L)) {
                if (item.getUsedCount() > 1)
                    Assert.fail("chickpeas found, used more than once");
            }
            if (item.getTag().getId().equals(211L) && item.getRemovedOn() == null) {
                Assert.fail("scallion found");
            }
            if (item.getRawDishSources() != null && item.getRawDishSources().contains(";83;")) {
                Assert.fail("dish id still found in source");
            }
        }

    }

    @Test
    public void fillSources() throws Exception {
        // test begin state - list contains
        //    broccoli(21) for scoozi (90)
        //    onions(16) for cheeseburger maccaroni (16)
        //    honey (359) for pickup list
        //    cat food (470) for base list

        // get listEntity (list 2)
        ShoppingListEntity shoppingListEntity = shoppingListService.getListById(TestConstants.USER_3_NAME, TestConstants.LIST_2_ID);

        // call fillSources
        shoppingListService.fillSources(shoppingListEntity);

        // results -
        // dish sources should be 2 - scoozi (90) and cheeseburger maccaroni (16)
        Assert.assertTrue(shoppingListEntity.getDishSources().size() >= 2);
        boolean hasScoozi = false;
        boolean hasCheeseMac = false;
        for (DishEntity dish : shoppingListEntity.getDishSources()) {
            if (dish.getId().equals(90L)) {
                hasScoozi = true;
            } else if (dish.getId().equals(16L)) {
                hasCheeseMac = true;
            }
        }
        Assert.assertTrue(hasScoozi);
        Assert.assertTrue(hasCheeseMac);
        Optional<DishEntity> test = shoppingListEntity.getDishSources().stream().filter(d -> d.getId().equals(16L)).findFirst();
        Assert.assertTrue(test.isPresent()); // cheeseburger maccaroni there
        test = shoppingListEntity.getDishSources().stream().filter(d -> d.getId().equals(90L)).findFirst();
        Assert.assertTrue(test.isPresent()); // scoozi there

    }

    @Test
    public void changeListLayout() throws Exception {
        // use modified list - which begins with layout fine grained (1)
        // and will be changed to all

        ShoppingListEntity modifiedList = shoppingListService.getListById(TestConstants.USER_1_NAME, TestConstants.LIST_1_ID);
        Long origLayout = modifiedList.getListLayoutId();

        shoppingListService.changeListLayout(TestConstants.USER_1_NAME, TestConstants.LIST_1_ID, TestConstants.LIST_LAYOUT_3_ID);

        ShoppingListEntity check = shoppingListService.getListById(TestConstants.USER_1_NAME, TestConstants.LIST_1_ID);
        Assert.assertNotEquals(origLayout, check.getListLayoutId());
    }



    @Test
    public void testMerge_EmptyMerge() {
        //  test empty merge - empty client list
        // pull list before merge and save updated, item count
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);
        Date beforeDate = before.getLastUpdate();
        int beforeItemCount = before.getItems().size();


        MergeRequest mergeRequest = createMergeList(5000L);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);
        shoppingListRepository.flush();
        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);
        Date afterDate = after.getLastUpdate();
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count
        Assert.assertEquals(beforeDate, afterDate);
        Assert.assertEquals(beforeItemCount, afterItemCount);
    }


    @Test
    public void testMerge_NewFromClient() {
        // pull list before merge and save updated, item count
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);
        Date beforeDate = before.getLastUpdate();
        int beforeItemCount = before.getItems().size();


        MergeRequest mergeRequest = createMergeList(5000L);
        // add one new item to the list
        Item item = createTestItem("45", new Date(), null, null, null);
        mergeRequest.getMergeItems().add(item);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);

        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);
        Date afterDate = after.getLastUpdate();
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count increased by 1
        Assert.assertNotEquals(beforeDate, afterDate);
        Assert.assertEquals(beforeItemCount + 1, afterItemCount);
        // assert that new tag is in the list
        Map<Long, ItemEntity> itemsByTag = new HashMap<>();
        after.getItems().forEach(e -> itemsByTag.put(e.getTag().getId(), e));
        Assert.assertTrue(itemsByTag.containsKey(45L));

    }

    @Test
    public void testMerge_NoChangeFromClient() {
        // pull list before merge and save updated, item count
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);
        Date beforeDate = before.getLastUpdate();
        int beforeItemCount = before.getItems().size();


        MergeRequest mergeRequest = createMergeList(5000L);
        // add one new item to the list

        Item item = createTestItem("501", 5, 0, 0, 0);
        Item item2 = createTestItem("502", 5, 4, 0, 0);
        mergeRequest.getMergeItems().add(item);
        mergeRequest.getMergeItems().add(item2);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);

        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);
        Date afterDate = after.getLastUpdate();
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count
        Assert.assertEquals(beforeDate, afterDate);
        Assert.assertEquals(beforeItemCount, afterItemCount);

    }

    @Test
    public void testMerge_UpdatedAndNewFromClient() {
        // pull list before merge and save updated, item count
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);
        Date beforeDate = before.getLastUpdate();
        int beforeItemCount = before.getItems().size();


        MergeRequest mergeRequest = createMergeList(5000L);
        // add one new item to the list

        Item item = createTestItem("501", 5, 0, 0, 0);
        Item item2 = createTestItem("502", 5, 4, 0, 0);
        Item item3 = createTestItem("503", 5, 4, 3, 0);
        Item item4 = createTestItem("504", 5, 4, 3, 2);
        mergeRequest.getMergeItems().add(item);
        mergeRequest.getMergeItems().add(item2);
        mergeRequest.getMergeItems().add(item3);
        mergeRequest.getMergeItems().add(item4);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);

        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);
        Date afterDate = after.getLastUpdate();
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count
        Assert.assertEquals(beforeDate, afterDate);
        Assert.assertEquals(beforeItemCount, afterItemCount);

    }

    @Test
    public void testMerge_AllUpdatedNoChangeOneNewFromClient() {
        // pull list before merge and save updated, item count
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);
        Date beforeDate = before.getLastUpdate();
        int beforeItemCount = before.getItems().size();


        MergeRequest mergeRequest = createMergeList(5000L);
        // add one new item to the list

        Item item = createTestItem("501", 5, 0, 0, 0);
        Item item2 = createTestItem("502", 5, 4, 0, 0);
        Item item3 = createTestItem("503", 5, 4, 3, 0);
        Item item4 = createTestItem("504", 5, 4, 3, 2);
        Item item5 = createTestItem("45", new Date(), new Date(), null, null);
        mergeRequest.getMergeItems().add(item);
        mergeRequest.getMergeItems().add(item2);
        mergeRequest.getMergeItems().add(item3);
        mergeRequest.getMergeItems().add(item4);
        mergeRequest.getMergeItems().add(item5);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);

        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 5000L);
        Date afterDate = after.getLastUpdate();
        int afterItemCount = after.getItems().size();

        // expect change in last update date, item count increased by 1
        Assert.assertNotEquals(beforeDate, afterDate);
        Assert.assertEquals(beforeItemCount + 1, afterItemCount);
        // assert that new tag is in the list
        Map<Long, ItemEntity> itemsByTag = new HashMap<>();
        after.getItems().forEach(e -> itemsByTag.put(e.getTag().getId(), e));
        Assert.assertTrue(itemsByTag.containsKey(45L));

    }

    @Test
    public void testMerge_ReplaceServerEmptyClient() {
        // pull list before merge and save updated, item count
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 50001L);
        Date beforeDate = before.getLastUpdate();
        int beforeItemCount = before.getItems().size();


        MergeRequest mergeRequest = createMergeList(50001L);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);
        shoppingListRepository.flush();
        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 50001L);
        Date afterDate = after.getLastUpdate();
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count
        Assert.assertEquals(beforeItemCount, afterItemCount);


        // assert that replaced tag is in the list
        Map<Long, ItemEntity> itemsByTag = new HashMap<>();
        after.getItems().forEach(e -> itemsByTag.put(e.getTag().getId(), e));
        Assert.assertTrue(itemsByTag.containsKey(55L));
        Assert.assertFalse(itemsByTag.containsKey(6666L));
        Assert.assertNotEquals(beforeDate, afterDate);
    }

    @Test
    public void testMerge_ReplaceServerNonEmptyNonCollidingClient() {
        // pull list before merge and save updated, item count
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 50001L);
        Date beforeDate = before.getLastUpdate();
        int beforeItemCount = before.getItems().size();
        Map<Long, ItemEntity> beforeItemsByTag = new HashMap<>();
        before.getItems().forEach(e -> beforeItemsByTag.put(e.getTag().getId(), e));


        MergeRequest mergeRequest = createMergeList(50001L);
        // add one new item to the list

        Item item = createTestItem("502", 6, 0, 0, 0);
        mergeRequest.getMergeItems().add(item);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);
        shoppingListRepository.flush();
        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 50001L);
        Date afterDate = after.getLastUpdate();
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count
        Assert.assertEquals(beforeItemCount, afterItemCount);


        // assert that replaced tag is in the list
        Map<Long, ItemEntity> itemsByTag = new HashMap<>();
        after.getItems().forEach(e -> itemsByTag.put(e.getTag().getId(), e));
        Assert.assertTrue(itemsByTag.containsKey(55L));
        Assert.assertFalse(itemsByTag.containsKey(6666L));
        Assert.assertNotEquals(beforeDate, afterDate);
        Assert.assertTrue(itemsByTag.containsKey(502L));
        ItemEntity beforeItem = beforeItemsByTag.get(502L);
        ItemEntity afterItem = itemsByTag.get(502L);
        Assert.assertEquals(beforeItem.getCrossedOff(), afterItem.getCrossedOff());
    }

    @Test
    public void testMerge_ReplaceServerNonEmptyCollidingClient() {
        // pull list before merge and save updated, item count
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 50001L);
        Date beforeDate = before.getLastUpdate();
        int beforeItemCount = before.getItems().size();
        Map<Long, ItemEntity> beforeItemsByTag = new HashMap<>();
        before.getItems().forEach(e -> beforeItemsByTag.put(e.getTag().getId(), e));


        MergeRequest mergeRequest = createMergeList(50001L);
        // add one new item to the list

        Item item = createTestItem("6666", 6, 0, 0, 1);
        mergeRequest.getMergeItems().add(item);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);
        shoppingListRepository.flush();
        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 50001L);
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count
        Assert.assertEquals(beforeItemCount, afterItemCount);


        // assert that replaced tag is in the list
        Map<Long, ItemEntity> itemsByTag = new HashMap<>();
        after.getItems().forEach(e -> itemsByTag.put(e.getTag().getId(), e));
        Assert.assertTrue(itemsByTag.containsKey(55L));
        Assert.assertFalse(itemsByTag.containsKey(6666L));
        ItemEntity afterItem = itemsByTag.get(55L);
        Assert.assertNotNull(afterItem.getRemovedOn());

    }

    @Test
    public void testMerge_ReplaceTagServerExists() {
        // the replace tag already exists in the list.
        // pull list before merge and save updated, item count
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 50002L);
        int beforeItemCount = before.getItems().size();
        Map<Long, ItemEntity> beforeItemsByTag = new HashMap<>();
        before.getItems().forEach(e -> beforeItemsByTag.put(e.getTag().getId(), e));
        Integer beforeItemUsedCount = beforeItemsByTag.get(55L).getUsedCount();


        MergeRequest mergeRequest = createMergeList(50002L);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);
        shoppingListRepository.flush();
        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 50002L);
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count
        // minus one because the two tags have been combined into one item
        Assert.assertEquals(beforeItemCount - 1, afterItemCount);


        // assert that replaced tag is in the list
        Map<Long, ItemEntity> itemsByTag = new HashMap<>();
        after.getItems().forEach(e -> itemsByTag.put(e.getTag().getId(), e));
        Assert.assertTrue(itemsByTag.containsKey(55L));
        Assert.assertFalse(itemsByTag.containsKey(6666L));
        ItemEntity beforeItem = beforeItemsByTag.get(55L);
        ItemEntity afterItem = itemsByTag.get(55L);
        Assert.assertEquals(beforeItemUsedCount+ 1, (long)afterItem.getUsedCount());

    }

    @Test
    public void testMerge_ReplaceTagServerExistsClientUpdates() {
        // the replace tag already exists in the list.
        // client sends only replace tag - more recent than server
        // should result in usedcount 2
        // pull list before merge and save updated, item count
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 50002L);
        int beforeItemCount = before.getItems().size();
        Map<Long, ItemEntity> beforeItemsByTag = new HashMap<>();
        before.getItems().forEach(e -> beforeItemsByTag.put(e.getTag().getId(), e));
        Integer beforeItemUsedCount = beforeItemsByTag.get(55L).getUsedCount();


        MergeRequest mergeRequest = createMergeList(50002L);
        Item item = createTestItem("6666", 3, 0, 0, 0);
        mergeRequest.getMergeItems().add(item);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);
        shoppingListRepository.flush();
        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 50002L);
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count
        // minus one because the two tags have been combined into one item
        Assert.assertEquals(beforeItemCount - 1, afterItemCount);


        // assert that replaced tag is in the list
        Map<Long, ItemEntity> itemsByTag = new HashMap<>();
        after.getItems().forEach(e -> itemsByTag.put(e.getTag().getId(), e));
        Assert.assertTrue(itemsByTag.containsKey(55L));
        Assert.assertFalse(itemsByTag.containsKey(6666L));
        ItemEntity beforeItem = beforeItemsByTag.get(55L);
        ItemEntity afterItem = itemsByTag.get(55L);
        Assert.assertEquals(beforeItemUsedCount+ 1, (long)afterItem.getUsedCount());

    }

    @Test
    public void testMerge_ReplaceTagServerExistsClientUpdates2() {
        // the replace tag already exists in the list.
        // client sends replace tag and replaced with tag - more recent than server
        // should result in usedcount 2
        // pull list before merge and save updated, item count
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 50002L);
        int beforeItemCount = before.getItems().size();
        Map<Long, ItemEntity> beforeItemsByTag = new HashMap<>();
        before.getItems().forEach(e -> beforeItemsByTag.put(e.getTag().getId(), e));
        Integer beforeItemUsedCount = beforeItemsByTag.get(55L).getUsedCount();


        MergeRequest mergeRequest = createMergeList(50002L);
        Item item = createTestItem("6666", 3, 0, 0, 0);
        Item item2 = createTestItem("55", 3, 99, 0, 0);
        mergeRequest.getMergeItems().add(item);
        mergeRequest.getMergeItems().add(item2);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);
        shoppingListRepository.flush();
        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 50002L);
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count
        // minus one because the two tags have been combined into one item
        Assert.assertEquals(beforeItemCount - 1, afterItemCount);


        // assert that replaced tag is in the list
        Map<Long, ItemEntity> itemsByTag = new HashMap<>();
        after.getItems().forEach(e -> itemsByTag.put(e.getTag().getId(), e));
        Assert.assertTrue(itemsByTag.containsKey(55L));
        Assert.assertFalse(itemsByTag.containsKey(6666L));
        ItemEntity beforeItem = beforeItemsByTag.get(55L);
        ItemEntity afterItem = itemsByTag.get(55L);
        Assert.assertEquals(beforeItemUsedCount+ 1, (long)afterItem.getUsedCount());

    }

    @Test
    public void testMerge_ReplaceTagServerExistsClientUpdates3() {
        // the replace tag 55 doesn't exist in the list.
        // client sends replace tag and replaced with tag - more recent than server
        // should result in usedcount 2
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 50001L);
        int beforeItemCount = before.getItems().size();
        Map<Long, ItemEntity> beforeItemsByTag = new HashMap<>();
        before.getItems().forEach(e -> beforeItemsByTag.put(e.getTag().getId(), e));
        Integer beforeItemUsedCount = beforeItemsByTag.get(6666L).getUsedCount();


        MergeRequest mergeRequest = createMergeList(50001L);
        Item item = createTestItem("6666", 3, 99, 0, 0);
        Item item2 = createTestItem("55", 3, 99, 0, 0);
        mergeRequest.getMergeItems().add(item);
        mergeRequest.getMergeItems().add(item2);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);
        shoppingListRepository.flush();
        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 50001L);
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count
        // minus one because the two tags have been combined into one item
        Assert.assertEquals(beforeItemCount, afterItemCount);


        // assert that replaced tag is in the list
        Map<Long, ItemEntity> itemsByTag = new HashMap<>();
        after.getItems().forEach(e -> itemsByTag.put(e.getTag().getId(), e));
        Assert.assertTrue(itemsByTag.containsKey(55L));
        Assert.assertFalse(itemsByTag.containsKey(6666L));
        ItemEntity afterItem = itemsByTag.get(55L);
        Assert.assertEquals(beforeItemUsedCount+ 1, (long)afterItem.getUsedCount());

    }

    @Test
    public void testMerge_ReplaceTagServerExistsClientUpdates4() {
        // the replace tag 55 doesn't exist in the list.
        // client sends replace tag and replaced with tag - NOT more recent than server
        // should result in usedcount 1
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 50001L);
        int beforeItemCount = before.getItems().size();
        Map<Long, ItemEntity> beforeItemsByTag = new HashMap<>();
        before.getItems().forEach(e -> beforeItemsByTag.put(e.getTag().getId(), e));
        Integer beforeItemUsedCount = beforeItemsByTag.get(6666L).getUsedCount();


        MergeRequest mergeRequest = createMergeList(50001L);
        Item item = createTestItem("6666", 5, 0, 0, 0);
        Item item2 = createTestItem("55", 5, 5, 0, 0);
        mergeRequest.getMergeItems().add(item);
        mergeRequest.getMergeItems().add(item2);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);
        shoppingListRepository.flush();
        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 50001L);
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count
        // minus one because the two tags have been combined into one item
        Assert.assertEquals(beforeItemCount, afterItemCount);


        // assert that replaced tag is in the list
        Map<Long, ItemEntity> itemsByTag = new HashMap<>();
        after.getItems().forEach(e -> itemsByTag.put(e.getTag().getId(), e));
        Assert.assertTrue(itemsByTag.containsKey(55L));
        Assert.assertFalse(itemsByTag.containsKey(6666L));
        ItemEntity afterItem = itemsByTag.get(55L);
        Assert.assertEquals(beforeItemUsedCount,afterItem.getUsedCount());

    }

    @Test
    public void testMerge_ReplaceTagServerExistsClientUpdates5() {
        // the replace tag 55 doesn't exist in the list.
        // client sends replaced (6666) with tag - NOT more recent than server
        // should result in usedcount 1
        ShoppingListEntity before = shoppingListService.getListById(TestConstants.USER_1_NAME, 50001L);
        int beforeItemCount = before.getItems().size();
        Map<Long, ItemEntity> beforeItemsByTag = new HashMap<>();
        before.getItems().forEach(e -> beforeItemsByTag.put(e.getTag().getId(), e));
        Integer beforeItemUsedCount = beforeItemsByTag.get(6666L).getUsedCount();


        MergeRequest mergeRequest = createMergeList(50001L);
        Item item = createTestItem("6666", 5, 0, 0, 0);
        mergeRequest.getMergeItems().add(item);

        shoppingListService.mergeFromClient(TestConstants.USER_1_NAME, mergeRequest);
        shoppingListRepository.flush();
        // pull list after merge
        ShoppingListEntity after = shoppingListService.getListById(TestConstants.USER_1_NAME, 50001L);
        int afterItemCount = after.getItems().size();

        // expect no change in last update date, item count
        // minus one because the two tags have been combined into one item
        Assert.assertEquals(beforeItemCount, afterItemCount);


        // assert that replaced tag is in the list
        Map<Long, ItemEntity> itemsByTag = new HashMap<>();
        after.getItems().forEach(e -> itemsByTag.put(e.getTag().getId(), e));
        Assert.assertTrue(itemsByTag.containsKey(55L));
        Assert.assertFalse(itemsByTag.containsKey(6666L));
        ItemEntity afterItem = itemsByTag.get(55L);
        Assert.assertEquals(1,(long)afterItem.getUsedCount());

    }

    @Test
    public void testDates() {
        //LocalDateTime dateTime = LocalDateTime.now().minusMonths(6);
        //Date date = java.sql.Timestamp.valueOf(dateTime);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -6);
        Date date = calendar.getTime();
        List<ItemEntity> items = shoppingListService.getChangedItemsForMostRecentList(TestConstants.USER_3_NAME, date, 5L);
        Assert.assertNotNull(items);
        Assert.assertFalse(items.isEmpty());
    }

    // done - empty merge
    // done - test basic merge - 1 new no replace
    // done - test no change merge
    // done - test no change merge - plus new item from client
    // done - test updated from client - takes new dates

    // done - test server side updated - no client objects
    // done - test server side updated - client updated also - but not replace tag
    // done - test server side updated - client updated also - replace tag
    // ip test server side updated - updated already exists - count is incremented
    //  test server side updated - updated already exists - and client is added with old id count should be 3
    // test no replace, but added - test that counts are updated

    /*    -- basic list (5000), 4 items different status - 5000, tags 501-504, items, 50001-50002
                -- tag 6666 - replaced by tag 55
                -- replacement list (5001), 1 item with tag_id 6666, 1 item without - tag_id 502
                -- replacement double jeopardy (5002) - 1 item with tag_id 6666, 1 item with tag_id 55
*/


    private MergeRequest createMergeList(long listId) {
        MergeRequest testMergeRequest = new MergeRequest();
        testMergeRequest.setListId(listId);
        return testMergeRequest;
    }


    private Item createTestItem(String tagId, Date addedDate, Date updatedDate, Date crossedOffDate, Date removedDate) {
        Item item = new Item();
        item.setTagId(tagId);
        item.addedOn(addedDate);
        item.updated(updatedDate);
        item.usedCount(1);
        item.crossedOff(crossedOffDate);
        item.removed(removedDate);
        return item;
    }

    private Item createTestItem(String tagId, int addedDateInterval, int updatedDateInterval, int crossedOffDateInterval, int removedDateInterval) {
        Item item = new Item();
        item.setTagId(tagId);
        item.usedCount(1);
        item.addedOn(getDateForInterval(addedDateInterval));
        item.updated(getDateForInterval(updatedDateInterval));
        item.crossedOff(getDateForInterval(crossedOffDateInterval));
        item.removed(getDateForInterval(removedDateInterval));
        return item;
    }

    private Date getDateForInterval(int interval) {
        LocalDateTime now = LocalDateTime.now();
        if (interval == 0) {
            return null;
        }
        if (interval == 99) {
            // cheating for test - 5 seconds in the future
            LocalDateTime newDate = now.plusSeconds(5);
            return java.sql.Timestamp.valueOf(newDate);
        }
        LocalDateTime newDate = now.minusDays(interval);
        return java.sql.Timestamp.valueOf(newDate);
    }


}