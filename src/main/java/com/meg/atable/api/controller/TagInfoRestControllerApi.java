package com.meg.atable.api.controller;

import com.meg.atable.api.model.TagDrilldownResource;
import com.meg.atable.api.model.TagInfoResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/taginfo")
public interface TagInfoRestControllerApi {


    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<List<TagDrilldownResource>> retrieveTagList(@RequestParam(value = "tag_type", required = false) String tag_type);

    }
