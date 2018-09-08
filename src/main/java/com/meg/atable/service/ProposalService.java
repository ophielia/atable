package com.meg.atable.service;

import com.meg.atable.data.entity.ProposalEntity;

import java.security.Principal;

/**
 * Created by margaretmartin on 30/10/2017.
 */
public interface ProposalService {

    ProposalEntity getProposalById(String name, Long proposalId);

    ProposalEntity getTargetProposalById(String name, Long proposalId);

    void selectDishInSlot(String userName,  Long proposalId, Long slotId, Long dishId);

    void clearDishFromSlot(Principal principal, Long proposalId, Long slotId, Long dishId);
}
