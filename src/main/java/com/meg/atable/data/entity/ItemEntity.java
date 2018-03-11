package com.meg.atable.data.entity;

import com.meg.atable.api.model.ItemSourceType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "list_item")
@SequenceGenerator(name = "list_item_sequence", sequenceName = "list_item_sequence")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "list_item_sequence")
    @Column(name = "item_id")
    private Long item_id;

    @OneToOne
    @JoinColumn(name = "tag_id")
    private TagEntity tag;

    @Column(name = "source")
    private String itemSource;

    @Column(name = "list_id")
    private Long listId;

    @Column(name = "used_count")
    private Integer usedCount;

    private Date addedOn;

    private String freeText;

    private Date crossedOff;

    private String listCategory;

    @Transient
    private Long tag_id;

    @Transient
    private boolean isFrequent = false;

    private Long categoryId;

    public ItemEntity(Long id) {
        item_id = id;
    }

    public ItemEntity() {
        // necessary for jpa construction
    }

    public Long getId() {
        return item_id;
    }

    public TagEntity getTag() {
        return tag;
    }

    public void setTag(TagEntity tag) {
        this.tag = tag;
    }

    public String getItemSource() {
        return itemSource;
    }

    public void setItemSource(String itemSource) {
        this.itemSource = itemSource;
    }

    public Date getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(Date addedOn) {
        this.addedOn = addedOn;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public Date getCrossedOff() {
        return crossedOff;
    }

    public void setCrossedOff(Date crossedOff) {
        this.crossedOff = crossedOff;
    }

    public String getListCategory() {
        return listCategory;
    }

    public void setListCategory(String listCategory) {
        this.listCategory = listCategory;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Long getTagId() {
        return tag_id;

    }

    public void setTagId(Long tag_id) {
        this.tag_id = tag_id;
    }


    public void addItemSource(ItemSourceType sourceType) {
        if (this.itemSource == null) {
            this.itemSource = sourceType.name();
            return;
        }
        if (this.itemSource.contains(sourceType.name())) {
            return;
        }
        this.itemSource = this.itemSource + ";" + sourceType.name();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isFrequent() {
        return isFrequent;
    }

    public void setFrequent(boolean frequent) {
        isFrequent = frequent;
    }
}
