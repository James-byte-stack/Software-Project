package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.Carlist;
import com.example.springboot.mapper.CarlistMapper;
import com.example.springboot.service.ICarlistService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.Result;
import org.springframework.web.multipart.MultipartFile;
import com.example.springboot.entity.User;
import com.example.springboot.utils.TokenUtils;

import com.example.springboot.service.ICarService;
import com.example.springboot.entity.Car;

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
@RequestMapping("/car")
public class CarController {

    @Resource
    private ICarService carService;
    @Resource
    private CarlistMapper carlistMapper;

    private final String now = DateUtil.now();

    private  String accesskey="dSvPpmyg3NoQAlYC6sX76ZB8yKXyTUJG";

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Car car) throws IOException {
        if (car.getId() == null) {
            //car.setTime(DateUtil.now());
            //car.setUser(TokenUtils.getCurrentUser().getUsername());
        };
        String carlistname = car.getCarlistname();
        QueryWrapper<Carlist> carlistQueryWrapper = new QueryWrapper<>();
        carlistQueryWrapper.eq("carlistname", carlistname);
        Carlist carlist = carlistMapper.selectOne(carlistQueryWrapper);
        String  url = "http://yingyan.baidu.com/api/v3/entity/add";
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("ak", accesskey);
        builder.addTextBody("service_id", carlist.getCarlistbiaoshi());
        builder.addTextBody("entity_name", car.getCarNumber());
        builder.addTextBody("city", car.getLocation());
        builder.addTextBody("district", car.getLocation());
        String StringResult = sendPostFormData(url, builder);
        System.out.println(StringResult);
        carService.save(car);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        carService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        carService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(carService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(carService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Car> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
//        User currentUser = TokenUtils.getCurrentUser();
//        if (currentUser.getRole().equals("ROLE_USER")) {
//            queryWrapper.eq("user", currentUser.getUsername());
//        }
        return Result.success(carService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
    * 导出接口
    */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Car> list = carService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Car信息表", "UTF-8");
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
        List<Car> list = reader.readAll(Car.class);

        carService.saveBatch(list);
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







    /**
     * 解析HttpEntity
     * @param entity
     * @return
     * @throws IOException
     */
//    private static String entityToString(HttpEntity entity) throws IOException {
//        String result = null;
//        InputStreamReader inputStreamReader = null;
//        try {
//            if (entity != null) {
//                long contentLength = entity.getContentLength();
//                if (contentLength != -1L && contentLength < 2048L) {
//                    result = EntityUtils.toString(entity, "UTF-8");
//                } else {
//                    inputStreamReader = new InputStreamReader(entity.getContent(), "UTF-8");
//                    CharArrayBuffer charArrayBuffer = new CharArrayBuffer(2048);
//                    char[] chars = new char[1024];
//
//                    int index;
//                    while((index = inputStreamReader.read(chars)) != -1) {
//                        charArrayBuffer.append(chars, 0, index);
//                    }
//
//                    result = charArrayBuffer.toString();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            inputStreamReader.close();
//        }
//
//        return result;
//    }
}

