package com.example.springboot.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@ApiModel(value = "Locus对象", description = "")
public class Locus implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @ApiModelProperty("车牌号")
    @Alias("车牌号")
    private String carnumber;

    @ApiModelProperty("纬度")
    @Alias("纬度")
    private String latitude;

    @ApiModelProperty("经度")
    @Alias("经度")
    private String longitude;

    @ApiModelProperty("时间")
    @Alias("时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime nowtime;

    @ApiModelProperty("速度")
    @Alias("速度")
    private String spedd;

    @ApiModelProperty("车队名")
    @Alias("车队名")
    private String carlistname;


}
