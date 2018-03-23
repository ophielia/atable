package com.meg.atable.data.entity;

import com.meg.atable.api.model.ApproachType;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "proposal_context")
@SequenceGenerator(name="proposal_context_sequence", sequenceName = "proposal_context_sequence")
public class ProposalContextEntity {

    @Id
    @GeneratedValue( strategy=GenerationType.SEQUENCE, generator="proposal_context_sequence")
    @Column(name = "proposal_context_id")
    private Long proposalContextId;

    private Long proposalId;

    @OneToMany(mappedBy = "proposalContext", fetch = FetchType.EAGER)
    private List<ProposalContextApproachEntity> contextApproaches;

    private Integer maximumEmpties;

    private Integer dishCountPerSlot;

    @Enumerated(EnumType.STRING)
    private ApproachType approachType;

    private Integer proposalCount;


    private String refreshFlag;
    private Integer currentAttemptIndex;

    public ProposalContextEntity() {
        // jpa empty constructor
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public String getRefreshFlag() {
        return refreshFlag;
    }

    public void setRefreshFlag(String refreshFlag) {
        this.refreshFlag = refreshFlag;
    }

    public List<ProposalContextApproachEntity> getSlots() {
        return contextApproaches;
    }

    public Integer getMaximumEmpties() {
        return maximumEmpties;
    }

    public void setMaximumEmpties(int maximumEmpties) {
        this.maximumEmpties = maximumEmpties;
    }

    public Integer getDishCountPerSlot() {
        return dishCountPerSlot;
    }

    public void setDishCountPerSlot(int dishCountPerSlot) {
        this.dishCountPerSlot = dishCountPerSlot;
    }

    public ApproachType getApproachType() {
        return approachType;
    }

    public void setApproachType(ApproachType approachType) {
        this.approachType = approachType;
    }

    public Integer getProposalCount() {
        return proposalCount;
    }

    public void setProposalCount(int proposalCount) {
        this.proposalCount = proposalCount;
    }

    public void setContextApproaches(List<ProposalContextApproachEntity> contextApproaches) {
        this.contextApproaches = contextApproaches;
    }

    public int getCurrentAttemptIndex() {
        return currentAttemptIndex;
    }

    public void setCurrentAttemptIndex(int currentAttemptIndex) {
        this.currentAttemptIndex = currentAttemptIndex;
    }
}