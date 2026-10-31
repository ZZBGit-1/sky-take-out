package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    public void insert(SetmealDTO setmealDTO) {
      //往套餐表中新增套餐基础信息

        Setmeal setmeal = new Setmeal();
        //将setmealDTO中的属性复制到setmeal中
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insertSetmeal(setmeal);

        //往套餐菜品表中新增套餐菜品信息
        Long setmealId = setmeal.getId();//获取插入的菜品的id
        if(setmealDTO.getSetmealDishes() != null && setmealDTO.getSetmealDishes().size() > 0){
            setmealDTO.getSetmealDishes().forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            setmealDishMapper.insertSetmealDishes(setmealDTO.getSetmealDishes());
        }

    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult setmealQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.setmealQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        //根据id查询套餐基础信息
        Setmeal setmeal = setmealMapper.getById(id);
        //根据套餐id查询套餐菜品信息
        List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDishes(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateSetmeal(SetmealDTO setmealDTO) {

        //修改套餐的基础信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        //修改套餐菜品信息
        //先删除套餐菜品信息
        setmealDishMapper.deleteSetmealDishes(setmealDTO.getId());
        //再新增套餐菜品信息
        //先获取当前套餐菜品列表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //判断当前套餐菜品列表是否为空  如果为空就不进行更新操作
        if(setmealDishes != null && setmealDishes.size() > 0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });
            setmealDishMapper.insertSetmealDishes(setmealDishes);
        }

    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    public void deleteSetmeal(List<Long> ids) {
        //根据id删除套餐
        //判断套餐是否在售
        if(setmealMapper.countByStatus(ids) > 0)
        {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }

        //删除套餐
        setmealMapper.deleteSetmeal(ids);

        //删除套餐菜品信息
        setmealDishMapper.deleteSetmealDishesByIds(ids);
    }

    /**
     * 更新套餐状态
     * @param status
     * @param id
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        if(StatusConstant.ENABLE == status){
            Integer disableCount = setmealDishMapper.countByStatus(id);
            if(disableCount > 0){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        setmealMapper.update(setmeal);
    }

    /**
     * 根据分类id查询套餐列表
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> getByCategoryId(Long categoryId) {
        return setmealMapper.getByCategoryId(categoryId);
    }

    /**
     * 根据id查询套餐菜品列表
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getSetmealDish(Long id) {
        return setmealMapper.getSetmealDish(id);
    }


}
