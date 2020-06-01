package com.leyou.item.service;

import ch.qos.logback.classic.spi.LoggerContextVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;
import com.leyou.item.mapper.BrandMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class BrandServiece {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<BrandDTO> pageQuery(Integer page,Integer rows,String sortBy,Boolean desc,String key) {
        //分页查询
        PageHelper.startPage(page,rows);
        //动态sql生成器
        Example example = new Example(Brand.class);
        if (!StringUtils.isBlank(sortBy)){
            example.setOrderByClause(sortBy+(desc?" DESC":" ASC")); //相当于group by
        }
        //动态sql拼接工具
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(key)){
            criteria.andLike("name","%+key+%");
            criteria.orEqualTo("letter",key);
        }
        //无条件，只分页查询品牌
        List<Brand> brands = this.brandMapper.selectByExample(example);//根据动态生成的sql去查询

        if (CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //根据查询条件，自动统计总元素个数，总和的页码数
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        //返回分页组合的结果，三个参数，总元素个数，总页数

        return new PageResult<>(pageInfo.getTotal(),pageInfo.getPages(), BeanHelper.copyWithCollection(brands,BrandDTO.class));

    }


    @Transactional
    public void addBrand(BrandDTO brandDTO, List<Long> cids) {

        Brand brand = BeanHelper.copyProperties(brandDTO, Brand.class);
        //保存主键信息并伴随主键回显
        int count = this.brandMapper.insertSelective(brand);
        if (1!=count){
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }

        //保存品牌和分类中间表的数据,不能使用通用mapper，所以要用手写sql
        count= this.brandMapper.insertCategoryBrand(cids,brand.getId());
        if (cids.size()!=count){
            new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
    }


    @Transactional
    public void updataBrand(BrandDTO brandDTO, List<Long> cids) {
        Brand brand=BeanHelper.copyProperties(brandDTO,Brand.class);
        brand.setUpdateTime(new Date());
        //对品牌进行修改
        int count = this.brandMapper.updateByPrimaryKeySelective(brand);

        if (1!=count){
            throw new LyException(ExceptionEnum.DATA_MODIFY_ERROER);
        }

        //先删除之前品牌和分类在中间表之间的关系

        //TODO 先统计要删除的个数，然后执行删除，删除成功后，比较和删除成功的行数，以及要删除的行数
        count = this.brandMapper.deleteCategoryBrandByBrandId(brand.getId());

        //重建表之间的关系
        //保存品牌和分类中间表的数据,不能使用通用mapper，所以要用手写sql
        count= this.brandMapper.insertCategoryBrand(cids,brand.getId());
        if (cids.size()!=count) {
            new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
    }

    public BrandDTO queryBrandById(Long brandId) {
        Brand brand = this.brandMapper.selectByPrimaryKey(brandId);
        if (null==brand){
            new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return BeanHelper.copyProperties(brand,BrandDTO.class);
    }

    public List<BrandDTO> queryBrandByCategory(Long cid) {

        List<Brand> brands = this.brandMapper.queryBrandByCategory(cid);
        if (CollectionUtils.isEmpty(brands)){
            new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(brands,BrandDTO.class);
    }
}
