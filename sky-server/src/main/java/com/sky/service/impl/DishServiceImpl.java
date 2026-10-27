package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorsMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorsMapper dishFlavorsMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    @Transactional // 事务管理
    public void addDishWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //属性拷贝
        dishMapper.insert(dish);

        long dishId = dish.getId();// 获取插入菜品的ID
        //向菜品口味表插入数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {dishFlavor.setDishId(dishId);});

            dishFlavorsMapper.insertBatch(flavors);

        }

    }
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        Long total = page.getTotal();
        return new PageResult(total, page.getResult());
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否在售
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //菜品是否被套餐关联
        List<Long> setmealDishIds = setmealDishMapper.getSetmealDishIds(ids);
        if (setmealDishIds != null && setmealDishIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品
        dishMapper.deleteBatch(ids);

        //删除口味数据
        dishFlavorsMapper.deleteByDishIds(ids);
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品
        Dish dish = dishMapper.getById(id);

        //根据菜品id查询口味
        List<DishFlavor> flavors = dishFlavorsMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Override
    @Transactional // 菜品和口味要么一起改成功，要么一起回滚
    public void updateDishWithFlavors(DishDTO dishDTO) {
        //根据id修改菜品：DTO 没有公共字段，必须拷贝成实体才能走 @AutoFill
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateById(dish);

        //根据id修改口味
        //先删除当前口味信息，然后再把前端更改的新增进去
        Long dishId = dishDTO.getId();
        dishFlavorsMapper.deleteByDishId(dishId);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            //前端传来的口味不带 dishId，必须手动回填，否则外键为 NULL 查不出来
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            dishFlavorsMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品启用停用状态
     * @param status
     * @param id
     */
    @Override
    public void startOrStopDish(Integer status, Long id) {
        log.info("修改菜品状态，状态：{}，id：{}", status, id);
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);

        dishMapper.updateById(dish);
    }


}
