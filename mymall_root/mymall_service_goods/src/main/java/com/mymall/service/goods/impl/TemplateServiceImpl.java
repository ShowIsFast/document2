package com.mymall.service.goods.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mymall.pojo.goods.Template;
import com.mymall.contract.goods.TemplateService;
import com.mymall.service.goods.dao.TemplateMapper;
import com.mymall.pojo.entity.PageResult;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@DubboService
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateMapper templateMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Template> findAll() {
        return templateMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Template> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Template> templates = (Page<Template>) templateMapper.selectAll();

        return new PageResult<Template>(templates.getTotal(),templates.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Template> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);

        return templateMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Template> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Template> templates = (Page<Template>) templateMapper.selectByExample(example);

        return new PageResult<Template>(templates.getTotal(),templates.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Template findById(Integer id) {
        return templateMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param template
     */
    public void add(Template template) {
        templateMapper.insert(template);
    }

    /**
     * 修改
     * @param template
     */
    public void update(Template template) {
        templateMapper.updateByPrimaryKeySelective(template);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        templateMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Template.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 模板名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }

            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 规格数量
            if(searchMap.get("specNum")!=null ){
                criteria.andEqualTo("specNum",searchMap.get("specNum"));
            }
            // 参数数量
            if(searchMap.get("paraNum")!=null ){
                criteria.andEqualTo("paraNum",searchMap.get("paraNum"));
            }
        }

        return example;
    }

}
