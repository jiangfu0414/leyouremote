package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.*;
import com.leyou.item.entity.Sku;
import com.leyou.item.entity.Spu;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.common.base.insert.InsertSelectiveMapper;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandServiece brandServiece;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    public PageResult<SpuDTO> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page,rows);
        //动态sql生成器
        Example example = new Example(Spu.class);
        //动态sql的拼接工具
        Example.Criteria criteria = example.createCriteria();
        //如果有值，对name进行模糊查询
        if (!StringUtils.isBlank(key)){
            criteria.andLike("name","%"+key+"%");
        }
        //如果有值，匹配saleable对应的值
        if(null != saleable) {
            criteria.andEqualTo("saleable", saleable);
        }
        List<Spu> spus = this.spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(spus)){
            throw new LyException(ExceptionEnum.DATA_NOT_FOUND);
        }
        //自动统计总元素个数，和页码数
        PageInfo<Spu> pageInfo = new PageInfo<>();
        List<SpuDTO> spuDTOS = BeanHelper.copyWithCollection(spus, SpuDTO.class);
        spuDTOS.forEach(spuDTO -> {

            List<CategoryDTO> categoryDTOS = this.categoryService.queryCategoryByIds(spuDTO.getCategoryIds());
            //迭代分类集合，挨个取出name，并把所有的name，拼接成字符串，分隔符自定义
            String names = categoryDTOS.stream()
                    .map(CategoryDTO::getName)
                    .collect(Collectors.joining("/"));
            spuDTO.setCategoryName(names);

            //调用品牌查询业务，根据品牌的id查询对应的对象
            spuDTO.setBrandName(this.brandServiece.queryBrandById(spuDTO.getBrandId()).getName());
        });
        //封装spu查询结果的返回
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getPages(),spuDTOS);
    }

    @Transactional
    public void addGoods(SpuDTO spuDTO) {

        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);

        //保存spu，同时也有主键回显
        int count = this.spuMapper.insertSelective(spu);

        if (count!=1){
            throw new LyException(ExceptionEnum.DATA_SAVE_ERROR);
        }

        SpuDetail spuDetail = BeanHelper.copyProperties(spuDTO.getSpuDetail(), SpuDetail.class);
        spuDetail.setSpuId(spu.getId());

        //保存spuDetail
        count = this.spuDetailMapper.insertSelective(spuDetail);
        if (count!=1){
            throw new LyException(ExceptionEnum.DATA_SAVE_ERROR);
        }
        saveSkus(spuDTO, spu);

    }

    private void saveSkus(SpuDTO spuDTO, Spu spu) {
        int count;//因为一个spu下可以有多个sku，sku前台提交过来的是一个数组
        List<Sku> skus = spuDTO.getSkus().stream()    //从spuDTO中获得所有的skuDTO
                .map(skuDTO -> {                      //循环迭代skuDTO
                    skuDTO.setSpuId(spu.getId());     //把每个skuDTO赋值spuid
                    return BeanHelper.copyProperties(skuDTO, Sku.class);   //把skuDTO转为sku返回
                }).collect(Collectors.toList());


        //批量保存sku
        count = this.skuMapper.insertList(skus);
        if (count != skus.size()) {
            throw new LyException(ExceptionEnum.DATA_SAVE_ERROR);
        }
    }

    public void motifysaleable(Long spuId, Boolean saleable) {
        Spu spu = this.spuMapper.selectByPrimaryKey(spuId);

        spu.setUpdateTime(new Date());

        spu.setSaleable(saleable);
        //修改spu的上下架状态
        this.spuMapper.updateByPrimaryKeySelective(spu);

        Sku record = new Sku();
        record.setSpuId(spu.getId());

        //根据spuid查询对应的sku
        List<Sku> skus = this.skuMapper.select(record);

        //遍历sku的集合,修改每一个sku的上下架状态
        skus.forEach(sku -> {
            sku.setUpdateTime(spu.getUpdateTime());
            sku.setEnable(saleable);
            this.skuMapper.updateByPrimaryKeySelective(sku);
        });

    }

    public SpuDetailDTO querySpuDetailBySpuId(Long spuid) {

        SpuDetail spuDetail = this.spuDetailMapper.selectByPrimaryKey(spuid);
        if (spuDetail == null) {
            new LyException(ExceptionEnum.DATA_NOT_FOUND);
        }
        return BeanHelper.copyProperties(spuDetail,SpuDetailDTO.class);
    }

    public List<SkuDTO> querySkuBySpuid(Long spuid) {

        Sku record = new Sku();
        record.setSpuId(spuid);
        List<Sku> skus = this.skuMapper.select(record);
        if (CollectionUtils.isEmpty(skus)){
            new LyException(ExceptionEnum.DATA_NOT_FOUND);
        }
       return BeanHelper.copyWithCollection(skus,SkuDTO.class);
    }



    @Transactional
    public void updateGoods(SpuDTO spuDTO) {
        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);
        spu.setUpdateTime(new Date());

        //修改spu
        int count = this.spuMapper.updateByPrimaryKeySelective(spu);
        if (1!=count){
            throw new LyException(ExceptionEnum.DATA_MODIFY_ERROER);
        }

        //直接修改spuDetail
        SpuDetail spuDetail = BeanHelper.copyProperties(spuDTO.getSpuDetail(), SpuDetail.class);
        spuDetail.setUpdateTime(spu.getUpdateTime());
        count = this.spuDetailMapper.updateByPrimaryKeySelective(spuDetail);
        if (1!=count){
            throw new LyException(ExceptionEnum.DATA_MODIFY_ERROER);
        }

        //修改sku，1.清理之前的skus
        Sku record = new Sku();
        record.setSpuId(spu.getId());

        //统计要删除sku的数量
        int skuCount = this.skuMapper.selectCount(record);
        //删除sku
        count = this.skuMapper.delete(record);
        //比较sku的数量和要删除的商品的数量
        if (skuCount!=count){
           throw new LyException(ExceptionEnum.DATA_MODIFY_ERROER);
        }

        //重新保存sku
        saveSkus(spuDTO,spu);
    }
}
