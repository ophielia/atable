package com.meg.atable.service.tag.impl;

import com.meg.atable.api.model.TagFilterType;
import com.meg.atable.api.model.TagType;
import com.meg.atable.data.entity.DishEntity;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.data.entity.TagSearchGroupEntity;
import com.meg.atable.data.repository.DishRepository;
import com.meg.atable.data.repository.TagRepository;
import com.meg.atable.data.repository.TagSearchGroupRepository;
import com.meg.atable.service.tag.TagChangeListener;
import com.meg.atable.service.tag.TagService;
import com.meg.atable.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Service
public class StandardTagChangeListener implements TagChangeListener {


    @Autowired
    private TagService tagService;

    @Autowired
    private TagStructureService tagStructureService;

    @Autowired
    private TagSearchGroupRepository tagSearchGroupRepository;

    @Autowired
    private TagRepository tagRepository;


    @PostConstruct
    public void init() {
        tagService.addTagChangeListener(this);
    }


    @Override
    public void onParentChange(TagEntity origParentTag, TagEntity newParentTag, TagEntity childTag) {
        // assignSelect - original Parent
        // if we just removed the last child, the assign select should be set to false
        List<TagEntity> oldParentChildren = tagStructureService.getDescendantTags(origParentTag, false);
        if (oldParentChildren == null || oldParentChildren.isEmpty()) {
            // now this is a selectable tag, because it doesn't have children
            origParentTag.setAssignSelect(false);
        }

        // assignSelect - new Parent
        newParentTag.setAssignSelect(true);

        // assignSelect - child - no changes made / necessary

        // searchSelect - only interesting if the childTag is searchselectable
        if (childTag.getSearchSelect()) {
            List<TagEntity> childrenTags = tagStructureService.getDescendantTags(childTag, true);
            childrenTags.add(childTag);
            // remove tags (and children) from oldParent
            if (origParentTag.getSearchSelect())  {
                // get parent tags
                List<TagEntity> origParentTags = tagStructureService.getAscendantTags(origParentTag,true );
                origParentTags.add(origParentTag);
                List<Long> groupTagIds = origParentTags.stream().map(t -> t.getId()).collect(Collectors.toList());
                List<Long> memberTagIds = childrenTags.stream().map(t -> t.getId()).collect(Collectors.toList());
                // delete tag groups with groups in the parenttag, and children in the children tag
                tagStructureService.deleteTagGroupsByGroupAndMember(groupTagIds,memberTagIds);
            }

            // add tags to newParent - if new Parent is SearchSelect
            if (newParentTag.getSearchSelect()) {
                List<TagEntity> newParentTags = tagStructureService.getAscendantTags(newParentTag,true );
                newParentTags.add(newParentTag);
                // add all children tags to a group with this tag as the group
                List<TagSearchGroupEntity> groupAssignments = new ArrayList<>();
                // also add all children tags to any parent groups
                for (TagEntity parentTag: newParentTags) {
                    groupAssignments.addAll(tagStructureService.buildGroupAssignments(parentTag.getId(),childrenTags));
                }
                tagSearchGroupRepository.save(groupAssignments);
            }
        }


        // save origParentTag, newParentTag, childTag
        tagRepository.save(origParentTag);
        tagRepository.save(newParentTag);
    }

    @Override
    public void onSearchSelectChange(TagEntity updatedTag) {
        List<TagEntity> parentTags = tagStructureService.getAscendantTags(updatedTag,true );
        List<TagEntity> childrenTags = tagStructureService.getDescendantTags(updatedTag, true);
        childrenTags.add(updatedTag);

        if (!updatedTag.getSearchSelect()) {
        parentTags.add(updatedTag);
            List<Long> groupTagIds = parentTags.stream().map(t -> t.getId()).collect(Collectors.toList());
            List<Long> memberTagIds = childrenTags.stream().map(t -> t.getId()).collect(Collectors.toList());
            // delete tag groups with groups in the parenttag, and children in the children tag
            tagStructureService.deleteTagGroupsByGroupAndMember(groupTagIds,memberTagIds);
            return;
        }
        // no change of parent here - just either adding to groups or subtracting from groups
        // we'll need parent tags and direct descendant tags ( that have assign search set to true)

        // this tag is now a search select.
        // add all children tags to a group with this tag as the group
        List<TagSearchGroupEntity> groupAssignments = tagStructureService.buildGroupAssignments(updatedTag.getId(),childrenTags);
        // also add all children tags to any parent groups
        for (TagEntity parentTag: parentTags) {
            groupAssignments.addAll(tagStructureService.buildGroupAssignments(parentTag.getId(),childrenTags));
        }
        tagSearchGroupRepository.save(groupAssignments);
        return;
    }
}
