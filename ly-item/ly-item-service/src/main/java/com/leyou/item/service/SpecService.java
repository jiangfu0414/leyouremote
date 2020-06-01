package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecService {

    @Autowired
    private SpecGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroupDTO> querySpecGroupByCategoryId(Long cid) {
        SpecGroup specGroups = new SpecGroup();
        specGroups.setCid(cid);
        List<SpecGroup> list = groupMapper.select(specGroups);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //对象转换
        return BeanHelper.copyWithCollection(list,SpecGroupDTO.class);
    }

    public List<SpecParamDTO> querySpecParamsBySpecGroupId(Long gid,Long cid) {
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        List<SpecParam> specParams=this.specParamMapper.select(record);

        if (CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.DATA_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(specParams,SpecParamDTO.class);
    }
}
