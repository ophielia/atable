package com.meg.atable.api;

import com.meg.atable.model.Tag;
import com.meg.atable.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/tag")
public class TagRestController {

    private final TagService tagService;

    @Autowired
    TagRestController(TagService tagService) {
        this.tagService = tagService;
    }


    @RequestMapping(method = RequestMethod.GET)
    Collection<Tag> retrieveTagList() {
        return this.tagService.getTagList();
    }

    @RequestMapping(method = RequestMethod.POST,produces = "application/json",consumes = "application/json")
    ResponseEntity<TagResource> add(@RequestBody Tag input) {
        Tag result = this.tagService.save(input);

        if (result != null) {
            Link forOneTag= new TagResource(result).getLink("self");
            return ResponseEntity.created(URI.create(forOneTag.getHref())).build();

        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{tagId}",produces = "application/json")
    ResponseEntity<Tag> readTag(@PathVariable Long tagId) {
        // MM
        // invalid dishId - returns invalid id supplied - 400

        return this.tagService
                .getTagById(tagId)
                .map(tag -> {
                    TagResource tagResource = new TagResource(tag);

                    return new ResponseEntity(tagResource, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());

    }

    @RequestMapping(method = RequestMethod.PUT,value = "/{tagId}",consumes = "application/json")
    ResponseEntity<Object> updateTag(@PathVariable Long tagId,@RequestBody Tag input) {
        // MM
        // invalid tagId - returns invalid id supplied - 400

        // MM
        // invalid contents of input - returns 405 validation exception

        return this.tagService
                .getTagById(tagId)
                .map(tag -> {
                    tag.setDescription(input.getDescription());
                    tag.setName(input.getName());

                    tagService.save(tag);

                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());

    }

}
