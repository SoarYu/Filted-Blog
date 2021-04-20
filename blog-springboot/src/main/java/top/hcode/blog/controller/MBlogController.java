package top.hcode.blog.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import top.hcode.blog.common.results.CommonResult;
import top.hcode.blog.entity.MBlog;
import top.hcode.blog.sensi.SensitiveFilter;
import top.hcode.blog.service.MBlogService;
import top.hcode.blog.util.ShiroUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 */
@RestController
@RequestMapping
public class MBlogController {
    @Autowired
    MBlogService blogService;
    @Autowired
    SensitiveFilter filter;
    final static String PIC_PATH = "static/img";
    @GetMapping("/blogs")
    public CommonResult blogs(Integer currentPage) {
        if(currentPage == null || currentPage < 1) currentPage = 1;
        Page page = new Page(currentPage, 5);

        IPage pageData = blogService.page(page, new QueryWrapper<MBlog>().orderByDesc("gmt_create"));
        //对博客列表进行过滤
        pageData.setRecords(filter.BlogsFilter(pageData.getRecords()));
        return CommonResult.successResponse(pageData,"获取成功");
    }

    @GetMapping("/blog/{id}")
    public CommonResult detail(@PathVariable(name = "id") Long id) {
        MBlog blog = blogService.getById(id);
        Assert.notNull(blog, "该博文已删除！");
        return CommonResult.successResponse(filter.BlogFilter(blog),"查询成功");
    }

    @GetMapping("/blog/real/{id}")
    public CommonResult realDetail(@PathVariable(name = "id") Long id) {
        MBlog blog = blogService.getById(id);
        Assert.notNull(blog, "该博文已删除！");
        return CommonResult.successResponse(blog,"查询成功");
    }

    @RequiresAuthentication
    @PostMapping("/blog/edit")
    public CommonResult edit(@Validated @RequestBody MBlog blog) {
        MBlog temp = null;
        if(blog.getId() != null) {
            temp = blogService.getById(blog.getId());
            Assert.isTrue(temp.getUserId().longValue() == ShiroUtil.getProfile().getId().longValue(), "没有权限编辑");
        } else {
            temp = new MBlog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setGmtCreate(LocalDateTime.now());
            temp.setStatus(0);
        }
        BeanUtil.copyProperties(blog, temp, "id", "userId", "gmtCreate", "status");
        blogService.saveOrUpdate(temp);
        return CommonResult.successResponse( null,"操作成功");
    }

    @RequiresAuthentication
    @GetMapping("/blog/delete/{id}")
    public CommonResult delete(@PathVariable(name = "id") Long id) {
        boolean result = blogService.removeById(id);
        Assert.isTrue(result, "删除失败！该博文不存在！");
        return CommonResult.successResponse( null,"删除成功");
    }

    @PostMapping("upload")
    public CommonResult upload(MultipartHttpServletRequest mutiRequest,HttpServletRequest request){
        Long blogId;
        if(mutiRequest.getParameter("blogId").equals("undefined"))
            blogId = Long.valueOf(0);
        else
            blogId =Long.valueOf(mutiRequest.getParameter("blogId"));
        String savePath = "src/main/resources/" + PIC_PATH +"/"+blogId; // 存储路径
        File folder = new File(savePath); //生成带当前日期的文件路径
        if(!folder.isDirectory()){
            folder.mkdirs();
        }

        String originationalName = mutiRequest.getFile("image").getOriginalFilename(); //获取图片名
        String absolutePath = folder.getAbsolutePath(); //转换成绝对路径
        try {

            File fileToSave = new File(absolutePath + File.separator + originationalName);
            mutiRequest.getFile("image").transferTo(fileToSave); //图片存储到服务端
            String returnPath = request.getScheme() + "://"
                    + request.getServerName()+":"+request.getServerPort()+"/"+PIC_PATH+"/"+blogId+"/"
                     + originationalName;
            return CommonResult.successResponse( returnPath,"上传成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return CommonResult.errorResponse("上传失败");
    }


}