package com.meg.atable.controller;

import com.meg.atable.api.controller.TagInfoRestControllerApi;
import com.meg.atable.api.model.*;
import com.meg.atable.data.entity.TagEntity;
import com.meg.atable.service.tag.TagService;
import com.meg.atable.service.tag.TagStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by margaretmartin on 13/05/2017.
 */
@Controller
public class TagInfoRestController implements TagInfoRestControllerApi {

    private final TagService tagService;

    private final TagStructureService tagStructureService;

    @Autowired
    TagInfoRestController(TagService tagService, TagStructureService tagStructureService) {
        this.tagService = tagService;
        this.tagStructureService = tagStructureService;
    }


    public ResponseEntity<TagInfoResource> retrieveTagList(@RequestParam(value = "tag_type", required = false) String tag_type,
                                                           @RequestParam(value = "filter", required = false) String filter) {
        List<TagType> tagTypes = processTagTypeInput(tag_type);
        TagFilterType filterType = TagFilterType.All;
        if (filter != null ) {
            filterType = TagFilterType.valueOf(filter);
        }
        // get tag list
        List<TagEntity> tagList =  tagService.getTagList(filterType,tagTypes);

        // fill in relationship info
        tagList = tagStructureService.fillInRelationshipInfo(tagList);
        // create taginforesource
        TagInfoResource tagInfo = new TagInfoResource(tagList);

        return new ResponseEntity(tagInfo, HttpStatus.OK);
    }


    public ResponseEntity<List<TagDrilldownResource>> retrieveTagListNew(@RequestParam(value = "tag_type", required = false) String tag_type) {
        List<TagType> tagTypes = processTagTypeInput(tag_type);
    List<FatTag> filledTags = tagStructureService.getTagsWithChildren(tagTypes);

    // create taginforesource
        List<TagDrilldownResource> resource = filledTags.stream()
                .map(TagDrilldownResource::new)
                .collect(Collectors.toList());

        return new ResponseEntity(resource, HttpStatus.OK);
    }



    private List<TagType> processTagTypeInput(String tag_type) {
        if (tag_type == null) {
            return null;
        } else if (tag_type.contains(",")) {
            return Arrays.asList(tag_type.split(",")).stream()
                    .map(t -> TagType.valueOf(t.trim()))
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList(TagType.valueOf(tag_type));
        }
    }

}
