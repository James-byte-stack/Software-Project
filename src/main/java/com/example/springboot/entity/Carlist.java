package com.example.springboot.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import cn.hutool.core.annotation.Alias;

/**
 * <p>
 * 
 * </p>
 *
 * @author Jing
 * @since 2024-03-09
 */
@Getter
@Setter
@ApiModel(value = "Carlist对象", description = "")
public class Carlist implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("负责人")
    @Alias("负责人")
    private String chargeman;

    @ApiModelProperty("车队名称")
    @Alias("车队名称")
    private String carlistname;

    @ApiModelProperty("车队观看地址")
    @Alias("车队观看地址")
    private String carlisturl;

    @ApiModelProperty("车队服务标识")
    @Alias("车队服务标识")
    private String carlistbiaoshi;


}
