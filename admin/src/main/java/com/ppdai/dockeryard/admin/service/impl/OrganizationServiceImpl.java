package com.ppdai.dockeryard.admin.service.impl;

import com.ppdai.atlas.model.OrgDto;
import com.ppdai.dockeryard.admin.service.OrganizationService;
import com.ppdai.dockeryard.core.mapper.OrganizationMapper;
import com.ppdai.dockeryard.core.po.OrganizationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private static final String SYNCHRONIZATION_MESSAGE = "by synchronization";
    private static Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    @Autowired
    private OrganizationMapper organizationMapper;

    @Override
    public List<OrganizationEntity> getAll() {
        return organizationMapper.getAll();
    }

    @Override
    public OrganizationEntity getById(Long id) {
        return organizationMapper.getById(id);
    }

    @Override
    public void update(OrganizationEntity org) {
        organizationMapper.update(org);
    }

    @Override
    public void delete(Long id) {
        organizationMapper.delete(id);
    }

    @Override
    public void insert(OrganizationEntity org) {
        organizationMapper.insert(org);
    }

    @Override
    public void syncOrganizations(List<OrgDto> remoteOrgList) {
        for (OrgDto orgDto : remoteOrgList) {
            Long id = orgDto.getId();
            OrganizationEntity organization = organizationMapper.getById(id);
            try {
                if (organization == null) {
                    organization = new OrganizationEntity();
                    organization.setId(orgDto.getId());
                    organization.setName(orgDto.getName());
                    organization.setShortCode(orgDto.getOrgCode());
                    organization.setInsertBy(StringUtils.hasLength(orgDto.getInsertBy()) ? orgDto.getInsertBy() : SYNCHRONIZATION_MESSAGE);
                    organization.setUpdateBy(StringUtils.hasLength(orgDto.getUpdateBy()) ? orgDto.getUpdateBy() : SYNCHRONIZATION_MESSAGE);
                    organization.setIsActive(orgDto.isActive());
                    organizationMapper.insert(organization);
                } else {
                    organization.setName(orgDto.getName());
                    organization.setShortCode(orgDto.getOrgCode());
                    organization.setUpdateBy(StringUtils.hasLength(orgDto.getUpdateBy()) ? orgDto.getUpdateBy() : SYNCHRONIZATION_MESSAGE);
                    organization.setIsActive(orgDto.isActive());
                    organizationMapper.update(organization);
                }
            } catch (Exception e) {
                logger.error("insert or update organization failed", e);
            }
        }
    }

}
