package com.meg.atable.service.impl;

import com.meg.atable.Application;
import com.meg.atable.api.model.TagInfo;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.DishService;
import com.meg.atable.service.TagService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class TagServiceImplTest {
    @Autowired
    private TagService tagService;

    @Autowired
    private DishService dishService;

    private TagEntity testTag;
    private TagEntity a;
    private TagEntity b;
    private TagEntity c;

    private DishEntity dish;

    @Before
    public void setUp() {
        // setting up for taginfo
        TagEntity parent = new TagEntity("parent","main1");

        parent = tagService.save(parent);

        testTag = tagService.createTag(parent,"testTag");
        TagEntity sub2 = tagService.createTag(parent,"testTagSibling");
        TagEntity sub3 = tagService.createTag(parent,"testTagSibling2");

        TagEntity sub4 = tagService.createTag(testTag,"testTagChild");
        TagEntity sub5 = tagService.createTag(testTag,"testTagAnotherChild");

        // setting up for error assign tag
        a = new TagEntity("a","a");
        a = tagService.createTag(null,a.getName());

        b = tagService.createTag(a,"b");
        c = tagService.createTag(b, "c");

        // setting up dish
        dish = new DishEntity();
        dish.setDishName("tagTest");
        dish.getTags().add(b);
        dish.getTags().add(c);

        dishService.save(dish);
    }

    @Test
    public void save() throws Exception {
        TagEntity testSave = new TagEntity();
        testSave.setName("testname");
        testSave.setDescription("testdescription");

        testSave = tagService.save(testSave);
        Long id = testSave.getId();

        TagEntity check = tagService.getTagById(id).get();
        Assert.assertNotNull(check);
        Assert.assertEquals(testSave.getName(),check.getName());
        Assert.assertEquals(testSave.getDescription(), check.getDescription());
    }


    @Test
    public void getTagById() throws Exception {
    }

    @Test
    public void getTagList() throws Exception {
    }

    @Test
    public void createTag() throws Exception {
    }

    @Test
    public void createTag1() throws Exception {
    }

    @Test
    public void getTagInfo() throws Exception {

        TagInfo tagInfo = tagService.getTagInfo(testTag.getId());

        // check that everything was retrieved correctly
        Assert.assertNotNull(tagInfo);
        Assert.assertEquals(testTag.getName(),tagInfo.getName());
        Assert.assertEquals(testTag.getDescription(),tagInfo.getDescription());

        Assert.assertNotNull(tagInfo.getParentId());
        TagEntity parent = tagService.getTagById(tagInfo.getParentId()).get();
        Assert.assertEquals(parent.getId().longValue(),tagInfo.getParentId().longValue());

        Assert.assertNotNull(tagInfo.getSiblingIds());
        List<Long> siblingids = tagInfo.getSiblingIds();
        List <TagInfo> siblingtags = getTagList(siblingids);
        Assert.assertNotNull(siblingtags);
        Assert.assertTrue(siblingtags.size()>1);
        Assert.assertTrue(siblingtags.get(0).getName().toLowerCase().contains("sibling"));

        Assert.assertNotNull(tagInfo.getChildrenIds());
        List<Long> childrenids = tagInfo.getChildrenIds();
        List <TagInfo> childrentags = getTagList(childrenids);
        Assert.assertNotNull(childrentags);
        Assert.assertTrue(childrentags.size()>1);
        Assert.assertTrue(childrentags.get(0).getName().toLowerCase().contains("child"));
    }

    @Test
    public void testGetTagsForDish() throws Exception {
        List<TagEntity> tags = tagService.getTagsForDish(dish.getId());

        Assert.assertNotNull(tags);
        Assert.assertTrue(tags.size() > 0);
        Assert.assertTrue(tags.size() == 2);

    }

    @Test
    public void testAddTagToDish() throws Exception {
        tagService.addTagToDish(dish.getId(),a.getId());

        List<TagEntity> tags = tagService.getTagsForDish(dish.getId());

        Assert.assertNotNull(tags);
        Assert.assertTrue(tags.size()==3);
        boolean containsTagA = false;
        for (TagEntity testTag:tags) {
            if (testTag.getId()==a.getId()) {
                containsTagA=true;
                break;
            }
        }
        Assert.assertTrue(containsTagA);
    }

    @Test
    public void testAssignTagToParent_error() {
        // tags a, b, c
        // assign a as child of c

        // service call
        boolean result = tagService.assignTagToParent(a.getId(),c.getId());

        Assert.assertFalse(result);

    }

    @Test
    public void testAssignTagToParent_noError() {
        // tags a, b, c
        // assign a as child of c

        // service call
        boolean result = tagService.assignTagToParent(c.getId(),a.getId());

        Assert.assertTrue(result);
        TagInfo resultInfo = tagService.getTagInfo(c.getId());
        Assert.assertNotNull(resultInfo);
        Assert.assertEquals(a.getId(),resultInfo.getParentId());
    }

    @Test
    public void testGetTagInfoFullList() {
        List<TagInfo> allTags = tagService.getTagInfoList(false);

        // test that each tag only exists once in list
        List<Long> idCheck = new ArrayList<>();
        for (TagInfo tag : allTags) {
            Long id = tag.getId();
            assertFalse(idCheck.contains(id));
            idCheck.add(id);
        }
    }

    private List<TagInfo> getTagList(List<Long> tagids) {
        return tagService.getTagList()
                .stream()
                .filter(t -> tagids.contains(t.getId()))
                .map(TagInfo::new)
                .collect(Collectors.toList());

    }

}