package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.Carlist;
import com.example.springboot.mapper.CarlistMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.Result;
import org.springframework.web.multipart.MultipartFile;
import com.example.springboot.entity.User;
import com.example.springboot.utils.TokenUtils;

import com.example.springboot.service.ILocusService;
import com.example.springboot.entity.Locus;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Jing
 * @since 2024-03-09
 */
@RestController
@RequestMapping("/locus")
public class LocusController {

    @Resource
    private ILocusService locusService;
    @Resource
    private CarlistMapper carlistMapper;
    private final String now = DateUtil.now();

    private  String accesskey="dSvPpmyg3NoQAlYC6sX76ZB8yKXyTUJG";
    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Locus locus) {
        if (locus.getId() == null) {
            //locus.setTime(DateUtil.now());
            //locus.setUser(TokenUtils.getCurrentUser().getUsername());
        }
        String carlistname = locus.getCarlistname();
        LocalDateTime nowtime = locus.getNowtime();
        System.out.println(nowtime);



        ZonedDateTime zonedDateTime = nowtime.atZone(ZoneId.of("Asia/Shanghai"));
        Instant instant = zonedDateTime.toInstant();
        long epochSecond = instant.getEpochSecond();
        QueryWrapper<Carlist> carlistQueryWrapper = new QueryWrapper<>();
        carlistQueryWrapper.eq("carlistname", carlistname);
        Carlist carlist = carlistMapper.selectOne(carlistQueryWrapper);
        String  url = "http://yingyan.baidu.com/api/v3/track/addpoint";
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("ak", accesskey);
        builder.addTextBody("service_id", carlist.getCarlistbiaoshi());
        builder.addTextBody("entity_name", locus.getCarnumber());
        builder.addTextBody("latitude", locus.getLatitude());
        builder.addTextBody("longitude", locus.getLongitude());
        System.out.println(epochSecond);
        builder.addTextBody("loc_time", String.valueOf(epochSecond));
        builder.addTextBody("coord_type_input", "wgs84");
        String StringResult = sendPostFormData(url, builder);
        System.out.println(StringResult);
        locusService.saveOrUpdate(locus);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        locusService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        locusService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(locusService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(locusService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Locus> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
//        User currentUser = TokenUtils.getCurrentUser();
//        if (currentUser.getRole().equals("ROLE_USER")) {
//            queryWrapper.eq("user", currentUser.getUsername());
//        }
        return Result.success(locusService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
    * 导出接口
    */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Locus> list = locusService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Locus信息表", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

        }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
        List<Locus> list = reader.readAll(Locus.class);

        locusService.saveBatch(list);
        return Result.success();
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }
    public static String  sendPostFormData(String url, MultipartEntityBuilder builder){
        String StringResult = "";
        //不需要手动关闭httpClient
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);

        // 构建form-data请求体
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);

        // 发送请求并获取响应
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            StringResult = EntityUtils.toString(responseEntity);
        } catch (IOException e) {
            StringResult = "errorException:" + e.getMessage();
            e.printStackTrace();
        }
        return StringResult;
    }
}

