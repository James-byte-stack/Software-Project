package com.example.springboot.service.impl;

import com.example.springboot.entity.Carlist;
import com.example.springboot.mapper.CarlistMapper;
import com.example.springboot.service.ICarlistService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jing
 * @since 2024-03-09
 */
@Service
public class CarlistServiceImpl extends ServiceImpl<CarlistMapper, Carlist> implements ICarlistService {

}
