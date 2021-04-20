package top.hcode.blog.service.impl;

import top.hcode.blog.entity.MBlog;
import top.hcode.blog.mapper.MBlogMapper;
import top.hcode.blog.service.MBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class MBlogServiceImpl extends ServiceImpl<MBlogMapper, MBlog> implements MBlogService {

}
